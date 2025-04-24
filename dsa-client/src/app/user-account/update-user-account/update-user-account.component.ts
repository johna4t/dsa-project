import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserAccount } from '../user-account';
import { UserAccountService } from '../user-account.service';
import { Role } from '../../role/role';
import { UserLocalStorageService } from '../../access/user-local-storage.service';
import { Router, ActivatedRoute } from '@angular/router';
import { matchPassword } from '../../access/match-password.validator';

@Component({
  selector: 'app-update-user-account',
  templateUrl: './update-user-account.component.html',
  styleUrls: ['./update-user-account.component.css']
})
export class UpdateUserAccountComponent implements OnInit {

  userAccount: UserAccount = new UserAccount();
  availableRoles: Role[] = [
    { id: 1, name: 'MEMBER', permissions: [] },
    { id: 2, name: 'ACCOUNT_ADMIN', permissions: [] }
  ];
  roleSelectionIsDisabled: boolean = false;
  id: number = 0;

  userAccountForm!: FormGroup;
  initialValues: any = {}; // For tracking changes

  constructor(
    private fb: FormBuilder,
    private userAccountService: UserAccountService,
    private userLocalStorage: UserLocalStorageService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.userAccountForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      contactNumber: [''],
      jobTitle: [''],
      role: [null, Validators.required],
      password: ['', [Validators.minLength(5)]],
      confirmPassword: ['']
    }, { validators: matchPassword });
  }

  ngOnInit(): void {
    this.id = this.route.snapshot.params['id'];

    this.userAccountService.getUserAccountById(this.id).subscribe(data => {
      this.userAccount = data;

      const currentRole = data.roles?.[0] || null;
      const matchedRole = this.availableRoles.find(role => role.name === currentRole?.name);

      this.initialValues = {
        firstName: data.firstName,
        lastName: data.lastName,
        contactNumber: data.contactNumber,
        jobTitle: data.jobTitle ?? '',
        role: matchedRole,
        password: '',
        confirmPassword: ''
      };

      this.userAccountForm.patchValue(this.initialValues);

      const customerAccountId = data.parentAccount.id;
      this.userAccountService.getUserAccountsCountByRoleAndCustomerAccount('ACCOUNT_ADMIN', customerAccountId)
        .subscribe(count => {
          if (count === 1 && currentRole?.name === 'ACCOUNT_ADMIN') {
            this.roleSelectionIsDisabled = true;
          }
        });

      this.userAccountForm.valueChanges.subscribe(() => {
        this.userAccountForm.updateValueAndValidity();
      });

      this.userAccountForm.get('password')?.valueChanges.subscribe(() => {
        this.userAccountForm.get('confirmPassword')?.updateValueAndValidity();
      });
    });
  }

  formHasChanged(): boolean {
    const current = this.userAccountForm.value;
    const fieldsToCheck = ['firstName', 'lastName', 'contactNumber', 'jobTitle', 'password', 'confirmPassword', 'role'];

    return fieldsToCheck.some(key =>
      (current[key]?.id || current[key]) !== (this.initialValues[key]?.id || this.initialValues[key])
    );
  }

  formIsValid(): boolean {
    const password = this.userAccountForm.get('password')?.value?.trim() || '';
    const confirmPassword = this.userAccountForm.get('confirmPassword')?.value?.trim() || '';
    const updatingPassword = password !== '';

    const passwordsMatch = password === confirmPassword;
    const passwordLengthValid = !updatingPassword || password.length >= 5;

    return this.userAccountForm.invalid ||
      !this.formHasChanged() ||
      !passwordsMatch ||
      !passwordLengthValid;
  }

  onSubmit(): void {
    if (this.userAccountForm.invalid) return;

    const formValues = this.userAccountForm.value;

    this.userAccount.firstName = formValues.firstName;
    this.userAccount.lastName = formValues.lastName;
    this.userAccount.contactNumber = formValues.contactNumber;
    this.userAccount.jobTitle = formValues.jobTitle;
    this.userAccount.roles = [formValues.role];

    if (formValues.password && formValues.password.trim() !== '') {
      this.userAccount.password = formValues.password;
    }

    this.userAccountService.putUserAccount(this.id, this.userAccount).subscribe(() => {
      if (this.userLocalStorage.getUserId() === this.userAccount.id?.toString()) {
        this.userLocalStorage.setUserRoles(this.userAccount.roles);
        this.userLocalStorage.setUserFirstName(this.userAccount.firstName);
        this.userLocalStorage.setUserLastName(this.userAccount.lastName);
      }
      this.next();
    }, error => console.log(error));
  }

  next(): void {
    if (this.userLocalStorage.getUserRoles()?.includes('ACCOUNT_ADMIN')) {
      this.router.navigate(['/view-user-account', this.id]);
    } else {
      this.router.navigate(['/data-sharing-agreements']);
    }
  }
}
