import {Component, Input, OnInit} from '@angular/core';
import {IntakeComponentBase} from '../../../../util/intake/IntakeComponentBase';
import {Occupation} from '../../../../../model/occupation';
import {CandidateVisaJobCheck} from '../../../../../model/candidate';
import {FormBuilder} from '@angular/forms';
import {CandidateService} from '../../../../../services/candidate.service';

@Component({
  selector: 'app-job-occupation',
  templateUrl: './job-occupation.component.html',
  styleUrls: ['./job-occupation.component.scss']
})
export class JobOccupationComponent extends IntakeComponentBase implements OnInit {

  @Input() occupations: Occupation[];
  @Input() selectedIndex: number;
  @Input() selectedJobCheck: CandidateVisaJobCheck;

  constructor(fb: FormBuilder, candidateService: CandidateService) {
    super(fb, candidateService);
  }

  ngOnInit(): void {
    console.log(this.selectedJobCheck?.occupation?.id)
    this.form = this.fb.group({
      visaJobId: [this.selectedJobCheck?.id],
      visaJobOccupationId: [this.selectedJobCheck?.occupation?.id],
    });

    this.form.controls['visaJobOccupationId']?.valueChanges.subscribe(
      change => {
        //Update my existingRecord
        if (this.selectedJobCheck.occupation) {
          this.selectedJobCheck.occupation.id = +change;
        }
      }
    );
  }

  get occupationId(): number {
    return this.selectedJobCheck?.occupation?.id;
  }

}