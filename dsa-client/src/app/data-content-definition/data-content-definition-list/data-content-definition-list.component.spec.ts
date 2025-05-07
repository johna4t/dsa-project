import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DataContentDefinitionListComponent } from './data-content-definition-list.component';

describe('DataContentDefinitionListComponent', () => {
  let component: DataContentDefinitionListComponent;
  let fixture: ComponentFixture<DataContentDefinitionListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DataContentDefinitionListComponent]
    });
    fixture = TestBed.createComponent(DataContentDefinitionListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
