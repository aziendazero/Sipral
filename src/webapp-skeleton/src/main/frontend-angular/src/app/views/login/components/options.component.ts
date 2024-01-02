import { Component, OnDestroy } from '@angular/core';
import { AuthGuard } from '../../../services/auth-guard.service';
import { select } from '@angular-redux/store';
import { TranslateService } from '@ngx-translate/core';
import { ProcessActions } from '../../../actions/process.actions';
import { SelectItem } from '../../../services/datamodel/select-item';
import { EmployeeRole } from '../../../services/entities/base-entity/employee-role';
import { ConfigActions } from '../../../actions/config.action';
import { Subscription } from 'rxjs';
import { environment } from '../../../../environments/environment';
/**
 * Created by Daniele on 19/10/2017.
 */

@Component({
  selector: 'phi-options',
  templateUrl: './options.component.html'
})

export class OptionsComponent implements OnDestroy {

  environment = environment;

  selectedRole: number;
  selectedLang: string;

  @select(['config']) config$;
  configSub: Subscription;

  @select(['config', 'employeeRoleId']) employeeRoleId$;
  configemployeeRoleSub: Subscription;

  @select(['process', 'lang']) lang$;
  langSub: Subscription;

  employee;
  enabledSdlocs;
  enabledRoles;
  sdLocs;
  sdlocChange = false;

  constructor(
    private AuthGuard: AuthGuard,
    private translateService: TranslateService,
    private processActions: ProcessActions,
    private configActions: ConfigActions,
  ) {

    this.configSub = this.config$.subscribe(conf => {
      this.employee = conf.employee;
      this.enabledSdlocs = conf.enabledSdlocs;
      this.enabledRoles = conf.enabledRoles;
      this.sdLocs = conf.sdLocs;

    });

    this.configemployeeRoleSub = this.employeeRoleId$.subscribe(er => {
      this.selectedRole = er;
    });

    this.langSub = this.lang$.subscribe(er => {

      const supportedLang = environment.languages.find(lang =>
        lang[0] === er
      );

      if (!supportedLang) {
        this.selectedLang = environment.languages[0][0];
        this.changeLang(this.selectedLang);
      } else {
        this.selectedLang = er;
      }


    });
}

ngOnDestroy() {
  this.configSub.unsubscribe();
  this.configemployeeRoleSub.unsubscribe();
  this.langSub.unsubscribe();
}

  options() {
    let sdlocsIds: Array<number> = [];
    if (this.sdLocs) {
      sdlocsIds = this.sdLocs.map((sdl) => parseInt(sdl.id));
    }
    this.AuthGuard.setOptions(this.selectedRole, this.sdlocChange ? sdlocsIds : null, this.selectedLang);
  }

  logout() {
    this.AuthGuard.logout();
  }

  onSdlocChange() {
    this.sdlocChange = true;
  }

  reloadSdloc() {
    this.AuthGuard.loadSdlocs(this.selectedRole);
  }

  changeLang(lang: string) {
    this.processActions.setLanguage(lang);
    this.translateService.use(lang);

    let key = 'lang' + this.selectedLang.substring(0, 1).toUpperCase() + this.selectedLang.substring(1, 2);

    if (this.employee && this.employee.employeeRole) {
      const enabledRoles: Array<SelectItem> = this.employee.employeeRole.map((er: EmployeeRole) => {
        return {
          label: ((er.code[key] ? er.code[key] : er.code['currentTranslation']) + ((environment.options.showComment && er.loginComment) ? (' - ' + er.loginComment) : '')),
          value: er.internalId,
          effectiveTime: er.effectiveTime.low
        }
      }).sort((a, b) => {
        let nameA = a.label; // ignore upper and lowercase
        let nameB = b.label; // ignore upper and lowercase
        if (nameA < nameB) {
          return -1;
        } else if (nameA > nameB) {
          return 1;
        } else {
          let etA = a.effectiveTime;
          let etB = b.effectiveTime;
          if (etA > etB) {
            return -1;
          } else if (etA < etB) {
            return 1;
          }
        }
      });
      this.configActions.put('enabledRoles', enabledRoles);
    }
  }

}
