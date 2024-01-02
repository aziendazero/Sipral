import { Injectable, Inject } from '@angular/core';
import { CodeValue } from "./entities/data-types/code-value";
import { HttpService } from './http.service';

@Injectable()
export class DictionaryManager {

  // REQUEST_GET = 'PHI-DictionaryManager-get';
  // RECEIVE_GET = 'PHI-DictionaryManager-get-response';

  restBaseUrl = 'resource/rest/';
  entityUrl = 'codevalues';

  GET_ENTITY = '/';
  GET_DOMAIN = '/domain/';
  SEARCH_CODE_VALUES = '/search/';
  SUGGEST = '/suggest/';

  domains = {};
  cv = {};

  // Bind to form to search
  public searchByName: any = {};
  public searchByCode: any = {};
  public searchResults: any = {};

    constructor(
      @Inject('apiUrl') private apiUrl,
      private httpService: HttpService
    ) {
        // this.REQUEST_GET = 'PHI-DictionaryManager-get';
        // this.RECEIVE_GET = 'PHI-DictionaryManager-get-response';
    }

  /**
   * Get codeValues by id
   * @returns CodeValue
   */
  getById(id): Promise<CodeValue> {

    if (this.cv[id] != null) {
      return Promise.resolve(this.cv[id]);
    } else {
      let url = this.apiUrl + this.restBaseUrl + this.entityUrl + this.GET_ENTITY + id;
      return this.httpService.fetch(url, {method: 'GET', credentials: 'include'})
        .then(response => response.json())
        .then(codeValue => {
          if (codeValue) {
            this.cv[id] = codeValue;
            return codeValue;
          } else {
            return null;
          }
        })
        .catch(error => {
          console.error('Error getting CodeValue with id ' + id + ' ' + error.message, error);
          throw error;
        });
    }
  }

    /**
     * Get codeValues by id codeSystem and domain d=CodeSystem:Domain:code1,code2,code3
     * @param codeSystemAndDomains
     * @param lazy true used for phi-tree
     * @returns {Function}
     */
    get(codeSystemAndDomains, lazy = false): Promise<any> {
        // return function (dispatch) {
      let domainz = codeSystemAndDomains.replace(':', '_');

      if (this.domains[domainz] != null) {
        return Promise.resolve(this.domains[domainz]);
      } else {
        let url = this.apiUrl + this.restBaseUrl + this.entityUrl + this.GET_DOMAIN;
        if (lazy) {
          url += 'lazy=true';
        } else {
          url += 'lazy=false';
        }
        url += ';domain=' + encodeURIComponent(codeSystemAndDomains);

        // dispatch({type: this.REQUEST_GET, codeSystemAndDomains});

        return this.httpService.fetch(url, {method: 'GET', credentials: 'include'})
          .then(response => response.json())
          .then(codeValues => {
            let domainNoFilter = domainz;
            let i = domainz.indexOf(":");
            if (i !== -1) {
              domainNoFilter = domainz.substring(0, i);
            }
            this.domains[domainz] = codeValues[domainNoFilter];
            // return {type: this.RECEIVE_GET, codeValues, receivedAt: Date.now()};
            return this.domains[domainz];
          })
          .catch(error => {
            console.error('Error getting domain ' + codeSystemAndDomains + ' ' + error.message, error);
            throw error;
          });
        // }.bind(this);
      }
    }

  // search(codeSystem:string, rootDomain:string, code:string = null, name:string = null): Promise<any> {
  search(): Promise<any> {

    let codeSystemAndDomain;
    let name:string;

    for (let key in this.searchByName) {
      if (this.searchByName.hasOwnProperty(key)) {
        let value = this.searchByName[key];
        if (value && value !== '') {
          codeSystemAndDomain = key; // .replace(':', '_');
          name = value;
        }
      }
    }

    if (codeSystemAndDomain) {

      let csAndD = codeSystemAndDomain.split(':');

      let url = this.apiUrl + this.restBaseUrl + this.entityUrl + this.SEARCH_CODE_VALUES + 'codeSystem=' + csAndD[0] + ';rootDomain=' + csAndD[1];
      // if (code != null) {
        url += ';code=' + name;
      // } else {
        url += ';name=' + name;
      // }

      if (/*code != null || */name != null) {

        return this.httpService.fetch(url, {method: 'GET', credentials: 'include'})
          .then(response => response.json())
          .then(codeValues => {
            this.searchResults[codeSystemAndDomain] = codeValues;
            return codeValues;
          })
          .catch(error => {
            console.error('Error searching domain ' + csAndD[0] + ' ' + csAndD[1] + ' ' + error.message, error);
            throw error;
          });
      }
    }
  }


  loadTree(codeSystem: string, domain: string, parentId: string, dataComponent: string, levelOfDepth: number, codeValueClass: string ): Promise<any> {

    let operation = this.apiUrl + 'resource/rest/tree/dictionary/getTree';

    let body;
    if (codeSystem) {
      body = 'codeSystem=' + codeSystem + '&domain=' + domain;
    } else if (parentId) {
      body = 'parentId=' + parentId;
    } else {
      throw new Error('Illegal parameters: codeSystem or parentId must be specified!');
    }

    body+= '&dataComponent=' + dataComponent + '&levelOfDepth=' + levelOfDepth + '&codeValueClass=' + codeValueClass;

    return this.httpService.fetch(operation,
      {
        method: 'POST',
        body: body,
        headers: new Headers({'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'}),
        credentials: 'include'
      })
      .then(response => response.json())
      .then(cvTree => {
        return cvTree;
      });
  }



}
