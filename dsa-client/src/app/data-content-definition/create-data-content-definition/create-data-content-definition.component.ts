// ✅ Updated: Use DataContentType enum and labels
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { DataContentDefinitionService } from '../data-content-definition.service';
import { DataContentDefinition } from '../data-content-definition';
import { UserLocalStorageService } from '../../access/user-local-storage.service';
import { LawfulBasis } from '../../enums/lawful-basis.enum';
import { LawfulBasisLabels } from '../../enums/lawful-basis-labels';
import { SpecialCategoryData } from '../../enums/special-category-data.enum';
import { SpecialCategoryDataLabels } from '../../enums/special-category-data-labels';
import { Article9Condition } from '../../enums/article-9-condition.enum';
import { Article9ConditionLabels } from '../../enums/article-9-condition-labels';
import { DataContentType } from '../../enums/data-content-type.enum';
import { DataContentTypeLabels } from '../../enums/data-content-type-labels';

@Component({
  selector: 'app-create-data-content-definition',
  templateUrl: './create-data-content-definition.component.html',
})
export class CreateDataContentDefinitionComponent implements OnInit {
  dcdForm!: FormGroup;

  dataContentTypeKeys = Object.keys(DataContentTypeLabels) as DataContentType[];
  dataContentTypeLabels = DataContentTypeLabels;

  lawfulBasisKeys = Object.keys(LawfulBasisLabels) as LawfulBasis[];
  lawfulBasisLabels = LawfulBasisLabels;

  specialCategoryKeys = Object.keys(SpecialCategoryDataLabels) as SpecialCategoryData[];
  specialCategoryLabels = SpecialCategoryDataLabels;

  article9ConditionKeys = Object.keys(Article9ConditionLabels) as Article9Condition[];
  article9ConditionLabels = Article9ConditionLabels;

  shouldDisableSubmit = false;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private dcdService: DataContentDefinitionService,
    private userLocalStorageService: UserLocalStorageService,
  ) {}

  ngOnInit(): void {
    this.dcdForm = this.fb.group({
      name: ['', Validators.required],
      description: [''],
      sourceSystem: ['', Validators.required],
      ownerEmail: ['', [Validators.required, Validators.email]],
      ownerName: [''],
      retentionValue: [1, [Validators.required, Validators.min(1)]],
      retentionUnit: ['D', Validators.required],
      dataContentType: [DataContentType.NOT_SPECIFIED],
      lawfulBasis: [LawfulBasis.NOT_PERSONAL_DATA],
      specialCategory: [SpecialCategoryData.NOT_SPECIAL_CATEGORY_DATA],
      article9Condition: [Article9Condition.NOT_APPLICABLE],
    });

    // First GDPR Perspective validation rule: lawful basis = NOT_PERSONAL_DATA
    this.disableGdprFieldsIfNotPersonalData(this.dcdForm.get('lawfulBasis')?.value);
    this.dcdForm.get('lawfulBasis')?.valueChanges.subscribe((value: LawfulBasis) => {
      this.disableGdprFieldsIfNotPersonalData(value);
    });

    // Second GDPR Perspective validation rule: special category = NOT_SPECIAL_CATEGORY_DATA
    this.updateArticle9Readonly(this.dcdForm.get('specialCategory')?.value);
    this.dcdForm.get('specialCategory')?.valueChanges.subscribe((value: SpecialCategoryData) => {
      this.updateArticle9Readonly(value);
    });

    // Third GDPR Perspective validation rule: disable submit if article9Condition = NOT_APPLICABLE with special category ≠ NOT_SPECIAL_CATEGORY_DATA
    this.dcdForm.get('specialCategory')?.valueChanges.subscribe(() => {
      this.checkSubmitDisallowedCondition();
    });
    this.dcdForm.get('article9Condition')?.valueChanges.subscribe(() => {
      this.checkSubmitDisallowedCondition();
    });
    this.checkSubmitDisallowedCondition(); // also run initially
  }

  onSubmit(): void {
    if (this.dcdForm.invalid) return;

    const formValues = this.dcdForm.value;
    const retentionPeriod = `P${formValues.retentionValue}${formValues.retentionUnit}`;
    const customerAccountId = this.userLocalStorageService.getUserParentAccountId();
    if (customerAccountId === null) {
      console.error('Customer account ID not found in local storage');
      return;
    }

    const newDcd: DataContentDefinition = {
      ...formValues,
      retentionPeriod,
      provider: { id: customerAccountId, name: '' },
      perspectives: [
        {
          metadataScheme: 'GDPR',
          metadata: {
            lawfulBasis: formValues.lawfulBasis,
            specialCategory: formValues.specialCategory,
            article9Condition: formValues.article9Condition,
          },
        },
      ],
    };

    this.dcdService.postDataContentDefinition(newDcd).subscribe(() => {
      this.router.navigate(['/data-content-definitions']);
    });
  }

  private disableGdprFieldsIfNotPersonalData(value: LawfulBasis): void {
    if (value === LawfulBasis.NOT_PERSONAL_DATA) {
      this.dcdForm.patchValue({
        specialCategory: SpecialCategoryData.NOT_SPECIAL_CATEGORY_DATA,
        article9Condition: Article9Condition.NOT_APPLICABLE,
      });
      this.dcdForm.get('specialCategory')?.disable();
      this.dcdForm.get('article9Condition')?.disable();
    } else {
      this.dcdForm.get('specialCategory')?.enable();
      this.dcdForm.get('article9Condition')?.enable();
    }
  }

  private updateArticle9Readonly(specialCategory: SpecialCategoryData): void {
    const article9Control = this.dcdForm.get('article9Condition');

    if (specialCategory === SpecialCategoryData.NOT_SPECIAL_CATEGORY_DATA) {
      article9Control?.patchValue(Article9Condition.NOT_APPLICABLE);
      article9Control?.disable();
    } else {
      article9Control?.enable();
    }
  }

private checkSubmitDisallowedCondition(): void {
  const specialCategory = this.dcdForm.get('specialCategory')?.value;
  const article9 = this.dcdForm.get('article9Condition');

  const requiresArticle9 =
    specialCategory !== SpecialCategoryData.NOT_SPECIAL_CATEGORY_DATA &&
    article9?.value === Article9Condition.NOT_APPLICABLE;

  this.shouldDisableSubmit = requiresArticle9;

  // Set a custom validation error on the control
  if (requiresArticle9) {
    article9?.setErrors({ requiredForSpecialCategory: true });
  } else {
    article9?.setErrors(null);
    article9?.updateValueAndValidity();
  }
}

  shouldHighlightArticle9(): boolean {
  const specialCategory = this.dcdForm.get('specialCategory')?.value;
  const article9 = this.dcdForm.get('article9Condition')?.value;

  return (
    specialCategory !== SpecialCategoryData.NOT_SPECIAL_CATEGORY_DATA &&
    article9 === Article9Condition.NOT_APPLICABLE
  );
}
}
