import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DataSharingAgreementListComponent } from './data-sharing-agreement-list.component';

describe('DataSharingAgreementListComponent', () => {
  let component: DataSharingAgreementListComponent;
  let fixture: ComponentFixture<DataSharingAgreementListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DataSharingAgreementListComponent]
    });
    fixture = TestBed.createComponent(DataSharingAgreementListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
