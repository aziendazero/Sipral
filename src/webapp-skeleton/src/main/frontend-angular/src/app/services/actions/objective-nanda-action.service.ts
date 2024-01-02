import { Injectable, Injector } from '@angular/core';
import { select } from '@angular-redux/store';

import { BaseActionService } from './base-action.service';
import { ObjectiveNanda, PatientEncounter } from '../entities/act';
import { Employee } from '../entities/role';
import { Config } from '../../store/config.reducer';
import {Nanda} from "../entities/act/nanda";
import {LEPActivityActionService} from "./lep-activity-action.service";

@Injectable()
export class ObjectiveNandaActionService extends BaseActionService<ObjectiveNanda> {

  @select(['config']) config$;

  employee: Employee;
  employeeRoleCode: string;

  constructor(injectorz: Injector, private lepActivityAction: LEPActivityActionService) {
    super(injectorz);
    this.entityName = 'ObjectiveNanda';
    this.entityUrl = 'objectivenandas';

    this.config$.subscribe( (cfg: Config) => {
      this.employee = cfg.employee;
      this.employeeRoleCode = cfg.employeeRoleCode;
    });
  }

  public newObjectiveNanda(nanda: Nanda, patientEncounter: PatientEncounter): ObjectiveNanda {
    this.entity = {};

    this.entity.activity = {};
    this.entity.activity.author = { internalId: this.employee.internalId, entityName: 'com.phi.entities.role.Employee'};
    this.entity.activity.authorRole = { codeSystemName: 'ROLES', domainName: 'EmployeeFunction', code: this.employeeRoleCode };
    this.entity.activity.dosage = [];
    this.entity.activity.effectiveDate = { low: new Date(), high: null};
    this.entity.activity.effectiveDate.low.setSeconds(0);
    this.entity.activity.lepSource =  {codeSystemName: 'PHIDIC', domainName: 'LEPSource', code: 'OBJECTIVE'};
    this.entity.activity.nanda = { internalId: nanda.internalId, entityName: 'com.phi.entities.act.Nanda' };
    this.entity.activity.nandaLep = {codeSystemName: 'NANDA', domainName: 'K630048', code: 'K630368'};
    this.entity.activity.patientEncounter = { internalId: patientEncounter.internalId, entityName: 'com.phi.entities.act.PatientEncounter' };
    this.entity.activity.responsibleRole = { codeSystemName: 'ROLES', domainName: 'EmployeeFunction', code: '10' }; // INFERMIERE
    this.entity.activity.timeSpent = 1;
    this.entity.activity.supportNumber = 0;

    this.entity.author = { internalId: this.employee.internalId, entityName: 'com.phi.entities.role.Employee'};
    this.entity.authorRole = { codeSystemName: 'ROLES', domainName: 'EmployeeFunction', code: this.employeeRoleCode };
    this.entity.levelCode = {codeSystemName: 'PHIDIC', domainName: 'ObjectveNanda', code: 'ELSEOBJECTIVE'};
    this.entity.nanda = { internalId: nanda.internalId, entityName: 'com.phi.entities.act.Nanda' };
    this.entity.objReached = {codeSystemName: 'PHIDIC', domainName: 'ObjectiveReach', code: 'OBJECTIVENOTREACHED'};
    this.entity.patientEncounter = { internalId: patientEncounter.internalId, entityName: 'com.phi.entities.act.PatientEncounter' };
    this.entity.text = {};

    this.conversationActions.put(this._entityName, this.entity);

    return this.entity;
  }

  /**
   * Clone ObjectiveNanda when updating
   * @param objectiveNanda
   * @returns {ObjectiveNanda}
   */
  public clone(objectiveNanda: ObjectiveNanda): ObjectiveNanda {

    let clone: ObjectiveNanda  = {};

    clone.author = { internalId: this.employee.internalId, entityName: 'com.phi.entities.role.Employee'};
    clone.authorRole = { codeSystemName: 'ROLES', domainName: 'EmployeeFunction', code: this.employeeRoleCode };

    if (objectiveNanda.levelCode) {
      clone.levelCode = Object.assign({}, objectiveNanda.levelCode);
    }
    if (objectiveNanda.nanda) {
      clone.nanda = Object.assign({}, objectiveNanda.nanda);
    }
    clone.note = objectiveNanda.note;
    if (objectiveNanda.objReached) {
      clone.objReached = Object.assign({}, objectiveNanda.objReached);
    }
    // this.entity.objReached = {codeSystemName: 'PHIDIC', domainName: 'ObjectiveReach', code: 'OBJECTIVENOTREACHED'};
    if (objectiveNanda.patientEncounter) {
      clone.patientEncounter = Object.assign({}, objectiveNanda.patientEncounter);
    }
    if (objectiveNanda.text) {
      clone.text = Object.assign({}, objectiveNanda.text);
    }

    clone.activity = this.lepActivityAction.clone(objectiveNanda.activity, objectiveNanda.activity.patientEncounter, objectiveNanda.activity.nanda);

    return clone;
  }

  /**
   * Inject and replace patientEncounter with proxy
   */
  public inject(id: number, additionals: Array<any> = null, loads: Array<any> = null, conversationName = null): Promise<ObjectiveNanda> {
    return super.inject(id, additionals, loads, conversationName).then( () => {
      if (this.entity && this.entity.patientEncounter) {
        this.entity.patientEncounter = {
          internalId: this.entity.patientEncounter.internalId,
          entityName: 'com.phi.entities.act.PatientEncounter'
        }
      }
      if (this.entity && this.entity.activity && this.entity.activity.patientEncounter) {
        this.entity.activity.patientEncounter = {
          internalId: this.entity.activity.patientEncounter.internalId,
          entityName: 'com.phi.entities.act.PatientEncounter'
        }
      }
      return this.entity;
    });
  }

  injectClone(id): Promise<ObjectiveNanda> {
    return this.inject(id, null, ['activity', 'activity.nanda', 'activity.nanda.nandaDiag']).then((original: ObjectiveNanda) => {
      this.entity = this.clone(original);
      this.conversationActions.put(this._entityName, this.entity);
      return Promise.resolve(original);
    });
  }

  updateAndChangeStatus(newObjectiveNanda: ObjectiveNanda): Promise<any> {
    this.cancel(true);
    if (this.lepActivityAction.entity.cancellationNote && this.entity.cancellationNote == null) {
      this.entity.cancellationNote = this.lepActivityAction.entity.cancellationNote;
    } else {
      this.entity.cancellationNote = "Disactivated_because_the_related_activity_was_disactivated";
    }
    return this.update().then(() => {
      return this.lepActivityAction.changeStatus(this.entity.activity.internalId, LEPActivityActionService.ACTION_INVALIDATE).then(() => {
        this.entity = newObjectiveNanda;
        return this.create();
      });
    });
  }

  cancel(modified = false) {
    // this.entity.cancellationNote = this.entity.activity.cancellationNote;

    this.entity.cancellationDate = new Date();

    this.entity.cancelledBy = {internalId: this.employee.internalId, entityName: 'com.phi.entities.role.Employee'}; // PROXY
    this.entity.cancelledByRole = {codeSystemName: 'ROLES', domainName: 'EmployeeFunction', code: this.employeeRoleCode};

    this.lepActivityAction.cancel(this.entity.activity, modified);
  }

  cancelAndChangeStatus(): Promise<any> {

    this.cancel();
    this.lepActivityAction.cancel(this.entity.activity);

    return this.update().then(() =>
      this.lepActivityAction.changeStatus(this.entity.activity.internalId, LEPActivityActionService.ACTION_INVALIDATE)
    );
  }

}
