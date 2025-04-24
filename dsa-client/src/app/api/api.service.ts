import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ApiService {
  constructor(private http: HttpClient) {}

  get<T>(path: string, version?: string): Observable<T> {
    return this.http.get<T>(path);
  }

  post<T>(path: string, body: unknown, version?: string): Observable<T> {
    return this.http.post<T>(path, body);
  }

  put<T>(path: string, body: unknown, version?: string): Observable<T> {
    return this.http.put<T>(path, body);
  }

  delete<T>(path: string, version?: string): Observable<T> {
    return this.http.delete<T>(path);
  }
}
