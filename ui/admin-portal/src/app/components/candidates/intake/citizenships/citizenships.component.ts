/*
 * Copyright (c) 2021 Talent Beyond Boundaries.
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

import {Component, Input, OnInit} from '@angular/core';
import {Candidate, CandidateIntakeData} from "../../../../model/candidate";
import {Nationality} from "../../../../model/nationality";
import {CandidateCitizenshipService} from "../../../../services/candidate-citizenship.service";
import {Subject} from "rxjs/index";

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
  open: boolean;
  saving: boolean;
  activeIds: string;

  @Input() toggleAll: Subject<any>;

  constructor(
    private candidateCitizenshipService: CandidateCitizenshipService
  ) {}

  ngOnInit(): void {
    this.activeIds = 'intake-citizenships';
    this.open = true;
    // called when the toggleAll method is called in the parent component
    this.toggleAll.subscribe(isOpen => {
      this.open = isOpen;
      this.setActiveIds();
    })
  }

  toggleOpen() {
    this.open = !this.open
    this.setActiveIds();
  }

  setActiveIds(){
    if (this.open) {
      this.activeIds = 'intake-citizenships';
    } else {
      this.activeIds = '';
    }
  }

  addRecord() {
    this.saving = true;
    this.open = true;
    this.setActiveIds();
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
