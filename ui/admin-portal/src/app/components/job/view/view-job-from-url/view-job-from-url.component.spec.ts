import { ComponentFixture, TestBed } from "@angular/core/testing";

import { ViewJobFromUrlComponent } from "./view-job-from-url.component";
import { of } from "rxjs";
import { JobService } from "src/app/services/job.service";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { Job } from "../../../../model/job";
import { ActivatedRoute, convertToParamMap } from '@angular/router';


describe("ViewJobFromUrlComponent", () => {
  let component: ViewJobFromUrlComponent;
  let fixture: ComponentFixture<ViewJobFromUrlComponent>;

  beforeEach(async () => {
    const jobService = jasmine.createSpyObj("JobService", ["get"]);
    const getSpy = jobService.get.and.returnValue(of({} as Job));
    await TestBed.configureTestingModule({
      declarations: [ViewJobFromUrlComponent],
      imports: [HttpClientTestingModule],
      providers: [
        { provides: JobService, useValue: jobService },
        {
          provide: ActivatedRoute,
          useValue: {
            paramMap: of(convertToParamMap({id:1}))
          }
        }
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ViewJobFromUrlComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
