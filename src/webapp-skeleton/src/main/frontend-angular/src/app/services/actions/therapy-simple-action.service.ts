import { Injectable, Injector } from '@angular/core';
import { select } from '@angular-redux/store';
import { BaseActionService } from './base-action.service';
import { TherapySimple } from '../entities/base-entity';
import { Employee } from '../entities/role';
import { PatientEncounter } from '../entities/act';


@Injectable()
export class TherapySimpleActionService extends BaseActionService<TherapySimple> {

  @select(['config', 'employee']) employee$;

  employee: Employee;

  constructor(injectorz: Injector) {
    super(injectorz);
    this.entityName = 'TherapySimple';

    this.employee$.subscribe( (emp: Employee) => {
      this.employee = emp;
    });
  }

  public newTherapySimple(patientEncounter: PatientEncounter): TherapySimple {
    this.entity = {};

    this.entity.author = { internalId: this.employee.internalId, entityName: 'com.phi.entities.role.Employee'};
    this.entity.patientEncounter = { internalId: patientEncounter.internalId, entityName: 'com.phi.entities.act.PatientEncounter' };

    this.conversationActions.put(this._entityName, this.entity);

    return this.entity;
  }

}
