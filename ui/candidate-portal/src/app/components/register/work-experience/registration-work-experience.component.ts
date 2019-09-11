import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Router} from "@angular/router";
import {countries} from "../../../model/countries";

@Component({
  selector: 'app-registration-work-experience',
  templateUrl: './registration-work-experience.component.html',
  styleUrls: ['./registration-work-experience.component.scss']
})
export class RegistrationWorkExperienceComponent implements OnInit {


  form: FormGroup;
  countries: string[];
  experiences: any[];
  startDate: Date;

  constructor(private fb: FormBuilder,
              private router: Router) { }


  ngOnInit() {
    this.countries = countries;
    this.experiences = []
    this.setUpForm();
  }

 setUpForm(){
    this.form = this.fb.group({
      companyName: ['', Validators.required],
      country: ['', Validators.required],
      role: ['', Validators.required],
      startDate: ['', Validators.required],
      endDate: ['', Validators.required],
      isFullTime: [false, Validators.required],
      isPaid: [false, Validators.required],
      roleDescription: ['', Validators.required]
    })
  }

  addMore() {
    this.experiences.push(this.form.value);
    this.setUpForm();
    console.log(this.experiences);
    // TODO add the set of form values to the experience array
    // TODO patch the form values back to sensible defaults
  }

  save() {
    // TODO save
    console.log(this.experiences);
    this.router.navigate(['register', 'education']);
  }

}
