import {Component, EventEmitter, Injector, Input, OnInit, Output, ViewChild} from '@angular/core';
import { select } from '@angular-redux/store';
import { TranslateService } from '@ngx-translate/core';

import { BaseForm } from '../../../widgets/form/base-form';
import { SubstanceAdministrationActionService, LEPExecutionActionService, PrescriptionActionService } from '../../../services/actions';
import { SubstanceAdministration, LEPExecution } from '../../../services/entities/act';
import { Employee } from '../../../services/entities/role';
import { CodeValueProxy } from '../../../services/proxyes/code-value-proxy';
import { ViewManager } from '../../../services/view-manager';
import { Config } from '../../../store/config.reducer';
import { CodeValue } from '../../../services/entities/data-types';
import { TabbedPanelComponent } from '../../../widgets/tabbed-panel.component';
import { environment } from '../../../../environments/environment';

/**
 * Created by Daniele Tarticchio on 21/06/2017.
 */

@Component({
selector: 'phi-nurse-activity-panel',
templateUrl: './nurse-activity-panel.html',
styleUrls: ['./../nurse-activity.component.scss']
})
export class NurseActivityPanel extends BaseForm  implements OnInit {

  @ViewChild('TabPnlNrsActvtyErgt') tabPanel: TabbedPanelComponent;

  @select(['config']) config$;
  Employee: Employee;

  erogationdate;
  executionTime = 1;
  difference = '';
  diffMinute = 0;
  planneddate;
  @select(['conversation', 'NurseActivity']) NurseActivity$;
  NurseActivity;
  @select(['conversation', 'Prescription']) Prescription$;
  Prescription;
  @select(['conversation', 'LEPExecution']) LEPExecution$;
  LEPExecution: LEPExecution;
  //@select(['conversation', 'note']) note$;
  note;
  //@select(['conversation', 'statusDetailsCode']) statusDetailsCode$;
  statusDetailsCode: CodeValue;
  @select(['conversation', 'SubstanceAdministration']) SubstanceAdministration$;
  SubstanceAdministration: SubstanceAdministration;

  @Input() data: any;

  // ADMINISTRATION_LOW_GAP = 60;
  // ADMINISTRATION_HIGH_GAP = 60;
  static MILLISECONDS_PER_MINUTES = 60000;

  statusCode: CodeValueProxy;

  now;

  @Output() save : EventEmitter<any> = new EventEmitter();

  constructor (injector: Injector,
               private viewManager: ViewManager,
               private PrescriptionAction: PrescriptionActionService,
               private SubstanceAdministrationAction: SubstanceAdministrationActionService,
               private LEPExecutionAction: LEPExecutionActionService,
               private translateService: TranslateService) {
    super(injector);

    this.config$.subscribe( (cfg: Config) => {
      this.Employee = cfg.employee;
    });

    this.NurseActivity$.subscribe(res => this.NurseActivity = res);
    this.Prescription$.subscribe(res => this.Prescription = res);
    this.LEPExecution$.subscribe(res => this.LEPExecution = res);
    this.SubstanceAdministration$.subscribe(res => this.SubstanceAdministration = res);
    //this.note$.subscribe(res => this.note = res);
    //this.statusDetailsCode$.subscribe(res => this.statusDetailsCode = res);
    this.now = new Date;
  }

  ngOnInit() {

    this.erogationdate = this.NurseActivity.status === 'MISSED' ? this.NurseActivity.planneddate : new Date();
    this.erogationdate.setSeconds(0);
    this.erogationdate.setMilliseconds(0);

    // Calculate difference
    if (this.NurseActivity.planneddate != null) {
      this.planneddate = new Date(this.NurseActivity.planneddate);
      this.planneddate.setSeconds(0);
      this.planneddate.setMilliseconds(0);
      this.calculateDifference();
    }

    this.statusDetailsCode = { codeSystemName: 'PHIDIC', domainName: 'AdministrationReason', code: 'OCRE'} // Altra ragione clinica
  }

  ngOnDestroy() {
    if (!environment.nurseActivity.onlyExecutions) {
      this.PrescriptionAction.eject();
      this.SubstanceAdministrationAction.eject();
    }
    this.LEPExecutionAction.eject();
  }

  calculateDifference(): void {

    let diff: number = this.erogationdate.getTime() - this.planneddate.getTime();

    this.diffMinute = diff / NurseActivityPanel.MILLISECONDS_PER_MINUTES;

    let absDiffInSec = Math.abs(diff/1000);
    let hour: number = Math.trunc(absDiffInSec/3600);
    let min: number = (absDiffInSec - hour * 3600)/60;

    let time: string = '';
    if (hour < 10) {
      time = "0";
    }
    time += hour + ':';
    if (min < 10) {
      time += "0";
    }
    time += min;

    this.difference = time + ' ';

    if (diff < 0) {
      this.difference += this.translateService.instant('in_advance');
    } else if (diff > 0) {
      this.difference += this.translateService.instant('in_delay');
    }
  }

  changeErogationDate(_offset) {
    this.diffMinute += _offset;
    this.refreshErogationDate();
  }

  refreshErogationDate() {
    this.erogationdate = new Date(this.planneddate.getTime() + this.diffMinute * NurseActivityPanel.MILLISECONDS_PER_MINUTES);
    this.calculateDifference();
  }

  changeStatus(tabIndex) {
    if (tabIndex) {
      this.statusCode = {codeSystemName: 'PHIDIC', domainName: 'AdministrationStatus'};

      if (tabIndex === 1) { // Erogato
        this.statusCode.code = 'DONE';
      } else if (tabIndex === 2) { // Insuccesso
        this.statusCode.code = 'UNSUCCESSFUL';
      } else if (tabIndex === 3) { // Errore
        this.statusCode.code = 'EXCEPTION';
      }
    }
  }

  onSave() {
    if (this.tabPanel.selectedTabIndex === 0) {
      this.tabPanel.ie(null, 1);
    } else {
      if (this.SubstanceAdministration) {
        this.SubstanceAdministrationAction.erogate(this.erogationdate, this.statusCode, this.Prescription.continuous, this.statusDetailsCode, this.note).then(() => {
          this.close();
          this.save.next(null);
        });
      } else if (this.LEPExecution) {
        this.LEPExecutionAction.erogate(this.erogationdate, this.statusCode, this.executionTime, this.statusDetailsCode, this.note).then(() => {
          this.close();
          this.save.next(null);
        });
      }
    }
  }

  cancel() {
    this.close();
  }

  close() {
    this.viewManager.setPopupViewId(null);
  }
}
