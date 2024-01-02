import { Component, forwardRef, Input, HostBinding } from '@angular/core';
import {NG_VALIDATORS, NG_VALUE_ACCESSOR} from '@angular/forms';
import { TextBoxComponent } from './textBox.component';

@Component({
  selector: 'phi-password',
  template: `
  <label>{{widgetLabel}}</label>
  <input #input type="password" [value]="value" [disabled]="disabled" (change)="onChange($event)" (blur)="onBlur()" [autocomplete]="autocomplete"/>
  <span class="errorMessages" *ngIf="!isValid()">{{'REQUIRED' | translate}}</span>
  `,
  providers: [{ provide: NG_VALUE_ACCESSOR, useExisting: forwardRef(() => PasswordComponent), multi: true },
              { provide: NG_VALIDATORS, useExisting: forwardRef(() => PasswordComponent), multi: true}]
})
export class PasswordComponent extends TextBoxComponent {
  @HostBinding('class') clazz = 'phi-widget';
}
