import { Injectable, Injector } from '@angular/core';
import { select } from '@angular-redux/store';
// import { getIn } from '@angular-redux/store/lib/src/utils/get-in';
import { BannerActions } from '../../actions/banner.action';
import { Datamodel } from '../datamodel/datamodel';
import { BaseEntity } from '../entities/base-entity';
import { dateToString } from '../converters/date.converter';
import { anyToString, reviver } from '../converters/any.converter';
import { ConversationActions } from '../../actions/conversation.actions';
import { HttpService } from '../http.service';
import { ViewManager } from '../view-manager';
import { TranslateService } from '@ngx-translate/core';
import { environment } from 'environments/environment';


@Injectable()
export class BaseActionService<T extends BaseEntity> {

  public static ASC = 'ascending';
  public static DESC = 'descending';

  @select(['config']) config$;
  sid: any;

  @select(['process', 'cid']) cid$;
  cid: any;

  entity: T = null;
  _entityName: string = null;
  entityUrl: string = null;

  // results of last read
  list: Array<T>;

  apiUrl: string;
  restBaseUrl = 'resource/rest/';

  environment = environment;

  // Read restrictions

  public select: Array<string> = [];

  public equal: any = {};
  public greater: any = {};
  public greaterEqual: any = {};
  public less: any = {};
  public lessEqual: any = {};
  public notEqual: any = {};
  public like: any = {};
  public fullLike: any = {};
  public isNull: any = {};
  public orderBy: any = {};

  protected httpService: HttpService;

  protected conversationActions: ConversationActions;
  protected bannerActions: BannerActions;

  public readPageSize = 10;

  public filterBySdl = true;

  protected vm: ViewManager;
  protected ts: TranslateService;

  @select(['banner']) Banner$;
  Banner;

  constructor(injector: Injector) {
    this.httpService = injector.get(HttpService);
    this.bannerActions = injector.get(BannerActions);
    this.apiUrl = injector.get('apiUrl');
    this.conversationActions = injector.get(ConversationActions);
    this.vm = injector.get(ViewManager);
    this.ts = injector.get(TranslateService);

    this.Banner$.subscribe((res) => this.Banner = res);
    this.cid$.subscribe((res) => this.cid = res);

    this.config$.subscribe((res) =>
      this.sid = res.sid
    );
  }

  set entityName(entityName: string) {
    this._entityName = entityName;
    this.entityUrl = this._entityName.toLowerCase() + 's';
  }

  get entityName() {
    return this._entityName;
  }

  public newEntity(): T {
    this.entity = {} as T;
    this.conversationActions.put(this._entityName, this.entity);
    return this.entity;
  }

  /**
   * Get an entity by id, and inject same entity in conversation
   *
   * Result can be found in: Conversation.instance().EntityName or into this.entity
   */
  public inject(id: number, additionals: Array<any> = null, loads: Array<any> = null, conversationName = null): Promise<T> {
    return this.load(id, additionals, loads, conversationName, true);
  }

  
  /**
   * Get an entity by id, without injecting into server side conversation
   *
   * Result can be found in: Conversation.instance().EntityName or into this.entity
   */
  public get(id: number, additionals: Array<any> = null, loads: Array<any> = null, conversationName = null): Promise<T> {
    return this.load(id, additionals, loads, conversationName, false);
  }

  private load(id: number, additionals: Array<any> = null, loads: Array<any> = null, conversationName = null, inject: boolean): Promise<T> {

    let additionalsRestrictions = '';
    let loadsRestrictions = '';

    if (id === undefined || isNaN(id)) {
      throw new Error('Error injecting ' + this.entityUrl + ' illegal argument id ' + id + ' ');
    }

    if (id !== 0) {
      if (additionals != null) {
        for (let i = 0; i < additionals.length; i++) {
          additionalsRestrictions += ';additional=' + additionals[i];
        }
      }

      if (loads != null) {
        for (let i = 0; i < loads.length; i++) {
          loadsRestrictions += ';load=' + loads[i];
        }
      }
    }

    return this.httpService.fetch(this.apiUrl + this.restBaseUrl + this.entityUrl + (inject ? '' : '/get') + '/' + id +
      additionalsRestrictions + loadsRestrictions + '?cid=' + this.cid,
      {credentials: 'include'})
      .then(response =>
        response.text()
      )
      .then(text =>
        JSON.parse(text, reviver)
      )
      .then(rawObject => {

        this.entity = rawObject.entity as T;

        if (!conversationName) {
          conversationName = this._entityName;
        }

        if (this.entity) {

          if (conversationName === 'Patient') {
            this.conversationActions.remove('PatientEncounter');
            this.conversationActions.remove('Appointment');
          } else if (conversationName === 'PatientEncounter') {
            this.conversationActions.remove('Appointment');
          } else if (conversationName === 'Appointment') {
            this.conversationActions.remove('PatientEncounter');
          }

          this.conversationActions.put(conversationName, this.entity);

          this.entity = this.conversationActions.unProxy(rawObject, this.entity);

          // Tentativo utilizzo proxy ES6
          // if (loads != null) {
          //
          //   const action = this;
          //
          //   const handler = {
          //     async get(target, name) {
          //       console.log('proxy access', target, name);
          //       let value = target[name];
          //       if (!value) {
          //         let targetUrl = target.entityName.substring(target.entityName.lastIndexOf('.') + 1, target.entityName.length);
          //         targetUrl = targetUrl.toLowerCase() + 's';
          //         // console.log('targetUrl', targetUrl);
          //         const response = await fetch(action.apiUrl + action.restBaseUrl + targetUrl + '/' +
          //            target.internalId + '?cid=' + action.cid, {credentials: 'include'});
          //         const text = await response.text();
          //         const injectedDm = JSON.parse(text, reviver);
          //         Object.assign(target, injectedDm.entity);
          //         //console.log('proxy loaded', json);
          //         value = target[name];
          //       }
          //       return value;
          //       // const v = target[name];
          //       // return typeof v == "object" ? new Proxy(v, handler) : v;
          //     }
          //   };
          //
          //   loads.map((path) => {
          //     const pathParts = path.split('.');
          //     const lastPart = pathParts.pop();
          //     let parentOfProxy = this.entity;
          //     if (pathParts.length > 0) {
          //       parentOfProxy = getIn(this.entity, pathParts);
          //     }
          //     if (parentOfProxy[lastPart]) {
          //       parentOfProxy[lastPart] = new Proxy(parentOfProxy[lastPart], handler);
          //     }
          //     //const proxy = getIn(this.entity, path.split('.'));
          //   });
          // }

          if (rawObject.additional != null) {
            rawObject.additional = this.conversationActions.unProxy(rawObject, rawObject.additional);
            this.conversationActions.put(conversationName + 'Additional', rawObject.additional);
          }

          if (rawObject.banner) {
            this.bannerActions.put(rawObject.banner);
          }

          for (let key in rawObject.bannerEntities) {
            if (rawObject.bannerEntities.hasOwnProperty(key)) {
              rawObject.bannerEntities[key] = this.conversationActions.unProxy(rawObject, rawObject.bannerEntities[key]);
              this.conversationActions.put(key, rawObject.bannerEntities[key]);
            }
          }

        } else { // eject

          this.conversationActions.remove(conversationName);
          this.conversationActions.remove(conversationName + 'Additional');

          if (conversationName === 'Patient') {
            this.bannerActions.remove();
            this.conversationActions.remove('PatientEncounter');
            this.conversationActions.remove('PatientEncounterAdditional');
            this.conversationActions.remove('Appointment');
          } else if (conversationName === 'PatientEncounter') {
            this.bannerActions.put(rawObject.banner);
          } else if (conversationName === 'Appointment') {
            this.bannerActions.put(rawObject.banner);
          }

        }
        return this.entity;
      });
  }

  /**
   * Eject entity from conversation
   *
   * Clean Conversation.instance().EntityName and into this.entity
   */
  public eject(): Promise<T> {
    return this.inject(0);
  }

  /**
   * Execute criteria query and return a list of results.
   * Generated criteria query is based on select and like fields.
   * To be extended (equal, notequal, less, greater, not in...)
   *
   * Result list can be found in: Conversation.instance().EntityNameList
   */
  public read(listName = null): Promise<Datamodel> {

    let matrixParam = '';

    let page = 1;
    if (this.readPageSize === 0) {
      page = 0; // not paged
    } else if (this.readPageSize !== 10) {
      this.equal['readPageSize'] = this.readPageSize;
    }

    for (let key in this.orderBy) {
      if (this.orderBy.hasOwnProperty(key)) {
        let value = this.orderBy[key];
        if (value !== null && value !== '') {
          matrixParam += key + '=' + encodeValue(value) + ';';
        }
      }
    }

    for (let key in this.equal) {
      if (this.equal.hasOwnProperty(key)) {
        let value = this.equal[key];
        if (value instanceof Array) {
          for (let j = 0; j < value.length; j++) {

            if (value[j] !== null && value[j] !== '') {
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
    for (let key in this.greater) {
      if (this.greater.hasOwnProperty(key)) {
        let value = this.greater[key];
        if (value !== null && value !== '') {
          matrixParam += key + '=>>' + encodeValue(value) + ';';
        }
      }
    }
    for (let key in this.greaterEqual) {
      if (this.greaterEqual.hasOwnProperty(key)) {
        let value = this.greaterEqual[key];
        if (value !== null && value !== '') {
          matrixParam += key + '=>' + encodeValue(value) + ';';
        }
      }
    }
    for (let key in this.less) {
      if (this.less.hasOwnProperty(key)) {
        let value = this.less[key];
        if (value !== null && value !== '') {
          matrixParam += key + '=<<' + encodeValue(value) + ';';
        }
      }
    }
    for (let key in this.lessEqual) {
      if (this.lessEqual.hasOwnProperty(key)) {
        let value = this.lessEqual[key];
        if (value !== null && value !== '') {
          matrixParam += key + '=<' + encodeValue(value) + ';';
        }
      }
    }
    for (let key in this.notEqual) {
      if (this.notEqual.hasOwnProperty(key)) {
        let value = this.notEqual[key];
        if (value !== null && value !== '') {
          matrixParam += key + '=!' + encodeValue(value) + ';';
        }
      }
    }
    for (let key in this.like) {
      if (this.like.hasOwnProperty(key)) {
        let value = this.like[key];
        if (value !== null && value !== '') {
          matrixParam += key + '=~' + encodeValue(value) + ';';
        }
      }
    }
    for (let key in this.fullLike) {
      if (this.fullLike.hasOwnProperty(key)) {
        let value = this.fullLike[key];
        if (value !== null && value !== '') {
          matrixParam += key + '=~~' + encodeValue(value) + ';';
        }
      }
    }
    for (let key in this.isNull) {
      if (this.isNull.hasOwnProperty(key)) {
        let value = this.isNull[key];
        if (value !== null && value !== '') {
          matrixParam += key + '=*' + encodeValue(value) + ';';
        }
      }
    }

    this.select.map(value => {
      matrixParam += value + ';';
    });

    if (!this.filterBySdl) {
      matrixParam += 'filterBySdl;'
    }

    /*
     this.isNull.forEach((value, key) => {
     if (value != null && value != '') {
     matrixParam += key + '=°' + encodeValue(value) + ';';
     }
     });
     */

    return this.httpService.fetch(this.apiUrl + this.restBaseUrl + this.entityUrl + '/' + matrixParam + '/' + page,
      {credentials: 'include'})
      .then(response => response.text())
      .then(text => JSON.parse(text, reviver))
      .then(rawObject => {

        let dm: Datamodel = new Datamodel(rawObject.entities || [], this.httpService);
        dm.nextUrl = rawObject.next;
        dm.prevUrl = rawObject.prev;

        // this.conversation[this._entityName + 'List'] = dm;
        if (listName) {
          this.conversationActions.put(listName, dm);
        } else {
          this.conversationActions.put(this._entityName + 'List', dm);
        }

        this.list = dm.entities;

        return dm;
      })
      .catch(error => {
        console.error('BaseAction: Error reading ' + this._entityName + ' ' + error.message || error);
        throw error;
      });
  }

  /**
   * public create method
   * Create a new entity
   */
  public create(method = ''): Promise<T> {

    if (this.entity.internalId) {
      return this.update(method);
    } else {

      let body = JSON.stringify(this.entity, dateReplacer);

      return this.httpService.fetch(this.apiUrl + this.restBaseUrl + this.entityUrl + method
          + ';jsessionid=' + this.sid + '?cid=' + this.cid,
        {
          method: 'POST',
          body: body,
          headers: {'Content-Type': 'application/json; charset=utf-8'},
          credentials: 'include'
        }
        ).then(response =>
          response.text()
        ).then((internalId) => {
          this.entity.internalId = +internalId;
          this.entity.version = 1;
          return this.entity;
        })
        .catch(error => {
          console.error('BaseAction: Error creating ' + this._entityName + ' ' + error.message || error);
          throw error;
        });
    }
  }

  /**
   * Create array of entities
   * @param entities
   * @returns {any}
   */
  public createAll(entities: Array<T> = null): Promise<any> {
    if (!entities) {
      return this.create();
    } else {
      let createPromises: Array<Promise<any>> = entities.map((e: T) => {
        this.entity = e;
        return this.create();
      });
      // return when all done promise
      return Promise.all(createPromises);
    }
  }

  /**
   * public update method
   * Update an entity
   */
  public update(method = ''): Promise<T> {

    let body = JSON.stringify(this.entity, dateReplacer);

    return this.httpService.fetch(this.apiUrl + this.restBaseUrl + this.entityUrl + method
       + ';jsessionid=' + this.sid + '?cid=' + this.cid,
      {
        method: 'PUT',
        body: body,
        headers: {'Content-Type': 'application/json; charset=utf-8'},
        credentials: 'include'
      }
    )
      .then(response => response.text())
      .then(text => JSON.parse(text, reviver))
      .then(res => {
        if (res.locked) {
          this.vm.openAlertMessage(this.ts.instant('Warning'),
          res.locked + ' ' + res.lockingUser, false);
        } else if (res.status && res.status === 'error') {
          this.vm.openAlertMessage(this.ts.instant('Warning'),
          res.message, false);
        } else {
          this.entity = res;
          // this.conversation[this._entityName] = entity;
          this.conversationActions.put(this._entityName, res);
        }
        return this.entity;
      })
      .catch(error => {
        console.error('BaseAction: Error updating ' + this._entityName + ' ' + error.message || error);

        // FIXME manage, show modifica cancorrente instead of error Caused by: org.hibernate.StaleObjectStateException:
        //  Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect):
        //  [com.phi.entities.act.PatientEncounter#4203905]
        //   Caused by: org.hibernate.StaleObjectStateException: Row was updated or deleted by another transaction
        //    (or unsaved-value mapping was incorrect): [com.phi.entities.act.PatientEncounter#4177118]
        //   at org.hibernate.event.def.DefaultMergeEventListener.entityIsDetached(DefaultMergeEventListener.java:417)
        //   at org.hibernate.event.def.DefaultMergeEventListener.onMerge(DefaultMergeEventListener.java:233)
        //   at org.hibernate.event.def.DefaultMergeEventListener.onMerge(DefaultMergeEventListener.java:83)
        //   at org.hibernate.impl.SessionImpl.fireMerge(SessionImpl.java:707)
        //   at org.hibernate.impl.SessionImpl.merge(SessionImpl.java:691)
        //   at org.hibernate.impl.SessionImpl.merge(SessionImpl.java:695)
        //   at org.hibernate.ejb.AbstractEntityManagerImpl.merge(AbstractEntityManagerImpl.java:235)
        // ... 142 more
        //   2018-03-29 08:17:24,481 ERROR [com.phi.rest.log.LogRest] cominetto [cid=60402]
        //  Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36
        //   Error: Uncaught (in promise): Internal Server Error

        throw error;
      });
  }

  /**
   * public update method
   * Delete an entity
   */
  public delete() {

    let body = JSON.stringify(this.entity.internalId);

    return this.httpService.fetch(this.apiUrl + this.restBaseUrl + this.entityUrl + '/' + this.entity.internalId
      + ';jsessionid=' + this.sid + '?cid=' + this.cid,
      {
        method: 'DELETE',
        body: body,
        headers: {'Content-Type': 'application/json; charset=utf-8'},
        credentials: 'include'
      }
    )
      .then(() => {
        this.entity = null;
        this.conversationActions.remove(this._entityName);

      })
      .catch(error => {
        console.error('BaseAction: Error deleting ' + this._entityName + ' ' + error.message || error);
        throw error;
      });
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
    this.fullLike = {};
    this.isNull = {};
    this.orderBy = {};

    this.filterBySdl = true;
  }

  public isLocked() {
    return this.Banner['appointmentLocked'] || this.Banner['encounterLocked'];
  }

  getLockedStatus(id: number): Promise<boolean> {
    if (this.environment.checkLockedStatus) {
      return this.httpService.fetch(this.apiUrl + this.restBaseUrl + this.entityUrl + '/isLocked/' + id
        + ';jsessionid=' + this.sid + '?cid=' + this.cid,
        {
          method: 'GET',
          headers: {'Content-Type': 'application/json; charset=utf-8'},
          credentials: 'include'
        }
      )
      .then(response => response.text())
      .then(text => JSON.parse(text, reviver))
      .then(res => {
        if (res.locked) {
          this.vm.openAlertMessage(this.ts.instant('Warning'),
          res.locked + ' ' + res.lockingUser, false);
          return true;
        } else {
          return false;
        }
      })
      .catch(error => {
          console.error('Error getLockedStatus ' + this.entity + ' ' + error.message);
          throw error;
        });
    } else {
      return Promise.resolve(false);
    }
  }

}

export function encodeValue(uri: string): string {
  uri = anyToString(uri);
  return encodeURIComponent(uri);
}

export function dateReplacer(key, value) {
  return dateToString(this[key]);
}
