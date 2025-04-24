import { Component, OnInit } from '@angular/core';
import { CustomerAccount } from '../customer-account';
import { CustomerAccountService } from '../customer-account.service';
import { UserAccount } from '../../user-account/user-account';
import { Router } from '@angular/router';
import { Validators, FormControl, FormGroup, FormBuilder, AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
import { matchPassword } from '../../access/match-password.validator';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmationDialogComponent } from '../../dialog/material/confirmation-dialog/confirmation-dialog.component';




@Component({
  selector: 'app-create-customer-account',
  templateUrl: './create-customer-account.component.html',
  styleUrls: ['./create-customer-account.component.css']
})
export class CreateCustomerAccountComponent {

  customerAccount: CustomerAccount = new CustomerAccount();

  static readonly PW_MIN_LENGTH: number = 5;

  customerAccountForm = new FormGroup({
    password: new FormControl('', [Validators.minLength(CreateCustomerAccountComponent.PW_MIN_LENGTH), Validators.required]),
    confirmPassword: new FormControl('', [Validators.required])
  }, { validators: matchPassword }); 

  constructor(
    private customerAccountService: CustomerAccountService,
    private router: Router,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {

    const customer = sessionStorage.getItem(CustomerAccount.name);
    if (customer) {
      this.customerAccount = JSON.parse(customer);
    }

    const adminUser = sessionStorage.getItem(UserAccount.name);
    if (adminUser) {
      this.customerAccount.users[0] = JSON.parse(adminUser);
    }

  }

  onSubmit() {

    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
      width: '250px',
      data: { title: 'Confirm', message: 'Submit request?' }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        // User clicked yes, proceed with the request submission
        this.createCustomerAccount();
      }
    });
  }

  createCustomerAccount() {
    console.log(JSON.stringify(this.customerAccount))
    this.customerAccountService.postCustomerAccount(this.customerAccount).subscribe(data => {
      console.log(data);
      this.next();
    });
  }

  back() {

    this.router.navigate(['/enter-admin-user-details']);

  }

  next() {

    this.router.navigate(['/login']);

  }

  cancel() {

    sessionStorage.removeItem(UserAccount.name);
    sessionStorage.removeItem(CustomerAccount.name);

    this.router.navigate(['/']);

  }

  formInvalid(): boolean {

    return this.customerAccountForm.invalid

  }


}

