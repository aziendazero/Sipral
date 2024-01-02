import { WidgetEvent } from './event/widget-event';
import { Component, Input, forwardRef, HostBinding, Output, EventEmitter } from '@angular/core';
import { NG_VALIDATORS, NG_VALUE_ACCESSOR } from '@angular/forms';
import { TextBoxComponent } from './textBox.component';
import { DateFormatPipe } from '../services/converters/date-format.pipe';

@Component({
  selector: 'phi-slider',
  template: `
    <label *ngIf="renderedEl && widgetLabel">{{widgetLabel}}</label>
    <section [class.range-slider]="range">
      <div class="valueContainer"><span class="value" [class.disabled]="disabled">{{getValue2display()}}</span></div>
      <span *ngIf="showBounds" class="min">{{getConvertedValue(minimum)}}</span>
      <input *ngIf="range" [value]="highValue" [disabled]="disabled" [min]="minimum" [max]="maximum"
        [step]="step" [attr.type]="'range'" (input)="onHighChange($event)"
        (change)="onHighChange($event)" (mouseup)="onHighMouseUp($event)" (blur)="onBlur()" [class]="styleClass"/>
      <input [value]="value" [disabled]="disabled" [min]="minimum" [max]="maximum"
        [step]="step" [attr.type]="'range'" (input)="onChange($event)"
        (change)="onChange($event)" (blur)="onBlur()" (mouseup)="onMouseUp($event)" [class]="styleClass"/>
      <span *ngIf="showBounds" class="max">{{getConvertedValue(maximum)}}</span>
    </section>
    <span class="errorMessages" *ngIf="!isValid()">{{'REQUIRED' | translate}}</span>
  `,
  styleUrls: ['./slider.component.scss'],
  providers: [{ provide: NG_VALUE_ACCESSOR, useExisting: forwardRef(() => SliderComponent), multi: true },
    { provide: NG_VALIDATORS, useExisting: forwardRef(() => SliderComponent), multi: true}]
})
export class SliderComponent extends TextBoxComponent {
  @HostBinding('class') clazz = 'phi-widget';

  @Input() step: string;
  @Input() showBounds: boolean = true;

  @Input() range: boolean = false;

  _highValue;
  convertedHighValue;

  constructor(private datePipe: DateFormatPipe) {
    super();
  }

  onHighChange(event) {
    event.stopPropagation();
    const eventVal = event.target.value;
    if (eventVal !== '') {
      this.highValue = eventVal;
    } else {
      this.highValue = null;
    }
    this.change.emit( {highValue: this.convertedHighValue} );
    // console.log('onHighChange ' + this.convertedHighValue );
  }

  onHighMouseUp(event) {
    event.stopPropagation();
    const eventVal = event.target.value;
    if (eventVal !== '') {
      this.highValue = eventVal;
    } else {
      this.highValue = null;
    }
    this.mouseup.emit( {highValue: this.convertedHighValue} );
    // console.log('onHighMouseUp ' + this.convertedHighValue );
  }

  get highValue() {
    return this._highValue;
  }

  @Input()
  set highValue(val) {
    if (val !== this._highValue) {
      this._highValue = val;

      if (this._converter === 'Integer Converter') {
        this.convertedHighValue = +val; // parseInt(val);
      } else if (this._converter === 'Double Converter') {
        this.convertedHighValue = parseFloat(val);
      } else if (this._converter === 'Date Time Converter') { // Used by slider, extends
        this.convertedHighValue = new Date(+val);
      } else {
        this.convertedHighValue = val;
      }
    }
  }

  getConvertedValue(value) {
    if (value && value !== '' && this._converter === 'Date Time Converter') {
      return this.datePipe.transform(new Date(+value), 'shortDate');
    } else {
      return value;
    }
  }

  getValue2display() {
    let value = this.getConvertedValue(this._value);
    if (this.range) {
      value = value +  ' - ' + this.getConvertedValue(this._highValue);
    }
    return value;
  }

}
