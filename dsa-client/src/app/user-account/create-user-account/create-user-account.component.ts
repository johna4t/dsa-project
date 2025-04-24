import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserAccount } from '../user-account';
import { Role } from '../../role/role';
import { Router } from '@angular/router';
import { UserAccountService } from '../user-account.service';
import { UserLocalStorageService } from '../../access/user-local-storage.service';
import { matchPassword } from '../../access/match-password.validator';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmationDialogComponent } from '../../dialog/material/confirmation-dialog/confirmation-dialog.component';

@Component({
  selector: 'app-create-user-account',
  templateUrl: './create-user-account.component.html',
  styleUrls: ['./create-user-account.component.css']
})
export class CreateUserAccountComponent implements OnInit {

  userAccount: UserAccount = new UserAccount();
  userAccountForm!: FormGroup;

  availableRoles: Role[] = [
    { id: 1, name: 'MEMBER', permissions: [] },
    { id: 2, name: 'ACCOUNT_ADMIN', permissions: [] }
  ];

  constructor(
    private fb: FormBuilder,
    private userAccountService: UserAccountService,
    private userLocalStorage: UserLocalStorageService,
    private dialog: MatDialog,
    private router: Router
  ) {
    this.userAccountForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      contactNumber: ['', [Validators.required, Validators.minLength(5)]],
      jobTitle: [''],
      role: [null, Validators.required],
      password: ['', [Validators.required, Validators.minLength(5)]],
      confirmPassword: ['']
    }, { validators: matchPassword });
  }

  ngOnInit(): void {
    // Assign the admin's parent account ID to the new user
    this.userAccount.parentAccount.id = Number(this.userLocalStorage.getUserParentAccountId()!);
  }

  onSubmit(): void {

    if (this.userAccountForm.invalid) return;

    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
      width: '250px',
      data: { title: 'Confirm new user', message: 'Submit request?' }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        // User clicked yes, proceed with the request submission
        this.createUserAccount();
      }
    });


  }

  formIsInvalid(): boolean {
    return this.userAccountForm.invalid;
  }

  private createUserAccount() {

    const form = this.userAccountForm.value;

    this.userAccount.firstName = form.firstName;
    this.userAccount.lastName = form.lastName;
    this.userAccount.email = form.email;
    this.userAccount.contactNumber = form.contactNumber;
    this.userAccount.jobTitle = form.jobTitle;
    this.userAccount.password = form.password;
    this.userAccount.roles = [form.role];

    this.userAccountService.postUserAccount(this.userAccount).subscribe(() => {
      this.router.navigate(['/user-accounts']);
    });
  }

  get firstName() { return this.userAccountForm.get('firstName'); }
  get lastName() { return this.userAccountForm.get('lastName'); }
  get email() { return this.userAccountForm.get('email'); }
  get contactNumber() { return this.userAccountForm.get('contactNumber'); }
  get password() { return this.userAccountForm.get('password'); }
  get confirmPassword() { return this.userAccountForm.get('confirmPassword'); }
}
