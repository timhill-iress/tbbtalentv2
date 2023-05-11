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

import {Directive, Input, OnInit} from '@angular/core';
import {FormBuilder} from '@angular/forms';
import {AutoSaveComponentBase} from "../autosave/AutoSaveComponentBase";
import {Job} from "../../../model/job";
import {JobOppIntake} from "../../../model/job-opp-intake";
import {JobOppIntakeService} from "../../../services/job-opp-intake.service";

/**
 * Base class for all job intake components.
 * <p/>
 * See IntakeComponentBase doc
 * @author John Cameron
 */
@Directive()
export abstract class JobIntakeComponentBase extends AutoSaveComponentBase implements OnInit {

  /**
   * This is the existing candidate data (if any) which is used to
   * initialize the form data.
   */
  @Input() jobIntakeData: JobOppIntake;

  /**
   * Index into a array member of data if that is what is being updated.
   */
  @Input() myRecordIndex: number;

  @Input() editable: boolean = true;

  /**
   * Inject in a FormBuilder to create the form and an IntakeService to perform the saves.
   * @param fb FormBuilder
   * @param jobOppIntakeService JobOppIntakeService which saves the intake data
   */
  protected constructor(protected fb: FormBuilder, jobOppIntakeService: JobOppIntakeService) {
    super(jobOppIntakeService);
  }

  /**
   * The entity that we are working with in this class is a Job - this returns the entity
   * cast as a Job so that anyone referencing "job" will see the entity with all
   * its Job properties.
   */
  get job(): Job {
    return <Job>this.entity;
  }

  /**
   * Convert any multiselected enums
   */
  preprocessFormValues(formValue: Object): Object {
    return AutoSaveComponentBase.convertEnumOptions(formValue);
  }

  /**
   * Sets a standard no response value to the named form control.
   * @param formControlName Name of control in the intake form.
   */
  setNoResponse(formControlName: string) {
    this.form.controls[formControlName].setValue('NoResponse');
  };
}
