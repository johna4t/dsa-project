import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateDataSharingPartyComponent } from './data-sharing-party/create-data-sharing-party.component';

describe('CreateDataSharingPartyComponent', () => {
  let component: CreateDataSharingPartyComponent;
  let fixture: ComponentFixture<CreateDataSharingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CreateDataSharingPartyComponent]
    });
    fixture = TestBed.createComponent(CreateDataSharingPartyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
