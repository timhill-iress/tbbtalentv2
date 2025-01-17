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
import {CandidateVisaJobCheck, VisaEligibility} from "../../../../../model/candidate";
import {EnumOption, enumOptions} from "../../../../../util/enum";
import {FormBuilder} from "@angular/forms";
import {CandidateService} from "../../../../../services/candidate.service";
import {IntakeComponentBase} from "../../../../util/intake/IntakeComponentBase";

@Component({
  selector: 'app-visa-job-put-forward',
  templateUrl: './visa-job-put-forward.component.html',
  styleUrls: ['./visa-job-put-forward.component.scss']
})
export class VisaJobPutForwardComponent extends IntakeComponentBase implements OnInit {

  @Input() selectedJobCheck: CandidateVisaJobCheck;
  //Drop down values for enumeration
  visaEligibilityOptions: EnumOption[] = enumOptions(VisaEligibility);

  constructor(fb: FormBuilder, candidateService: CandidateService) {
    super(fb, candidateService);
  }

  ngOnInit(): void {
    this.form = this.fb.group({
      visaJobId: [this.selectedJobCheck?.id],
      visaJobPutForward: [this.selectedJobCheck?.putForward],
    });
  }

}
