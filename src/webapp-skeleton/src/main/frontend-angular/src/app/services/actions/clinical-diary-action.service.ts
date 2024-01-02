import { Injectable, Injector } from '@angular/core';
import { select } from '@angular-redux/store';

import { BaseActionService } from './base-action.service';
import { ClinicalDiary, PatientEncounter, LEPExecution } from '../entities/act';
import { Employee } from '../entities/role';
import { Config } from '../../store/config.reducer';

@Injectable()
export class ClinicalDiaryActionService extends BaseActionService<ClinicalDiary> {

  @select(['config']) config$;

  employee: Employee;
  employeeRoleCode: string;

  constructor(injectorz: Injector) {
    super(injectorz);
    this.entityName = 'ClinicalDiary';
    this.entityUrl = 'clinicaldiarys';

    this.config$.subscribe( (cfg: Config) => {
      this.employee = cfg.employee;
      this.employeeRoleCode = cfg.employeeRoleCode;
    });
  }

  newClinicalDiary(patientEncounter: PatientEncounter, lEPExecution: LEPExecution, serviceDeliveryLocationId: number): ClinicalDiary {
    const now = new Date();
    now.setSeconds(0);

    this.entity = {};
    this.entity.availabilityTime = now;
    this.entity.code = {codeSystemName: 'PHIDIC', domainName: 'AnnotationKind', code: 'CLINICALDIARY'};
    this.entity.levelCode = {codeSystemName: 'PHIDIC', domainName: 'ClinicalDiaryClassification', code: 'ObjectiveEvaluation'};
    this.entity.text; //  = activity.title + "\n" + activity.subtitle + "\n" + note;
    this.entity.patientEncounter = { internalId: patientEncounter.internalId, entityName: 'com.phi.entities.act.PatientEncounter' };
    this.entity.execution = lEPExecution;
    this.entity.author = { internalId: this.employee.internalId, entityName: 'com.phi.entities.role.Employee' };
    this.entity.authorRole = { codeSystemName: 'ROLES',  domainName: 'EmployeeFunction', code: this.employeeRoleCode };
    this.entity.serviceDeliveryLocation = { internalId: serviceDeliveryLocationId, entityName: 'com.phi.entities.role.ServiceDeliveryLocation' };

    this.conversationActions.put(this._entityName, this.entity);
    return this.entity;
  }

}
