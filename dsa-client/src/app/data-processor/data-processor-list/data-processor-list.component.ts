import { Component, OnInit } from '@angular/core';
import { DataProcessor } from '../data-processor';
import { DataProcessorService } from '../data-processor.service';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AccessService } from '../../access/access.service';
import { ConfirmationDialogComponent } from '../../dialog/material/confirmation-dialog/confirmation-dialog.component';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-data-processor-list',
  templateUrl: './data-processor-list.component.html',
  styleUrls: ['./data-processor-list.component.css'],
})
export class DataProcessorListComponent implements OnInit {
  displayedColumns: string[] = ['name', 'website', 'btnView', 'btnEdit', 'btnDelete'];

  dataProcessors: DataProcessor[] = [];

  constructor(
    private dataProcessorService: DataProcessorService,
    private router: Router,
    private accessService: AccessService,
    private dialog: MatDialog,
  ) {}

  ngOnInit(): void {
    this.getDataProcessors();
  }

  private getDataProcessors() {
    this.dataProcessorService.getDataProcessorList().subscribe({
      next: (response) => {
        this.dataProcessors = response;
      },
      error: (error: HttpErrorResponse) => {
        console.log(error);
      },
    });
  }

  updateDataProcessor(id: number) {
     this.router.navigate(['update-data-processor', id]);
  }

  deleteDataProcessor(id: number) {
    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
      width: '250px',
      data: { title: 'Confirm delete', message: 'Delete data processor?' },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result === true) {
        this.dataProcessorService.deleteDataProcessor(id).subscribe({
          next: () => {
            // ✅ Update the in-memory list
            this.dataProcessors = this.dataProcessors.filter(
              (dp) => dp.id !== id,
            );
          },
          error: (error) => {
            console.error('Error deleting data processor:', error);
          },
        });
      }
    });
  }

  viewDataProcessor(id: number) {
    this.router.navigate(['view-data-processor', id]);
  }

  createDataProcessor() {
    this.router.navigate(['create-data-processor']);
  }
}
