/**
 * Created by Daniele on 26/06/2017.
 */
import { Inject, Injectable } from '@angular/core';
import { NgRedux, select } from '@angular-redux/store';
import { IAppState } from '../store/index';
import { ConversationActions } from './conversation.actions';
import { HttpService } from '../services/http.service';
import { reviver } from '../services/converters/any.converter';
// import { ViewManager } from '../services/view-manager';

@Injectable()
export class BannerActions {
  static BANNER_PUT = 'BANNER_PUT';
  static BANNER_REMOVE = 'BANNER_REMOVE';

  @select(['process', 'cid']) cid$;
  cid: number;
  @select(['config', 'sid']) sid$;
  sid: number;

  constructor(
    private ngRedux: NgRedux<IAppState>,
    @Inject('apiUrl') protected apiUrl,
    private ConversationActions:ConversationActions,
    private httpService: HttpService,
    // private viewManager: ViewManager
  ) {
    this.cid$.subscribe(res => this.cid = res);
    this.sid$.subscribe(res => this.sid = res);
  }

  /**
   * Put objects into Banner
   * @returns {Function}
   */
  put(banner: any) {
    this.ngRedux.dispatch({
      type: BannerActions.BANNER_PUT,
      payload: banner
    });
  }

  remove() {
    this.ngRedux.dispatch({
      type: BannerActions.BANNER_REMOVE,
      payload: { }
    });
  }

  refresh() {
      return this.httpService.fetch(this.apiUrl + 'resource/rest/banner/'
        + ';jsessionid=' + this.sid + '?conversationId=' + this.cid, {credentials: 'include'}
      )
        .then(response => response.text())
        .then(text => JSON.parse(text, reviver))
        .then(rowObject => {
          this.ngRedux.dispatch({
            type: BannerActions.BANNER_PUT,
            payload: rowObject.banner
          });
          return rowObject;
        })
        .then((rowObject: any) => { // TODO: do multiply remove and inject?
          //reset
          this.ConversationActions.remove('Patient');
          this.ConversationActions.remove('PatientEncounter');
          this.ConversationActions.remove('Appointment');
          this.ConversationActions.remove('AppointmentGrouper');

          //populate with new objects
          if (rowObject && rowObject.hasOwnProperty('bannerEntities') && Object.keys(rowObject.bannerEntities).length > 0) {
            for(let key in rowObject.bannerEntities) {
              let ent = this.ConversationActions.unProxy(rowObject, rowObject.bannerEntities[key]);
              this.ConversationActions.put(key, ent);
            }
          }
        })
        .catch(error => {
          console.error('Error refreshing banner ' + error.message);
          throw error;
        });
  }
}
