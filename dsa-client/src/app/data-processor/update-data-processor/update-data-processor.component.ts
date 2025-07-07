import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DataProcessorService } from '../data-processor.service';
import { FormBuilder, FormGroup, Validators, FormArray, FormControl } from '@angular/forms';
import { DataProcessor } from '../data-processor';
import { ProcessingCertificationStandard } from '../../enums/processing-certification-standard.enum';
import { ProcessingCertificationStandardLabels } from '../../enums/processing-certification-standard-labels';

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
    private fb: FormBuilder,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.id = this.route.snapshot.params['id'];
    this.dpService.getDataProcessorById(this.id).subscribe((response) => {
      this.dp = response;
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
}
