import { Inject, Injectable } from '@angular/core';
import { HttpService } from './http.service';
import { ProcessActions } from '../actions/process.actions';
import { BannerActions } from '../actions/banner.action';
import { ConfigActions } from '../actions/config.action';
import { ConversationActions } from '../actions/conversation.actions';
import { logError } from './error/global-error-handler';
import { AuthGuard } from './auth-guard.service';

@Injectable()
export class ProcessManager {

  constructor(
    @Inject('apiUrl') private apiUrl,
    private httpService: HttpService,
    private ProcessAction: ProcessActions,
    private BannerActions: BannerActions,
    private ConfigActions: ConfigActions,
    private conversationActions: ConversationActions,
    private authGuard: AuthGuard
    ) { }


  processlist() {

    let cid = sessionStorage['cid'];

    return this.httpService.fetch(this.apiUrl + 'resource/rest/processmanager/processlist'
      + (cid ? '?cid=' + cid : ''), {credentials: 'include'})
      .then(response => response.json())
      .then(json => {
        // tslint:disable-next-line:radix
        this.ProcessAction.putProcesses(json['processList'], parseInt(json['cid']), json['version']);
        if (json.banner) {
          this.BannerActions.put(json.banner.banner);
        }
        this.ConfigActions.putFlashVars(json['flashVars']);

        if (json.banner && json.banner.bannerEntities) {
          for (let key in json.banner.bannerEntities) {
            if (json.banner.bannerEntities.hasOwnProperty(key)) {
              json.banner.bannerEntities[key] = this.conversationActions.unProxy(json.banner, json.banner.bannerEntities[key]);
              this.conversationActions.put(key, json.banner.bannerEntities[key]);
            }
          }
        }
      })
      .catch(error => {
        console.error('Error loading process list!!!!', sessionStorage['cid'], document.cookie);
        logError('Error loading process list: RELOGIN NEEDED!', sessionStorage['cid']);
        this.authGuard.logout();
      });
  }

  processsecurity(id: number = null): Promise<Array<any>> {
    let cid = sessionStorage['cid'];
    return this.httpService.fetch(this.apiUrl + 'resource/rest/processmanager/processsecurity'
      + (cid ? '?cid=' + cid : '') + (id ? '&id=' + id : ''), {credentials: 'include'})
      .then(response => response.json())
      .then(json => {
        return json;
      })
      .catch(error => {
        throw new Error('Error getting processes security ' + error.message);
      });
  }

}
