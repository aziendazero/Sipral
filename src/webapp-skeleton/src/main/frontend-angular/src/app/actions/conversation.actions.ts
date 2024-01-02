/**
 * Created by Alex on 23/06/2017.
 */
import {Injectable} from '@angular/core';
import {NgRedux} from '@angular-redux/store';
import {IAppState} from '../store/index';

@Injectable()
export class ConversationActions {
  static CONVERSATION_PUT = 'CONVERSATION_PUT';
  static CONVERSATION_REMOVE = 'CONVERSATION_REMOVE';
  static CONVERSATION_CLEAN = 'CONVERSATION_CLEAN';

  constructor(private ngRedux: NgRedux<IAppState>) {}

  /**
   * Put BaseEntity into Conversation
   * @returns {Function}
   */
  put(key: string, value: any) {
    this.ngRedux.dispatch({
      type: ConversationActions.CONVERSATION_PUT,
      payload: {key, value}
    });
  }

  remove(key: string) {
    this.ngRedux.dispatch({
      type: ConversationActions.CONVERSATION_REMOVE,
      payload: {key}
    });
  }

  clean() {
    this.ngRedux.dispatch({
      type: ConversationActions.CONVERSATION_CLEAN,
      payload: null
    });
  }

  unProxy(rawObject: any, entity: any): any {
    // FIXME UNPROXY OF NESTED RESOURCESSSSS
    // IF PRESCRIPTION HAS 2 same medicne -> error!!!
    if (typeof entity.internalId !== 'undefined' &&
      typeof entity.isActive === 'undefined') {

      let unproxied: any = this.findProxy(rawObject, entity.internalId, entity.entityName);
      if (unproxied != null) {
        entity = unproxied;
      }
    }
    return entity;
  }

  //recursively search for unproxied object
  findProxy(theObject, internalId: number, entityName: string): any {

    let result = null;
    if (theObject instanceof Array) {
      for (var i = 0; i < this.findProxy.length; i++) {
        result = this.findProxy(theObject[i], internalId, entityName);
      }

    } else {
      for (var prop in theObject) {
        if (result != null)
          break;

        if (prop == 'proxyString') {
          if (theObject[prop].internalId == internalId && theObject[prop].entityName == entityName) {
            return theObject;
          }
        }
        if (theObject[prop] instanceof Object || theObject[prop] instanceof Array)
          result = this.findProxy(theObject[prop], internalId, entityName);
      }
    }
    return result;
  }
}
