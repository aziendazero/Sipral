import { Component, Input, forwardRef, HostBinding } from '@angular/core';
import { NG_VALIDATORS, NG_VALUE_ACCESSOR } from '@angular/forms';
import { ComboBoxComponent } from './combo-box.component';
import { DictionaryManager } from '../services/dictionary-manager';

@Component({
  selector: 'phi-groupcheckbox',
  // host: {'class': 'layout vertical'},
  template: `
    <label *ngIf="renderedEl && widgetLabel">{{widgetLabel}}</label>
    <div *ngIf="renderedEl" class="widget-value {{layout}} {{styleClass}}">
      <label *ngFor="let option of listElementsExpression; let i = index">
        <input type="checkbox" [value]="i" [checked]="isChecked(i)" (change)="onChange($event)" (blur)="onBlur()"/>
        <span class="label-body">{{option.label}}{{option.currentTranslation}}</span>
      </label>
    </div>
    <span class="errorMessages" *ngIf="!isValid()">{{'REQUIRED' | translate}}</span>
  `,
  providers: [{ provide: NG_VALUE_ACCESSOR, useExisting: forwardRef(() => GroupCheckBoxComponent), multi: true },
              { provide: NG_VALIDATORS, useExisting: forwardRef(() => GroupCheckBoxComponent), multi: true}]
})
export class GroupCheckBoxComponent extends ComboBoxComponent {
  @Input() layout: string;

  @HostBinding('class') clazz = 'phi-widget';

  constructor(dictionaryManager: DictionaryManager) {
    super(dictionaryManager);
    this._value = [];
  }

  // Same as: value?.includes(i) but debuggable
  isChecked(i) {
    if (this._value) {
      return this._value.indexOf(i) !== -1; // .includes(i);
    }
    return false;
  }

  onChange(event) {
    event.stopPropagation();

    const index = parseInt(event.target.value);

    if (event.target.checked) {
      this._value.push(index);
    } else {
      this._value.splice(this._value.indexOf(index), 1);
    }

    const convertedValues = this.listElementsExpression
      .filter((element, i) => this._value.indexOf(i) !== -1) //.includes(i))
      .map((value) => this.convertValue(value));

    if (this.onChangeCallback) {
      this.onChangeCallback(convertedValues);
    }
    if (this.onTouchedCallback) {
      this.onTouchedCallback();
    }

    this.change.emit({ value: convertedValues, element: this.convertValue(this.listElementsExpression[index]), selected: event.target.checked });
  }

  // From ControlValueAccessor interface
  writeValue(val) {
    this._value = [];
    if (val != null) {
      if (typeof this.listElementsExpression !== 'undefined') {
        if (val instanceof Array) {
          for (let value of val) {
            const index = super.indexInDomain(value);
            if (index !== -1) {
              this._value.push(index);
            }
          }
        }
      } else {
        this._value = val;
      }
    }
  }

}
