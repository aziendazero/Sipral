import { Component, forwardRef, HostBinding } from '@angular/core';
import {NG_VALIDATORS, NG_VALUE_ACCESSOR} from '@angular/forms';
import { TextBoxComponent } from './textBox.component';

@Component({
  selector: 'phi-checkbox',
  template: `
  <label *ngIf="renderedEl">{{widgetLabel}}
    <input type="checkbox" value="{{value}}" [checked]="value" (change)="onChange($event)" *ngIf="renderedEl" [disabled]="disabled"/>
    <span class="label-body"><ng-content></ng-content></span>
  </label>
  <span class="errorMessages" *ngIf="!isValid()">{{'REQUIRED' | translate}}</span>
  `,
  providers: [{ provide: NG_VALUE_ACCESSOR, useExisting: forwardRef(() => CheckBoxComponent), multi: true },
              { provide: NG_VALIDATORS, useExisting: forwardRef(() => CheckBoxComponent), multi: true}]
})
export class CheckBoxComponent extends TextBoxComponent {

  @HostBinding('class') clazz = 'phi-widget';

  onChange(event) {
    event.stopPropagation();
    this.value = event.target.checked;
  }

  // From ControlValueAccessor interface
  writeValue(val) {
    if (val !== this._value) {
      this._value = val;
    }
  }

}
