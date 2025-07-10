import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DataProcessingActivityDetailsComponent } from './data-processing-activity-details.component';

describe('DataProcessingActivityDetailsComponent', () => {
  let component: DataProcessingActivityDetailsComponent;
  let fixture: ComponentFixture<DataProcessingActivityDetailsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DataProcessingActivityDetailsComponent]
    });
    fixture = TestBed.createComponent(DataProcessingActivityDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
