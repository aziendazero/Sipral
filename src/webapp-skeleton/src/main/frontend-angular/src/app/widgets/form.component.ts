import { Component, Input } from '@angular/core';

@Component({
  selector: '[phi-form]',
  template: `
    <h1 *ngIf="label !== undefined" id="formTitle">{{label}}</h1>
    <ng-content></ng-content>
  `
})
export class FormComponent {
  @Input() id: string;
  @Input() label: string;
}
