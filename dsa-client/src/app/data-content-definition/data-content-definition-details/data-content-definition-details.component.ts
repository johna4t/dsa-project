import { Component, OnInit } from '@angular/core';
import { DataContentDefinition } from '../data-content-definition';
import { ActivatedRoute } from '@angular/router';
import { DataContentDefinitionService } from '../data-content-definition.service';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { LawfulBasisLabels } from '../../enums/lawful-basis-labels';
import { SpecialCategoryDataLabels } from '../../enums/special-category-data-labels';
import { Article9ConditionLabels } from '../../enums/article-9-condition-labels';
import { DataContentTypeLabels } from '../../enums/data-content-type-labels';
import { LawfulBasis } from '../../enums/lawful-basis.enum';
import { SpecialCategoryData } from '../../enums/special-category-data.enum';
import { Article9Condition } from '../../enums/article-9-condition.enum';
import { DataContentType } from '../../enums/data-content-type.enum';

@Component({
  selector: 'app-data-content-definition-details',
  templateUrl: './data-content-definition-details.component.html',
  styleUrls: ['./data-content-definition-details.component.css'],
})
export class DataContentDefinitionDetailsComponent implements OnInit {
  lawfulBasisLabels = LawfulBasisLabels;
  specialCategoryLabels = SpecialCategoryDataLabels;
  article9ConditionLabels = Article9ConditionLabels;
  dataContentTypeLabels = DataContentTypeLabels;

  lawfulBasisEnum = LawfulBasis;
  specialCategoryEnum = SpecialCategoryData;
  article9ConditionEnum = Article9Condition;
  dataContentTypeEnum = DataContentType;

  id = 0;
  dcd: DataContentDefinition = new DataContentDefinition();

  constructor(
    private route: ActivatedRoute,
    private dcdService: DataContentDefinitionService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.id = this.route.snapshot.params['id'];

    this.dcdService.getDataContentDefinitionById(this.id).subscribe({
      next: (response) => {
        this.dcd = response;
      },
      error: (error: HttpErrorResponse) => {
        console.log(error);
      },
    });
  }

  updateDataContentDefinition(id: number) {
    this.router.navigate(['update-data-content-definition', id]);
  }

  getFormattedRetentionPeriod(): string | undefined {
    const iso = this.dcd?.retentionPeriod;
    if (!iso || typeof iso !== 'string') return undefined;

    const match = iso.match(/^P(\d+)([DWMY])$/);
    if (!match) return iso; // fallback for unexpected formats

    const [_, numberStr, unit] = match;
    const number = parseInt(numberStr, 10);

    const unitMap: Record<'D' | 'W' | 'M' | 'Y', string> = {
      D: 'Day',
      W: 'Week',
      M: 'Month',
      Y: 'Year',
    };

    const label = unitMap[unit as keyof typeof unitMap];
    const pluralLabel = number === 1 ? label : `${label}s`;

    return `${number} ${pluralLabel}`;
  }

  getGdprPerspectiveValue(
    key: 'lawfulBasis' | 'specialCategory' | 'article9Condition',
  ): string | undefined {
    const gdpr = this.dcd.perspectives?.find((p) => p.metadataScheme === 'GDPR');
    return gdpr?.metadata?.[key] as string | undefined;
  }

  getLawfulBasisLabel(): string {
    const key = (this.getGdprPerspectiveValue('lawfulBasis') as LawfulBasis) || 'NOT_PERSONAL_DATA';
    return this.lawfulBasisLabels[key];
  }

  getSpecialCategoryLabel(): string {
    const key =
      (this.getGdprPerspectiveValue('specialCategory') as SpecialCategoryData) ||
      'NOT_SPECIAL_CATEGORY_DATA';
    return this.specialCategoryLabels[key];
  }

  getArticle9ConditionLabel(): string {
    const key =
      (this.getGdprPerspectiveValue('article9Condition') as Article9Condition) || 'NOT_APPLICABLE';
    return this.article9ConditionLabels[key];
  }

  getDataContentTypeLabel(): string {
    const key = (this.dcd.dataContentType as DataContentType) || 'NOT_SPECIFIED';
    return this.dataContentTypeLabels[key];
  }
}
