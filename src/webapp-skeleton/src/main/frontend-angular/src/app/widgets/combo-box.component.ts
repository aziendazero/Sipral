import { Component, Input, OnInit, forwardRef, HostBinding } from '@angular/core';
import { NG_VALIDATORS, NG_VALUE_ACCESSOR } from '@angular/forms';
import { TextBoxComponent } from './textBox.component';
import { DictionaryManager } from '../services/dictionary-manager';

@Component({
  selector: 'phi-combobox',
  template: `
  <label *ngIf="renderedEl && widgetLabel">{{widgetLabel}}</label>
  <select [value]="selectedIndex" *ngIf="renderedEl" [disabled]="disabled" (change)="onChange($event)" (blur)="onBlur()" class="{{styleClass}}">
    <option [value]="-1" [selected]="selectedIndex == -1" >{{emptyField}}</option>
    <option *ngFor="let option of list; let i = index" [value]="i" [selected]="selectedIndex === i">
      {{option.label}}{{option.currentTranslation}}
    </option>
  </select>
  <span class="errorMessages" *ngIf="!isValid()">{{'REQUIRED' | translate}}</span>
  `,
  providers: [{ provide: NG_VALUE_ACCESSOR, useExisting: forwardRef(() => ComboBoxComponent), multi: true },
              { provide: NG_VALIDATORS, useExisting: forwardRef(() => ComboBoxComponent), multi: true}]
})
export class ComboBoxComponent extends TextBoxComponent {

  protected selectedIndex: number | Array<number> = -1;

  @Input() emptyField: string;

  @Input()
  set domain(domain) {
    if (domain) {
      this.dictionaryManager.get(domain, this.lazy).then((domain) => {
        this.listElementsExpression = domain;
      });
    } else {
      this.list = [];
      this._value = null;
    }
  }

  @Input()
  set listElementsExpression(listElementsExpression) {
    this.list = listElementsExpression;
    if (this._value) {
      if (this._value instanceof Array) {
        this.selectedIndex = [];
        for (let value of this._value) {
          const index = this.indexInDomain(value);
          if (index !== -1) {
            this.selectedIndex.push(index);
          }
        }
      } else {
        this.selectedIndex = this.indexInDomain(this._value);
      }
    }
  }

  get listElementsExpression() {
    return this.list;
  }

  protected list: Array<any>;

  protected lazy = false;

  @HostBinding('class') clazz = 'phi-widget phi-combobox';

  constructor(private dictionaryManager: DictionaryManager) {
    super();
    this.emptyField = this.emptyField || '...';
  }

  get value() {
    return this._value;
  }

  set value(val) {
    this.selectedIndex = parseInt(val);
    if (this.selectedIndex === -1) {
      this._value = null;
    } else {
      const selectedValue = this.list[this.selectedIndex];
      this._value = this.convertValue(selectedValue);
    }
    if (this.onChangeCallback) {
      this.onChangeCallback(this._value);
    }
    if (this.onTouchedCallback) {
      this.onTouchedCallback();
    }
    //this.change.emit(convertedValue);
    this.change.emit({ value: this._value });
  }

  // From ControlValueAccessor interface
  writeValue(val) {
    if (val !== this._value) {
      if (val != null) {
        if (this.list && this.list.length > 0) {
          this.selectedIndex = this.indexInDomain(val);
        }
        this._value = val;
      } else if (this.emptyField) {
        this.selectedIndex = -1;
      }
    }
  }

  protected convertValue(selectedValue): any {
    let convertedValue;

    if (this._converter === 'Code Value Converter') {
      convertedValue = selectedValue;
    } else if (this._converter === 'Entity Converter') {
      const entitNameAndId: String[] = selectedValue.value.split("-");
      convertedValue = { entityName: entitNameAndId[0], internalId: entitNameAndId[1] };
    } else if (this._converter === 'Integer Converter') {
      convertedValue = parseInt(selectedValue.value);
    } else { // No converter
      convertedValue = selectedValue.value || selectedValue.code;
    }
    return convertedValue;
  }

  protected indexInDomain(val): number {
    let index = -1;
    if (this.list && this.list !== undefined) {
      index = this.list.indexOf(val);
      if (index === -1) {
        this.list.map((element, i) => {
          if (val.id && val.id === element.id) { //CodeValue
            index = i;
          } else if (val.code && val.code === element.code) { //CodeValueProxy
            index = i;
          } else if (val === element.value) { //SelectItem
            index = i;
          } else if (val === element.code) { //CodeValue without converter
            index = i;
          }
        });
      }
    }
    return index;
  }

}
