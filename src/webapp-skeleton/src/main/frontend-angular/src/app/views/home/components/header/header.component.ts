import { Component, Inject } from '@angular/core';
import { select } from '@angular-redux/store';
import { TranslateService } from '@ngx-translate/core';
import { AuthGuard } from '../../../../services';
import { ConfigActions } from '../../../../actions/config.action';
import { ProcessActions } from '../../../../actions/process.actions';
import { Location } from '@angular/common';
import { Config } from '../../../../store/config.reducer';
import { ViewManager } from '../../../../services/view-manager';
import { environment } from '../../../../../environments/environment';

@Component({
  selector: 'phi-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent {

  @select(['config']) config$;
  config: Config;

  @select(['conversation', 'Patient']) Patient$;
  Patient;
  @select(['process', 'current']) currentProcess$;
  currentProcess;

  environment = environment;

  constructor(private ConfigActions: ConfigActions,
              private ProcessActions: ProcessActions,
              public authGuard: AuthGuard,
              public translateService: TranslateService,
              private location: Location,
              private viewManager: ViewManager,
              @Inject('apiUrl') protected apiUrl) {
    this.Patient$.subscribe((res) => this.Patient = res);
    this.currentProcess$.subscribe((res) => this.currentProcess = res);
    this.config$.subscribe((res) =>
      this.config = res
    );
  }

  onClick() {
    this.ConfigActions.toggleMenu();
  }

  goHome() {
    this.ConfigActions.toggleMenu();
    if (this.currentProcess) {
      this.ProcessActions.endProcess(this.currentProcess).then(resp => {
        this.authGuard.needRelogin(resp);
      });
    }
  }

  searchUpToDate(input) {
    if (input.value) {
      fetch(this.apiUrl + 'resource/rest/uptodate/url/' + input.value, {credentials: 'include'})
        .then(response => response.text())
        .then(url => {
          this.viewManager.openIframe(url, true);
          input.value = '';
        });
    }
  }

  goOut() {
    if (environment.banner.confirmLogout) {
      this.viewManager.openAlertMessage(this.translateService.instant('Warning'),
        '&Egrave; stato premuto il pulsante di LOGOUT.\n\nConfermi l\'uscita dall\'applicazione?',
        true,
        true,
        'OK',
        'ANNULLA',
        true,
        () => {
          this.authGuard.logout();
        });
    } else {
      this.authGuard.logout();
    }
  }
}
