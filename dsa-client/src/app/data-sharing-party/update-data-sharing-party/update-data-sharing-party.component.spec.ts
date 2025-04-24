import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateDataSharingPartyComponent } from './data-sharing-party/update-data-sharing-party.component';

describe('UpdateDataSharingPartyComponent', () => {
  let component: UpdateDataSharingPartyComponent;
  let fixture: ComponentFixture<UpdateDataSharingPartyComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UpdateDataSharingPartyComponent]
    });
    fixture = TestBed.createComponent(UpdateDataSharingPartyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
