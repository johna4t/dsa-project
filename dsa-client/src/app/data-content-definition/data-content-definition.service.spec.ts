import { TestBed } from '@angular/core/testing';

import { DataContentDefinitionService } from './data-content-definition.service';

describe('DataContentDefinitionService', () => {
  let service: DataContentDefinitionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DataContentDefinitionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
