import {Component, Input, OnInit} from '@angular/core';
import {IntakeComponentBase} from '../../../../util/intake/IntakeComponentBase';
import {Occupation} from '../../../../../model/occupation';
import {CandidateVisa, CandidateVisaJob} from '../../../../../model/candidate';
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
  @Input() visaRecord: CandidateVisa;

  constructor(fb: FormBuilder, candidateService: CandidateService) {
    super(fb, candidateService);
  }

  ngOnInit(): void {
    this.form = this.fb.group({
      jobOccupation: [this.myRecord?.jobOccupation],
    });
    console.log(this.myRecord);
  }

  private get myRecord(): CandidateVisaJob {
    return this.visaRecord ?
      this.visaRecord.jobChecks[this.selectedIndex]
      : null;
  }

}