import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';

import { DataContentDefinitionService } from '../data-content-definition.service';
import { DataContentDefinition } from '../data-content-definition';

import { DataProcessingActivity } from '../../data-processing-activity/data-processing-activity';
import { DataProcessingActivityService } from '../../data-processing-activity/data-processing-activity.service';
import { ConfirmationDialogComponent } from '../../dialog/material/confirmation-dialog/confirmation-dialog.component';
import { NavigationService } from '../../access/navigation.service';

import { DataContentTypeLabels } from '../../enums/data-content-type-labels';
import { LawfulBasisLabels } from '../../enums/lawful-basis-labels';
import { SpecialCategoryDataLabels } from '../../enums/special-category-data-labels';
import { Article9ConditionLabels } from '../../enums/article-9-condition-labels';

@Component({
  selector: 'app-update-data-content-definition',
  templateUrl: './update-data-content-definition.component.html',
})
export class UpdateDataContentDefinitionComponent implements OnInit {
  dcdForm!: FormGroup;
  dcdId!: number;
  dcd!: DataContentDefinition;

  // Single render source for the DPA table (mirrors UpdateDataProcessor)
  dataProcessingActivities: DataProcessingActivity[] = [];

  dataContentTypeLabels = DataContentTypeLabels;
  dataContentTypeKeys = Object.keys(
    DataContentTypeLabels,
  ) as (keyof typeof DataContentTypeLabels)[];

  lawfulBasisLabels = LawfulBasisLabels;
  lawfulBasisKeys = Object.keys(LawfulBasisLabels) as (keyof typeof LawfulBasisLabels)[];

  specialCategoryDataLabels = SpecialCategoryDataLabels;
  specialCategoryDatasKeys = Object.keys(
    SpecialCategoryDataLabels,
  ) as (keyof typeof SpecialCategoryDataLabels)[];

  article9ConditionLabels = Article9ConditionLabels;
  article9ConditionKeys = Object.keys(
    Article9ConditionLabels,
  ) as (keyof typeof Article9ConditionLabels)[];

  private originalFormValues!: Partial<DataContentDefinition>;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private dcdService: DataContentDefinitionService,
    private dataProcessingActivityService: DataProcessingActivityService,
    private dialog: MatDialog,
    private navigation: NavigationService,
  ) {}

  ngOnInit(): void {
    this.dcdId = Number(this.route.snapshot.paramMap.get('id'));
    this.dcdService.getDataContentDefinitionById(this.dcdId).subscribe((dcd) => {
      this.dcd = dcd;

      // Pull associated DPAs into a standalone array (same pattern as UpdateDataProcessor)
      this.dataProcessingActivities = [...(dcd.associatedDataProcessing ?? [])];

      const match = dcd.retentionPeriod?.match(/^P(\d+)([DWMY])$/);
      const value = match ? Number(match[1]) : null;
      const unit = match ? match[2] : 'D';

      const gdpr = dcd.perspectives?.find((p) => p.metadataScheme === 'GDPR');
      const lawfulBasis = gdpr?.metadata?.['lawfulBasis'] || 'NOT_PERSONAL_DATA';
      const specialCategory = gdpr?.metadata?.['specialCategory'] || 'NOT_SPECIAL_CATEGORY_DATA';
      const article9Condition = gdpr?.metadata?.['article9Condition'] || 'NOT_APPLICABLE';

      this.dcdForm = this.fb.group({
        name: [dcd.name, Validators.required],
        description: [dcd.description],
        sourceSystem: [dcd.sourceSystem, Validators.required],
        ownerEmail: [dcd.ownerEmail, [Validators.required, Validators.email]],
        ownerName: [dcd.ownerName || ''],
        retentionValue: [value, [Validators.required, Validators.min(1)]],
        retentionUnit: [unit, Validators.required],
        dataContentType: [dcd.dataContentType],
        lawfulBasis: [lawfulBasis],
        specialCategory: [specialCategory],
        article9Condition: [article9Condition],
      });

      // Disable fields if initial value is NOT_PERSONAL_DATA
      if (lawfulBasis === 'NOT_PERSONAL_DATA') {
        this.dcdForm.get('specialCategory')?.disable();
        this.dcdForm.get('article9Condition')?.disable();
      }

      // React to changes in lawfulBasis
      this.dcdForm.get('lawfulBasis')?.valueChanges.subscribe((value) => {
        if (value === 'NOT_PERSONAL_DATA') {
          this.dcdForm.patchValue({
            specialCategory: 'NOT_SPECIAL_CATEGORY_DATA',
            article9Condition: 'NOT_APPLICABLE',
          });
          this.dcdForm.get('specialCategory')?.disable();
          this.dcdForm.get('article9Condition')?.disable();
        } else {
          this.dcdForm.get('specialCategory')?.enable();
          const specialCat = this.dcdForm.get('specialCategory')?.value;
          if (specialCat === 'NOT_SPECIAL_CATEGORY_DATA') {
            this.dcdForm.get('article9Condition')?.disable();
          } else {
            this.dcdForm.get('article9Condition')?.enable();
          }
        }
      });

      // Handle changes in specialCategory
      this.dcdForm.get('specialCategory')?.valueChanges.subscribe((value) => {
        if (value === 'NOT_SPECIAL_CATEGORY_DATA') {
          this.dcdForm.patchValue({ article9Condition: 'NOT_APPLICABLE' });
          this.dcdForm.get('article9Condition')?.disable();
        } else if (this.dcdForm.get('lawfulBasis')?.value !== 'NOT_PERSONAL_DATA') {
          this.dcdForm.get('article9Condition')?.enable();
        }
      });

      this.originalFormValues = this.dcdForm.value;
    });
  }

  // ===== Form behaviour (unchanged) =====

  isFormUnchanged(): boolean {
    return JSON.stringify(this.dcdForm.value) === JSON.stringify(this.originalFormValues);
  }

  shouldDisableSubmit(): boolean {
    if (this.dcdForm.invalid || this.isFormUnchanged()) {
      return true;
    }
    const specialCategory = this.dcdForm.get('specialCategory')?.value;
    const article9Condition = this.dcdForm.get('article9Condition')?.value;
    return specialCategory !== 'NOT_SPECIAL_CATEGORY_DATA' && article9Condition === 'NOT_APPLICABLE';
  }

  onSubmit(): void {
    if (this.dcdForm.invalid || this.isFormUnchanged()) return;

    const formValues = this.dcdForm.getRawValue();
    const retentionPeriod = `P${formValues.retentionValue}${formValues.retentionUnit}`;

    // Enforce GDPR metadata rules
    const lawfulBasis = formValues.lawfulBasis;
    let specialCategory = formValues.specialCategory;
    let article9Condition = formValues.article9Condition;

    if (lawfulBasis === 'NOT_PERSONAL_DATA') {
      specialCategory = 'NOT_SPECIAL_CATEGORY_DATA';
      article9Condition = 'NOT_APPLICABLE';
    } else if (specialCategory === 'NOT_SPECIAL_CATEGORY_DATA') {
      article9Condition = 'NOT_APPLICABLE';
    }

    const updated: DataContentDefinition = {
      ...formValues,
      id: this.dcd.id,
      provider: this.dcd.provider,
      retentionPeriod,
      isReferenced: this.dcd.isReferenced,
      perspectives: [
        {
          id: this.dcd.perspectives?.[0]?.id,
          dataContentDefinition: { id: this.dcd.id } as DataContentDefinition,
          metadataScheme: 'GDPR',
          metadata: {
            lawfulBasis,
            specialCategory,
            article9Condition,
          },
        },
      ],
    };

    this.dcdService.putDataContentDefinition(this.dcdId, updated).subscribe(() => {
      this.router.navigate(['/view-data-content-definition', this.dcdId]);
    });
  }

  // ===== DPA actions (mirrors UpdateDataProcessor) =====

  trackById = (_: number, item: DataProcessingActivity) => item.id;

  viewDataProcessingActivity(id: number): void {
    this.navigation.navigateWithReturnTo(['view-data-processing-activity', id]);
  }

  updateDataProcessingActivity(id: number): void {
    this.navigation.navigateWithReturnTo(['update-data-processing-activity', id]);
  }

createDataProcessingActivity(): void {
  this.navigation.navigateWithReturnTo(['create-data-processing-activity'], {
    queryParams: {
      from: 'data-content-definition',
      dataContentDefinitionId: this.dcdId,
    },
  });
}

  deleteDataProcessingActivity(id: number): void {
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
            const idNum = Number(id);
            // Update the local render array
            this.dataProcessingActivities = this.dataProcessingActivities.filter(
              (dpa) => Number(dpa.id) !== idNum,
            );

            // Keep the dcd.associatedDataProcessing in sync if referenced elsewhere
            this.dcd = {
              ...this.dcd,
              associatedDataProcessing: (this.dcd.associatedDataProcessing ?? []).filter(
                (dpa: DataProcessingActivity) => Number(dpa.id) !== idNum,
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

  viewDataProcessor(id: number): void {
    this.navigation.navigateWithReturnTo(['view-data-processor', id]);
  }

  goBack(): void {
    // If we arrived here after creating a DPA (Submit/Cancel), prefer the View page
    const cameFromCreate = (window.history.state as any)?.cameFromCreateDpa === true;
    if (cameFromCreate) {
      this.router.navigate(['/view-data-content-definition', this.dcdId]);
    } else {
      this.navigation.goBackOr(['/data-content-definitions']);
    }
  }
}
