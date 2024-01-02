
import { Component, Input, forwardRef, HostBinding, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { NG_VALIDATORS, NG_VALUE_ACCESSOR } from '@angular/forms';
import { TextBoxComponent } from './textBox.component';
import { dateToInputDate, dateToInputDateTime } from '../services/converters/date.converter';
import { default as InputPoly } from '../../polyfill/input-date/input';
import { default as Picker } from '../../polyfill/input-date/picker';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'phi-monthcalendar',
  template: `
  <label for={{id}} *ngIf="renderedEl && widgetLabel" >{{widgetLabel}}</label>
  <label for={{id}} *ngIf="inputType !== 'time'" 
    style="display:inline-block;  text-transform: uppercase;" class="lbl" >{{date|date:'EEE'}}</label>
  <input #input [attr.type]="inputType" *ngIf="renderedEl" [disabled]="disabled" (change)="onChange($event)" (blur)="onBlur()"/>
  <span class="errorMessages" *ngIf="!isValid()">{{'REQUIRED' | translate}}</span>
  `,
  providers: [{ provide: NG_VALUE_ACCESSOR, useExisting: forwardRef(() => MonthCalendarComponent), multi: true },
              { provide: NG_VALIDATORS, useExisting: forwardRef(() => MonthCalendarComponent), multi: true}]
})
export class MonthCalendarComponent extends TextBoxComponent implements AfterViewInit {

  protected static supportsDateTimeInput = InputPoly.supportsDateTimeInput();

  date: Date = null;
  inputType = 'date';

  @HostBinding('class') clazz = 'phi-widget';

  @Input()
  todayIsDefaultTime = false;

  constructor(private translateService: TranslateService) {
    super();
  }

  @Input()
  set dateTimeFormat(dateTimeFormat) {
    if ('Time only' === dateTimeFormat) {
      this.inputType = 'time';
    } else if ('Date only' === dateTimeFormat) {
      this.inputType = 'date';
    } else if ('Date and Time' === dateTimeFormat) {
      this.inputType = 'datetime-local';
    }
  }

  get value() {
    return this._value;
  }

  set value(val) {
    if (val === null) {
      this.date = null;
    } else if (this.inputType === 'time') {
      const HandM = val.split(':');
      if (this.date == null) {
        this.date = new Date(0);
      } else {
        this.date = new Date(this.date);
      }
      this.date.setHours(HandM[0]);
      this.date.setMinutes(HandM[1]);
    } else if (this.inputType === 'date') {
      this.date = new Date(val+" 00:00");
    } else {
      this.date = new Date(val);
    }
    
    if(!(this.date instanceof Date) || isNaN(this.date.getMilliseconds())) {
    	this.date = null;
    }
    
    this.onChangeCallback(this.date);
    this.onTouchedCallback();

    this.change.emit( {value: this.date} );
  }

  writeValue(val) {
    if (val === null) {
      if (this.todayIsDefaultTime) {
        this.date = new Date();
        this.date.setSeconds(0);
        this._value = this.convertValue(this.date);
      } else {
        this.date = null;
        this._value = '';
      }
    } else if (val instanceof Date) {
      this.date = val;
      this._value = this.convertValue(val);
    }

    // Polyfill
    if (!MonthCalendarComponent.supportsDateInput 
     || !MonthCalendarComponent.supportsDateTimeInput && this.inputType === 'datetime-local') {
      if (this.nativeElement && !this.nativeElement.hasAttribute(`data-has-picker`)) {
        Picker.instance = new Picker();
        new InputPoly(this.nativeElement, this.translateService.currentLang);
      }
    }

    this.nativeElement.value = this.value;

  }

  protected convertValue(date: Date): string {
    let convertedValue: string;

    if (this.inputType === 'time') {
      convertedValue = date.toTimeString().substr(0, 8); // date.toLocaleTimeString() is the same?
    } else if (this.inputType === 'date') {
      convertedValue = dateToInputDate(date); // date.toISOString().substr(0, 10);
    } else if (this.inputType === 'datetime-local') {
      convertedValue = dateToInputDateTime(date);
    }
    return convertedValue;
  }

}
