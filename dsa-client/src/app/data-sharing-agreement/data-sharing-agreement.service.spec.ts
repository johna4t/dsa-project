import { TestBed } from '@angular/core/testing';

import { DataSharingAgreementService } from './data-sharing-agreement.service';

describe('DataSharingAgreementService', () => {
  let service: DataSharingAgreementService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DataSharingAgreementService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
