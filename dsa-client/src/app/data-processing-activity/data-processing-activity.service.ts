import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { DataProcessingActivity } from './data-processing-activity';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DataProcessingActivityService {

    private baseUrl = 'http://localhost:8080/api/v1/data-processing-activities';

    constructor(private httpClient: HttpClient) { }

    getDataProcessingActivityList(): Observable<DataProcessingActivity[]> {
      return this.httpClient.get<DataProcessingActivity[]>(this.baseUrl)
    }

    postDataProcessingActivity(dpv: DataProcessingActivity): Observable<Object> {
      return this.httpClient.post(this.baseUrl,dpv);
    }

    getDataProcessingActivityById(id: number): Observable<DataProcessingActivity> {
      return this.httpClient.get<DataProcessingActivity>(this.baseUrl + "/" + id);
    }

    putDataProcessingActivity(id: number, dpv: DataProcessingActivity): Observable<DataProcessingActivity> {
      return this.httpClient.put<DataProcessingActivity>(this.baseUrl + "/" + id, dpv);
    }

    deleteDataProcessingActivity(id: number): Observable<DataProcessingActivity> {

      return this.httpClient.delete<DataProcessingActivity>(this.baseUrl + "/" + id);
    }
}
