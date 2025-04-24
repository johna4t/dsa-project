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

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> { // eslint-disable-line @typescript-eslint/no-explicit-any
    if (!request.url.startsWith('http')) {
      const version = (request as any).version || this.defaultVersion; // eslint-disable-line @typescript-eslint/no-explicit-any
      const apiReq: HttpRequest<any> = request.clone({ url: `${this.versions[version]}/${request.url}` }); // eslint-disable-line @typescript-eslint/no-explicit-any
      return next.handle(apiReq);
    }
    return next.handle(request);
  }
}
