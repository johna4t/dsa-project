import { Component, OnInit } from '@angular/core';
import { UserAccount } from '../user-account';
import { UserAccountService } from '../user-account.service';
import { UserLocalStorageService } from '../../access/user-local-storage.service';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AccessService } from '../../access/access.service';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmationDialogComponent } from '../../dialog/material/confirmation-dialog/confirmation-dialog.component';

@Component({
  selector: 'app-user-account-list',
  templateUrl: './user-account-list.component.html',
  styleUrls: ['./user-account-list.component.css']
})
export class UserAccountListComponent implements OnInit{

  displayedColumns: string[] = ['firstName', 'lastName', 'email', 'role', 'btnView', 'btnEdit', 'btnDelete'];

  userAccounts: UserAccount[] = [];

  constructor(
    private userAccountService: UserAccountService,
    private userLocalStorage: UserLocalStorageService,
    private dialog: MatDialog,
    private router: Router,
    private accessService: AccessService) { };

  ngOnInit(): void {
    this.getUserAccounts();
  };

  private getUserAccounts() {
    this.userAccountService.getUserAccountList().subscribe(
      (response) => {
        this.userAccounts = response;
      },
      (error: HttpErrorResponse) => {
        console.log(error);
      }
    )
  }

  updateUserAccount(id: number) {
    this.router.navigate(['update-user-account', id]);
  }

  deleteUserAccount(id: number) {

    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
      width: '250px',
      data: { title: 'Confirm delete', message: 'Delete user?' }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        // User clicked yes, proceed with delete user
        this.userAccountService.deleteUserAccount(id).subscribe(data => {
          console.log(data);
          this.getUserAccounts();
        })
      }
    });

  }

  showDeleteButton(userId: number): boolean {
    const currentUserId = this.userLocalStorage.getUserId();
    return userId.toString() !== currentUserId; // 👈 Hide button for self
  }


  viewUserAccount(id: number) {
    this.router.navigate(['view-user-account', id]);
  }

  public getAccessService(): AccessService {
    return this.accessService;
  }
}
