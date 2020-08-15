import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {UserService} from '../../../services/user.service';
import {ReCaptchaV3Service} from "ng-recaptcha";
import {SendResetPasswordEmailRequest} from "../../../model/candidate";

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss']
})
export class ResetPasswordComponent implements OnInit {

  loading: boolean;
  error: any;
  form: FormGroup;
  updated: boolean;

  constructor(private fb: FormBuilder,
              private reCaptchaV3Service: ReCaptchaV3Service,
              private userService: UserService) {
  }

  ngOnInit() {
    this.loading = false;
    this.error = null;
    this.updated = false;
    this.form = this.fb.group({
      email: ['', Validators.required]
    });
  }

  get email(): string {
    return this.form.value.email;
  }

  resetForm() {
    this.form.patchValue({
      email: '',
    });
    const keys: string[] = Object.keys(this.form.controls);
    for (const key of keys) {
      this.form.controls[key].markAsPristine();
    }
  }

  sendResetEmail() {
    this.updated = false;
    this.error = null;

    const action = 'resetPassword';
    this.reCaptchaV3Service.execute(action).subscribe(
      (token) => this.sendResetWithToken(token),
      (error) => {
        console.log(error);
      }
    );
  }

  private sendResetWithToken(token: string) {
    const req: SendResetPasswordEmailRequest = new SendResetPasswordEmailRequest();
    req.email = this.email;
    req.reCaptchaV3Token = token;

    this.userService.sendResetPassword(req).subscribe(
      () => {
        this.resetForm();
        this.updated = true;
      },
      (error) => {
        this.error = error;
      }
    );

  }
}
