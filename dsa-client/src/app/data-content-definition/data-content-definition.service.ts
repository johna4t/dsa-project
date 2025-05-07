import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { DataContentDefinition } from './data-content-definition';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DataContentDefinitionService {

  private baseUrl = 'http://localhost:8080/api/v1/data-content-definitions';
  constructor(private httpClient: HttpClient) { }

  getDataContentDefinitionList(): Observable<DataContentDefinition[]> {
    return this.httpClient.get<DataContentDefinition[]>(this.baseUrl)
  }

  postDataContentDefinition(dcd: DataContentDefinition): Observable<Object> {
    return this.httpClient.post(this.baseUrl, dcd);
  }

  getDataContentDefinitionById(id: number): Observable<DataContentDefinition> {
    return this.httpClient.get<DataContentDefinition>(this.baseUrl + "/" + id);
  }

  putDataContentDefinition(id: number, dcd: DataContentDefinition): Observable<DataContentDefinition> {
    return this.httpClient.put<DataContentDefinition>(this.baseUrl + "/" + id, dcd);
  }

  deleteDataContentDefinition(id: number): Observable<DataContentDefinition> {

    return this.httpClient.delete<DataContentDefinition>(this.baseUrl + "/" + id);
  }



}
