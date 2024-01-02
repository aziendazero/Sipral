/**
 * Created by Daniele on 27/06/2017.
 */
import { Injectable, Injector } from '@angular/core';
import { select } from '@angular-redux/store';
import { BaseActionService } from './base-action.service';
import { SubstanceAdministration } from '../entities/act';
import { Employee } from '../entities/role';
import { CodeValue } from '../entities/data-types';
import { CodeValueProxy } from '../proxyes/code-value-proxy';
import { DateFormatPipe } from '../converters/date-format.pipe';


@Injectable()
export class SubstanceAdministrationActionService extends BaseActionService<SubstanceAdministration> {

  @select(['config', 'employee']) employee$;
  employee: Employee;

  @select(['config', 'employeeRoleCode']) employeeRoleCode$;
  employeeRoleCode: string;

  constructor(injectorz: Injector, private datePipe: DateFormatPipe) {
    super(injectorz);
    this.entityName = 'SubstanceAdministration';
    this.entityUrl = 'substanceadministrations';
    this.employee$.subscribe(res => this.employee = res);
    this.employeeRoleCode$.subscribe(res => this.employeeRoleCode = res);
  }

  erogate(erogationDate: Date, statusCode: CodeValue | CodeValueProxy, continuous: boolean,
          statusDetailsCode: CodeValue = null, note: string = null): Promise<SubstanceAdministration> {
    this.entity.text = {string: note};
    this.entity.statusCode = statusCode;
    this.entity.statusDetailsCode = statusDetailsCode;

    this.entity.availabilityTime = new Date();

    let endingContinuous = false;
    if (continuous) { //continuous
      if (!this.entity.administratedDate || !this.entity.administratedDate.low) {
        this.entity.administratedDate = { low: erogationDate };
      } else {
        this.entity.administratedDate.high = erogationDate;
        endingContinuous = true;
      }
    } else {
      this.entity.administratedDate = {low: erogationDate, high: erogationDate};
    }

    if (!endingContinuous) {
      this.entity.author = {internalId: this.employee.internalId, entityName: 'com.phi.entities.role.Employee'}; // PROXY
      this.entity.authorRole = {codeSystemName: 'ROLES', domainName: 'EmployeeFunction', code: this.employeeRoleCode};
    } else {
      this.entity.stopAuthor = {internalId: this.employee.internalId, entityName: 'com.phi.entities.role.Employee'}; // PROXY
      this.entity.stopAuthorRole = {codeSystemName: 'ROLES', domainName: 'EmployeeFunction', code: this.employeeRoleCode};
    }

    return this.update();
  }

  cancel(deleteNote: string, status: string): Promise<SubstanceAdministration> {

    this.entity.cancellationDate = new Date();

    this.entity.cancellationNote = deleteNote + ' [' + this.employee.name.fam + ' ' + this.employee.name.giv + ', '
      + this.datePipe.transform(this.entity.administratedDate.low, 'short') + ', ' + status;

    if (this.entity.text.string) {
      this.entity.cancellationNote += ', ' + this.entity.text.string;
    }

    this.entity.cancellationNote += ']';

    this.entity.cancelledBy = {internalId: this.employee.internalId, entityName: 'com.phi.entities.role.Employee'}; // PROXY
    this.entity.cancelledByRole = {codeSystemName: 'ROLES', domainName: 'EmployeeFunction', code: this.employeeRoleCode};

    // asNeeded prescription
    if (this.entity.plannedDate === null) {
      this.entity.statusCode = {codeSystemName: 'PHIDIC', domainName: 'AdministrationStatus', code: 'CANCELLED'};
    } else {
      this.entity.statusCode = {codeSystemName: 'PHIDIC', domainName: 'AdministrationStatus', code: 'PLANNED'};
      this.entity.statusDetailsCode = null;
      this.entity.administratedDate.low = null;
      this.entity.administratedDate.high = null;
      this.entity.availabilityTime = null;
      this.entity.text.string = null;
      this.entity.author = null;
      this.entity.authorRole = null;
    }

    return this.update();
  }
}
