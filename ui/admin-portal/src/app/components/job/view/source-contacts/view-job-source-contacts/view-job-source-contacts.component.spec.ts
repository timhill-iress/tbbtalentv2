import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ViewJobSourceContactsComponent} from './view-job-source-contacts.component';
import { AuthService } from 'src/app/services/auth.service';
import { PartnerService } from 'src/app/services/partner.service';
import { UserService } from 'src/app/services/user.service';
import { of } from 'rxjs';


describe('ViewJobSourceContactsComponent', () => {
  let component: ViewJobSourceContactsComponent;
  let fixture: ComponentFixture<ViewJobSourceContactsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ViewJobSourceContactsComponent],
      providers: [
        {
          provide: AuthService, useValue: {
            getLoggedInUser: () =>{ partner: { id: 1 } }
        } },
        {
          provide: PartnerService, useValue: {
            listSourcePartners: () => of([])
        } },
        { provide: UserService, useValue: {} }
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ViewJobSourceContactsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
