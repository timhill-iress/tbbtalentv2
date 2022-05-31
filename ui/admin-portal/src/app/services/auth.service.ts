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

import {Injectable} from '@angular/core';
import {JwtResponse} from "../model/jwt-response";
import {throwError} from "rxjs";
import {catchError, map} from "rxjs/operators";
import {environment} from "../../environments/environment";
import {Router} from "@angular/router";
import {HttpClient} from "@angular/common/http";
import {LocalStorageService} from "angular-2-local-storage";
import {AdminRole, User} from "../model/user";
import {LoginRequest} from "../model/base";
import {Observable} from "rxjs/index";
import {EncodedQrImage} from "../util/qr";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  apiUrl = environment.apiUrl + '/auth';

  private loggedInUser: User;

  constructor(private router: Router,
              private http: HttpClient,
              private localStorageService: LocalStorageService) {
  }

  login(credentials: LoginRequest) {
    return this.http.post(`${this.apiUrl}/login`, credentials).pipe(
      map((response: JwtResponse) => {
        this.storeCredentials(response);
      }),
      catchError(e => {
          console.log('error', e);
          return throwError(e);
        }
      )
    );
  }

  //Can be used when we switch to user providing AdminRole enum
  assignableUserRoles(): AdminRole[] {
    const userRole: AdminRole = this.currentRole();
    let assignableRoles: AdminRole[] = [];
    switch (userRole) {
      case AdminRole.sourcepartneradmin:
        assignableRoles.push(AdminRole.limited, AdminRole.semilimited);
        break;

      case AdminRole.admin:
        assignableRoles.push(AdminRole.limited, AdminRole.semilimited, AdminRole.sourcepartneradmin);
        break;

      case AdminRole.systemadmin:
        //System admin can assign all roles
        assignableRoles = Object.values(AdminRole);
        break;
    }
    return assignableRoles;
  }

  canAssignPartner(): boolean {
    const loggedInUser = this.getLoggedInUser();
    return loggedInUser == null ? false : loggedInUser.role === 'systemadmin';
  }

  currentRole(): AdminRole {
    const loggedInUser = this.getLoggedInUser();
    return loggedInUser == null ? AdminRole.limited : AdminRole[loggedInUser.role];
  }

  isAuthenticated(): boolean {
    return this.getLoggedInUser() != null;
  }

  isReadOnly(): boolean {
    const loggedInUser = this.getLoggedInUser();
    return loggedInUser == null ? true : loggedInUser.readOnly;
  }

  getLoggedInUser(): User {
    if (!this.loggedInUser){
      this.loggedInUser = this.localStorageService.get('user');
    }

    if (!this.isValidUserInfo(this.loggedInUser)){
      console.log("invalid user");
      this.logout();
      this.router.navigate(['login']);
      return this.loggedInUser = null;
    }else {
      return this.loggedInUser;
    }
  }

  setNewLoggedInUser(new_user) {
    this.localStorageService.set('user', new_user);
  }

  isValidUserInfo(user: User){
    if (user == null) {
      return true;
    }
    if (user.role) {
      return user.readOnly != null;
    } else {
      return false;
    }
  }

  getToken(): string {
    return this.localStorageService.get('access-token');
  }

  logout() {
    this.http.post(`${this.apiUrl}/logout`, null);
    this.localStorageService.remove('user');
    this.localStorageService.remove('access-token');
  }

  mfaSetup(): Observable<EncodedQrImage> {
    return this.http.post<EncodedQrImage>(`${this.apiUrl}/mfa-setup`, null);
  }

  private storeCredentials(response: JwtResponse) {
    this.localStorageService.remove('access-token');
    this.localStorageService.remove('user');
    this.localStorageService.set('access-token', response.accessToken);
    this.localStorageService.set('user', response.user);
    this.loggedInUser = response.user;
  }

}
