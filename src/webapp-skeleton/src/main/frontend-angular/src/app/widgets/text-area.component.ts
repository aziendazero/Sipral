import { Component, forwardRef, Input, HostBinding } from '@angular/core';
import { NG_VALIDATORS, NG_VALUE_ACCESSOR } from '@angular/forms';
import { TextBoxComponent } from './textBox.component';

@Component({
  selector: 'phi-textarea',
  template: `
  <label *ngIf="renderedEl && widgetLabel">{{widgetLabel}}</label>
  <textarea #input textAreaRows="rows" [value]="value"
    (change)="onChange($event)" (blur)="onBlur()" (keyup)="onKeyUp($event)"
    *ngIf="renderedEl" [disabled]="disabled" [class]="styleClass" [maxLength]="maxlength"></textarea>
  <br/>
  <span class="errorMessages" *ngIf="!isValid()">{{'REQUIRED' | translate}}</span>
  `,
  providers: [{ provide: NG_VALUE_ACCESSOR, useExisting: forwardRef(() => TextAreaComponent), multi: true },
              { provide: NG_VALIDATORS, useExisting: forwardRef(() => TextAreaComponent), multi: true}]
})
export class TextAreaComponent extends TextBoxComponent {
  @HostBinding('class') clazz = 'phi-widget phi-textarea';

  @HostBinding('class.no-label')
  get noLabel() {
    return !this.widgetLabel;
  }

  @Input() rows;
  @Input() maxlength: number = 255;

  onKeyUp(event) {
    event.stopPropagation();
    const eventVal = event.target.value;
    if (eventVal !== '') {
      this.value = eventVal;
    } else {
      this.value = null;
    }
  }

}
