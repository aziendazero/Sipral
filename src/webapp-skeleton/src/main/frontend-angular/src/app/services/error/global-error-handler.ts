import { Inject, ErrorHandler, Injectable, NgZone, Injector, ApplicationRef } from '@angular/core';
import { HttpService } from '../http.service';
import { ViewManager } from '../view-manager';
import { environment } from '../../../environments/environment';

@Injectable()
export class GlobalErrorHandler implements ErrorHandler {

  // private app: ApplicationRef;

  private errorCount = 0;
  private max = 5;

  private viewManager: ViewManager;

  constructor(@Inject('apiUrl') protected apiUrl,
              private zone: NgZone,
              private injector: Injector) {

  }

  handleError(error) {

    this.errorCount = this.errorCount + 1;

    try {

      this.zone.run(() => {

        // if (!this.app) {
        //   this.app = this.injector.get(ApplicationRef)
        // }

        if (!this.viewManager) {
          this.viewManager = this.injector.get(ViewManager)
        }

        if (error.rejection === HttpService.SESSION_EXPIRED) {

          this.viewManager.openErrorMessage('Sessione scaduta!');

        } else {
          console.error(error.stack);

          this.viewManager.openErrorMessage('Error', error);

          // this.app.tick();
        }
      });

    } finally {
      // TODO: detect if the error is coming from the server...
      if( this.errorCount === this.max ) {
        logError('Preventing recursive error after ' + this.errorCount + ' recursive errors ' + error.stack, this.viewManager.cid);
        let appRef = this.injector.get( ApplicationRef );
        appRef.tick();
      } else {
        logError(error.stack, this.viewManager.cid);
      }
    }

  }

}

export function logError(error, cid) {
  let body = window.navigator.userAgent + '\n' + error;
  let url = environment.apiUrl + 'resource/rest/logs/error';
  if (cid) {
    url = url + '?cid=' + cid;
  }

  fetch(url,
    {
      method: 'POST',
      body: body,
      headers: {'Content-Type': 'text/plain'},
      credentials: 'include'
    });
}

export function logInfo(error, cid) {
  let body = window.navigator.userAgent + '\n' + error;
  let url = environment.apiUrl + 'resource/rest/logs/info';
  if (cid) {
    url = url + '?cid=' + cid;
  }

  fetch(url,
    {
      method: 'POST',
      body: body,
      headers: {'Content-Type': 'text/plain'},
      credentials: 'include'
    });
}
