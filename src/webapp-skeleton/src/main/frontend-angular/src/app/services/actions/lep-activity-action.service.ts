import { Injectable, Injector } from '@angular/core';
import { select } from '@angular-redux/store';

import { reviver } from '../converters/any.converter';
import { BaseActionService } from './base-action.service';
import { PatientEncounter, LEPActivity, LEPExecution } from '../entities/act';
import { Employee } from '../entities/role';
import { Nanda, Dosage } from '../entities/act';
import {LEPExecutionActionService} from "./lep-execution-action.service";

@Injectable()
export class LEPActivityActionService extends BaseActionService<LEPActivity> {

  private static STATUS = '/status';
  public static CHANGE_STATUS = 'CHANGE_STATUS';

  private static SUGGESTED_LEP = '/suggestedLep';
  private static FAVORITE_LEP = '/favoriteLep';

  public static ACTION_VALIDATE = 'validate';
  public static ACTION_REVALIDATE = 'revalidate';
  public static ACTION_INVALIDATE = 'invalidate';

  @select(['config', 'employee']) employee$;
  employee: Employee;

  @select(['config', 'employeeRoleCode']) employeeRoleCode$;
  employeeRoleCode: string;

  constructor(injectorz: Injector,
              private lEPExecutionAction: LEPExecutionActionService) {
    super(injectorz);
    this.entityName = 'LEPActivity';
    this.entityUrl = 'lepactivitys';

    this.employee$.subscribe(res => this.employee = res);
    this.employeeRoleCode$.subscribe(res => this.employeeRoleCode = res);
  }

  /**
   * Create a new LEPActivity
   * if manual true crates also LEPExecution
   * @returns {LEPActivity}
   */
  public newLEPActivity(patientEncounter: PatientEncounter, nanda: Nanda, manual = false): LEPActivity {
    this.entity = {};
    this.initLEPActivity(this.entity, patientEncounter, nanda, manual);
    this.conversationActions.put(this._entityName, this.entity);
    return this.entity;
  }

  private initLEPActivity(lEPActivity: LEPActivity, patientEncounter: PatientEncounter, nanda: Nanda, manual) {
    const now = new Date();
    now.setSeconds(0);

    if (!lEPActivity.dosage) {
      lEPActivity.dosage = [];
    }

    if (nanda) {
      lEPActivity.nanda = nanda; // {internalId: nanda.internalId, entityName: "com.phi.entities.act.Nanda"};
    }
    if (patientEncounter) {
      lEPActivity.author = { internalId: this.employee.internalId, entityName: 'com.phi.entities.role.Employee' };
      lEPActivity.authorRole = { codeSystemName: 'ROLES',  domainName: 'EmployeeFunction', code: this.employeeRoleCode };
      lEPActivity.effectiveDate = {low: now};
      lEPActivity.patientEncounter = { internalId: patientEncounter.internalId, entityName: 'com.phi.entities.act.PatientEncounter' };
    }
    if (!lEPActivity.responsibleRole) {
      lEPActivity.responsibleRole = {codeSystemName: 'ROLES', domainName: 'EmployeeFunction', code: '10'}; // Nurse
    }
    if (!lEPActivity.supportNumber) {
      lEPActivity.supportNumber = 0;
    }
    if (!lEPActivity.timeSpent) {
      lEPActivity.timeSpent = 1;
    }

    if (manual) {
      const nowPlusOneMin = new Date(now);
      nowPlusOneMin.setMinutes(nowPlusOneMin.getMinutes() + 1);

      lEPActivity.confirmed = true;
      lEPActivity.dosage.push({ dayInterval: 1, daytime: now});
      lEPActivity.effectiveDate.high = nowPlusOneMin;
      lEPActivity.extemporaneous = true;
      lEPActivity.lepSource = { codeSystemName: 'PHIDIC', domainName: 'LEPSource', code: 'MANUAL'};
      lEPActivity.note = 'Attività generata automaticamente da un\'esecuzione manuale';
      lEPActivity.statusCode = { codeSystemName: 'PHIDIC', domainName: 'StatusCodes', code: 'active'};

      lEPActivity.lepExecution = [];

      let lepExecution:LEPExecution = this.lEPExecutionAction.newEntity();

      lEPActivity.lepExecution.push(lepExecution);
    }
  }

  /**
   * Clone LEPActivity, from FavoriteProfile or updating
   * @param lEPActivity
   * @returns {LEPActivity}
   */
  public clone(lEPActivity: LEPActivity, patientEncounter: PatientEncounter, nanda: Nanda): LEPActivity {

    let clone: LEPActivity  = {};

    if (lEPActivity.dosage) {
      clone.dosage = lEPActivity.dosage.map((d:Dosage) => {
        return { dayInterval: d.dayInterval, daytime: d.daytime };
      });
    }
    clone.effectiveDate = Object.assign({}, lEPActivity.effectiveDate);
    clone.statusCode = Object.assign({}, lEPActivity.statusCode);
    clone.extemporaneous = lEPActivity.extemporaneous;
    clone.modified = lEPActivity.modified;
    if (lEPActivity.nanda) {
      clone.nanda = Object.assign({}, lEPActivity.nanda);  
    }
    clone.nandaLep = Object.assign({}, lEPActivity.nandaLep);
    clone.note = lEPActivity.note;
    if (lEPActivity.objective) {
      clone.objective = Object.assign({}, lEPActivity.objective);
    }
    clone.responsibleRole = Object.assign({}, lEPActivity.responsibleRole);
    clone.statusCode = Object.assign({}, lEPActivity.statusCode);
    if (lEPActivity.supportNumber) {
      clone.supportNumber = lEPActivity.supportNumber;
    }
    if (lEPActivity.supportRole) {
      clone.supportRole = Object.assign({}, lEPActivity.supportRole);
    }
    clone.timeSpent = lEPActivity.timeSpent;
    clone.validityPeriod = Object.assign({}, lEPActivity.validityPeriod);

    this.initLEPActivity(clone, patientEncounter, nanda, false);

    return clone;
  }

  /**
   * Inject and replace patientEncounter with proxy
   */
  public inject(id: number, additionals: Array<any> = null, loads: Array<any> = null, conversationName = null): Promise<LEPActivity> {
    return super.inject(id, additionals, loads, conversationName).then( () => {
      if (this.entity && this.entity.patientEncounter) {
        this.entity.patientEncounter = {
          internalId: this.entity.patientEncounter.internalId,
          entityName: 'com.phi.entities.act.PatientEncounter'
        }
      }
      return this.entity;
    });
  }

  injectClone(id): Promise<LEPActivity> {
    return this.inject(id, null, ['nanda', 'nanda.nandaDiag', 'nandaLep']).then((original: LEPActivity) => {
      this.entity = this.clone(original, original.patientEncounter, null);
      this.conversationActions.put(this._entityName, this.entity);
      return Promise.resolve(original);
    });
  }

  updateAndChangeStatus(newActivity: LEPActivity): Promise<any> {
    this.cancel(this.entity, true);
    return this.update().then(() => {
      return this.changeStatus(this.entity.internalId, LEPActivityActionService.ACTION_INVALIDATE).then(() => {
        this.entity = newActivity;
        return this.create();
      });
    });
  }

  cancelAndChangeStatus(): Promise<any> {
    this.cancel(this.entity);
    return this.update().then(() =>
      this.changeStatus(this.entity.internalId, LEPActivityActionService.ACTION_INVALIDATE)
    );
  }

  cancel(lEPActivity: LEPActivity, modified = false) {

    if (lEPActivity.effectiveDate.high != null || modified) {
      lEPActivity.validityPeriod.high = new Date();
      lEPActivity.statusCode = {codeSystemName: 'PHIDIC', domainName: 'StatusCodes', code: 'cancelled'};
    } else {
      lEPActivity.effectiveDate.high = new Date();
    }

    if (modified) {
      lEPActivity.modified = true;
    }

    lEPActivity.cancellationDate = new Date();

    lEPActivity.cancelledBy = {internalId: this.employee.internalId, entityName: 'com.phi.entities.role.Employee'}; // PROXY
    lEPActivity.cancelledByRole = {codeSystemName: 'ROLES', domainName: 'EmployeeFunction', code: this.employeeRoleCode};

    // return this.update();
  }

  /**
   * Change status
   */
  changeStatus(activityId: number, action: string): Promise<any> {

    return this.httpService.fetch(this.apiUrl + this.restBaseUrl + this.entityUrl + LEPActivityActionService.STATUS + '/' + activityId
        + ';jsessionid=' + this.sid + '?conversationId=' + this.cid,
      {
        method: 'POST',
        body: 'action=' + action,
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        credentials: 'include'
      })
      .then(response => response.text())
      .then(text => JSON.parse(text, reviver) )
      .then(raw => {
        return {type: LEPActivityActionService.CHANGE_STATUS, data: raw, receivedAt: Date.now()};
      })
      .catch(error => {
        console.error('Error changeStatus ' + activityId + ' ' + error.message);
        throw error;
      });
  }

  /**
   * Get suggested LEP by nandaId
   */
  getSuggestedLEP(nandaId: number): Promise<any> {

    return this.httpService.fetch(this.apiUrl + this.restBaseUrl + this.entityUrl + LEPActivityActionService.SUGGESTED_LEP + '/' + nandaId
        + ';jsessionid=' + this.sid + '?conversationId=' + this.cid,
      {
        method: 'GET',
        headers: {'Content-Type': 'application/json'},
        credentials: 'include'
      }
      )
      .then(response => response.text())
      .then(text => JSON.parse(text, reviver) )
      .then(entity => {
        this.conversationActions.put('SuggestedLEPList', entity);
        return entity;
      })
      .catch(error => {
        console.error('Error getSuggestedLEP ' + this.entity + ' ' + error.message);
        throw error;
      });
  }

  /**
   * Get favorite LEP by wardId
   */
  getFavoriteLEP(wardId: number): Promise<any> {

    return this.httpService.fetch(this.apiUrl + this.restBaseUrl + this.entityUrl + LEPActivityActionService.FAVORITE_LEP + '/' + wardId
        + ';jsessionid=' + this.sid + '?conversationId=' + this.cid,
      {
        method: 'GET',
        headers: {'Content-Type': 'application/json'},
        credentials: 'include'
      }
      )
      .then(response => response.text())
      .then(text => JSON.parse(text, reviver) )
      .then(entity => {
        this.conversationActions.put('FavoriteLEPList', entity);
        return entity;
      })
      .catch(error => {
        console.error('Error getFavoriteLEP ' + this.entity + ' ' + error.message);
        throw error;
      });
  }

}
