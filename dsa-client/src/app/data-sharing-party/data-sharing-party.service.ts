import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { DataSharingParty } from './data-sharing-party';
import { Observable } from 'rxjs';
@Injectable({
  providedIn: 'root'
})
export class DataSharingPartyService {

  private baseUrl = 'http://localhost:8080/api/v1/data-sharing-parties';

  constructor(private httpClient: HttpClient) { }

  getDataSharingPartyList(): Observable<DataSharingParty[]> {
    return this.httpClient.get<DataSharingParty[]>(this.baseUrl)
  }

  postDataSharingParty(dataSharingParty: DataSharingParty): Observable<Object>{
    return this.httpClient.post(this.baseUrl, dataSharingParty);
  }

  getDataSharingPartyById(id: number): Observable<DataSharingParty> {
    return this.httpClient.get<DataSharingParty>(this.baseUrl + "/" + id);
  }

  putDataSharingParty(dataSharingParty: DataSharingParty): Observable<DataSharingParty> {
    return this.httpClient.put<DataSharingParty>(this.baseUrl, dataSharingParty);
  }

  deleteDataSharingParty(id: number): Observable<DataSharingParty> {
    return this.httpClient.delete<DataSharingParty>(this.baseUrl + "/" + id);
  }
}
