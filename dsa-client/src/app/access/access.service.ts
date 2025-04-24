import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { UserLocalStorageService } from './user-local-storage.service';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AccessService {

  private baseUrl = 'http://localhost:8080/api/v1/auth';

  requestHeader = new HttpHeaders(
    { "No-Auth": "True" }
  );

  constructor(
    private httpClient: HttpClient,
    private userLocalStorage: UserLocalStorageService
  ) { }

  public login(login: any): Observable<Object> {
    return this.httpClient.post(this.baseUrl + "/authenticate", login, { headers: this.requestHeader });
  }

  public refreshToken(): Observable<any> {
    const refreshToken = this.userLocalStorage.getRefreshToken();
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${refreshToken}`,
      'No-Auth': 'True'
    });

    return this.httpClient.post(`${this.baseUrl}/refresh-token`, {}, { headers });

  }

  public logout(): Observable<any> {
    const accessToken = this.userLocalStorage.getAccessToken();

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${accessToken}`
    });

    return this.httpClient.post(`${this.baseUrl}/logout`, {}, { headers });
  }

  public roleMatch(allowedRoles: string[]): boolean {
    let isMatch = false;

    const userRoles: string | null = this.userLocalStorage.getUserRoles();

    if (userRoles) {
      for (let i = 0; i < allowedRoles.length; i++) {
        if (userRoles.includes(allowedRoles[i])) {
          isMatch = true;
          break;
        }
      }
    }

    return isMatch;
  }

}
