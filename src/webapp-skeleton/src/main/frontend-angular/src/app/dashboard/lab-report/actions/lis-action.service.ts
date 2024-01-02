import { Injectable, Inject } from '@angular/core';
import { Router } from '@angular/router';
import { DashboardBaseAction } from '../../dashboard-base-action.service';
import { ConversationActions } from '../../../actions/conversation.actions';
import { HttpService } from '../../../services/http.service';
import { DictionaryManager } from 'app/services';


@Injectable()
export class LisAction extends DashboardBaseAction {

  constructor(@Inject('apiUrl') protected apiUrl,
              protected conversationActions: ConversationActions,
              protected dictionaryManager: DictionaryManager,
              protected router: Router,
              protected httpService: HttpService) {
    super(apiUrl, conversationActions, dictionaryManager, router, httpService);
    this.dashboardName = 'DettaglioRefertoLab';
    this.dashboardUrl = 'lis';
  }

}
