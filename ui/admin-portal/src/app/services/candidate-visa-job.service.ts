/*
 * Copyright (c) 2020 Talent Beyond Boundaries. All rights reserved.
 */

import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {environment} from '../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {CandidateVisaJobCheck} from '../model/candidate';

export interface CreateCandidateVisaJobRequest {
  name: string;
  sfJobLink: string;
}

@Injectable({providedIn: 'root'})
export class CandidateVisaJobService {

  private apiUrl = environment.apiUrl + '/candidate-visa-job';

  constructor(private http: HttpClient) {}

  create(visaId: number, candidateVisaJobRequest: CreateCandidateVisaJobRequest):
    Observable<CandidateVisaJobCheck>  {
    return this.http.post<CandidateVisaJobCheck>(
      `${this.apiUrl}/${visaId}`, candidateVisaJobRequest);
  }

  delete(id: number): Observable<boolean>  {
    return this.http.delete<boolean>(`${this.apiUrl}/${id}`);
  }
}
