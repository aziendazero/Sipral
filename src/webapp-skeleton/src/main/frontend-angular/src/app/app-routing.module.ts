import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { HomeComponent } from './views/home/home.component';
import { LoginComponent } from './views/login/login.component';
import { AuthGuard } from './services/auth-guard.service';

@NgModule({
  imports: [
    RouterModule.forRoot([
      { path: '', component: HomeComponent },
      { path: 'login', component: LoginComponent },
      { path: '**', redirectTo: '' }
  ], {useHash: true})],
  exports: [
    RouterModule
  ],
  providers: []
})
export class AppRoutingModule {}
