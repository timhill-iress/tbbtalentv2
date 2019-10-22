import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Router} from "@angular/router";
import {CandidateService} from "../../../services/candidate.service";
import {CandidateOccupationService} from "../../../services/candidate-occupation.service";
import {CandidateOccupation} from "../../../model/candidate-occupation";
import {Occupation} from "../../../model/occupation";
import {OccupationService} from "../../../services/occupation.service";
import {RegistrationService} from "../../../services/registration.service";

@Component({
  selector: 'app-registration-candidate-occupation',
  templateUrl: './registration-candidate-occupation.component.html',
  styleUrls: ['./registration-candidate-occupation.component.scss']
})
export class RegistrationCandidateOccupationComponent implements OnInit {

  error: any;
  _loading = {
    candidate: true,
    occupations: true
  };
  saving: boolean;
  form: FormGroup;
  candidateOccupations: CandidateOccupation[];
  occupations: Occupation[];

  constructor(private fb: FormBuilder,
              private router: Router,
              private candidateService: CandidateService,
              private occupationService: OccupationService,
              private candidateOccupationService: CandidateOccupationService,
              public registrationService: RegistrationService) { }

  ngOnInit() {
    this.candidateOccupations = [];
    this.saving = false;
    this.setUpForm();

    this.occupationService.listOccupations().subscribe(
      (response) => {
        this.occupations = response;
        this._loading.occupations = false;
      },
      (error) => {
        this.error = error;
        this._loading.occupations = false;
      }
    );

    this.candidateService.getCandidateCandidateOccupations().subscribe(
      (candidate) => {
        this.candidateOccupations = candidate.candidateOccupations.map(occ => {
          return {
            id: occ.id,
            occupationId: occ.occupation.id,
            yearsExperience: occ.yearsExperience
          }
        });
        this._loading.candidate = false;
      },
      (error) => {
        this.error = error;
        this._loading.candidate = false;
      }
    );
  }

  setUpForm(){
    this.form = this.fb.group({
      id: [null],
      occupationId: [null, Validators.required],
      yearsExperience: [null, Validators.required],
    });
  }

  addOccupation() {
    this.candidateOccupations.push(this.form.value);
    this.setUpForm();
  }

  deleteOccupation(index: number) {
    this.candidateOccupations.splice(index, 1);
  }

  save(dir: string) {
    if (this.form.valid) {
      this.addOccupation();
    }
    const request = {
      updates: this.candidateOccupations
    };
    this.candidateOccupationService.updateCandidateOccupations(request).subscribe(
      (response) => {
        if (dir === 'next') {
          this.registrationService.next();
        } else {
          this.registrationService.back();
        }
      },
      (error) => {
        this.error = error;
      });
  }

  back() {
    this.save('back');
  }

  next() {
    this.save('next');
  }

  get loading() {
    return this._loading.candidate || this._loading.occupations;
  }
}
