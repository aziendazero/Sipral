import { Injectable, Inject } from '@angular/core';
import { select } from '@angular-redux/store';
import { Router } from '@angular/router';
import { Datamodel } from '../services/datamodel/datamodel';
import { encodeValue } from '../services/actions/base-action.service';
import { ConversationActions } from '../actions/conversation.actions';
import { reviver } from '../services/converters/any.converter';
import { HttpService } from '../services/http.service';
import { environment } from 'environments/environment';
import { DictionaryManager } from 'app/services';

export class DashboardBaseActionEvent {
  public static INIT = 'INIT';
  public static REFRESH = 'REFRESH';

  public static PRINT_REPORT = 'PRINT_REPORT';
}

@Injectable()
export class DashboardBaseAction {

  protected static INIT = '/init';
  protected static REFRESH = '/refresh';
  protected static PRINT_REPORT = '/printReport';

  protected static GET = 'GET';
  protected static POST = 'POST';

  @select(['process', 'cid']) cid$;
  cid: any;

  restBaseUrl = 'resource/rest/';

  protected dashboardUrl: string = null;
  protected dashboardName: string = null;

  protected initialized = false;

  // Read restrictions

  public select: Array<string> = [];
  public equal: any = {};
  public greaterEqual: any = {};
  public greater: any = {};
  public lessEqual: any = {};
  public like: any = {};
  public less: any = {};
  public notEqual: any = {};
  public isNull: any = {};
  public orderBy: any = {};

  constructor(@Inject('apiUrl') protected apiUrl,
              protected conversationActions: ConversationActions,
              protected dictionaryManager: DictionaryManager,
              protected router: Router,
              protected httpService: HttpService) {
    this.cid$.subscribe((res) => this.cid = res);
  }
  
  public initOrRefresh(): Promise<Datamodel> {
	if (!this.initialized) {
    	return this.init();
    } else {
    	return this.refresh();
    }
  }

  public init(): Promise<Datamodel> {

    let restUrl: string = this.restBaseUrl + this.dashboardUrl + DashboardBaseAction.INIT;

    const promise = this.execute(restUrl, DashboardBaseActionEvent.INIT);
	this.initialized = true;
	return promise;
  }

  public refresh(): Promise<Datamodel> {

    let restUrl: string = this.restBaseUrl + this.dashboardUrl + DashboardBaseAction.REFRESH;

    return this.execute(restUrl, DashboardBaseActionEvent.REFRESH);

  }


  public printReport(reportUrl: string): void {

    let restUrl: string = this.restBaseUrl + this.dashboardUrl + DashboardBaseAction.PRINT_REPORT;

    this.execute(restUrl, DashboardBaseActionEvent.PRINT_REPORT, reportUrl);

  }

  private execute(restUrl: string, onCompleteEventType: string, reportUrl: string = null): Promise<Datamodel> {

    if (onCompleteEventType === DashboardBaseActionEvent.INIT || onCompleteEventType === DashboardBaseActionEvent.REFRESH) {
      this.cleanResults();
    }

    let matrixParam = ''; // example: ';name.fam;name.giv;birthTime;name.fam=ad;name.giv=ad'
    for (let i = 0; i < this.select.length; i++) {
      matrixParam += this.select[i] + ';';
    }

    for (let key in this.equal) {
      if (this.equal.hasOwnProperty(key)) {
        let value = this.equal[key];
        if (value instanceof Array) {
          for (let j = 0; j < value.length; j++) {

            if (value[j] && value[j] !== '') {
              matrixParam += key + '=' + encodeValue(value[j]) + ';';
            }
          }
        } else {
        if (value !== null && value !== '') {
          matrixParam += key + '=' + encodeValue(value) + ';';
        }
        }
      }
    }

    for (let key in this.like) {
      if (this.like.hasOwnProperty(key)) {
        let value = this.like[key];
        if (value && value !== '') {
          matrixParam += key + '=~' + encodeValue(value) + ';';
        }
      }
    }

    for (let key in this.orderBy) {
      if (this.orderBy.hasOwnProperty(key)) {
        let value = this.orderBy[key];
        if (value && value !== '') {
          matrixParam += key + '=' + encodeValue(value) + ';';
        }
      }
    }

    if (matrixParam === '') {
      matrixParam = 'all';
    }

    let url: string;

    if (onCompleteEventType === DashboardBaseActionEvent.INIT || onCompleteEventType === DashboardBaseActionEvent.REFRESH) {
      url = this.buildUrl(restUrl + '/' + matrixParam + '/1');
    } else if (onCompleteEventType === DashboardBaseActionEvent.PRINT_REPORT) {
      url = this.buildUrl(restUrl + '/' + matrixParam);
    }

    return this.httpService.fetch(this.apiUrl + url, { credentials: 'include' })
      .then(response => response.text())
      .then(text => {
        if (onCompleteEventType === DashboardBaseActionEvent.INIT || onCompleteEventType === DashboardBaseActionEvent.REFRESH) {

          const rawObject = JSON.parse(text, reviver);

          let dm: Datamodel = null;
          if (rawObject.main.entities == null) {
            // delete this.conversation[this.dashboardName + 'List'];
            this.conversationActions.remove(this.dashboardName + 'List');
          } else {
            dm = new Datamodel(rawObject.main.entities, this.httpService);
            dm.headers = rawObject.main.headers;
            dm.nextUrl = rawObject.main.next;
            dm.prevUrl = rawObject.main.prev;
            // this.conversation[this.dashboardName + 'List'] = dm;
            this.conversationActions.put(this.dashboardName + 'List', dm);
          }

          if (rawObject.additional != null) {
            if (!dm) {
              dm = new Datamodel([]);
            }
            dm.additional = rawObject.additional;
            for (let property in rawObject.additional) {
              if (rawObject.additional.hasOwnProperty(property)) {
                // this.conversation[this.dashboardName + 'Additional' + property] = rawObject.additional[property];
                this.conversationActions.put(this.dashboardName + 'Additional' + property, rawObject.additional[property]);
              }
            }
          }

          if (rawObject.dictionaries != null) {
            for (let name in rawObject.dictionaries) {
              this.dictionaryManager.domains[name] = rawObject.dictionaries[name];
            }
          }

          return dm;

      } else if (onCompleteEventType === DashboardBaseActionEvent.PRINT_REPORT) {
        if (environment.reportInNewTab) {
          window.open(this.apiUrl + this.buildUrl(reportUrl));
        } else {
          this.router.navigate([{outlets: {popup: ['iframe', this.apiUrl + this.buildUrl(reportUrl)]}}], { queryParamsHandling: 'preserve' });
        }
      }
    })
  .catch(error => {
    console.error('Error ' + this.dashboardName + 'Action Operation: GET ' + url + ' ' + error.message);
    throw error;
  });

  // restService.addEventListener(FaultEvent.FAULT, errorHandler);
  //
  // restService.send();
}

//   protected function errorHandler(event:FaultEvent):void {
//
//   let restService:PhiHTTPService = event.currentTarget.httpService as PhiHTTPService;
//   let operation:string = '';
//
//   if (restService!=null) {
//     operation = 'Operation: ' + restService.method + ' ' + restService.url + ' ' + restService.headers['x-method-override'];
//   }
//   let error:string = 'Error: ' + event.fault;
//
//   logger.log(dashboardName + 'Action', operation, error);
//   dispatchEvent(new ActionEvent(ActionEvent.ERROR, operation + ' ' + error));
// }

  public buildUrl(url: string): string {

    // url += ';jsessionid=' + this.apiService.sid;

  if (url.indexOf('?') >= 0) {
    url += '&';
  } else {
    url += '?';
  }

  url += 'cid=' + this.cid;
  url += '&_=' + new Date().getTime();

  return url;
}

  /**
   * Clean read restriction, to execute a new read
   */
  public cleanRestrictions(): void {
    this.select = [];

    this.equal = {};
    this.greater = {};
    this.greaterEqual = {};
    this.less = {};
    this.lessEqual = {};
    this.notEqual = {};
    this.like = {};
    this.isNull = {};
    this.orderBy = {};
  }

  public cleanResults(): void {
    // this.conversation[this.dashboardName + 'List'] = null;
    this.conversationActions.remove(this.dashboardName + 'List');
  }

}
