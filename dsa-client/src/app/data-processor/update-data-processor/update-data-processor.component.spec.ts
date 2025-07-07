import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateDataProcessorComponent } from './update-data-processor.component';

describe('UpdateDataProcessorComponent', () => {
  let component: UpdateDataProcessorComponent;
  let fixture: ComponentFixture<UpdateDataProcessorComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UpdateDataProcessorComponent]
    });
    fixture = TestBed.createComponent(UpdateDataProcessorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
