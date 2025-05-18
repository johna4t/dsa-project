import { Component, OnInit } from '@angular/core';
import { DataContentDefinition } from '../data-content-definition';
import { DataContentDefinitionService } from '../data-content-definition.service';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AccessService } from '../../access/access.service';
import { ConfirmationDialogComponent } from '../../dialog/material/confirmation-dialog/confirmation-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-data-content-definition-list',
  templateUrl: './data-content-definition-list.component.html',
  styleUrls: ['./data-content-definition-list.component.css']
})
export class DataContentDefinitionListComponent implements OnInit {

  displayedColumns: string[] = ['name', 'sourceSystem', 'dataContentType', 'btnView', 'btnEdit', 'btnDelete'];

   dataContentDefinitions: DataContentDefinition[] = [];

  constructor(
    private dataContentDefinitionService: DataContentDefinitionService,
    private router: Router,
    private accessService: AccessService,
    private dialog: MatDialog,) { };

    ngOnInit(): void {
      this.getDataContentDefinitions();
    };

    private getDataContentDefinitions() {
      this.dataContentDefinitionService.getDataContentDefinitionList().subscribe({
        next: (response) => {
          this.dataContentDefinitions = response;
        },
        error: (error: HttpErrorResponse) => {
          console.log(error);
        }
      });
    }

    updateDataContentDefinition(id: number) {
      this.router.navigate(['update-data-content-definition', id]);
    }

    deleteDataContentDefinition(id: number) {
      const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
        width: '250px',
        data: { title: 'Confirm delete', message: 'Delete data asset?' },
      });

      dialogRef.afterClosed().subscribe((result) => {
        if (result === true) {
          this.dataContentDefinitionService.deleteDataContentDefinition(id).subscribe({
            next: () => {
              // ✅ Update the in-memory list
              this.dataContentDefinitions = this.dataContentDefinitions.filter(
                (dcd) => dcd.id !== id,
              );
            },
            error: (error) => {
              console.error('Error deleting data content definition:', error);
            },
          });
        }
      });
    }

    viewDataContentDefinition(id: number) {
      this.router.navigate(['view-data-content-definition', id]);
    }

    public getAccessService(): AccessService {
      return this.accessService;
    }


}
