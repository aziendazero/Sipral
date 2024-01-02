import { Injectable, Inject } from '@angular/core';
import { Router } from '@angular/router';
import { DashboardBaseAction } from '../../dashboard-base-action.service';
import { Datamodel } from '../../../services/datamodel/datamodel';
import { ConversationActions } from '../../../actions/conversation.actions';
import { HttpService } from '../../../services/http.service';
import { reviver } from '../../../services/converters/any.converter';
import { DictionaryManager } from 'app/services';

@Injectable()
export class DrugPrescriberAction extends DashboardBaseAction {

  protected static PRESCRIPTIONSDISCHARGE = '/prescriptiondischarge';
  protected static ADMINISTRATION_HISTORY = '/administrationHistory';

  constructor(
    @Inject('apiUrl') protected apiUrl,
    protected conversationActions: ConversationActions,
    protected dictionaryManager: DictionaryManager,
    protected router: Router,
    protected httpService:HttpService
  ) {
    super(apiUrl, conversationActions, dictionaryManager, router, httpService);
    this.dashboardName = 'Prescription';
    this.dashboardUrl = 'drugprescriber';
  }

  public copyPrescriptionsToPrescriptionDischarges(prescriptionIds: Array<number>, showMedicineName = false): Promise<any> {

    let formData = '';
    for (let id of prescriptionIds) {
      formData = formData.concat('id=', id.toString(), '&');
    }
    formData = formData.concat('showMedicineName=', showMedicineName.toString());

    return this.httpService.fetch(this.apiUrl + this.restBaseUrl + this.dashboardUrl + DrugPrescriberAction.PRESCRIPTIONSDISCHARGE,
      { method: 'POST',
        body: formData,
        headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},
        credentials: 'include'
      })
      .then(response => response.text());
  }

  public getAdministrationHistory(prescriptionId: number): Promise<any> {

    return this.httpService.fetch(this.apiUrl + this.restBaseUrl + this.dashboardUrl + DrugPrescriberAction.ADMINISTRATION_HISTORY + '/' + prescriptionId,
      { credentials: 'include' })
      .then(response => response.text())
      .then(text => JSON.parse(text, reviver) )
      .then(rawObject => {
        this.conversationActions.put('ErogationHistoryList', new Datamodel(rawObject));
      });
  }

}
