import { TestBed } from '@angular/core/testing';

import { DataProcessingActivityService } from './data-processing-activity.service';

describe('DataProcessingActivityService', () => {
  let service: DataProcessingActivityService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DataProcessingActivityService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
