import {ComponentFixture, TestBed} from '@angular/core/testing';

import {JobSalaryComponent} from './job-salary.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms'
import { JobService } from 'src/app/services/job.service';

describe('JobSalaryComponent', () => {
  let component: JobSalaryComponent;
  let fixture: ComponentFixture<JobSalaryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [JobSalaryComponent],
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
    fixture = TestBed.createComponent(JobSalaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
