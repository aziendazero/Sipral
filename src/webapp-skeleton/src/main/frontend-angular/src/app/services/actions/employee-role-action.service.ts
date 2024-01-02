import { Injectable, Injector } from '@angular/core';
import { NgRedux } from '@angular-redux/store';

import { BaseActionService } from './base-action.service';
import { EmployeeRole } from '../entities/base-entity/employee-role';

@Injectable()
export class EmployeeRoleActionService extends BaseActionService<EmployeeRole> {

  private static FAVORITESDL: string = '/favoriteSdl';
  private static MOVESDL: string = '/moveSdl';
  private static SAVEFAVORITESDL: string = '/saveFavoriteSdl';

  constructor(injectorz: Injector, private ngRedux: NgRedux<any>) {
    super(injectorz);
    this.entityName = 'EmployeeRole';
    this.entityUrl = 'employeeroles';
  }

  public getFavoriteSdl(): Promise<Array<number>> {

    const employeeRoleId: Array<any> = this.ngRedux.getState().config.employeeRoleId;

    const parameters: string = '/' + employeeRoleId + EmployeeRoleActionService.FAVORITESDL;

    return this.httpService.fetch(this.apiUrl + this.restBaseUrl + this.entityUrl + parameters + ';jsessionid=' + this.sid + '?cid=' + this.cid,
      {
        method: 'GET',
        credentials: 'include'
      })
      .then(response => response.json())
      .then(json =>
        json
      )
      .catch(error => {
        console.error('Error getting favoriteSdl for EmployeeRole ' + employeeRoleId + ' ' + error.message);
        throw error;
      });
  }

  public moveSdl(sdlId: number, forward: boolean, sdl2Id: number): Promise<any> {

    const employeeRoleId: Array<any> = this.ngRedux.getState().config.employeeRoleId;

    const parameters: string = '/' + employeeRoleId + EmployeeRoleActionService.MOVESDL + '/' + sdlId + '/' + forward + '/' + sdl2Id;

    return this.httpService.fetch(this.apiUrl + this.restBaseUrl + this.entityUrl + parameters + ';jsessionid=' + this.sid + '?cid=' + this.cid,
      {
        method: 'POST',
        credentials: 'include'
      })
      .then(response => {
        return response;
      })
      .catch(error => {
        console.error('Error moving sdl ' + sdlId + ' ' + error.message);
        throw error;
      });
  }

    public saveFavoriteSdl(sdlId: Array<number>, isAgenda: boolean): Promise<any> {

    const employeeRoleId: Array<any> = this.ngRedux.getState().config.employeeRoleId;

    const parameters: string = '/' + employeeRoleId + EmployeeRoleActionService.SAVEFAVORITESDL + '/' + sdlId.join() +'/' + isAgenda;

    return this.httpService.fetch(this.apiUrl + this.restBaseUrl + this.entityUrl + parameters + ';jsessionid=' + this.sid + '?cid=' + this.cid,
      {
        method: 'POST',
        credentials: 'include'
      })
      .then(response => {
        return response;
      })
      .catch(error => {
        console.error('Error saving selected sdl ' + sdlId.join() + ' ' + error.message);
        throw error;
      });
  }

}
