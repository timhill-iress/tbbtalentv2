import {ComponentFixture, TestBed} from '@angular/core/testing';

import {MinSalaryComponent} from './min-salary.component';
import { JobService } from 'src/app/services/job.service';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

describe('MinSalaryComponent', () => {
  let component: MinSalaryComponent;
  let fixture: ComponentFixture<MinSalaryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MinSalaryComponent],
      imports: [
        FormsModule,
        ReactiveFormsModule
      ],
      providers: [
        { provide: JobService, useValue: null }
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MinSalaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
