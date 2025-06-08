import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateDataContentDefinitionComponent } from './update-data-content-definition.component';

describe('UpdateDataContentDefinitionComponent', () => {
  let component: UpdateDataContentDefinitionComponent;
  let fixture: ComponentFixture<UpdateDataContentDefinitionComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UpdateDataContentDefinitionComponent]
    });
    fixture = TestBed.createComponent(UpdateDataContentDefinitionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
