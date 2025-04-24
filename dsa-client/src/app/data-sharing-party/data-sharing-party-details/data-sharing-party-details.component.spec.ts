import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DataSharingPartyDetailsComponent } from './data-sharing-party/data-sharing-party-details.component';

describe('DataSharingPartyDetailsComponent', () => {
  let component: DataSharingPartyDetailsComponent;
  let fixture: ComponentFixture<DataSharingPartyDetailsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DataSharingPartyDetailsComponent]
    });
    fixture = TestBed.createComponent(DataSharingPartyDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
