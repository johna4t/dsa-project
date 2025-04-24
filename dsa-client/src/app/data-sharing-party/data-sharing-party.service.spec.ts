import { TestBed } from '@angular/core/testing';

import { DataSharingParty } from './data-sharing-party.service';

describe('DataSharingPartyService', () => {
  let service: DataSharingParty;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DataSharingParty);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
