import { Injectable, Inject } from '@angular/core';
import { Router } from '@angular/router';
import { DashboardBaseAction } from '../../dashboard-base-action.service';
import { ConversationActions } from '../../../actions/conversation.actions';
import { HttpService } from '../../../services/http.service';
import {reviver} from '../../../services/converters/any.converter';
import { NgRedux, select } from '@angular-redux/store';
import { DictionaryManager } from 'app/services';

@Injectable()
export class PsAction extends DashboardBaseAction {

  protected static STATUS = '/status';

  // ['RADIOLOGO']
  public static radiologist = ['radio'];

  @select(['config', 'employeeRoleCode']) employeeRoleCode$;
  employeeRoleCode: string;

  constructor(@Inject('apiUrl') protected apiUrl,
              protected conversationActions: ConversationActions,
              protected dictionaryManager: DictionaryManager,
              protected router: Router,
              protected httpService: HttpService) {
    super(apiUrl, conversationActions, dictionaryManager, router, httpService);
    this.dashboardName = 'Ps';
    this.dashboardUrl = 'ps';
    this.employeeRoleCode$.subscribe(res => this.employeeRoleCode = res);
  }

  public isRadiologist = () => PsAction.radiologist.indexOf(this.employeeRoleCode) !== -1;

  /**
   * Change status
   */
  changeStatus(encounterId: number, appointmentId: number, action: string): Promise<any> {

    return this.httpService.fetch(this.buildUrl(this.apiUrl + this.restBaseUrl + this.dashboardUrl + PsAction.STATUS),
      {
        method: 'POST',
        body: 'appointmentId=' + appointmentId + '&action=' + action + '&encounterId=' + encounterId,
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        credentials: 'include'
      })
      .then(response => response.text())
      .then(text => JSON.parse(text, reviver) )
      .catch(error => {
        console.error('Error changeStatus ' + encounterId + ' ' + error.message);
        throw error;
      });
  }
}
