import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { MatDialog } from '@angular/material/dialog';

import { DataProcessingActivity } from '../data-processing-activity';
import { DataProcessingActivityService } from '../data-processing-activity.service';
import { AccessService } from '../../access/access.service';
import { ConfirmationDialogComponent } from '../../dialog/material/confirmation-dialog/confirmation-dialog.component';
import { NavigationService } from '../../access/navigation.service';

@Component({
  selector: 'app-data-processing-activity-list',
  templateUrl: './data-processing-activity-list.component.html',
  styleUrls: ['./data-processing-activity-list.component.css'],
})
export class DataProcessingActivityListComponent implements OnInit {
  displayedColumns: string[] = [
    'name',
    'processorName',
    'dataAssetName',
    'btnView',
    'btnEdit',
    'btnDelete',
  ];

  dataProcessingActivities: DataProcessingActivity[] = [];

  constructor(
    private activityService: DataProcessingActivityService,
    private router: Router,
    private accessService: AccessService,
    private dialog: MatDialog,
    private navigation: NavigationService
  ) {}

  ngOnInit(): void {
    this.getActivities();
  }

  private getActivities(): void {
    this.activityService.getDataProcessingActivityList().subscribe({
      next: (response) => {
        this.dataProcessingActivities = response ?? [];
      },
      error: (error: HttpErrorResponse) => {
        console.log(error);
      },
    });
  }

  viewDataProcessor(id?: number): void {
    if (!id && id !== 0) return;
    this.router.navigate(['view-data-processor', id]);
  }

  viewDataContentDefinition(id?: number): void {
    if (!id && id !== 0) return;
    this.router.navigate(['view-data-content-definition', id]);
  }

  viewDataProcessingActivity(id: number): void {
    this.navigation.navigateWithReturnTo(['view-data-processing-activity', id]);
  }

  updateDataProcessingActivity(id: number): void {
    this.navigation.navigateWithReturnTo(['update-data-processing-activity', id]);
  }

  deleteDataProcessingActivity(id: number): void {
    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
      width: '250px',
      data: { title: 'Confirm delete', message: 'Delete data processing activity?' },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result === true) {
        this.activityService.deleteDataProcessingActivity(id).subscribe({
          next: () => {
            this.dataProcessingActivities = this.dataProcessingActivities.filter(
              (a) => a.id !== id,
            );
          },
          error: (error) => {
            console.error('Error deleting data processing activity:', error);
          },
        });
      }
    });
  }

  createDataProcessingActivity(): void {
    this.navigation.navigateWithReturnTo(['create-data-processing-activity']);
  }
}
