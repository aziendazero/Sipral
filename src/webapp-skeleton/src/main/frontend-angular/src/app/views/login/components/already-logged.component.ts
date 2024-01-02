import { Component } from '@angular/core';
import { AuthGuard } from '../../../services/auth-guard.service';
import { select } from '@angular-redux/store';
import { Router } from '@angular/router';
/**
 * Created by Daniele on 19/10/2017.
 */

@Component({
  selector: 'phi-already-logged',
  templateUrl: './already-logged.component.html',
  // styleUrls: ['./options.component.scss']
})

export class AlreadyLoggedComponent {

  @select(['config', 'employee']) employee$;
  employee;

  constructor(
    private AuthGuard: AuthGuard,
    private router: Router,
  ) {
    this.employee$.subscribe(res => this.employee = res);
  }

  logout() {
    this.AuthGuard.logout();
  }

  goHome() {
    this.router.navigate(['/'])
  }

}
