import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpInterceptor,
  HttpEvent
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable()
export class ApiInterceptor implements HttpInterceptor {
  private versions = environment.apiVersions;
  private defaultVersion = environment.defaultApiVersion;

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (!request.url.startsWith('http')) {
      const version = (request as any).version || this.defaultVersion;
      const normalisedPath = request.url.startsWith('/') ? request.url.substring(1) : request.url;
      const apiReq = request.clone({
        url: `${this.versions[version]}/${normalisedPath}`
      });
      return next.handle(apiReq);
    }
    return next.handle(request);
  }
}
