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

import {Component, OnInit} from '@angular/core';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {FormBuilder, FormGroup} from '@angular/forms';
import {SavedList, SearchSavedListRequest} from '../../../model/saved-list';
import {IDropdownSettings} from 'ng-multiselect-dropdown';
import {SavedListService} from '../../../services/saved-list.service';
import {JoblinkValidationEvent} from '../../util/joblink/joblink.component';
import {CandidateStatus, UpdateCandidateStatusInfo} from "../../../model/candidate";


export interface TargetListSelection {
  //List id - 0 if new list requested
  savedListId: number;

  //Name of new list to be created (if any - only used if savedListId = 0)
  newListName?: string;

  //If true any existing contents of target list should be replaced, otherwise
  //contents are added (merged).
  replace: boolean;

  sfJoblink?: string;

  /**
   * If present, the statuses of all candidates in list are set according to this.
   */
  statusUpdateInfo?: UpdateCandidateStatusInfo;
}


@Component({
  selector: 'app-select-list',
  templateUrl: './select-list.component.html',
  styleUrls: ['./select-list.component.scss']
})
export class SelectListComponent implements OnInit {

  error: string = null;
  excludeList: SavedList;
  form: FormGroup;
  jobName: string;
  loading: boolean;
  saving: boolean;
  sfJoblink: string;
  action: string = "Save";
  title: string = "Select List";

  lists: SavedList[] = [];

  dropdownSettings: IDropdownSettings = {
    idField: 'id',
    textField: 'name',
    enableCheckAll: false,
    singleSelection: true,
    allowSearchFilter: true
  };

  constructor(
    private savedListService: SavedListService,
    private activeModal: NgbActiveModal,
    private fb: FormBuilder) { }

  ngOnInit() {
    this.form = this.fb.group({
      newListName: [null],
      newList: [false],
      savedList: [null],
      replace: [false],
      makeActive: [false],
      activeComment: [null],
      activeCandidateMessage: [null],
    });
    this.loadLists();
  }

  get makeActive(): boolean { return this.form.value.makeActive; }
  get activeComment(): string { return this.form.value.activeComment; }
  get activeCandidateMessage(): string { return this.form.value.activeCandidateMessage; }
  get newListNameControl() { return this.form.get('newListName'); }
  get newListName(): string { return this.form.value.newListName; }
  get newList(): boolean { return this.form.value.newList; }
  get replace(): boolean { return this.form.value.replace; }
  get savedList(): SavedList { return this.form.value.savedList; }

  private loadLists() {
    /*load all our non fixed lists */
    this.loading = true;
    const request: SearchSavedListRequest = {
      owned: true,
      shared: true,
      fixed: false
    };

    this.savedListService.search(request).subscribe(
      (results) => {
        this.lists = results.filter(list => list.id !== this.excludeList?.id) ;
        this.loading = false;
      },
      (error) => {
        this.error = error;
        this.loading = false;
      }
    );
  }

  dismiss() {
    this.activeModal.dismiss(false);
  }

  select() {
    const selection: TargetListSelection = {
      savedListId: this.savedList === null ? 0 : this.savedList[0].id,
      newListName: this.newList ? this.newListName : null,
      replace: this.replace,
      sfJoblink: this.sfJoblink ? this.sfJoblink : null
    }
    if (this.makeActive) {
      const info: UpdateCandidateStatusInfo = {
        status: CandidateStatus.active,
        comment: this.activeComment,
        candidateMessage: this.activeCandidateMessage
      }
      selection.statusUpdateInfo = info;
    }
    this.activeModal.close(selection);
  }

  disableNew() {
    this.form.controls['newList'].disable();
  }

  enableNew() {
    this.form.controls['newList'].enable();
    this.form.controls['savedList'].patchValue(null);
  }


  onJoblinkValidation(jobOpportunity: JoblinkValidationEvent) {
    if (jobOpportunity.valid) {
      this.sfJoblink = jobOpportunity.sfJoblink;
      this.jobName = jobOpportunity.jobname;

      //If existing name is empty, auto copy into them
      if (!this.newListNameControl.value) {
        this.newListNameControl.patchValue(this.jobName);
      }
    } else {
      this.sfJoblink = null;
      this.jobName = null;
    }
  }
}
