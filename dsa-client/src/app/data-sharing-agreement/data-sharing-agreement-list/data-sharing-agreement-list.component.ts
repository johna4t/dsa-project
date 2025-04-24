import { Component } from '@angular/core';
import { DataSharingAgreement } from '../data-sharing-agreement';
import { DataSharingAgreementService } from '../data-sharing-agreement.service';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-data-sharing-agreement-list',
  templateUrl: './data-sharing-agreement-list.component.html',
  styleUrls: ['./data-sharing-agreement-list.component.css']
})
export class DataSharingAgreementListComponent {

  dataSharingAgreements: DataSharingAgreement[] = [];

  displayedColumns: string[] = ['name', 'controllerRelationship'];

  constructor(
    private dataSharingAgreementService: DataSharingAgreementService,
    private router: Router) { };

  ngOnInit(): void {
    this.getDataSharingAgreements();
  };

  private getDataSharingAgreements() {
    this.dataSharingAgreementService.getDataSharingAgreementList().subscribe(
      (response) => {
        this.dataSharingAgreements = response;
      },
      (error: HttpErrorResponse) => {
        console.log(error);
      }
    )
  }

}
