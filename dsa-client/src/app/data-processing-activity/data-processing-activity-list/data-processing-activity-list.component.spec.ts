import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DataProcessingActivityListComponent } from './data-processing-activity-list.component';

describe('DataProcessingActivityListComponent', () => {
  let component: DataProcessingActivityListComponent;
  let fixture: ComponentFixture<DataProcessingActivityListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DataProcessingActivityListComponent]
    });
    fixture = TestBed.createComponent(DataProcessingActivityListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
