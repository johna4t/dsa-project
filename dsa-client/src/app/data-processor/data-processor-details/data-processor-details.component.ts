import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { DataProcessor } from '../data-processor';
import { DataProcessorService } from '../data-processor.service';
import { ProcessingCertificationStandardLabels } from '../../enums/processing-certification-standard-labels';
import { ProcessingCertificationStandard } from '../../enums/processing-certification-standard.enum';
import { NavigationService } from '../../access/navigation.service';

@Component({
  selector: 'app-data-processor-details',
  templateUrl: './data-processor-details.component.html',
  styleUrls: ['./data-processor-details.component.css'],
})
export class DataProcessorDetailsComponent implements OnInit {
  id = 0;
  processor: DataProcessor = new DataProcessor();

  processingCertificationStandardLabels = ProcessingCertificationStandardLabels;
  processingCertificationStandard = ProcessingCertificationStandard;

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
    private route: ActivatedRoute,
    private dpService: DataProcessorService,
    private navigation: NavigationService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.id = this.route.snapshot.params['id'];

    this.dpService.getDataProcessorById(this.id).subscribe({
      next: (response) => {
        this.processor = response;
      },
      error: (error: HttpErrorResponse) => {
        console.error('Failed to fetch data processor:', error);
      },
    });
  }

  getCertificationsLabel(): string {
    if (!this.processor.certifications || this.processor.certifications.length === 0) {
      return 'No Certifications';
    }

    return this.processor.certifications
      .map((cert) => this.processingCertificationStandardLabels[cert] || cert)
      .join('\n');
  }

  certificationKeys(): ProcessingCertificationStandard[] {
    return Object.values(this.processingCertificationStandard) as ProcessingCertificationStandard[];
  }

  updateDataProcessor(id: number): void {
    this.navigation.navigateWithReturnTo(['update-data-processor', id]);
  }

  viewDataContentDefinition(id: number) {
    this.navigation.navigateWithReturnTo(['view-data-content-definition', id]);
  }

  viewDataProcessingActivity(id: number) {
    this.navigation.navigateWithReturnTo(['view-data-processing-activity', id]);
  }

  goBack(): void {
    this.router.navigate(['/data-processors']);
  }
}
