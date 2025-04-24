import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateUserAccountComponent } from './update-user-account.component';

describe('UpdateUserAccountComponent', () => {
  let component: UpdateUserAccountComponent;
  let fixture: ComponentFixture<UpdateUserAccountComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UpdateUserAccountComponent]
    });
    fixture = TestBed.createComponent(UpdateUserAccountComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
