import { HttpService } from './../http.service';
import { select } from '@angular-redux/store';
import { reviver } from '../converters/any.converter';

import { environment } from './../../../environments/environment';

export class Datamodel {
  // FIXME restore protected on _variables, but fix adtAction
  public _pages = [];
  public _baseUrl: string;
  public currentPageNumber = 1;
  public _fullLoaded = true;
  public _loading = false;

  public headers: Array<any> = [];

  // Used with colspan in ambulatory calendar
  public headersFiltered: Array<any> = [];

  public entities: Array<any> = [];
  // public allEntities: Array<any> = [];

  public additional: {[key: string]: Array<{[key: string]: any}> } = {};

  @select(['process', 'cid']) cid$;
  cid: any;


  public _sortingColumn = null;
  public _orderDirection: 'ASC' | 'DESC' = 'ASC';

  constructor(entities, protected httpService?: HttpService) {
    this._pages[this.currentPageNumber] = entities;
    this.entities = entities;
    this.cid$.subscribe((res) => this.cid = res);
  }

  set baseUrl(url: string) {
    if (typeof url !== 'undefined' && url !== '') {
      this._baseUrl = url.substring(0, url.lastIndexOf('/'));
    }
  }

  set nextUrl(url: string) {
    if (url && url !== '') {
      this._fullLoaded = false;
      this._baseUrl = url.substring(0, url.lastIndexOf('/'));
    } else {
      this._fullLoaded = true;
    }
  }

  set prevUrl(url: string) {
    if (url && url !== '') {
      this._baseUrl = url.substring(0, url.lastIndexOf('/'));
    }
  }

  get fullLoaded(): boolean {
    return this._fullLoaded;
  }

  public getNext(): void {
    this.loadPage(this.currentPageNumber + 1);
  }

  public getPrev(): void {
    this.loadPage(this.currentPageNumber - 1);
  }

  private setPage(newEntities: Array<any>, number: number): void {
    this._pages[number] = newEntities;
    // this.allEntities.concat(newEntities);
    this.currentPageNumber = number;
    this.entities = this.entities.concat(newEntities);
}

  public loadPage(number: number): Promise<any> {
    if (number < 1) {
      return;
    }
    if (this._pages.hasOwnProperty(number)) {
      this.currentPageNumber = number;
      this.entities = this._pages[number];
    } else {
      if (!this._fullLoaded && !this._loading) {
        this._loading = true;

        let completeUrl = environment.apiUrl + this._baseUrl;
        if (completeUrl.indexOf('/' + number + '?') < 0) {
          completeUrl += '/' + number;
        }

        if (completeUrl.indexOf('?') >= 0) {
          completeUrl += '&';
        } else {
          completeUrl += '?';
        }
        completeUrl += 'cid=' + this.cid;
        completeUrl += '&_=' + new Date().getTime();

        let fetchResult: Promise<any>;
        if (typeof this.httpService === 'undefined') {
          fetchResult = fetch(completeUrl, { credentials: 'include' });
        } else {
          fetchResult = this.httpService.fetch(completeUrl, { credentials: 'include' });
        }

        return fetchResult.then(response => response.text())
          .then( json => JSON.parse(json, reviver) )
          .then( rawObject => {
            if (rawObject.main) { // BaseDashboad rest datamodel
              this.setPage(rawObject.main.entities, number);
              this.nextUrl = rawObject.main.next;
              this.prevUrl = rawObject.main.prev;
            } else if (rawObject.entities) {// BaseRest rest datamodel
              this.setPage(rawObject.entities, number);
              this.nextUrl = rawObject.next;
              this.prevUrl = rawObject.prev;
            } else { // array, special case for getAmbulatoryAppointments:
              this.setPage(rawObject, number);
              this._baseUrl = this._baseUrl.replace('/' + number + '?', '/' + (number + 1) + '?');
            }
            this._loading = false;
          });
      }
    }
  }

  public orderBy(binding: string): void {
    if (this._sortingColumn === binding) {
      // flip _orderDirection (datagrid column button)
      if (this._orderDirection === 'DESC') {
        this._orderDirection = 'ASC';
      } else {
        this._orderDirection = 'DESC';
      }
    } else {
      this._orderDirection = 'ASC';
    }
    this._sortingColumn = binding;

    // console.log('OrderBy ' + binding + ' ' + this._orderDirection);
    this.orderByColumn(binding, this._orderDirection);
  }

  public orderByColumn(binding: string, trueOrdering: 'ASC' | 'DESC') {
    if (this.fullLoaded) {
      this.sortEntities(binding, trueOrdering);
    } else {
      this._baseUrl = this._baseUrl.replace(/orderBy\=[^;]+;/g, 'sorting=' + 'orderBy:' + binding + trueOrdering);
      this._pages = [];
      this.currentPageNumber = 1;
      this._fullLoaded = false;
      this._loading = false;
      this.entities = [];
      this.loadPage(1);
    }
  }

  get orderDirection(): string {
    return this._orderDirection;
  }

  get sortingColumn(): string {
    return this._sortingColumn;
  }

  get sortArrow(): string {
    return 'fa ' + (this._orderDirection === 'ASC' ? 'fa-arrow-up' : 'fa-arrow-down');
  }

  public sortColumn(binding): boolean {
    return this._sortingColumn === binding;
  }

  public sortEntities(binding: string, direction: 'ASC' | 'DESC') {
    this.entities = this.entities.sort((a, b) => {
      let dir = direction === 'ASC' ? 1 : -1;
      let aObj = this.getNested(a, binding);
      let bObj = this.getNested(b, binding);
      let result = 0;
      if (!aObj || aObj === '') {
        if (bObj && bObj !== '') {
          result = -1;
        }
      } else {
        if (!bObj || bObj === '') {
          result = 1;
        } else {
          if (typeof aObj === 'string' && typeof bObj === 'string') { // STRING
            result = aObj.localeCompare(bObj);
          } else if (typeof aObj === 'number' && typeof bObj === 'number') { // NUMBER
            result = aObj - bObj;
          } else if (typeof aObj === 'boolean' && typeof bObj === 'boolean') { // BOOLEAN
            result = (aObj.valueOf === bObj.valueOf ? 0 : (aObj ? 1 : -1));
          } else if (typeof aObj.getTime === 'function' && typeof bObj.getTime === 'function') { // DATE
            result  = aObj.getTime() - bObj.getTime();
          }
        }
      }
      return dir * result;
    });
  }

  public getNested(obj: any, path: string) {
    if (obj && path) {
      let index = path.indexOf('.');
      if (index === -1) {
        return obj[path];
      } else {
        let segment = path.substring(0, path.indexOf('.')); // GET FIRST SEGMENT FROM DOTTED PATH [first.second.third]
        if (obj[segment]) {
          return this.getNested(obj[segment], path.substring(path.indexOf('.') + 1));
        }
      }
    }
    return null;
  }
}
