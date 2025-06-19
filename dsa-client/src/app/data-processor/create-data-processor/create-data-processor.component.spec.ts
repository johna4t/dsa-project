import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateDataProcessorComponent } from './create-data-processor.component';

describe('CreateDataProcessorComponent', () => {
  let component: CreateDataProcessorComponent;
  let fixture: ComponentFixture<CreateDataProcessorComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CreateDataProcessorComponent]
    });
    fixture = TestBed.createComponent(CreateDataProcessorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
