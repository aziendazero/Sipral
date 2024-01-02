import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { select } from '@angular-redux/store';
import { TranslateService } from '@ngx-translate/core';
import { environment } from '../../../environments/environment';
import { ViewManager } from '../../services/view-manager';
import { ProcessActions } from '../../actions/process.actions';
import { AdtAction } from '../../dashboard/adt/actions/adt-action.service';
import { PatientEncounterActionService } from 'app/services/actions';
import { PatientEncounter } from 'app/services/entities/act';
import { Employee } from 'app/services/entities/role';
import { Config } from 'app/store/config.reducer';

@Component({
  selector: 'phi-popup-menu',
  templateUrl: './popup-menu.component.html'
})
export class PopupMenuComponent {

  environmentPath = null;
  encounterId = 0;
  statusCode = '';
  appointmentId = 0;
  invoicingClosed = false;
  sdoClosed = false;

  menu = [];

  @select(['config']) config$;

  Employee: Employee;

  constructor(private route: ActivatedRoute,
              private viewManager: ViewManager,
              private processActions: ProcessActions,
              private adtAction: AdtAction,
              private patientEncounterAction: PatientEncounterActionService,
              private translateService: TranslateService) {

    this.environmentPath = this.route.snapshot.params['environmentPath'];
    this.encounterId = +this.route.snapshot.params['encounterId'];
    this.statusCode = this.route.snapshot.params['statusCode'];
    this.appointmentId = +this.route.snapshot.params['appointmentId'];
    this.invoicingClosed = this.route.snapshot.params['invoicingClosed'] === 'true';
    this.sdoClosed = this.route.snapshot.params['sdoClosed'] === 'true';

    this.menu = this.byString(environment, this.environmentPath);

    this.config$.subscribe( (cfg: Config) => {
      this.Employee = cfg.employee;
    });
  }

  public async clickHandler(item: any) {
    if (item.process) {

      this.startProcess(item.process);

    } else if (item.popup) {

      const popup: Array<string> = item.popup.map(item =>
        item.replace(":encounterId", this.encounterId.toString())
          .replace(":appointmentId", this.appointmentId.toString())
      );

      let popupViewId, params;
      [popupViewId, ...params] = popup;

      this.viewManager.setPopupViewId(popupViewId, ...params);

    } else if (item.action) {
      if (item.action === 'takeInCharge') {
        await this.adtAction.changeStatus(this.encounterId, this.appointmentId, 'active', null);
        this.viewManager.setPopupViewId(null);
      } else if (item.action === 'discharge') {
        this.viewManager.openAlertMessage(
          this.translateService.instant('Confirm'),
          this.translateService.instant('DcAdtDischarge'),
          true,
          true,
          'OK',
          'Cancel',
          true,
          () => {
            this.adtAction.changeStatus(this.encounterId, this.appointmentId, 'discharged', null);
          }
        );
      } else if (item.action === 'closeInvoicing') {
        this.viewManager.openAlertMessage(
          this.translateService.instant('Confirm'),
          this.translateService.instant('DcAdtInvoiceClose'),
          true,
          true,
          'OK',
          'Cancel',
          true,
          async () => {
            let patientEncounter: PatientEncounter = await this.patientEncounterAction.get(this.encounterId);
            patientEncounter.invoicingClosed = true;
            patientEncounter.invoicingClosedDate = new Date();
            patientEncounter.invoicingClosedBy = this.Employee;
            await this.patientEncounterAction.create();
            this.adtAction.refresh();
          }
        );
      } else if (item.action === 'closeSdo') {
        this.viewManager.openAlertMessage(
          this.translateService.instant('Confirm'),
          this.translateService.instant('DcAdtSdoClose'),
          true,
          true,
          'OK',
          'Cancel',
          true,
          async () => {
            let patientEncounter: PatientEncounter = await this.patientEncounterAction.get(this.encounterId);
            patientEncounter.sdoClosed = true;
            patientEncounter.sdoClosedDate = new Date();
            patientEncounter.sdoClosedBy = this.Employee;
            await this.patientEncounterAction.create();
            this.adtAction.refresh();
          }
        );
      }
    }
  }

  public async startProcess(process: string) {
    await this.viewManager.setPopupViewId(null);
    this.processActions.startProcess(process);
  }

  public checkStatus(item) {
    if (!item.status) {
      return true;
    } else {
      return item.status.indexOf(this.statusCode) !== -1;
    }
  }

  public isDisabled(item) {
    if (item.disabled && item.disabled === 'invoicingclosed') {
      return this.invoicingClosed;
    }
    if (item.disabled && item.disabled === 'sdoclosed') {
      return this.sdoClosed;
    }

    return false;
  }

  private byString(o: any, s: string) {
    s = s.replace(/\[(\w+)\]/g, '.$1'); // convert indexes to properties
    s = s.replace(/^\./, '');           // strip a leading dot
    const a = s.split('.');
    for (let i = 0, n = a.length; i < n; ++i) {
      let k = a[i];
      if (k in o) {
        o = o[k];
      } else {
        return;
      }
    }
    return o;
  }

  public close() {
    this.viewManager.setPopupViewId(null);
  }


}
