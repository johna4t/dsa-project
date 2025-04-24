import { Injectable } from '@angular/core';
import { HttpErrorResponse, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AlertifyService } from '../dialog/alertify/alertify.service';
import { environment } from '../../environments/environment';
import { MatDialog } from '@angular/material/dialog';
import { ErrorDialogComponent } from '../dialog/material/error-dialog/error-dialog.component';

@Injectable({
  providedIn: 'root'
})
export class ErrorInterceptor implements HttpInterceptor {

  private MATERIAL_ALERTING: string = 'material';

  constructor(private alertify: AlertifyService, private dialog: MatDialog) { }

  intercept(request: HttpRequest<any>, next: HttpHandler) {
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        
        // Let AuthInterceptor handle auth errors other than login
        if ((error.status === 401 || error.status === 403) &&
          !request.url.includes('/authenticate')) {
          // Let AuthInterceptor handle refresh/token issues
          return throwError(() => error); 
        }

        // Fallback error handling
        const errorMessage = this.setError(error);
        if (this.MATERIAL_ALERTING === environment.alerting) {
          this.dialog.open(ErrorDialogComponent, {
            data: { message: errorMessage }
          });
        } else {
          this.alertify.error(errorMessage);
        }

        return throwError(() => new Error(errorMessage));
      })
    );
  }

  private setError(error: HttpErrorResponse): string {
    let errorMessage = 'Unknown error occurred';
    if (error.error instanceof ErrorEvent) {
      errorMessage = error.error.message;
    } else {
      if (error.status !== 0 && error.error?.message) {
        errorMessage = error.error.message;
      }
    }
    return errorMessage;
  }
}
