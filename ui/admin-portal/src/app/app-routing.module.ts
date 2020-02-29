import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {SearchCandidatesComponent} from './components/candidates/search/search-candidates.component';
import {ViewCandidateComponent} from './components/candidates/view/view-candidate.component';
import {AuthGuard} from "./services/auth.guard";
import {LoginComponent} from "./components/login/login.component";
import {SettingsComponent} from "./components/settings/settings.component";
import {HomeComponent} from "./components/candidates/home.component";
import {InfographicComponent} from "./components/infograhics/infographic.component";
import {DefineSearchComponent} from "./components/search/define-search/define-search.component";
import {NotFoundComponent} from "./not-found/not-found.component";

/*
Urls:
/ -> redirects to /candidates
/candidates - Saved search list
/candidates/:id - saved search preview results
    (child of above - see Angular router example - but maybe not needed because
    always same component on display - it is not a "router outlet" based on url)
/candidates/search/:id - Display results of saved search id
/candidates/candidate/:id - Display candidate id
 */

const routes: Routes = [
  {
    path: '',
    canActivate: [AuthGuard],
    children: [
      {
        path: '',
        redirectTo: '/candidates', pathMatch: 'full'
      },
      {
        path: 'search',
        children: [
          {
            path: '',
            pathMatch: 'full',
            component: DefineSearchComponent,
            data: {title: 'TBB Admin - Define Search'}
          },
          {
            path: ':savedSearchId',
            component: DefineSearchComponent,
            data: {title: 'TBB Admin - Define Saved Search'}
          },
        ]
      },
      {
        path: 'candidates',
        children: [
          {
            path: '',
            pathMatch: 'full',
            component: HomeComponent,
            data: {title: 'TBB Admin - Browse searches'}
          },
          {
            path: 'search/:savedSearchId',
            component: SearchCandidatesComponent,
            data: {title: 'TBB Admin - Candidate search'}
          },
          {
            path: ':candidateId',
            component: ViewCandidateComponent,
            data: {title: 'TBB Admin - Candidate'}
          },

        ]
      },
      {
        path: 'settings',
        children: [
          {
            path: '',
            pathMatch: 'full',
            component: SettingsComponent,
            data: {title: 'TBB Admin - Settings'}
          },
        ]
      },
      {
        path: 'infographics',
        children: [
          {
            path: '',
            pathMatch: 'full',
            component: InfographicComponent,
            data: {title: 'TBB Admin - Infographics'}
          }
        ]
      }
    ]
  },
  {
    path: 'login',
    component: LoginComponent,
    data: {
      hideHeader: true
    }
  },
  {
    path: '**',
    component: NotFoundComponent,
    data: {
      hideHeader: true
    }
  }

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
