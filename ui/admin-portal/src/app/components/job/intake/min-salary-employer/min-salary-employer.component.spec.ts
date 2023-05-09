import {ComponentFixture, TestBed} from '@angular/core/testing';

import {MinSalaryEmployerComponent} from './min-salary-employer.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { JobService } from 'src/app/services/job.service';

describe('MinSalaryEmployerComponent', () => {
  let component: MinSalaryEmployerComponent;
  let fixture: ComponentFixture<MinSalaryEmployerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MinSalaryEmployerComponent],
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
    fixture = TestBed.createComponent(MinSalaryEmployerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
