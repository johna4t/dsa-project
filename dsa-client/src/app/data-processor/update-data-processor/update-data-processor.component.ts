import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DataProcessorService } from '../data-processor.service';
import { FormBuilder, FormGroup, Validators, FormArray, FormControl } from '@angular/forms';
import { DataProcessor } from '../data-processor';
import { ProcessingCertificationStandard } from '../../enums/processing-certification-standard.enum';
import { ProcessingCertificationStandardLabels } from '../../enums/processing-certification-standard-labels';
import { ConfirmationDialogComponent } from '../../dialog/material/confirmation-dialog/confirmation-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import { DataProcessingActivityService } from '../../data-processing-activity/data-processing-activity.service';
import { DataProcessingActivity } from '../../data-processing-activity/data-processing-activity';
import { NavigationService } from '../../access/navigation.service';
@Component({
  selector: 'app-update-data-processor',
  templateUrl: './update-data-processor.component.html',
  styleUrls: ['./update-data-processor.component.css'],
})
export class UpdateDataProcessorComponent implements OnInit {
  id = 0;
  dpForm!: FormGroup;
  dp!: DataProcessor;
  originalFormValues: any;

  // Single render source for the table
  dataProcessingActivities: DataProcessingActivity[] = [];

  processingCertificationStandardLabels = ProcessingCertificationStandardLabels;

  // Certification groupings
  infoSecStandards: ProcessingCertificationStandard[] = [
    ProcessingCertificationStandard.ISO_IEC_27001,
    ProcessingCertificationStandard.ISO_IEC_27002,
    ProcessingCertificationStandard.CYBER_ESSENTIALS,
    ProcessingCertificationStandard.NIST_SP,
  ];

  privacyStandards: ProcessingCertificationStandard[] = [
    ProcessingCertificationStandard.ISO_IEC_27701,
    ProcessingCertificationStandard.ISO_IEC_27018,
    ProcessingCertificationStandard.NIST_PRIVACY_FRAMEWORK,
  ];

  bcdrStandards: ProcessingCertificationStandard[] = [
    ProcessingCertificationStandard.ISO_IEC_22301,
    ProcessingCertificationStandard.ISO_IEC_27031,
  ];

  governanceStandards: ProcessingCertificationStandard[] = [
    ProcessingCertificationStandard.ISO_IEC_20000_1,
    ProcessingCertificationStandard.COBIT,
  ];

  constructor(
    private route: ActivatedRoute,
    private dpService: DataProcessorService,
    private navigation: NavigationService,
    private fb: FormBuilder,
    private router: Router,
    private dataProcessingActivityService: DataProcessingActivityService,
    private dialog: MatDialog,
  ) {}

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.params['id']);
    this.dpService.getDataProcessorById(this.id).subscribe((response) => {
      this.dp = response;

      // Use a fresh array copy for change detection
      this.dataProcessingActivities = [...(response.associatedDataProcessing ?? [])];

      this.initForm(response);
      this.originalFormValues = this.dpForm.getRawValue();
    });
  }

  initForm(dp: DataProcessor): void {
    const allCerts = this.getAllCerts();

    this.dpForm = this.fb.group({
      name: [dp.name, Validators.required],
      website: [dp.website, Validators.required],
      description: [dp.description],
      email: [dp.email],
      certifications: this.fb.array(
        allCerts.map((cert) => new FormControl(dp.certifications.includes(cert))),
      ),
    });
  }

  get certificationsArray(): FormArray {
    return this.dpForm.get('certifications') as FormArray;
  }

  getAllCerts(): ProcessingCertificationStandard[] {
    return [
      ...this.infoSecStandards,
      ...this.privacyStandards,
      ...this.bcdrStandards,
      ...this.governanceStandards,
    ];
  }

  onSubmit(): void {
    if (this.shouldDisableSubmit()) return;

    const selectedCerts = this.getAllCerts().filter((_, i) => this.certificationsArray.at(i).value);

    const updated: DataProcessor = {
      ...this.dp,
      ...this.dpForm.value,
      certifications: selectedCerts,
    };

    this.dpService.putDataProcessor(this.id, updated).subscribe(() => {
      this.originalFormValues = this.dpForm.getRawValue();
      this.router.navigate(['/view-data-processor', this.id]);
    });
  }

  shouldDisableSubmit(): boolean {
    return this.dpForm.invalid || this.isFormUnchanged();
  }

  isFormUnchanged(): boolean {
    return JSON.stringify(this.dpForm.getRawValue()) === JSON.stringify(this.originalFormValues);
  }

  getCertIndex(cert: ProcessingCertificationStandard): number {
    return this.getAllCerts().indexOf(cert);
  }

  getCertControl(index: number): FormControl {
    return this.certificationsArray.at(index) as FormControl;
  }

  // For *ngFor trackBy if you need it elsewhere
  trackById = (_: number, item: DataProcessingActivity) => item.id;

  updateDataProcessingActivity(id: number): void {
    this.navigation.navigateWithReturnTo(['update-data-processing-activity', id]);
  }

  deleteDataProcessingActivity(id: number) {
    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
      width: '250px',
      data: {
        title: 'Confirm delete',
        message: 'Delete data processing activity?',
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result === true) {
        this.dataProcessingActivityService.deleteDataProcessingActivity(id).subscribe({
          next: () => {
            // Replace the in-memory array (works with default & OnPush)
            const idNum = Number(id);
            this.dataProcessingActivities = this.dataProcessingActivities.filter(
              (dpa) => Number(dpa.id) !== idNum,
            );

            // Keep the dp.associatedDataProcessing in sync if used elsewhere
            this.dp = {
              ...this.dp,
              associatedDataProcessing: (this.dp.associatedDataProcessing ?? []).filter(
                (dpa) => Number(dpa.id) !== idNum,
              ),
            };
          },
          error: (error) => {
            console.error('Error deleting data processing activity:', error);
          },
        });
      }
    });
  }

  viewDataContentDefinition(id: number) {
    this.navigation.navigateWithReturnTo(['view-data-content-definition', id]);
  }

  viewDataProcessingActivity(id: number) {
    this.navigation.navigateWithReturnTo(['view-data-processing-activity', id]);
  }

createDataProcessingActivity(): void {
  this.navigation.navigateWithReturnTo(['create-data-processing-activity'], {
    queryParams: {
      from: 'data-processor',
      dataProcessorId: this.id,
    },
  });
}

goBack(): void {
  // Was this Update page reached via Create DPA Submit/Cancel?
  const cameFromCreate = (window.history.state as any)?.cameFromCreateDpa === true;

  if (cameFromCreate) {
    // Always go to the View page for this processor (not back to Create)
    this.router.navigate(['/view-data-processor', this.id]);
  } else {
    // Default behaviour: use your recorded history/returnTo, else fallback
    this.navigation.goBackOr(['/data-processors']);
  }
}
}
