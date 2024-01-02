import { Injectable, Injector } from '@angular/core';
import { select } from '@angular-redux/store';

import { BaseActionService } from './base-action.service';
import { LEPExecution } from '../entities/act';
import { Employee } from '../entities/role';
import { CodeValue } from '../entities/data-types';
import { CodeValueProxy } from '../proxyes';
import { DateFormatPipe } from '../converters/date-format.pipe';

@Injectable()
export class LEPExecutionActionService extends BaseActionService<LEPExecution> {

  @select(['config', 'employee']) employee$;
  employee: Employee;

  @select(['config', 'employeeRoleCode']) employeeRoleCode$;
  employeeRoleCode: string;

  constructor(injectorz: Injector, private datePipe: DateFormatPipe) {
    super(injectorz);
    this.entityName = 'LEPExecution';
    this.entityUrl = 'lepexecutions';
    this.employee$.subscribe(res => this.employee = res);
    this.employeeRoleCode$.subscribe(res => this.employeeRoleCode = res);
  }

  newEntity(): LEPExecution {
    const now = new Date();
    now.setSeconds(0);

    let lepExecution: LEPExecution = {};
    lepExecution.author = {internalId: this.employee.internalId, entityName: 'com.phi.entities.role.Employee'};
    lepExecution.authorRole = {codeSystemName: 'ROLES', domainName: 'EmployeeFunction', code: this.employeeRoleCode};
    lepExecution.availabilityTime = now;
    // lepExecution.lepActivity = lepActivity; // WARNING circular reference
    lepExecution.plannedTime = 1;
    lepExecution.plannedDate = now;
    lepExecution.executionTime = 1;
    lepExecution.executionDate = {low: now, high: now};
    lepExecution.statusCode = {codeSystemName: 'PHIDIC', domainName: 'AdministrationStatus', code: 'DONE'};
    lepExecution.availabilityTime = now;

    return lepExecution;
  }

  /**
   * Inject and replace patientEncounter with proxy
   */
  public inject(id: number, additionals: Array<any> = null, loads: Array<any> = null, conversationName = null): Promise<LEPExecution> {
    return super.inject(id, additionals, loads, conversationName).then( () => {
      if (this.entity && this.entity.lepActivity && this.entity.lepActivity.patientEncounter) {
        this.entity.lepActivity.patientEncounter = {
          internalId: this.entity.lepActivity.patientEncounter.internalId,
          entityName: 'com.phi.entities.act.PatientEncounter'
        }
      }
      return this.entity;
    });
  }

  erogate(erogationDate: Date, statusCode: CodeValue | CodeValueProxy = null, executionTime: number = 1,
          statusDetailsCode: CodeValue = null, note: string = null): Promise<LEPExecution> {
    this.entity.note = note;
    this.entity.statusCode = statusCode;
    this.entity.statusDetailsCode = statusDetailsCode;

    this.entity.availabilityTime = new Date();
    this.entity.executionDate = { low: erogationDate, high: erogationDate };

    this.entity.executionTime = executionTime;

    this.entity.author = {internalId: this.employee.internalId, entityName: 'com.phi.entities.role.Employee'}; // PROXY
    this.entity.authorRole = {codeSystemName: 'ROLES', domainName: 'EmployeeFunction', code: this.employeeRoleCode};

    return this.update();
  }


  cancel(deleteNote: string, status: string): Promise<LEPExecution>{

    this.entity.cancellationDate = new Date();

    this.entity.cancellationNote = deleteNote + ' [' + this.employee.name.fam + ' ' + this.employee.name.giv + ', '
      + this.datePipe.transform(this.entity.executionDate.low, 'short') + ', ' + status;

    if (this.entity.note) {
      this.entity.cancellationNote += ', ' + this.entity.note;
    }

    //Fixme implement Objective, see: /DrugAdministrator/src/modules/DrugAdministrator.mxml 1126

    this.entity.cancellationNote += ']';

    this.entity.cancelledBy = {internalId: this.employee.internalId, entityName: 'com.phi.entities.role.Employee'}; // PROXY
    this.entity.cancelledByRole = {codeSystemName: 'ROLES', domainName: 'EmployeeFunction', code: this.employeeRoleCode};

    this.entity.statusCode = {codeSystemName: 'PHIDIC', domainName: 'AdministrationStatus', code: 'PLANNED'};
    this.entity.statusDetailsCode = null;
    this.entity.executionDate.low = null;
    this.entity.executionDate.high = null;
    this.entity.executionTime = null;
    this.entity.availabilityTime = null;
    this.entity.note = null;
    this.entity.author = null;
    this.entity.authorRole = null;

    //Fixme implement Objective, see: /DrugAdministrator/src/modules/DrugAdministrator.mxml 1156

    // reopenObjective

    // reopenActivity

    return this.update();
  }

}
