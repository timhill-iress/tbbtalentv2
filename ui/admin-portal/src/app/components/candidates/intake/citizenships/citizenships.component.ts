import {Component, Input, OnInit} from '@angular/core';
import {Candidate, CandidateIntakeData} from "../../../../model/candidate";
import {Nationality} from "../../../../model/nationality";
import {CandidateCitizenshipService} from "../../../../services/candidate-citizenship.service";

@Component({
  selector: 'app-citizenships',
  templateUrl: './citizenships.component.html',
  styleUrls: ['./citizenships.component.scss']
})
export class CitizenshipsComponent implements OnInit {
  @Input() candidate: Candidate;
  @Input() candidateIntakeData: CandidateIntakeData;
  error: boolean;
  @Input() nationalities: Nationality[];
  saving: boolean;

  constructor(
    private candidateCitizenshipService: CandidateCitizenshipService
  ) {}

  ngOnInit(): void {
  }

  addRecord() {
    this.saving = true;
    this.candidateCitizenshipService.create(this.candidate.id, {}).subscribe(
      (citizenship) => {
        this.candidateIntakeData.candidateCitizenships.push(citizenship)
        this.saving = false;
      },
      (error) => {
        this.error = error;
        this.saving = false;
      });
  }

  deleteRecord(i: number) {
    this.candidateIntakeData.candidateCitizenships.splice(i, 1);
  }
}
