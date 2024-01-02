import { Injectable, Inject } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot } from '@angular/router';
import { HttpService } from '../../services/http.service';

@Injectable()
export class FlashVarsResolve implements Resolve<any> {

  constructor(
    @Inject('apiUrl') private apiUrl,
    private httpService: HttpService
  ) { }

  resolve(route: ActivatedRouteSnapshot) {
    return this.httpService.fetch(this.apiUrl + 'resource/rest/flexProxy/flashvars', { method: "GET", credentials: 'include'})
      .then(response => {
        // console.log('flash-vars resolver: flash vars loaded');
        return response.text();
      })
      .catch(error => {
        // console.error('Error getting flashvars ' +  error.message, error);
        throw error;
      });
  }
}
