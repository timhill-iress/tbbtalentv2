import {Component, Input, OnInit} from '@angular/core';
import {Candidate, CandidateIntakeData} from "../../../../../model/candidate";
import {CandidateService} from "../../../../../services/candidate.service";

@Component({
  selector: 'app-candidate-intake-tab',
  templateUrl: './candidate-intake-tab.component.html',
  styleUrls: ['./candidate-intake-tab.component.scss']
})
export class CandidateIntakeTabComponent implements OnInit {
  @Input() candidate: Candidate;
  candidateIntakeData: CandidateIntakeData;
  error: string;
  loading: boolean;

  constructor(private candidateService: CandidateService) { }

  ngOnInit(): void {
    //Load existing candidateIntakeData
    this.error = null;
    this.loading = true;
    this.candidateService.getIntakeData(this.candidate.id).subscribe(
      intakeData => {
        this.candidateIntakeData = intakeData;
        this.loading = false;
      },
      error => {
        this.error = error;
        this.loading = false;
      });
  }

}