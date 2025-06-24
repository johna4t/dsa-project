import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { DataProcessor } from '../data-processor';
import { DataProcessorService } from '../data-processor.service';
import { ProcessingCertificationStandard } from '../../enums/processing-certification-standard.enum';
import { ProcessingCertificationStandardLabels } from '../../enums/processing-certification-standard-labels';

@Component({
  selector: 'app-update-data-processor',
  templateUrl: './update-data-processor.component.html',
  styleUrls: ['./update-data-processor.component.css'],
})
export class UpdateDataProcessorComponent implements OnInit {
  dpForm!: FormGroup;
  id = 0;
  dp?: DataProcessor;

  processingCertificationStandard = ProcessingCertificationStandard;
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

  originalFormValue: any;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private dpService: DataProcessorService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.id = this.route.snapshot.params['id'];

    this.dpService.getDataProcessorById(this.id).subscribe((response) => {
      this.dp = response;

      this.dpForm = this.fb.group({
        name: [response.name],
        description: [response.description],
        website: [response.website],
        certifications: [response.certifications],
      });

      this.originalFormValue = this.dpForm.getRawValue();
    });
  }

  isCertificationChecked(cert: ProcessingCertificationStandard): boolean {
    return this.dpForm.value.certifications.includes(cert);
  }

  onCertChange(cert: ProcessingCertificationStandard): void {
    const currentCerts = this.dpForm.value.certifications || [];
    const updated = currentCerts.includes(cert)
      ? currentCerts.filter((c: ProcessingCertificationStandard) => c !== cert)
      : [...currentCerts, cert];

    this.dpForm.patchValue({ certifications: updated });
  }

  onSubmit(): void {
    if (this.dpForm.valid) {
      const updated = { ...this.dp, ...this.dpForm.value };
      this.dpService.putDataProcessor(this.id, updated).subscribe(() => {
        this.router.navigate(['/data-processors']);
      });
    }
  }

  shouldDisableSubmit(): boolean {
    return !this.dpForm || !this.dpForm.dirty || JSON.stringify(this.dpForm.getRawValue()) === JSON.stringify(this.originalFormValue);
  }
}
