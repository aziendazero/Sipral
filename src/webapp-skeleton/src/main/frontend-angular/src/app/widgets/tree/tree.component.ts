import { Component, Input, forwardRef } from '@angular/core';
import { NG_VALIDATORS, NG_VALUE_ACCESSOR } from '@angular/forms';
import { DictionaryManager } from '../../services';
import { ComboBoxComponent } from '../combo-box.component';

@Component({
  selector: 'phi-tree',
  template: `
  <label *ngIf="renderedEl && widgetLabel">{{widgetLabel}}</label>
  <ul *ngIf="renderedEl" class="widget-value {{styleClass}}">
    <li phi-node *ngFor="let item of listElementsExpression;" [item]="item" [value]="_value" [onSelect]="onSelect" [checkbox]="checkbox" [selectOnlyLeaves]="selectOnlyLeaves" [showCode]="showCode" [hide]="hide" [expand]="expand" [level]="1">
    </li>
  </ul>
  <span class="errorMessages" *ngIf="!isValid()">{{'REQUIRED' | translate}}</span>
  `,
  styles: ['ul {display: block;}', 'li {list-style: none;}'],
  providers: [{ provide: NG_VALUE_ACCESSOR, useExisting: forwardRef(() => TreeComponent), multi: true },
              { provide: NG_VALIDATORS, useExisting: forwardRef(() => TreeComponent), multi: true}]
})
export class TreeComponent extends ComboBoxComponent {

  @Input() checkbox = false;
  @Input() selectOnlyLeaves = false;
  @Input() showCode = false;

  // Hide CodeValues if hide number equal to current level of nesting and show its children
  @Input() hide: number = null;

  // Expand tree if expand number lower to current level of nesting
  @Input() expand: number = null;

  constructor(dictionaryManager: DictionaryManager) {
    super(dictionaryManager);
    this.lazy = true;
  }

  onSelect = (item) =>  {
    if (this.selectOnlyLeaves) {
      if (item.children === undefined && item.type !== 'C' || item.children) {
        return false;
      }
    }
    if (this.checkbox) {
      if (!(this._value.indexOf(item) !== -1)) { // .includes(item)) {
        this._value.push(item);
        this.cascadeSelect(item, true);
      } else {
        this._value.splice(this._value.indexOf(item), 1);
        this.cascadeSelect(item, false);
      }
    } else {
      if (!item) {
        this._value = null;
      } else {
        this._value = item;
      }
    }
    if (this.onChangeCallback) {
      this.onChangeCallback(this._value);
    }
    if (this.onTouchedCallback) {
      this.onTouchedCallback();
    }
    this.change.emit( {value: this._value} );
  };

  cascadeSelect(item, isChecked: boolean) {
    let nodeChildren = item.children;
    if (nodeChildren) {
      for (let i = 0; i < nodeChildren.length; i++) {
        let nodeChild = nodeChildren[i];
        if (isChecked) {
          if (!(this._value.indexOf(nodeChild) !== -1)) { //.includes(nodeChild)) {
            this._value.push(nodeChild);
            this.cascadeSelect(nodeChild, isChecked);
          }
        } else {
          if (this._value.indexOf(nodeChild) !== -1) { //.includes(nodeChild)) {
            this._value.splice(this._value.indexOf(nodeChild), 1);
            this.cascadeSelect(nodeChild, isChecked);
          }
        }
      }
    }
  }

  // From ControlValueAccessor interface
  writeValue(val) {
    if (this.checkbox) {
      this._value = val;
    } else {
      super.writeValue(val);
    }
  }

}
