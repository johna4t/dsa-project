import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { DataSharingAgreement } from './data-sharing-agreement';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DataSharingAgreementService {


  private baseUrl = 'http://localhost:8080/api/v1/data-sharing-agreements';

  constructor(private httpClient: HttpClient) { }

  getDataSharingAgreementList(): Observable<DataSharingAgreement[]> {
    return this.httpClient.get<DataSharingAgreement[]>(this.baseUrl)
  }

  postDataSharingAgreement(dataSharingAgreement: DataSharingAgreement): Observable<Object> {
    return this.httpClient.post(this.baseUrl, dataSharingAgreement);
  }

  getDataSharingAgreementById(id: number): Observable<DataSharingAgreement> {
    return this.httpClient.get<DataSharingAgreement>(this.baseUrl + "/" + id);
  }

  putDataSharingAgreement(dataSharingAgreement: DataSharingAgreement): Observable<DataSharingAgreement> {
    return this.httpClient.put<DataSharingAgreement>(this.baseUrl, dataSharingAgreement);
  }

  deletedataSharingAgreement(id: number): Observable<DataSharingAgreement> {
    return this.httpClient.delete<DataSharingAgreement>(this.baseUrl + "/" + id);
  }
}
