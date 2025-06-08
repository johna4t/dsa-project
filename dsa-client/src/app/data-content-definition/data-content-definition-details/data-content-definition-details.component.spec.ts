import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DataContentDefinitionDetailsComponent } from './data-content-definition-details.component';

describe('DataContentDefinitionDetailsComponent', () => {
  let component: DataContentDefinitionDetailsComponent;
  let fixture: ComponentFixture<DataContentDefinitionDetailsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DataContentDefinitionDetailsComponent]
    });
    fixture = TestBed.createComponent(DataContentDefinitionDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
