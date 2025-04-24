import { Component } from '@angular/core';
import { CustomerAccount } from '../../customer-account';
import { UserAccount } from '../../../user-account/user-account';
import { Router } from '@angular/router';
import { Validators, FormControl, FormGroup } from '@angular/forms';

@Component({
  selector: 'app-enter-customer-details',
  templateUrl: './enter-customer-details.component.html',
  styleUrls: ['./enter-customer-details.component.css']
})
export class EnterCustomerDetailsComponent {

  customerDetails: CustomerAccount = new CustomerAccount();

  readonly namesMinLength: number = 3;
  name = new FormControl('', [Validators.minLength(this.namesMinLength), Validators.required]);

  departmentName = new FormControl('', [Validators.minLength(this.namesMinLength), Validators.required]);

  readonly urlRegex = /(^|\s)((https?:\/\/)?[\w-]+(\.[\w-]+)+\.?(:\d+)?(\/\S*)?)/gi;
  url = new FormControl('', [Validators.pattern(this.urlRegex), Validators.required]);

  branchName = new FormControl();

  addressLine1 = new FormControl('', [Validators.required]);

  postalCode = new FormControl('', [Validators.required]);

  customerDetailsForm!: FormGroup;



  constructor(
    private router: Router
  ) { }


  ngOnInit(): void {

    // Get form data from session storage

    const customer = sessionStorage.getItem(CustomerAccount.name);
    if (customer) {
      this.customerDetails = JSON.parse(customer);
    } else {
      this.customerDetailsForm.reset();
    }

  }

  onSubmit() {

    // Write form data to session storage

    sessionStorage.setItem(CustomerAccount.name, JSON.stringify(this.customerDetails));

    this.next();

  }

  next() {

    this.router.navigate(['/enter-admin-user-details']);
  }

  formInvalid(): boolean {
    return this.name.invalid
      || this.departmentName.invalid
      || this.addressLine1.invalid
      || this.postalCode.invalid
      ||this.url.invalid
  }

  public cancel() {

    sessionStorage.removeItem(CustomerAccount.name);
    sessionStorage.removeItem(UserAccount.name);

    this.router.navigate(['/']);
  }


}
