import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError, BehaviorSubject } from 'rxjs';
import { catchError, filter, switchMap, take } from 'rxjs/operators';
import { Router } from '@angular/router';
import { UserLocalStorageService } from '../user-local-storage.service';
import { AccessService } from '../access.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  private isRefreshing = false;
  private refreshTokenSubject: BehaviorSubject<string | null> = new BehaviorSubject<string | null>(null);

  constructor(
    private localStorage: UserLocalStorageService,
    private accessService: AccessService,
    private router: Router
  ) { }

  intercept(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    if (request.headers.get('No-Auth') === 'True') {
      return next.handle(request);
    }

    const token = this.localStorage.getAccessToken();
    let cloned = this.addToken(request, token);

    return next.handle(cloned).pipe(
      catchError((err: HttpErrorResponse) => {
        if (err.status === 401) {
          return this.handle401Error(request, next);
        } else if (err.status === 403) {
          this.router.navigate(['/forbidden']);
        }

        return throwError(() => err);
      })
    );
  }

  private addToken(request: HttpRequest<any>, token: string | null) {
    if (!token) return request;

    return request.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  private handle401Error(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

   if (!this.isRefreshing) {
      this.isRefreshing = true;
      this.refreshTokenSubject.next(null);

      const refreshToken = this.localStorage.getRefreshToken();
      if (!refreshToken) {
        this.localStorage.clear();
        this.router.navigate(['/login']);
        return throwError(() => new Error('No refresh token available'));
      }

      return this.accessService.refreshToken().pipe(
        switchMap((res: any) => {
          this.isRefreshing = false;

          const newAccessToken = res.accessToken;
          const newRefreshToken = res.refreshToken;

          this.localStorage.setAccessToken(newAccessToken);
          this.localStorage.setRefreshToken(newRefreshToken);

          this.refreshTokenSubject.next(newAccessToken);

          return next.handle(this.addToken(request, newAccessToken));
        }),
        catchError((error) => {

          this.isRefreshing = false;
          this.localStorage.clear();
          this.router.navigate(['/login']);
          error = new Error('Refresh token failed');
          console.log(error);
          return throwError(() => new Error('Refresh token failed'));
        })
      );
    } else {
      return this.refreshTokenSubject.pipe(
        filter(token => token !== null),
        take(1),
        switchMap(token => next.handle(this.addToken(request, token)))
      );
    }
  }
}
