import { Component, Input } from '@angular/core';
import { Location } from '@angular/common';

@Component({
  selector: 'phi-error',
  template: `
  <phi-popup [title]="title" width="90%" (onClose)="close()">
    <div id="LayoutAlert" class="layout vertical fullHeightWidth">
      <p>{{date | dateFormat: 'short'}}</p>
      <p *ngIf="message">{{url}}</p>
      <p *ngIf="message">{{navigator.userAgent}}</p>
      <pre *ngIf="message">{{message}}</pre>
      <pre *ngIf="errorz">{{errorz}}</pre>
      <hr class="modal-buttons-seperator">
      <div class="modal-buttons">
        <button (click)="onOk()" class="button button-primary close-modal">OK</button>
      </div>
    </div>
  </phi-popup>
  `,
  styles: ['pre {word-break: break-all;}']
})
export class ErrorMessageComponent {

  date: Date;
  title = 'Error';
  message;
  errorz;
  url;

  navigator;

  constructor(protected location: Location) {
    this.date = new Date();
    this.navigator = window.navigator;
  }

  @Input()
  set error(err) {
    if (err) {
      this.message = err.stack;
      this.errorz = err.rejection;
    } else {
      this.message = null;
      this.errorz = null;
    }
  };

  onOk() {
    delete sessionStorage['cid'];
    window.location.replace('.')
  }

  close() {
    this.location.back();
  }
}
