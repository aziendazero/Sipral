import { Injectable, Injector } from '@angular/core';
import { select } from '@angular-redux/store';

import { BaseActionService } from './base-action.service';
import { ServiceDeliveryLocation } from '../entities/role';
import { Datamodel } from '../datamodel/datamodel';
import { environment } from '../../../environments/environment';

@Injectable()
export class ServiceDeliveryLocationActionService extends BaseActionService<ServiceDeliveryLocation> {

  @select(['conversation', 'ServiceDeliveryLocationList']) ServiceDeliveryLocationList$;
  ServiceDeliveryLocationList: Datamodel;

  constructor(injectorz: Injector) {
    super(injectorz);
    this.entityName = 'ServiceDeliveryLocation';
    this.entityUrl = 'servicedeliverylocations';

    this.ServiceDeliveryLocationList$.subscribe((res) => this.ServiceDeliveryLocationList = res);
  }

  public loadUds(): Promise<Datamodel> {
    // if (!this.ServiceDeliveryLocationList) {
      this.cleanRestrictions();
      this.readPageSize = 0;

      this.select.push('name.giv');
      this.select.push('name.fam');
      this.select.push('parent.internalId');
      this.select.push('parent.name.giv');
      this.select.push('waitingListSupported'); // VCO
      this.select.push('parent.waitingListSupported'); // OTHER
      if (environment.ambulatoryCalendar.loadIsPsychooncology) {
        this.select.push('psychoOncology'); // VCO
      }
      this.select.push('nurseVisibility');//VCO
      this.select.push('parent.nurseVisibility');//VCO
      this.select.push('g2Strt0');
      this.select.push('parent.intActSupported');
      this.select.push('parent.area.code');
      this.select.push('ordering');

      this.equal['code.code'] = 'UD';
      this.equal['isActive'] = true;

      this.orderBy['parent.name.giv'] = 'ascending';
      this.orderBy['name.giv'] = 'ascending';

      return this.read();
  }

  filterByParent(id: number): Array<ServiceDeliveryLocation> {
    if (this.ServiceDeliveryLocationList && this.ServiceDeliveryLocationList.entities) {
      return this.ServiceDeliveryLocationList.entities.filter((sdl: ServiceDeliveryLocation) => sdl.parent.internalId === id);
    }
  }

  loadTree(treeType, id = null): Promise<any> {

    let operation = this.apiUrl + 'resource/rest/tree/hbs/getTree';
    if (treeType === 'employeeManager') {
      operation = this.apiUrl + 'resource/rest/tree/hbs/getTree4CurrentEmployeeRole';
    } else if (treeType === 'codeValueRole') {
      operation = this.apiUrl + 'resource/rest/tree/hbs/getTree4CodeValueRole';
    }

    let body;
    // if (treeType == 'login') { // login not used
    //   body = 'selectedRole=' + role;
    // } else {
      if (!id) {
        body = 'levelOfDepth=3';
      } else {
        body = 'parentId=' + id + '&levelOfDepth=1';
      }
    // }

    return this.httpService.fetch(operation + '?cid=' + this.cid,
      {
        method: 'POST',
        body: body,
        headers: new Headers({'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'}),
        credentials: 'include'
      })
      .then(response => response.json())
      .then(hbsTree => {
        return hbsTree;
      });
  }
}
