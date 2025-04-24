import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomerAccountListComponent } from './customer-account-list.component';

describe('CustomerAccountListComponent', () => {
  let component: CustomerAccountListComponent;
  let fixture: ComponentFixture<CustomerAccountListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CustomerAccountListComponent]
    });
    fixture = TestBed.createComponent(CustomerAccountListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
