import { Component, OnInit } from '@angular/core';
import {AuthService} from "../../../../services/auth.service";
import {FormBuilder, FormGroup} from "@angular/forms";
import {debounceTime, distinctUntilChanged} from "rxjs/operators";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {SearchResults} from "../../../../model/search-results";
import {Partner} from "../../../../model/partner";
import {PartnerService} from "../../../../services/partner.service";
import {SearchPartnerRequest, SearchTaskRequest, Status} from "../../../../model/base";
import {CreateUpdatePartnerComponent} from "../create-update-partner/create-update-partner.component";

//MODEL - latest best practice on this kind of component

@Component({
  selector: 'app-search-partners',
  templateUrl: './search-partners.component.html',
  styleUrls: ['./search-partners.component.scss']
})
export class SearchPartnersComponent implements OnInit {
  error: any;
  loading: boolean;
  pageNumber: number;
  pageSize: number;
  readOnly: boolean;
  results: SearchResults<Partner>;
  searchForm: FormGroup;

  constructor(
    private partnerService: PartnerService,
    private fb: FormBuilder,
    private modalService: NgbModal,
    private authService: AuthService) { }

  ngOnInit(): void {
    this.searchForm = this.fb.group({
      keyword: [''],
      status: ['active'],
    });
    this.pageNumber = 1;
    this.pageSize = 50;

    const loggedInUser = this.authService.getLoggedInUser();
    this.readOnly = loggedInUser == null ? true : loggedInUser.readOnly;

    //Monitor form changes
    this.searchForm.valueChanges
    .pipe(
      debounceTime(400),
      distinctUntilChanged()
    )
    .subscribe(res => {
      this.search();
    });
    this.search();

  }

  /**
   * Search based on current search form contents
   */
  search() {
    this.error = null;
    this.loading = true;
    const request: SearchPartnerRequest =  {
      keyword: this.searchForm.value.keyword,
      status: this.searchForm.value.status,
      pageNumber: this.pageNumber - 1,
      pageSize: this.pageSize,
      sortFields: ['id'],
      sortDirection: 'ASC',
    };

    this.partnerService.searchPaged(request).subscribe(
      results => {
      this.results = results;
      this.loading = false;
    },
      error => {
        this.error = error;
        this.loading = false;
      });
  }

  addPartner() {
    const addPartnerModal = this.modalService.open(CreateUpdatePartnerComponent, {
      centered: true,
      backdrop: 'static'
    });

    addPartnerModal.result
    .then((result) => this.search())
    .catch(() => {});
  }

  editPartner(partner: Partner) {
    const editPartnerModal = this.modalService.open(CreateUpdatePartnerComponent, {
      centered: true,
      backdrop: 'static'
    });

    editPartnerModal.componentInstance.partner = partner;

    editPartnerModal.result
    .then((result) => this.search())
    .catch(() => {});
  }

}