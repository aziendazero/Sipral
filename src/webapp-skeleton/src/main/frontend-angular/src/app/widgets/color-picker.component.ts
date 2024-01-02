import { Component, forwardRef } from '@angular/core';
import {NG_VALIDATORS, NG_VALUE_ACCESSOR} from '@angular/forms';
import { TextBoxComponent } from './textBox.component';
import { color2hex } from '../services/converters/any.converter';

@Component({
  selector: 'phi-color-picker',
  template: `
  <label for="{{id}}" *ngIf="renderedEl && widgetLabel">{{widgetLabel}}</label>
  <input #input type="color" [value]="value" *ngIf="renderedEl" [disabled]="disabled" (change)="onChange($event)" (blur)="onBlur()"
         [style.color]="value" [style.background-color]="value" (focus)="onFocus()">
  <span class="errorMessages" *ngIf="!isValid()">{{'REQUIRED' | translate}}</span>
  `,
  styles: ['input {width: 3em; cursor: default}'],
  providers: [{ provide: NG_VALUE_ACCESSOR, useExisting: forwardRef(() => ColorPickerComponent), multi: true },
              { provide: NG_VALIDATORS, useExisting: forwardRef(() => ColorPickerComponent), multi: true}]
})
export class ColorPickerComponent extends TextBoxComponent {

  private static hasNativeColorSupport;

  private static ieNativePicker;

  constructor() {
    super();

    if (ColorPickerComponent.hasNativeColorSupport === undefined) {
      // test if browser has native support for color input
      try {
        ColorPickerComponent.hasNativeColorSupport = !!(document.createElement('input').type = 'color');
      } catch (e) {
        ColorPickerComponent.hasNativeColorSupport = false;
      }

      // no native support...
      if (!ColorPickerComponent.hasNativeColorSupport) {
        // create object element
        ColorPickerComponent.ieNativePicker = document.createElement('object');
        ColorPickerComponent.ieNativePicker.classid = 'clsid:3050f819-98b5-11cf-bb82-00aa00bdce0b';

        document.body.appendChild(ColorPickerComponent.ieNativePicker);
      }
    }
  }

  onFocus() {
    if (!ColorPickerComponent.hasNativeColorSupport) {
      let hex =  ColorPickerComponent.ieNativePicker.ChooseColorDlg(this.value.replace(/#/, '')).toString(16);
      // add extra zeroes if hex number is less than 6 digits
      if (hex.length < 6) {
        let tmpstr = '000000'.substring(0, 6 - hex.length);
        hex = tmpstr.concat(hex);
      }
      this.value = '#' + hex;

      this.nativeElement.blur();
    }
  }

  get value() {
    return this._value;
  }

  set value(val) {
    if (val !== this._value) {

      this._value = val;

      if (this._converter === 'Integer Converter') {
        val = val.substring(1, val.length);
        val = parseInt(val, 16);
      }

      this.onChangeCallback(val);
      this.onTouchedCallback();
    }
  }

  writeValue(val) {
    if (val !== this._value) {
      if (val == null) {
        this._value = '#ffffff';
      } else {
        if (this._converter === 'Integer Converter') {
          this._value = color2hex(val);
        } else {
          this._value = val;
        }
      }
    }
  }

}
