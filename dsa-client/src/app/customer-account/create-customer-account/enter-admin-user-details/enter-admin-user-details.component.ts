import { Component } from '@angular/core';
import { CustomerAccount } from '../../customer-account';
import { UserAccount } from '../../../user-account/user-account';
import { Router } from '@angular/router';
import { Validators, FormControl, FormGroup } from '@angular/forms';

@Component({
  selector: 'app-enter-admin-user-details',
  templateUrl: './enter-admin-user-details.component.html',
  styleUrls: ['./enter-admin-user-details.component.css']
})
export class EnterAdminUserDetailsComponent {

  adminUserDetails: UserAccount = new UserAccount();

  readonly firstNameMinLength: number = 2;
  firstName = new FormControl('', [Validators.minLength(this.firstNameMinLength), Validators.required]);

  readonly lastNameMinLength: number = 2;
  lastName = new FormControl('', [Validators.minLength(this.lastNameMinLength), Validators.required]);

  readonly emailRegex = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/;
  email = new FormControl('', [Validators.pattern(this.emailRegex), Validators.required]);

  readonly contactNumberMinLength: number = 5;
  contactNumber = new FormControl('', [Validators.minLength(this.contactNumberMinLength), Validators.required]);

  readonly strongPasswordRegex = /^(?=.* [a - z])(?=.* [A - Z])(?=.* [0 - 9])(?=.* [!@#\$ %\^&\*])(?=.{ 8, })/;
  readonly passwordMinLength: number = 3;
  password = new FormControl('', [Validators.minLength(this.passwordMinLength), Validators.required]);

  jobTitle = new FormControl();

  adminUserDetailsForm!: FormGroup;

  constructor(
    private router: Router
  ) { }


  ngOnInit(): void {

    // Get form data from session storage

    const adminUser = sessionStorage.getItem(UserAccount.name);
    if (adminUser) {
      this.adminUserDetails = JSON.parse(adminUser);
    } else {
      this.adminUserDetailsForm.reset();
    }

  }

  onSubmit() {

    // Write form data to session storage

    sessionStorage.setItem(UserAccount.name, JSON.stringify(this.adminUserDetails));

    this.next(); 

  }

  next() {

    this.router.navigate(['/create-customer-account']);
  }


  formIsValid(): boolean {
    return this.firstName.invalid
      || this.lastName.invalid
      || this.email.invalid
      || this.contactNumber.invalid
  }

  public cancel() {

    sessionStorage.removeItem(UserAccount.name);
    sessionStorage.removeItem(CustomerAccount.name);

    this.router.navigate(['/']);

  }

  public back() {

    sessionStorage.setItem(UserAccount.name, JSON.stringify(this.adminUserDetails));

    this.router.navigate(['/enter-customer-details']);
  }
}
