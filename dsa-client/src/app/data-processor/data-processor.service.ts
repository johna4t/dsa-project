import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { DataProcessor } from './data-processor';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DataProcessorService {

    private baseUrl = 'http://localhost:8080/api/v1/data-processors';

    constructor(private httpClient: HttpClient) { }

    getDataProcessorList(): Observable<DataProcessor[]> {
      return this.httpClient.get<DataProcessor[]>(this.baseUrl)
    }

    postDataProcessor(dp: DataProcessor): Observable<Object> {
      return this.httpClient.post(this.baseUrl,dp);
    }

    getDataProcessorById(id: number): Observable<DataProcessor> {
      return this.httpClient.get<DataProcessor>(this.baseUrl + "/" + id);
    }

    putDataProcessor(id: number, dp: DataProcessor): Observable<DataProcessor> {
      return this.httpClient.put<DataProcessor>(this.baseUrl + "/" + id, dp);
    }

    deleteDataProcessor(id: number): Observable<DataProcessor> {

      return this.httpClient.delete<DataProcessor>(this.baseUrl + "/" + id);
    }
}
