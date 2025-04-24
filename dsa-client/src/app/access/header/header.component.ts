import { Component, OnInit } from '@angular/core';
import { UserLocalStorageService } from '../user-local-storage.service';
import { Router } from '@angular/router';
import { AccessService } from '../access.service';
import { CustomerAccount } from '../../customer-account/customer-account';
import { UserAccount } from '../../user-account/user-account';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmationDialogComponent } from '../../dialog/material/confirmation-dialog/confirmation-dialog.component';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  constructor(private userLocalStorage: UserLocalStorageService,
    private router: Router,
    private accessService: AccessService,
    private dialog: MatDialog) {

  }

  ngOnInit(): void { }

  public isLoggedIn() {
    return this.userLocalStorage.isLoggedIn();
  }

  public logout() {

    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
      width: '250px',
      data: { title: 'Confirm', message: 'Logout?' }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        // User clicked yes, proceed with the request submission
        this.accessService.logout().subscribe({
          next: () => {
            this.userLocalStorage.clear();
            this.router.navigate(['/']);
          },
          error: (err) => {
            console.error('Logout failed:', err);
            // Clear anyway to ensure user is logged out client-side
            this.userLocalStorage.clear();
            this.router.navigate(['/']);
          }
        });
      }
    });

  }

  public roleMatch(allowedRoles: string[]): boolean {
    return this.accessService.roleMatch(allowedRoles);
  }

  public getAccessService(): AccessService {
    return this.accessService;
  }

  public getUserLocalStorage(): UserLocalStorageService {
    return this.userLocalStorage;
  }

  public signUp() {

    sessionStorage.removeItem(CustomerAccount.name);
    sessionStorage.removeItem(UserAccount.name);

    this.router.navigate(['/enter-customer-details']);
  }

  public viewUserProfile() {

    this.router.navigate(['/view-user-profile']);
  }



}
