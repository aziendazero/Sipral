import { Component, Output, EventEmitter } from '@angular/core';
import {/*NgRedux, */select} from '@angular-redux/store';
import { ConfigActions } from '../../../../../actions/config.action';
// import { IAppState } from '../../../../../store/index';


@Component({
  selector: 'phi-alert-message',
  template: `
  <phi-popup *ngIf="show" [title]="title" [modal]="modal" (onClose)="onCancel()">
    <div id="LayoutAlert" class="layout vertical">
      <div class="lbl">
      <span [innerHtml]="message" style="white-space:pre-line;"></span>
      </div>
      <hr class="modal-buttons-seperator" *ngIf="showCancel || showOk">
      <div class="modal-buttons">
        <button (click)="onCancel()" class="button close-modal" *ngIf="showCancel">{{cancelLabel}}</button>
        <button (click)="onOk()" class="button button-primary close-modal" *ngIf="showOk">{{okLebel}}</button>
      </div>
    </div>
  </phi-popup>
  `
})
export class AlertMessageComponent {

  show = false;

  @select(['config', 'alertMessage']) alertMessage$;
  alertMessage;

  title;
  message;
  showCancel;
  showOk;
  okLebel;
  cancelLabel;
  modal;

  onOkCallback;
  onCancelCallback;

  close: Function;

  @Output() ok : EventEmitter<any> = new EventEmitter();
  @Output() cancel : EventEmitter<any> = new EventEmitter();

  constructor(private configActions: ConfigActions, /*private ngRedux: NgRedux<IAppState>*//*, private viewManager: ViewManager*/) {

    this.alertMessage$.subscribe((msg) => {
    // let msg = this.ngRedux.getState().config.alertMessage;

    if (msg) {
      this.show = true;

      this.title = msg.title;
      this.message = msg.message;
      this.showCancel = msg.showCancel;
      this.showOk = msg.showOk;
      this.okLebel = msg.okLabel;
      this.cancelLabel = msg.cancelLabel;
      this.modal = msg.modal;

      this.onOkCallback = msg.onOk;
      this.onCancelCallback = msg.onCancel;
    } else {
      this.show = false;
    }
    });
  }

  onOk() {
    if (this.onOkCallback) {
      this.onOkCallback();
    }
    this.configActions.remove('alertMessage');
  }

  onCancel() {
    if (this.onCancelCallback) {
      this.onCancelCallback();
    }
    this.configActions.remove('alertMessage');
  }
}
