import { Component } from '@angular/core';
import { CustomerAccount } from '../customer-account';
import { ActivatedRoute } from '@angular/router';
import { CustomerAccountService } from '../customer-account.service';
import { UserLocalStorageService } from '../../access/user-local-storage.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-customer-account-details',
  templateUrl: './customer-account-details.component.html',
  styleUrls: ['./customer-account-details.component.css']
})
export class CustomerAccountDetailsComponent {
  id: number = 0;
  customerAccount: CustomerAccount = new CustomerAccount();

  constructor(
    private route: ActivatedRoute,
    private customerAccountService: CustomerAccountService,
    private userLocalStorage: UserLocalStorageService,
    private router: Router) { }

  ngOnInit(): void {
    let str: null | string = this.userLocalStorage.getUserParentAccountId();

    if (str) {
      this.id = +str;
      this.customerAccountService.getCustomerAccountById(this.id).subscribe(
        data => {
          this.customerAccount = data;
        }
      )
    } else {
      this.router.navigate(['page-not-found']);
    }

  }

  updateCustomerAccout(id: number) {
    this.router.navigate(['update-customer-account', id]);
  }
}
