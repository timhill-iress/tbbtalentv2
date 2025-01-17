import {Component, Input, OnInit} from '@angular/core';
import {EnumOption, enumOptions} from '../../../../../util/enum';
import {CandidateVisaJobCheck, YesNo} from '../../../../../model/candidate';
import {FormBuilder} from '@angular/forms';
import {CandidateService} from '../../../../../services/candidate.service';
import {IntakeComponentBase} from '../../../../util/intake/IntakeComponentBase';

@Component({
  selector: 'app-regional-area',
  templateUrl: './regional-area.component.html',
  styleUrls: ['./regional-area.component.scss']
})
export class RegionalAreaComponent extends IntakeComponentBase implements OnInit {

  @Input() selectedIndex: number;
  @Input() selectedJobCheck: CandidateVisaJobCheck;
  public regionalAreaOptions: EnumOption[] = enumOptions(YesNo);

  constructor(fb: FormBuilder, candidateService: CandidateService) {
    super(fb, candidateService);
  }

  ngOnInit(): void {
    this.form = this.fb.group({
      visaJobId: [this.selectedJobCheck?.id],
      visaJobRegional: [this.selectedJobCheck?.regional],
    });
  }

}
