import { ModuleWithProviders } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { SignInComponent } from './components/singin.component';
import { OptionsComponent } from './components/options.component';
import { LoginComponent } from './login.component';
import { PasswordComponent } from './components/password.component';
import { LoginCanActivateService } from './login.can-activate.service';
import { AlreadyLoggedComponent } from './components/already-logged.component';

const routes: Routes = [
  {
    path: 'login', component: LoginComponent, canActivateChild: [LoginCanActivateService],
    children: [
      { path: 'signin', component: SignInComponent },
      { path: 'options', component: OptionsComponent },
      { path: 'password', component: PasswordComponent },
      { path: 'alreadyLogged', component: AlreadyLoggedComponent },
      { path: '**', redirectTo: 'signin', pathMatch: 'full' },
    ]
  }
];

export const LoginRouting: ModuleWithProviders = RouterModule.forChild(routes);
