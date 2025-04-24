import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { UserAccount } from '../user-account/user-account';
import { UserProfileUpdate } from '../user-profile/user-profile-update';
import { Observable } from 'rxjs';
import { UserLocalStorageService } from '../access/user-local-storage.service';

@Injectable({
  providedIn: 'root'
})
export class UserProfileService {
  private baseUrl = 'http://localhost:8080/api/v1/personal-profiles';

  constructor(private httpClient: HttpClient,
    private userLocalStorage: UserLocalStorageService) { }

  getUserProfile(): Observable<UserAccount> {
    const userId = this.userLocalStorage.getUserId();
    return this.httpClient.get<UserAccount>(`${this.baseUrl}/${userId}`);
  }

  putUserProfile(userProfile: UserProfileUpdate): Observable<UserAccount> {

    return this.httpClient.put<UserAccount>(`${this.baseUrl}`, userProfile);
  }


}
