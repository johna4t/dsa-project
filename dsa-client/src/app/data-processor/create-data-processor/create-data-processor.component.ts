import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormArray, FormControl } from '@angular/forms';
import { Router } from '@angular/router';
import { DataProcessorService } from '../data-processor.service';
import { ProcessingCertificationStandard } from '../../enums/processing-certification-standard.enum';
import { ProcessingCertificationStandardLabels } from '../../enums/processing-certification-standard-labels';
import { DataProcessor } from '../data-processor';
import { UserLocalStorageService } from '../../access/user-local-storage.service';

@Component({
  selector: 'app-create-data-processor',
  templateUrl: './create-data-processor.component.html',
  styleUrls: ['./create-data-processor.component.css'],
})
export class CreateDataProcessorComponent implements OnInit {
  dpForm!: FormGroup;
  dp: DataProcessor = new DataProcessor();
  processingCertificationStandardLabels = ProcessingCertificationStandardLabels;

  infoSecStandards = [
    ProcessingCertificationStandard.ISO_IEC_27001,
    ProcessingCertificationStandard.ISO_IEC_27002,
    ProcessingCertificationStandard.CYBER_ESSENTIALS,
    ProcessingCertificationStandard.NIST_SP,
  ];

  privacyStandards = [
    ProcessingCertificationStandard.ISO_IEC_27701,
    ProcessingCertificationStandard.ISO_IEC_27018,
    ProcessingCertificationStandard.NIST_PRIVACY_FRAMEWORK,
  ];

  bcdrStandards = [
    ProcessingCertificationStandard.ISO_IEC_22301,
    ProcessingCertificationStandard.ISO_IEC_27031,
  ];

  governanceStandards = [
    ProcessingCertificationStandard.ISO_IEC_20000_1,
    ProcessingCertificationStandard.COBIT,
  ];

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private dpService: DataProcessorService,
    private userLocalStorageService: UserLocalStorageService
  ) {}

  ngOnInit(): void {
    this.dpForm = this.fb.group({
      name: ['', Validators.required],
      website: ['', Validators.required],
      description: [''],
      email: [''],
      certifications: this.fb.array(this.getAllCerts().map(() => new FormControl(false))),
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

  getCertControl(index: number): FormControl {
    return this.certificationsArray.at(index) as FormControl;
  }

  onSubmit(): void {
    if (this.dpForm.invalid) return;

    const selectedCerts = this.getAllCerts().filter(
      (_, i) => this.certificationsArray.at(i).value
    );

    const customerAccountId = this.userLocalStorageService.getUserParentAccountId();
    if (customerAccountId === null) {
      console.error('Customer account ID not found');
      return;
    }

    const newDp: DataProcessor = {
      ...this.dpForm.value,
      certifications: selectedCerts,
      controller: { id: customerAccountId, name: '' }
    };

    this.dpService.postDataProcessor(newDp).subscribe(() => {
      this.router.navigate(['/data-processors']);
    });
  }

  shouldDisableSubmit(): boolean {
    return this.dpForm.invalid || !this.dpForm.dirty;
  }
}
