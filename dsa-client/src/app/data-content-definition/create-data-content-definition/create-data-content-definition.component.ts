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

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private dcdService: DataContentDefinitionService,
    private userLocalStorageService: UserLocalStorageService
  ) {}

  ngOnInit(): void {
    this.dcdForm = this.fb.group({
      name: ['', Validators.required],
      description: [''],
      sourceSystem: ['', Validators.required],
      ownerEmail: ['', [Validators.required, Validators.email]],
      retentionValue: [1, [Validators.required, Validators.min(1)]],
      retentionUnit: ['D', Validators.required],
      dataContentType: [DataContentType.NOT_SPECIFIED],
      lawfulBasis: [LawfulBasis.NOT_PERSONAL_DATA],
      specialCategory: [SpecialCategoryData.NOT_SPECIAL_CATEGORY_DATA],
      article9Condition: [Article9Condition.NOT_APPLICABLE],
    });
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
      ]
    };

    this.dcdService.postDataContentDefinition(newDcd).subscribe(() => {
      this.router.navigate(['/data-content-definitions']);
    });
  }
}
