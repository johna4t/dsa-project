import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateDataProcessingActivityComponent } from './create-data-processing-activity.component';

describe('CreateDataProcessingActivityComponent', () => {
  let component: CreateDataProcessingActivityComponent;
  let fixture: ComponentFixture<CreateDataProcessingActivityComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CreateDataProcessingActivityComponent]
    });
    fixture = TestBed.createComponent(CreateDataProcessingActivityComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
