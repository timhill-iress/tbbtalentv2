import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ViewJobDescriptionComponent} from './view-job-description.component';
import {SafePipe} from "../../../../../pipes/safe.pipe";

describe('ViewJobDescriptionComponent', () => {
  let component: ViewJobDescriptionComponent;
  let fixture: ComponentFixture<ViewJobDescriptionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ViewJobDescriptionComponent, SafePipe ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ViewJobDescriptionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
