import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserProfileService } from '../user-profile.service';
import { UserLocalStorageService } from '../../access/user-local-storage.service';
import { Router } from '@angular/router';
import { matchPassword } from '../../access/match-password.validator';
import { UserProfileUpdate } from '../user-profile-update';

@Component({
  selector: 'app-update-user-profile',
  templateUrl: './update-user-profile.component.html',
  styleUrls: ['./update-user-profile.component.css']
})
export class UpdateUserProfileComponent implements OnInit {

  userProfile: UserProfileUpdate = new UserProfileUpdate();
  userProfileForm!: FormGroup;
  private initialValues: any = {}; // ✅ Store original values for comparison

  constructor(
    private fb: FormBuilder,
    private userProfileService: UserProfileService,
    private userLocalStorage: UserLocalStorageService,
    private router: Router
  ) {
    // ✅ Form with cross-field validation
    this.userProfileForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      contactNumber: [''],
      jobTitle: [''],
      password: ['', [Validators.minLength(5)]], // ✅ Ensure length validation is applied
      confirmPassword: [''],
      originalPassword: ['']
    }, { validators: matchPassword });
  }

  ngOnInit(): void {
    this.userProfileService.getUserProfile().subscribe(data => {

     // this.userProfile = new UserProfileUpdate();
      // Store returned UserAccount details
      this.userProfile.user = data;
      this.userProfile.oldPassword = '';

      // ✅ Store initial form values for tracking changes (excluding `originalPassword`)
      this.initialValues = {
        firstName: data.firstName,
        lastName: data.lastName,
        contactNumber: data.contactNumber,
        jobTitle: data.jobTitle ?? '',
        password: '',
        confirmPassword: ''
      };

      this.userProfileForm.patchValue({
        ...this.initialValues,
        originalPassword: ''
      });

      // ✅ Watch for any form field changes
      this.userProfileForm.valueChanges.subscribe(() => {
        this.userProfileForm.updateValueAndValidity();
      });

      // ✅ Watch for password changes to trigger re-validation
      this.userProfileForm.get('password')?.valueChanges.subscribe(() => {
        this.userProfileForm.get('confirmPassword')?.updateValueAndValidity();
      });

      // ✅ Watch for confirm password changes to check if original password is required
      this.userProfileForm.get('confirmPassword')?.valueChanges.subscribe(() => {
        this.userProfileForm.get('originalPassword')?.updateValueAndValidity();
      });
    }, error => console.log(error));
  }

  // ✅ Function to check if form values are different from initial values
  formHasChanged(): boolean {
    const currentValues = this.userProfileForm.value;

    // ✅ Ignore `originalPassword` in change detection
    const fieldsToCheck = ['firstName', 'lastName', 'contactNumber', 'jobTitle', 'password', 'confirmPassword'];

    return fieldsToCheck.some(key =>
      (currentValues[key] ?? '') !== (this.initialValues[key] ?? '')
    );
  }

  // ✅ Disable submit unless the form has changed AND passwords are valid
  formIsValid(): boolean {
    const newPassword = this.userProfileForm.get('password')?.value.trim();
    const confirmPassword = this.userProfileForm.get('confirmPassword')?.value.trim();
    const passwordsMatch = newPassword === confirmPassword;
    const passwordLengthValid = newPassword === '' || newPassword.length >= 5;
    const originalPasswordEntered = this.userProfileForm.get('originalPassword')?.value.trim() !== '';

    // ✅ Require `originalPassword` if user is updating password
    const updatingPassword = newPassword !== '' && passwordsMatch;

    return this.userProfileForm.invalid ||
      !this.formHasChanged() ||
      !passwordsMatch ||
      !passwordLengthValid ||
      (updatingPassword && !originalPasswordEntered);
  }

  onSubmit() {

    const formValues = this.userProfileForm.value;

    this.userProfile.user.firstName = formValues.firstName;
    this.userProfile.user.lastName = formValues.lastName;
    this.userProfile.user.contactNumber = formValues.contactNumber;
    this.userProfile.user.jobTitle = formValues.jobTitle;


    // Only set password if user entered one
    if (formValues.password && formValues.password.trim() !== '') {
      this.userProfile.user.password = formValues.password;
      this.userProfile.oldPassword = formValues.originalPassword;
    } else {
      this.userProfile.user.password = '';
    }

    this.userProfileService.putUserProfile(this.userProfile).subscribe(() => {

      // Update local storage
      this.userLocalStorage.setUserFirstName(this.userProfile.user.firstName);
      this.userLocalStorage.setUserLastName(this.userProfile.user.lastName);

      this.next();
    }, error => console.log(error));

  }

  next() {
    this.router.navigate(['/view-user-profile']);
  }
}
