/**
 * Created by Daniele Tarticchio on 21/06/2017.
 */

import { Component, Input, forwardRef, HostBinding } from '@angular/core';
import { NG_VALIDATORS, NG_VALUE_ACCESSOR } from '@angular/forms';
import { ComboBoxComponent } from './combo-box.component';

@Component({
  selector: 'phi-radiogroup',
  template: `
  <label *ngIf="renderedEl && widgetLabel">{{widgetLabel}}</label>
  <div class="widget-value {{layout}} {{styleClass}}">
    <label *ngFor="let option of listElementsExpression; let i = index">
      <input type="radio" name="{{id}}" [value]="i" [checked]="selectedIndex == i" [disabled]="disabled" (change)="onChange($event)"/>
      <span>{{option.label}}{{option.currentTranslation}}</span>
    </label>
  </div>
  <span class="errorMessages" *ngIf="!isValid()">{{'REQUIRED' | translate}}</span>
  `,
  styles: ['span {font-weight: normal; padding-left:5px}'],
  providers: [{ provide: NG_VALUE_ACCESSOR, useExisting: forwardRef(() => RadioGroupComponent), multi: true },
              { provide: NG_VALIDATORS, useExisting: forwardRef(() => RadioGroupComponent), multi: true}]
})

export class RadioGroupComponent extends ComboBoxComponent {
  @Input() layout: string;
  @HostBinding('class') clazz = 'phi-widget';
}
