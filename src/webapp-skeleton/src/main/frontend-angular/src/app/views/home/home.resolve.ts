import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot } from '@angular/router';
import { ProcessManager } from '../../services/process-manager';
import { logError } from '../../services/error/global-error-handler';

@Injectable()
export class HomeResolve implements Resolve<any> {

  constructor(private processManager: ProcessManager) {
  }

  resolve(route: ActivatedRouteSnapshot) {
    try {
      return this.processManager.processlist();
    } catch (error) {
      // Thrownig exception in resolver -> infinite loop
      console.log('Error getting processes list ' + error.message);
      logError(error, sessionStorage['cid']);
      delete sessionStorage['cid'];
    }
  }

}
