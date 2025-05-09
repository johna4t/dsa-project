import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { DataContentDefinitionService } from '../data-content-definition.service';
import { DataContentDefinition } from '../data-content-definition';
import { UserLocalStorageService } from '../../access/user-local-storage.service';

@Component({
  selector: 'app-create-data-content-definition',
  templateUrl: './create-data-content-definition.component.html',
})
export class CreateDataContentDefinitionComponent implements OnInit {
  dcdForm!: FormGroup;
  contentTypes = [
    'NOT_SPECIFIED',
    'STRUCTURED_ELECTR0NIC_DATA',
    'ELECTRONIC_DOCUMENT',
    'PAPER_DOCUMENT',
    'OTHER_MEDIUM',
  ];

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
      dataContentType: ['NOT_SPECIFIED'],
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
    };

    this.dcdService.postDataContentDefinition(newDcd).subscribe(() => {
      this.router.navigate(['/data-content-definitions']);
    });
  }
}
