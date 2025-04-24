import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { CustomerAccount } from './customer-account';
import { Observable } from 'rxjs';
@Injectable({
  providedIn: 'root'
})
export class CustomerAccountService {

  private baseUrl = 'http://localhost:8080/api/v1/customer-accounts';

  requestHeader = new HttpHeaders(
    { "No-Auth": "True" }
  );

  constructor(private httpClient: HttpClient) { }

  getCustomerAccountList(): Observable<CustomerAccount[]> {
    return this.httpClient.get<CustomerAccount[]>(this.baseUrl)
  }

  postCustomerAccount(customerAccount: CustomerAccount): Observable<Object> {
    return this.httpClient.post(this.baseUrl, customerAccount, { headers: this.requestHeader });
  }

  getCustomerAccountById(id: number): Observable<CustomerAccount> {
    return this.httpClient.get<CustomerAccount>(this.baseUrl + "/" + id);
  }

  }
