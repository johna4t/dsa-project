import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateDataContentDefinitionComponent } from './create-data-content-definition.component';

describe('CreateDataContentDefinitionComponent', () => {
  let component: CreateDataContentDefinitionComponent;
  let fixture: ComponentFixture<CreateDataContentDefinitionComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CreateDataContentDefinitionComponent]
    });
    fixture = TestBed.createComponent(CreateDataContentDefinitionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
