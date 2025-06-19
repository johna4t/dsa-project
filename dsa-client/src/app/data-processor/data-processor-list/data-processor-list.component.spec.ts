import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DataProcessorListComponent } from './data-processor-list.component';

describe('DataProcessorListComponent', () => {
  let component: DataProcessorListComponent;
  let fixture: ComponentFixture<DataProcessorListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DataProcessorListComponent]
    });
    fixture = TestBed.createComponent(DataProcessorListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
