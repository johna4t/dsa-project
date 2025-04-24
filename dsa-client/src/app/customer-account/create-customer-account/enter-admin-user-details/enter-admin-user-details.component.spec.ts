import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EnterAdminUserDetailsComponent } from './enter-admin-user-details.component';

describe('EnterAdminUserDetailsComponent', () => {
  let component: EnterAdminUserDetailsComponent;
  let fixture: ComponentFixture<EnterAdminUserDetailsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EnterAdminUserDetailsComponent]
    });
    fixture = TestBed.createComponent(EnterAdminUserDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
