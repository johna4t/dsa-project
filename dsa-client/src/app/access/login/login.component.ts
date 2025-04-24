import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { AccessService } from '../access.service';
import { UserLocalStorageService } from '../user-local-storage.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  private SUPER_ADMIN_ROLE: string = 'SUPER_ADMIN';
  private ACCOUNT_ADMIN_ROLE: string = 'ACCOUNT_ADMIN';
  private MEMBER_ROLE: string = 'MEMBER';


  constructor(private accessService: AccessService,
    private userLocalStorage: UserLocalStorageService,
    private router: Router
  ) { }

  ngOnInit(): void {
  }

  login(loginForm: NgForm) {


    this.accessService.login(loginForm.value).subscribe(
      (response: any) => {
        this.userLocalStorage.setAccessToken(response.accessToken);
        this.userLocalStorage.setRefreshToken(response.refreshToken);
        this.userLocalStorage.setUserId(response.user.id);
        this.userLocalStorage.setUserRoles(response.user.roles);
        this.userLocalStorage.setUserFirstName(response.user.firstName);
        this.userLocalStorage.setUserLastName(response.user.lastName);
        this.userLocalStorage.setUserParentAccountId(response.user.parentAccount.id);

        this.next();

      });
  }

  next() {

    if (this.accessService.roleMatch([this.SUPER_ADMIN_ROLE])) {
      this.router.navigate(['/customer-accounts']);
    } else {
      this.router.navigate(['/data-sharing-agreements']);
    }






    
  }

}
