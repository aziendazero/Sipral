import {Component, ElementRef, ViewChild} from '@angular/core';
import { AuthGuard } from '../../../services/auth-guard.service';
import { select } from '@angular-redux/store';
import {ButtonComponent} from '../../../widgets/button/button.component';
/**
 * Created by Daniele on 19/10/2017.
 */

@Component({
  selector: 'phi-sign-in',
  templateUrl: './signin.component.html',
})
export class SignInComponent {

  username;
  password;

  @select(['config', 'employee']) employee$;
  employee;

  @select(['config', 'passwordExpired']) passwordExpired$;
  passwordExpired;

  constructor(private AuthGuard: AuthGuard) {
    this.passwordExpired$.subscribe(res => this.passwordExpired = res);
    this.employee$.subscribe(res => this.employee = res);
  }

  login() {
    this.AuthGuard.login(this.username, this.password);
  }

}
