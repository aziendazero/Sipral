import {
  ApplicationRef, ComponentFactoryResolver, ComponentRef, EmbeddedViewRef, Inject,
  Injectable, Injector, Type
} from '@angular/core';
import { NavigationExtras, Router } from '@angular/router';
import { select } from '@angular-redux/store';
import { ErrorMessageComponent } from '../views/home/components/popup/components/error-message.component';
import { ConfigActions } from '../actions/config.action';

@Injectable()
export class ViewManager {

  @select(['process', 'cid']) cid$;
  cid: number;

  public formComponent: ComponentRef<any>;
  public popupComponent: any; //: ComponentRef<any>; // FIXME: because PopupRouterComponent checks existence of close() Method

  private popupViewId: string;

  constructor(
      private router: Router,
      @Inject('apiUrl') private url,
      private configActions: ConfigActions,
      private componentFactoryResolver: ComponentFactoryResolver,
      private appRef: ApplicationRef,
      private injector: Injector
  ) {
    this.cid$.subscribe(res => this.cid = res);
  }

  public setPopupViewId( popupViewId: string, ...param: Array<string>): Promise<any> {
    this.popupViewId = popupViewId;

    if (popupViewId) {
      let popupPath = ['form', popupViewId];
      if (param) {
        popupPath = popupPath.concat(param);
      }
      return this.router.navigate([{outlets: {popup: popupPath}}], {queryParamsHandling: 'preserve'}).then(function (ok) {
        return this.popupComponent;
      }.bind(this));
    } else {
      return this.router.navigate([{outlets: {popup: null}}]);
    }
  }

  openAlertMessage(title: string, message: string, showCancel = true, showOk = true, okLabel = 'OK',
    cancelLabel = 'Cancel', modal = true, onOk = null, onCancel = null)/*: Promise<any>*/ {

    this.setPopupViewId(null);

    this.configActions.put('alertMessage', {title, message, showCancel, showOk, okLabel, cancelLabel, modal, onOk, onCancel});

  }

  openIframe(url: string, external = false): Promise<any> {

    let extras: NavigationExtras = { queryParamsHandling: 'preserve' };

    if (external) {
      extras = {queryParams: { external: 'true' }};
    }

    return this.router.navigate([{outlets: {popup: ['iframe', url]}}], extras).then(function(ok) {
      return this.popupComponent;
    }.bind(this));
  }

  openReport(url: string): Promise<any> {
    const absoluteUrl = this.url + url + '?cid=' + this.cid;
    return this.openIframe(absoluteUrl);
  }

  openErrorMessage(title: string, error: any = null)/*: Promise<any>*/  {
    const previousUrl = this.router.url;

    return this.router.navigateByUrl('/error').then(function(ok) {
      if (this.formComponent instanceof ErrorMessageComponent) {
        this.formComponent.title = title;
        this.formComponent.error = error;
        this.formComponent.url = previousUrl;
      }
      return this.popupComponent;
    }.bind(this));
  }

  /**
   * Open generic menu configured in environment
   * @param {string} envMenuName name of env variable that defines menu items
   * @param {string} encounterId
   * @param {string} statusCode
   * @param {string} appointmentId
   * @returns {Promise<boolean>}
   */
  openMenu(envMenuName : string, encounterId: string, statusCode: string, appointmentId: string, invoicingClosed: boolean, sdoClosed: boolean /*, queryParams: Params*/) {
    return this.router.navigate([{outlets: {popup: ['menu', envMenuName, encounterId, statusCode, appointmentId, invoicingClosed, sdoClosed]}}]/*, extras*/);
  }

  openInNewTab(url: string) {
    window.open(url);
  }

  // private createComponent(component: any/*instructions: CreateComponentArgs*/, data): ComponentRef<any> {
  //   // const injector: Injector =  instructions.injector || instructions.vcRef.parentInjector;
  //   const cmpFactory: ComponentFactory<any> = this.injector.get(ComponentFactoryResolver).resolveComponentFactory(component);
  //   var componentRef: ComponentRef<any>;
  //   if (this.alertMessageContainer) {
  //     componentRef = this.alertMessageContainer.createComponent(
  //       cmpFactory
  //       // instructions.vcRef.length,
  //       // this.injector,
  //       // instructions.projectableNodes
  //     );
  //     componentRef.instance.componentRef = componentRef;
  //     componentRef.instance.title = data.title;
  //     componentRef.instance.message = data.message;
  //     componentRef.instance.showCancel = data.showCancel;
  //     componentRef.instance.showOk = data.showOk;
  //     componentRef.instance.okGoesBack = data.okGoesBack;
  //     componentRef.instance.cancelGoesBack = data.cancelGoesBack;
  //     componentRef.instance.modal = data.modal;
  //   }
  //   // } else {
  //   //   expComponent =  cmpFactory.create(this.injector);
  //   // }
  //   // let cdRef:ChangeDetectorRef = this.injector.get(ChangeDetectorRef);
  //   // cdRef.detectChanges();
  //   this.appRef.tick();
  //   return componentRef;
  // }

  appendComponentTo<T>(containerNode: HTMLElement, component: Type<T>): T {
    // 1. Create a component reference from the component
    const componentRef = this.componentFactoryResolver
      .resolveComponentFactory(component)
      .create(this.injector);

    // 2. Attach component to the appRef so that it's inside the ng component tree
    this.appRef.attachView(componentRef.hostView);

    // 3. Get DOM element from component
    const domElem = (componentRef.hostView as EmbeddedViewRef<any>).rootNodes[0] as HTMLElement;

    // 4. Append DOM element
    containerNode.style.display = 'flex';
    containerNode.appendChild(domElem);

    // // 5. Wait some time and remove it from the component tree and from the DOM
    // setTimeout(() => {
    //   this.appRef.detachView(componentRef.hostView);
    //   componentRef.destroy();
    // }, 3000);
    return componentRef.instance;
  }

}
