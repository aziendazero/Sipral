import { AfterViewInit, Component, ElementRef, OnDestroy, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Location } from '@angular/common';
import { HttpService } from '../../../../../services/http.service';
import { AuthGuard } from '../../../../../services/auth-guard.service';
import { ViewManager } from '../../../../../services/view-manager';

@Component({
  template: `    
    <phi-popup [fullScreen]="true" (onClose)="close()">
      <iframe #iframe class="iframe"></iframe>
    </phi-popup>
  `
})
export class IframeComponent implements AfterViewInit, OnDestroy {

  public _data; any;
  title = 'Iframe';

  @ViewChild('iframe')
  private iframe: ElementRef;

  private dataUrl: string;

  constructor(/*public sanitizer: DomSanitizer,*/
              protected location: Location,
              private route: ActivatedRoute,
              private authGuard: AuthGuard,
              private httpService: HttpService,
              private viewManager: ViewManager) {

    // this._data = this.route.snapshot.params['src'];
    // this._data = this.sanitizer.bypassSecurityTrustResourceUrl(this._data);

  }

  ngAfterViewInit() {

    if(window.navigator.msSaveOrOpenBlob) {

      // IE : doesen't support data url: 2 request: 1: session is still valid, 2: request iframe src
      this.authGuard.checkIsAuthenticated().then((isAuth: boolean) => {
        if (isAuth) {
          this.iframe.nativeElement.src = this.route.snapshot.params['src']
        } else {
          return Promise.reject(HttpService.SESSION_EXPIRED);
        }
      });
    } else {
      if (!this.route.snapshot.queryParams['external']) {
        // Other browsers: 1 request: get document, and pass to iframe with data url
        this.httpService.fetch(this.route.snapshot.params['src'], {credentials: 'include'})
          .then(response =>
            response.blob()
          ).then((blob) => {
          this.dataUrl = window.URL.createObjectURL(blob);
          this.iframe.nativeElement.src = this.dataUrl;
        });
      } else {
        this.iframe.nativeElement.src = this.route.snapshot.params['src'];
      }

    }
  }

  ngOnDestroy() {
    if (this.dataUrl) {
      window.URL.revokeObjectURL(this.dataUrl);
    }
  }

  close() {
    this.viewManager.setPopupViewId(null);
  }

}
