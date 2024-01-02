/**
 * Created by Alex on 23/06/2017.
 */
import { Injectable, Inject } from '@angular/core';
import { NgRedux, select } from '@angular-redux/store';
import { IAppState } from '../store/index';
import { HttpService } from '../services/http.service';
import { ConfigActions } from './config.action';
import { Router } from '@angular/router';
import { environment } from 'environments/environment';

export class ReloginResponse {
  reloginUrl?: string;
  isLockedNode?: boolean
};

@Injectable()
export class ProcessActions {
  static PROCESS_GET = 'PROCESS-GET';
  static PROCESS_GET_FORM = 'PROCESS-GET-FORM';
  static PROCESS_END = 'PROCESS-END';
  static PROCESS_NEW_CID = 'PROCESS-NEW-CID';
  static PROCESS_CURRENT_PROCESS = 'PROCESS-CURRENT-PROCESS';
  static PROCESS_DASHBOARD = 'PROCESS-DASHBOARD';
  static PROCESS_CLEAN = 'PROCESS-CLEAN';
  static PROCESS_SET_LANG = 'PROCESS_SET_LANG';

  loginRegexp = /common\/jsp\/login\.seam/;
  viewStateRegexp = /id=\"javax\.faces\.ViewState\" value="([^\"]+)"/;
  langRegexp = /lang=\"([^\"]+)\"/;

  @select(['process', 'cid']) cid$;
  cid: number;
  @select(['config', 'sid']) sid$;
  sid: number;
  @select(['config', 'menuVisible']) menuVisible$;
  menuVisible: boolean;
  @select(['process', 'dashboard']) targetDashboard$;
  targetDashboard;

  constructor(
    private ngRedux: NgRedux<IAppState>,
    private configActions: ConfigActions,
    private router: Router,
    @Inject('apiUrl') protected apiUrl,
    private httpService: HttpService) {
      this.cid$.subscribe(res => this.cid = res);
      this.sid$.subscribe(res => this.sid = res);
      this.menuVisible$.subscribe(res => this.menuVisible = res);
      this.targetDashboard$.subscribe((res) => this.targetDashboard = res);
    }

  /**
   * Get list of all processes
   * @returns {Function}
   */
 /*
  getProcesses() {
    return this.httpService.fetch(this.apiUrl + 'resource/rest/processmanager/processlist', {credentials: 'include'})
      .then(response => response.json())
      .then(json => {
        //this.cid = parseInt(json['cid']);
        this.putProcesses(json['processList'], parseInt(json['cid']));
      })
      .catch(error => {
        throw new Error('Error getting processes list ' + error.message);
      });
  }
*/
  putProcesses(processList, cid: number, version) {
    this.ngRedux.dispatch({
      type: ProcessActions.PROCESS_GET,
      payload: {processList: processList, cid: cid, version: version}
    });
  }

  /**
   * Get a jsf form
   * @param cid conversation id
   * @returns {Promise.<TResult>}
   */
  getForm(): Promise<any> {
    let url = this.apiUrl + 'home-jsf.seam';
    if (this.cid != null) {
      url += '?cid=' + this.cid;
    }
    return this.httpService.fetch(url, {credentials: 'include'}) // TODO: do a httpService.fetch without loader
      .then(response => response.text())
      .then(html => {
        let loginMatch = this.loginRegexp.exec(html);
        if (loginMatch) {
          // Fixes ie 11 session expired -> launch process: javax.servlet.ServletException:
          // java.lang.IllegalArgumentException: no file extension in view id: /
          return Promise.reject(HttpService.SESSION_EXPIRED);
        }
        let match = this.viewStateRegexp.exec(html);
        let viewState = match[1];
        let lang = null;
        match = this.langRegexp.exec(html);
        if (match) {
          lang = match[1];
        }

        this.ngRedux.dispatch({
          type: ProcessActions.PROCESS_GET_FORM,
          payload: {viewState, lang}
        });
        return Promise.resolve({viewState, lang});
      })
      .catch(error => {
        console.error('Error getting form ' + url + ' ' + error.message);
        throw error;
      });
  }

  /**
   * Starts a process
   * @param {String} process
   */
  startProcess(process: String) {
    if (this.menuVisible) {
      this.configActions.toggleMenu();
    }
    this.setDashboard(this.router.url);
    this.router.navigate(['/process', process]);
  }

  /**
   * public end process method
   * End a process
   */
  public endProcess(process): Promise<ReloginResponse> {
    return this.httpService.fetch(this.apiUrl + 'resource/rest/processmanager/' + process.split('/').join('.')
     // TODO: fix encoding %2F does not work!
      + ';jsessionid=' + this.sid + '?conversationId=' + this.cid ,
      {
        method: 'DELETE',
        headers: {'Content-Type': 'application/json'},
        credentials: 'include'
      }
    )
      .then(response => response.json())
      .then((resp: any) => {
        let newCid = parseInt(resp.cid);

        if (resp.reloginUrl) {
          let absoluteReloginUrl = resp.reloginUrl;
          if (absoluteReloginUrl.indexOf('/') === 0) {
            absoluteReloginUrl = absoluteReloginUrl.substring(1, absoluteReloginUrl.length);
          }

          return {
            reloginUrl: this.apiUrl + absoluteReloginUrl,
            isLockedNode: (absoluteReloginUrl.indexOf('isLockedNode') > -1)
          };
        }

        this.afterPocessEnded();

        this.ngRedux.dispatch({
          type: ProcessActions.PROCESS_END,
          payload: {cid: newCid}
        });
        return {};
      })
      .catch(error => {
        console.error('Error ending process ' + process + ' ' + error.message);
        throw error;
      });
  }

  public afterPocessEnded() {
    if (this.targetDashboard) {
      this.router.navigateByUrl(this.targetDashboard);
      this.setDashboard(null);
    } else {
      this.router.navigate([environment.home]);
    }
  }

  /**
   * public Set new cid
   */
  public newCid(cid: number) {
    this.ngRedux.dispatch({
      type: ProcessActions.PROCESS_NEW_CID,
      payload: {cid}
    });
  }

  public setCurrentProcess(process: string) {
    this.ngRedux.dispatch({
      type: ProcessActions.PROCESS_CURRENT_PROCESS,
      payload: {process}
    });
  }

  public setDashboard(dashboard: string) {
    this.ngRedux.dispatch({
      type: ProcessActions.PROCESS_DASHBOARD,
      payload: {dashboard}
    });
  }

  public setLanguage(lang: string) {
    this.ngRedux.dispatch({
      type: ProcessActions.PROCESS_SET_LANG,
      payload: {lang}
    });
  }

  public clean() {
    this.ngRedux.dispatch({
      type: ProcessActions.PROCESS_CLEAN,
      payload: null
    });
  }
}
