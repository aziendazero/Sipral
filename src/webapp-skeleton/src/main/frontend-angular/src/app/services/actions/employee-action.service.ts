import { Injectable, Injector } from '@angular/core';

import { BaseActionService } from './base-action.service';
import { Employee } from '../entities/role';
import { Datamodel } from '../datamodel/datamodel';
import { SelectItem } from '../datamodel/select-item';

@Injectable()
export class EmployeeActionService extends BaseActionService<Employee> {

  constructor(injectorz: Injector) {
    super(injectorz);
    this.entityName = 'Employee';
    this.entityUrl = 'employees';
  }

  changePassword(oldPassword: string, newPassword: string, checkPassword: string): Promise<any> {
    return this.httpService.fetch(this.apiUrl + this.restBaseUrl + this.entityUrl + '/password',
      {
        method: 'PUT',
        body: 'newPassword=' + newPassword + '&oldPassword=' + oldPassword + '&checkPassword=' + checkPassword,
        headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},
        credentials: 'include'
      }
    );
  }

  async getByEmployeeRoleCode(code: string): Promise<Array<SelectItem>> {

    this.cleanRestrictions();
    this.readPageSize = 0;
    this.select.push('name.fam');
    this.select.push('name.giv');
    this.equal['isActive'] = true;
    this.equal['employeeRole.code.code'] = code;
    // this.employeeAction.setDistinct(true);
    const dm: Datamodel = await this.read();
    if (dm && dm.entities) {
      return dm.entities.map((therapist) => (
        {value: therapist.internalId, label: therapist.name.fam + ' ' + therapist.name.giv}
      ));
    }
  }

}
