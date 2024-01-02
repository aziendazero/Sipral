import { Component } from '@angular/core';
import { select } from '@angular-redux/store';

@Component({
  selector: 'phi-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})

export class LoginComponent {
  @select(['config', 'errors']) errors$;
  errors;

  constructor() {
    this.errors$.subscribe((res) => this.errors = res);
  }
}
