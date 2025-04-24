import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { UserAccount } from './user-account';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserAccountService {

  private baseUrl = 'http://localhost:8080/api/v1/user-accounts';
  constructor(private httpClient: HttpClient) { }


  getUserAccountList(): Observable<UserAccount[]> {
    return this.httpClient.get<UserAccount[]>(this.baseUrl)
  }

  postUserAccount(userAccount: UserAccount): Observable<Object> {
    return this.httpClient.post(this.baseUrl, userAccount);
  }

  getUserAccountById(id: number): Observable<UserAccount> {
    return this.httpClient.get<UserAccount>(this.baseUrl + "/" + id);
  }

  putUserAccount(id: number, userAccount: UserAccount): Observable<UserAccount> {
    return this.httpClient.put<UserAccount>(this.baseUrl + "/" + id, userAccount);
  }

  deleteUserAccount(id: number): Observable<UserAccount> {

    return this.httpClient.delete<UserAccount>(this.baseUrl + "/" + id);
  }


  getUserAccountsCountByRoleAndCustomerAccount(roleName: string, parentAccountId: number): Observable<number> {

    return this.httpClient.get<number>(`${this.baseUrl}/count?roleName=${roleName}&parentAccountId=${parentAccountId}`);
  }
}
