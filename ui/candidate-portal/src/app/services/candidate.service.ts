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
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {Observable} from 'rxjs';
import {Candidate} from '../model/candidate';
import {map} from 'rxjs/operators';
import {LocalStorageService} from "angular-2-local-storage";
import {CandidateOpportunity, CandidateOpportunityStage} from "../model/candidate-opportunity";

export interface UpdateCandidateAdditionalInfo extends UpdateCandidateSurvey {
  additionalInfo?: string,
  linkedInLink?: string,
}

export interface UpdateCandidateSurvey {
  surveyTypeId?: number,
  surveyComment?: string,
}

@Injectable({
  providedIn: 'root'
})
export class CandidateService {

  apiUrl: string = environment.apiUrl + '/candidate';

  constructor(private http: HttpClient,
              private localStorage: LocalStorageService) {
  }

  /* Contact */
  getCandidateContact(): Observable<Candidate> {
    return this.http.get<Candidate>(`${this.apiUrl}/contact`);
  }

  updateCandidateContact(request): Observable<Candidate> {
    return this.http.post<Candidate>(`${this.apiUrl}/contact`, request);
  }

  /* Personal */
  getCandidatePersonal(): Observable<Candidate> {
    return this.http.get<Candidate>(`${this.apiUrl}/personal`);
  }

  updateCandidatePersonal(request): Observable<Candidate> {
    return this.http.post<Candidate>(`${this.apiUrl}/personal`, request);
  }

  /* Education */
  getCandidateEducation(): Observable<Candidate> {
    return this.http.get<Candidate>(`${this.apiUrl}/education`);
  }

  updateCandidateEducationLevel(request): Observable<Candidate> {
    return this.http.post<Candidate>(`${this.apiUrl}/education`, request);
  }

  /* Candidate Additional Info */
  getCandidateAdditionalInfo(): Observable<Candidate> {
    return this.http.get<Candidate>(`${this.apiUrl}/additional-info`);
  }

  updateCandidateAdditionalInfo(request): Observable<Candidate> {
    return this.http.post<Candidate>(`${this.apiUrl}/additional-info`, request);
  }

  /* Candidate Survey */
  getCandidateSurvey(): Observable<Candidate> {
    return this.http.get<Candidate>(`${this.apiUrl}/survey`);
  }

  updateCandidateSurvey(request): Observable<Candidate> {
    return this.http.post<Candidate>(`${this.apiUrl}/survey`, request);
  }

  getCandidateCandidateOccupations(): Observable<Candidate> {
    return this.http.get<Candidate>(`${this.apiUrl}/occupation`);
  }

  getCandidateLanguages(): Observable<Candidate> {
    return this.http.get<Candidate>(`${this.apiUrl}/languages`);
  }

  getCandidateJobExperiences(): Observable<Candidate> {
    return this.http.get<Candidate>(`${this.apiUrl}/job-experiences`);
  }

  getCandidateCertifications(): Observable<Candidate> {
    return this.http.get<Candidate>(`${this.apiUrl}/certifications`);
  }

  updateCandidateCertification(request): Observable<Candidate> {
    return this.http.post<Candidate>(`${this.apiUrl}/certifications`, request);
  }

  getStatus() {
    return this.http.get<Candidate>(`${this.apiUrl}/status`);
  }

  getProfile(): Observable<Candidate> {
    return this.http.get<Candidate>(`${this.apiUrl}/profile`).pipe(
        map(candidate => {
          const opp: CandidateOpportunity = {
            closingCommentsForCandidate: "Needs better english - otherwise a good candidate",
            jobId: 123,
            jobName: 'Mock Job',
            name: 'Mock Candidate Opp name',
            nextStep: 'Tell employer he is dreaming',
            nextStepDueDate: new Date('2023-11-9'),
            stage: CandidateOpportunityStage.prospect
          };
          candidate.candidateOpportunities = [opp];

          return candidate;
        })
    )
  }

  getCandidateNumber() {
    return this.http.get<Candidate>(`${this.apiUrl}/candidate-number`);
  }

  downloadCv() {
    return this.http.get(`${this.apiUrl}/cv.pdf`, {responseType: 'blob'}).pipe(map(res => {
      return new Blob([res], { type: 'application/pdf', });
    }));
  }

  submitRegistration(): Observable<Candidate> {
    return this.http.post<Candidate>(`${this.apiUrl}/submit`, null);
  }

  getCandNumberStorage(): string {
    return this.localStorage.get('candidateNumber');
  }

  setCandNumberStorage(candidateNumber: string) {
    this.localStorage.set('candidateNumber', candidateNumber);
  }

  clearCandNumberStorage() {
    this.localStorage.remove('candidateNumber');
  }
}
