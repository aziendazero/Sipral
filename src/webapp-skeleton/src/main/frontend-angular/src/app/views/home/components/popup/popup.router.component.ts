import { Component } from '@angular/core';
import { Location } from '@angular/common';
import { PopupComponent } from './popup.component';
import { ViewManager } from '../../../../services/view-manager';

@Component({
  selector: 'phi-popup-router',
  templateUrl: './popup.router.component.html'
})
export class PopupRouterComponent extends PopupComponent {

  constructor(private location: Location, private viewManager: ViewManager) {
    super();
  }

  popupLoaded(componentRef) {
    this.viewManager.popupComponent = componentRef;
  }

  popupUnLoaded(componentRef) {
    this.viewManager.popupComponent = null;
  }

  /**
   * Called by X btn
   * If popup content has a close method use that
   * else use this close.
   */
  onXclick() {
    if (this.viewManager.popupComponent && this.viewManager.popupComponent.close) {
      this.viewManager.popupComponent.close();
    } else {
      this.close();
    }
  }

  close() {
    this.location.back();
    this.onClose.emit(null);
  }

}
