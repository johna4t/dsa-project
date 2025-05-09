import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { DataContentDefinitionService } from '../data-content-definition.service';
import { DataContentDefinition } from '../data-content-definition';

@Component({
  selector: 'app-update-data-content-definition',
  templateUrl: './update-data-content-definition.component.html',
})
export class UpdateDataContentDefinitionComponent implements OnInit {
  dcdForm!: FormGroup;
  dcdId!: number;
  dcd!: DataContentDefinition;
  contentTypes = [
    'NOT_SPECIFIED',
    'STRUCTURED_ELECTR0NIC_DATA',
    'ELECTRONIC_DOCUMENT',
    'PAPER_DOCUMENT',
    'OTHER_MEDIUM',
  ];
  private originalFormValues!: Partial<DataContentDefinition>;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private dcdService: DataContentDefinitionService
  ) {}

  ngOnInit(): void {
    this.dcdId = Number(this.route.snapshot.paramMap.get('id'));
    this.dcdService.getDataContentDefinitionById(this.dcdId).subscribe((dcd) => {
      this.dcd = dcd;

      const match = dcd.retentionPeriod?.match(/^P(\d+)([DWMY])$/);
      const value = match ? Number(match[1]) : null;
      const unit = match ? match[2] : 'D';

      this.dcdForm = this.fb.group({
        name: [dcd.name, Validators.required],
        description: [dcd.description],
        sourceSystem: [dcd.sourceSystem, Validators.required],
        ownerEmail: [dcd.ownerEmail, [Validators.required, Validators.email]],
        ownerName: [dcd.ownerName || ''],
        retentionValue: [value, [Validators.required, Validators.min(1)]],
        retentionUnit: [unit, Validators.required],
        dataContentType: [dcd.dataContentType],
      });

      // Store the original values for change detection
      this.originalFormValues = this.dcdForm.value;
    });
  }

  isFormUnchanged(): boolean {
    return JSON.stringify(this.dcdForm.value) === JSON.stringify(this.originalFormValues);
  }

  onSubmit(): void {
    if (this.dcdForm.invalid || this.isFormUnchanged()) return;

    const formValues = this.dcdForm.value;
    const retentionPeriod = `P${formValues.retentionValue}${formValues.retentionUnit}`;

    const updated: DataContentDefinition = {
      ...formValues,
      id: this.dcdId,
      retentionPeriod,
      ownerName: formValues.ownerName,
    };

    this.dcdService.putDataContentDefinition(this.dcdId, updated).subscribe(() => {
      this.router.navigate(['/view-data-content-definition', this.dcdId]);
    });
  }
}
