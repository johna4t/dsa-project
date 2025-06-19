import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DataProcessorDetailsComponent } from './data-processor-details.component';

describe('DataProcessorDetailsComponent', () => {
  let component: DataProcessorDetailsComponent;
  let fixture: ComponentFixture<DataProcessorDetailsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DataProcessorDetailsComponent]
    });
    fixture = TestBed.createComponent(DataProcessorDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
