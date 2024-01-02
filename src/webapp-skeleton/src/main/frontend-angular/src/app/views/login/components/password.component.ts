import { Component } from '@angular/core';
import { AuthGuard } from '../../../services/auth-guard.service';
import { select } from '@angular-redux/store';
import {ConfigActions} from '../../../actions/config.action';
import {HttpService} from '../../../services/http.service';
import {EmployeeActionService} from '../../../services/actions/employee-action.service';
import {Router} from '@angular/router';

/**
 * Created by Daniele on 19/10/2017.
 */

@Component({
  selector: 'phi-password-change',
  templateUrl: './password.component.html',
  // styleUrls: ['./options.component.scss']
})

export class PasswordComponent {

  @select(['config', 'employee']) employee$;
  employee;

  oldPassword;
  newPassword;
  checkPassword;

  constructor(
    private AuthGuard: AuthGuard,
    private ConfigActions: ConfigActions,
    private EmployeeActionService: EmployeeActionService,
    private router: Router
  ) {
    this.employee$.subscribe(res => this.employee = res);
  }

  logout() {
    this.AuthGuard.logout();
  }

  password() {
    if (!this.oldPassword) {
      // vecchia password obbligatoria!
      this.ConfigActions.put('errors', ['vecchia password obbligatoria!']);
    } else if (!this.newPassword) {
      // nuova password obbligatoria!
      this.ConfigActions.put('errors', ['nuova password obbligatoria!']);
    } else if (this.newPassword.length < 8) {
      // password nuova troppo corta!
      this.ConfigActions.put('errors', ['password nuova troppo corta!']);
    } else if (!this.checkPassword) {
      // password di controllo obbligatoria!
      this.ConfigActions.put('errors', ['password di controllo obbligatoria!']);
    } else if (this.newPassword != this.checkPassword) {
      // password non corrisponde
      this.ConfigActions.put('errors', ['le nuove password non corrispondono!']);
    } else {
      // chiama rest per verificare la vecchia password -> è corretta e diversa da quella nuova? cambiala!
      this.EmployeeActionService.changePassword(this.oldPassword, this.newPassword, this.checkPassword)
        .then((res) => {
          if (!res.ok){
            // FIXME useless since httpService rejects promise and goes in onrejected
            // gestisci errori:
            // 1) vecchia password sbagliata
            // 2) nuova password uguale a quella vecchia
            res.json().then(
              (errors) => {
                this.ConfigActions.put('errors', errors.error)
              }
            );
          } else {
            // gestisci ok -> naviga verso options
            this.ConfigActions.put('errors', []);
            this.ConfigActions.putPasswordExpired(false);
            this.router.navigate(['/login']);
          }
        }, (fail) =>{
          const error = JSON.parse(fail);
          this.ConfigActions.put('errors', error.error)
        });
    }
  }
}
