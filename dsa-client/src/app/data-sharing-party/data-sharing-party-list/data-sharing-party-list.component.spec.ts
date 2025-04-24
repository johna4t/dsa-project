import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DataSharingPartyListComponent } from './data-sharing-party/data-sharing-party-list.component';

describe('DataSharingPartyListComponent', () => {
  let component: DataSharingPartyListComponent;
  let fixture: ComponentFixture<DataSharingPartyListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DataSharingPartyListComponent]
    });
    fixture = TestBed.createComponent(DataSharingPartyListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
