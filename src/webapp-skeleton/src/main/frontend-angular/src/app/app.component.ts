import { Component } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { ViewManager } from './services/view-manager';
import { PopupRouterComponent } from './views/home/components/popup/popup.router.component';
import '../style/normalize.css';
import '../style/skeleton.css';
import '../style/app.scss';


@Component({
  selector: 'phi-app',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent {

  title = 'Phi';

  constructor(
    translate: TranslateService,
    private viewManager: ViewManager
  ) {
    translate.setDefaultLang('it');
  }

  popupLoaded(componentRef) {
    if (!(componentRef instanceof PopupRouterComponent)) {
      // console.log('Popup loaded ' + componentRef.constructor.name);
      this.viewManager.popupComponent = componentRef;
    }
  }

  popupUnLoaded(componentRef) {
    if (!(componentRef instanceof PopupRouterComponent)) {
      // console.log('Popup unloaded ' + componentRef.constructor.name);
      this.viewManager.popupComponent = null;
    }
  }

  /*
  @HostListener('window:beforeunload', ['$event'])
  beforeunloadHandler(event) {
    event.preventDefault();
    this.authGuard.logout();
    return false;
  }
  */
}
