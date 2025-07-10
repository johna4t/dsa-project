import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateDataProcessingActivityComponent } from './update-data-processing-activity.component';

describe('UpdateDataProcessingActivityComponent', () => {
  let component: UpdateDataProcessingActivityComponent;
  let fixture: ComponentFixture<UpdateDataProcessingActivityComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UpdateDataProcessingActivityComponent]
    });
    fixture = TestBed.createComponent(UpdateDataProcessingActivityComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
