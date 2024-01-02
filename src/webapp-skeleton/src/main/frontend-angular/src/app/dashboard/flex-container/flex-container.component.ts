import { Component, OnInit, Inject, ViewChild, ElementRef, NgZone } from '@angular/core';
import { Router } from '@angular/router';
import { DomSanitizer } from '@angular/platform-browser';
import { select } from '@angular-redux/store';
import { BannerActions } from '../../actions/banner.action';
import { HttpService } from '../../services/http.service';
import { ConfigActions } from '../../actions/config.action';
import { FlexContainerService } from './flex-container.service';
import { ViewManager } from '../../services/view-manager';
import { ProcessActions } from '../../actions/process.actions';

@Component({
  selector: 'phi-flex-container',
  template: `
    <embed 
      #MDashboard
      id = "MDashboard"
      [src]="mDashboardUrl" 
      allowScriptAccess="sameDomain" 
      wmode="opaque">
`,
  styles: ['#MDashboard {margin-bottom:-4px; width:100%; height:100%;}']
})
export class FlexContainerComponent implements OnInit {

  @ViewChild('MDashboard')
  private mDashboard: ElementRef;

  mDashboardUrl;

  @select(['config', 'menuVisible']) menuVisible$;
  menuVisible: string;

  @select(['config', 'flashVars']) flashVars$;
  flashVars:string;

  @select(['process', 'dashboard']) targetDashboard$;
  targetDashboard;

  constructor(
      @Inject('apiUrl') apiUrl,
      private flexService: FlexContainerService,
      private router: Router,
      private sanitizer: DomSanitizer,
      private BannerActions: BannerActions,
      private viewManager: ViewManager,
      private httpService: HttpService,
      private ConfigActions: ConfigActions,
      private processActions: ProcessActions,
      private zone: NgZone
  ) {

    this.menuVisible$.subscribe(res => this.menuVisible = res);
    this.flashVars$.subscribe(res => this.flashVars = res);
    this.targetDashboard$.subscribe(res => this.targetDashboard = res);

    this.mDashboardUrl = this.sanitizer.bypassSecurityTrustResourceUrl(apiUrl + 'swf/MDashboard.swf');

    // register public functions to allow flex calling
    window['flexInit'] = this.flexInit.bind(this);
    window['startProcess'] = this.startProcess.bind(this);
    window['reloadPatientPanel'] = this.reloadPatientBanner.bind(this);
    window['openIframePopup'] = this.openIframePopup.bind(this);
    window['blockUI'] = this.blockUI.bind(this);
    window['unblockUI'] = this.unblockUI.bind(this);
  }

  ngOnInit() {
    //this.flashVars = this.route.snapshot.data['flashVars'];
    this.flexService.nativeElement = this.mDashboard.nativeElement;
    this.flexService.nativeElement.setAttribute("flashvars", this.flashVars + "&frontendWrapping=true");

    if (this.router.url.indexOf('flex/') !== -1) {
      this.processActions.setDashboard(this.router.url); // save the url to retry flexCommunicator if flex is not yet initialized
      let dashboardName = this.router.url.substr(this.router.url.indexOf('flex/') + 5,this.router.url.length);
      this.flexService.flexCommunicator(dashboardName, null, null);
    }
  }

  flexInit(){
    this.flexService.flexInitialized = true;
    console.log('flex-component: flex initialized');

    let dashboardName = this.router.url.substr(this.targetDashboard.indexOf('flex/') + 5, this.targetDashboard.length);
    this.flexService.flexCommunicator(dashboardName, null, null);
  }

  reloadPatientBanner(): void {
    this.BannerActions.refresh();
  }

  startProcess(process: String) {
    if(this.menuVisible){
      this.ConfigActions.toggleMenu();
    }
    this.zone.run(() =>
    this.processActions.startProcess(process)
    );
  }

  blockUI() {
    this.zone.run(() =>
      this.httpService.loading = true
    );
  }

  unblockUI() {
    this.zone.run(() =>
      this.httpService.loading = false
    );
  }

  openIframePopup(url: string) {
    this.zone.run(() =>
      this.viewManager.openIframe(url)
    );
  }

}

