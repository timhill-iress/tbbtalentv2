import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {SavedSearchService} from "../../../../services/saved-search.service";
import {SavedSearch} from "../../../../model/saved-search";

@Component({
  selector: 'app-save-search',
  templateUrl: './save-search.component.html',
  styleUrls: ['./save-search.component.scss']
})

export class SaveSearchComponent implements OnInit {

  form: FormGroup;
  error;
  public replacing: boolean;
  saving: boolean;
  savedSearchId;
  savedSearch;
  searchCandidateRequest;
  update;

  constructor(private activeModal: NgbActiveModal,
              private fb: FormBuilder,
              private savedSearchService: SavedSearchService) {
  }

  ngOnInit() {
    this.replacing = this.savedSearchId ? true : false;
    this.form = this.fb.group({
      name: [null, Validators.required],
      type: [null, Validators.required],
      update: [!this.replacing, Validators.required],
      searchCandidateRequest: [this.searchCandidateRequest]
    });
    if (this.savedSearchId) {
      this.savedSearchService.get(this.savedSearchId).subscribe(
        (savedSearch) => {
          this.savedSearch = savedSearch;
        },
        (error) => {
          this.error = error;
          this.saving = false;
        });
    }

  }


  save() {
    this.saving = true;
    const request = this.form.value;

    // todo Note that this is all a bit complicated - maybe unnecessary.
    // this.form.value gets a copy of the search form value in searchCandidateRequest
    // console.log('request', request);
    if (this.savedSearchId) {
      // console.log('updating', request);

      request.name = this.savedSearch.name;

      this.savedSearchService.update(this.savedSearchId, request).subscribe(
        (savedSearch) => {
          this.closeModal(savedSearch);
          this.saving = false;
        },
        (error) => {
          this.error = error;
          this.saving = false;
        });
    } else {
      this.closeModal(null);
      this.saving = false;
    }
  }


  closeModal(savedSearch: SavedSearch) {
    this.activeModal.close(savedSearch);
  }

  dismiss() {
    this.activeModal.dismiss(false);
  }
}
