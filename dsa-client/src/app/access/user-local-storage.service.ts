import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class UserLocalStorageService {

  public readonly ROLES_KEY: string = 'roles';
  public readonly ACCESS_TOKEN_KEY: string = 'access-token';
  public readonly REFRESH_TOKEN_KEY: string = 'refresh-token';
  public readonly FIRST_NAME_KEY: string = 'first-name';
  public readonly LAST_NAME_KEY: string = 'last-name';
  public readonly PARENT_ACCOUNT_ID_KEY: string = 'parent-account';
  public readonly ID_KEY: string = 'id';

  constructor() { }

  public setUserRoles(roles: any[]) {

    //Get role names from object array
    const roleNames = roles.map(role => {
      return role.name;
    });

    localStorage.setItem(this.ROLES_KEY, JSON.stringify(roleNames));
  }

  public getUserRoles(): string | null {
    return localStorage.getItem(this.ROLES_KEY);
  }

  public setAccessToken(token: string) {
    localStorage.setItem(this.ACCESS_TOKEN_KEY, token);
  }

  public setRefreshToken(token: string) {
    localStorage.setItem(this.REFRESH_TOKEN_KEY, token);
  }

  public getAccessToken(): string | null {
    return localStorage.getItem(this.ACCESS_TOKEN_KEY);
  }

  public getRefreshToken(): string | null {
    return localStorage.getItem(this.REFRESH_TOKEN_KEY);
  }

  public getUserFirstName(): string | null {
    return localStorage.getItem(this.FIRST_NAME_KEY);
  }

  public setUserFirstName(firstName: string) {
    localStorage.setItem(this.FIRST_NAME_KEY, firstName);
  }

  public getUserLastName(): string | null {
    return localStorage.getItem(this.LAST_NAME_KEY);
  }

  public setUserLastName(lastName: string) {
    localStorage.setItem(this.LAST_NAME_KEY, lastName);
  }

  public getUserParentAccountId(): string | null {
    return localStorage.getItem(this.PARENT_ACCOUNT_ID_KEY);
  }

  public setUserParentAccountId(accountId: string) {
    localStorage.setItem(this.PARENT_ACCOUNT_ID_KEY, accountId);
  }

  public getUserId(): string | null {
    return localStorage.getItem(this.ID_KEY);
  }

  public setUserId(id: string) {
    localStorage.setItem(this.ID_KEY, id);
  }

  public clear(): void {
    localStorage.clear();
  }

  public isLoggedIn(): boolean {
    let isLoggedIn  = false;
    if (null != this.getUserRoles() && null != this.getAccessToken()) {
      isLoggedIn = true;
    }
    return isLoggedIn;
  }

}
