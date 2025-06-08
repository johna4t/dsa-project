import { Component, OnInit } from '@angular/core';
import { UserAccount } from '../user-account';
import { ActivatedRoute } from '@angular/router';
import { UserAccountService } from '../user-account.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-user-account-details',
  templateUrl: './user-account-details.component.html',
  styleUrls: ['./user-account-details.component.css']
})
export class UserAccountDetailsComponent implements OnInit {

  id = 0;
  userAccount: UserAccount = new UserAccount();
  rolesDisplay = '';
  constructor(private route: ActivatedRoute,
    private userAccountService: UserAccountService,
    private router: Router) { }

  ngOnInit(): void {
    this.id = this.route.snapshot.params['id'];

    this.userAccountService.getUserAccountById(this.id).subscribe(
      data => {
        this.userAccount = data;
        this.formatRoles();
      },
      error => {
        console.error('Error fetching user account: ', error);
      }
    )
  }

  private formatRoles(): void {
    if (this.userAccount.roles) {
      this.rolesDisplay = this.userAccount.roles.map(role => role.name).join(', ');
    }
  }

  updateUserAccount(id: number) {
    this.router.navigate(['update-user-account', id]);
  }

}
