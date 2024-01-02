/**
 * Created by Daniele on 04/07/2017.
 */
import { Injectable, Inject } from '@angular/core';

@Injectable()
export class HttpService {

  public static SESSION_EXPIRED = 'Session expired!';
  private static GET = 'GET';

  public loading: boolean = false;
  public loadingCounter = 0;
  public urlStack = [];

  reloginNeeded = false;

  constructor(@Inject('apiUrl') private apiUrl) {

  }

  fetchNoLoading(url: string, parameters?: RequestInit): Promise<Response> {
    if (this.reloginNeeded) {
      return Promise.resolve(null);
    }

    let httpMethod = HttpService.GET;

    if (parameters && parameters.method) {
      httpMethod = parameters.method;
    }

    if (httpMethod === HttpService.GET) {
      // disable IE ajax request caching
      if (url.indexOf('?') === -1) {
        url = url + '?_=';
      } else {
        url = url + '&_=';
      }
      url = url + new Date().getTime();
    }


    return fetch(url, parameters).then((res: any) => {

      return this.manageResponse(httpMethod, url, res);

    }/*, (res: Response) => {

      return this.manageResponse(httpMethod, url, res);

    }*/).catch(error => {

      this.decreaseLoadingCounter(url);
      return this.checkIsAuthenticated().then(() => {
        throw error;
      });

    });
  }

  fetch(url: string, parameters?: RequestInit): Promise<Response> {

    this.loading = true;
    this.loadingCounter++;
    this.urlStack.push(url);

    return this.fetchNoLoading(url, parameters);

  }

  checkIsAuthenticated(): Promise<Response> {
    return fetch(this.apiUrl + 'authentication', {credentials: 'include'})
      .then(response => response.json())
      .then(json => {
        if (!json['currentEmployee']) {
          return Promise.reject(HttpService.SESSION_EXPIRED);
        } else {
          return Promise.resolve(null);
        }
      });
  }

  private manageResponse(httpMethod: string, url: string, res: Response): Promise<Response> {
    let promise: Promise<Response>;
    if (res.ok && !res['redirected']) {
      promise = Promise.resolve(res);
    } else {
      if ( res.url === '') {
        // IE11 since fetch is polifilled no redirect url is present, check if session is valid
        promise = this.checkIsAuthenticated();
      } else if (res.url.indexOf('login') !== -1) { //.includes('login')) {
        promise = Promise.reject(HttpService.SESSION_EXPIRED);
      } else if (res.url.indexOf('seam-doc') !== -1) {
        // if redirect to seam-doc: tutto ok
        promise =  Promise.resolve(res);
      } else {
        if (res.text) {
          promise = res.text().then(body => {
            return Promise.reject(body);
          });
        } else {
          promise = Promise.reject(res.statusText + '\n' + httpMethod + ': ' + url);
        }
      }
    }
    this.decreaseLoadingCounter(url);
    return promise;
  }

  private decreaseLoadingCounter(url) {
    this.loadingCounter--;
    if (this.loadingCounter <= 0) { // all requests are returned
      this.loadingCounter = 0;
      this.loading = false;
    } else { // not all request
      console.log('HttpService notice: there are ' + (this.loadingCounter + 1) + ' concurrent http requests');
      // this.urlStack.forEach((e,i) =>
      //   console.log(e + (e === url ? ' [RETURNED NOW]' : ''))
      // );
    }
    this.urlStack.splice(this.urlStack.indexOf(url),1);
  }
}
