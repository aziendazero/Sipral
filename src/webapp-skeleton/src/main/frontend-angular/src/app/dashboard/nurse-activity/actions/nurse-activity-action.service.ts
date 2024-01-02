import { Injectable, Inject } from '@angular/core';
import { Router } from '@angular/router';
import { DashboardBaseAction } from '../../dashboard-base-action.service';
import { ConversationActions } from '../../../actions/conversation.actions';
import { reviver } from '../../../services/converters/any.converter';
import { HttpService } from '../../../services/http.service';
import { DictionaryManager } from 'app/services';

@Injectable()
export class NurseActivityAction extends DashboardBaseAction {

  static AS_NEEDED_DETAILS: string = '/asNeededDetails';

  private enableLEPTime = false; // TODO parametrize

  constructor(@Inject('apiUrl') protected apiUrl,
              protected conversationActions: ConversationActions,
              protected dictionaryManager: DictionaryManager,
              protected router: Router,
              protected httpService:HttpService) {
    super(apiUrl, conversationActions, dictionaryManager, router, httpService);
    this.dashboardName = 'NurseActivity';
    this.dashboardUrl = 'nurseactivities';
  }

  public getAsNeededDetails(nurseActivity): Promise<any> {

    if (nurseActivity.planneddate == null) {
      // As needed prescription
      return this.httpService.fetch(this.apiUrl + this.restBaseUrl + this.dashboardUrl + NurseActivityAction.AS_NEEDED_DETAILS + '/' + nurseActivity.parent.internalid + '/' + nurseActivity.encounter.internalid,
        {credentials: 'include'})
        .then(response => response.text())
        .then(text => JSON.parse(text, reviver))
        .then(rawObject => {
          this.conversationActions.put('asNeededDetailsData', rawObject);
          return true;
        });

    } else {
      return Promise.resolve(false);
    }
  }

  public printPTVReport() {
    this.equal['calendarReport'] = false;
    this.equal['cartReport'] = false;
    this.equal['enableLEPTime'] = this.enableLEPTime;

    this.printReport('reports/r_erogationsPatient.seam');
  }

  public printCalendarReport() {
    this.equal['calendarReport'] = true;
    this.equal['cartReport'] = false;
    this.equal['enableLEPTime'] = this.enableLEPTime;

    this.printReport('reports/r_erogationsCalendar.seam');
  }

  public printWTVReport() {
    this.equal['calendarReport'] = false;
    this.equal['cartReport'] = false;
    this.equal['enableLEPTime'] = this.enableLEPTime;

    this.printReport('reports/r_erogationsWard.seam');
  }

  public printCartReport() {
    this.equal['calendarReport'] = false;
    this.equal['cartReport'] = true;
    this.equal['enableLEPTime'] = this.enableLEPTime;

    this.printReport('reports/r_cart.seam');
  }

}
