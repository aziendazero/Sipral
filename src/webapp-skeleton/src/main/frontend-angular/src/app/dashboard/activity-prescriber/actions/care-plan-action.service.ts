import { Injectable, Inject } from '@angular/core';
import { Router } from '@angular/router';
import { DashboardBaseAction } from '../../dashboard-base-action.service';
import { ConversationActions } from '../../../actions/conversation.actions';
import { HttpService } from '../../../services/http.service';
import { reviver } from '../../../services/converters/any.converter';
import { DictionaryManager } from 'app/services';


@Injectable()
export class CarePlanAction extends DashboardBaseAction {

  protected static DIAGNOSES_COUNT = '/diagnosesCount';

  constructor(@Inject('apiUrl') protected apiUrl,
              protected conversationActions: ConversationActions,
              protected dictionaryManager: DictionaryManager,
              protected router: Router,
              protected httpService: HttpService) {
    super(apiUrl, conversationActions, dictionaryManager, router, httpService);
    this.dashboardName = 'CarePlan';
    this.dashboardUrl = 'careplan';
  }

  /**
   * Get NANDAEtiologicFactors, NANDASymptoms and NANDARiskFactors di CodeVlalueNanada.id
   */
  getDiagnosesCount(encounterId: number): Promise<number> {

    return this.httpService.fetch(this.buildUrl(this.apiUrl + this.restBaseUrl + this.dashboardUrl + CarePlanAction.DIAGNOSES_COUNT + '/' + encounterId),
      {
        method: 'GET',
        headers: {'Content-Type': 'application/json'},
        credentials: 'include'
      }
      )
      .then(response => response.text())
      .then(text => JSON.parse(text, reviver) )
      .then(entity => {
        return entity.count;
      })
      .catch(error => {
        console.error('Error getDiagnosesCount ' + encounterId + ' ' + error.message);
        throw error;
      });
  }

}
