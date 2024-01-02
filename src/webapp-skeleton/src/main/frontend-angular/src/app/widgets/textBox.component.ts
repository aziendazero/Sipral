import {
  Component, Input, forwardRef, OnInit, HostBinding, Output, EventEmitter, ViewChild, AfterViewInit,
  ElementRef
} from '@angular/core';
import { AbstractControl, ControlValueAccessor, NG_VALUE_ACCESSOR, NG_VALIDATORS, ValidationErrors, Validator } from '@angular/forms';
import { WidgetEvent } from './event/widget-event';
import { default as InputPoly } from '../../polyfill/input-date/input';
import { default as Picker } from '../../polyfill/input-date/picker';


@Component({
  selector: 'phi-textbox',
  template: `
  <label *ngIf="renderedEl && widgetLabel">{{widgetLabel}}</label>
  <input #input [type]="inputType" [value]="value" *ngIf="renderedEl" [disabled]="disabled" [attr.min]="minimum" [attr.max]="maximum"
    (change)="onChange($event)" (blur)="onBlur()" [autocomplete]="autocomplete" class="{{styleClass}}"/>
    <span class="errorMessages" *ngIf="!isValid()">{{'REQUIRED' | translate}}</span>
  `,
  // #inputz="ngModel" [ngClass]="{'error': inputz.invalid && inputz.dirty}"
  providers: [{ provide: NG_VALUE_ACCESSOR, useExisting: forwardRef(() => TextBoxComponent), multi: true },
  { provide: NG_VALIDATORS, useExisting: forwardRef(() => TextBoxComponent), multi: true }]
})
export class TextBoxComponent implements ControlValueAccessor, Validator, OnInit, AfterViewInit {

  protected static supportsDateInput = InputPoly.supportsDateInput();

  @ViewChild('input')
  protected input: ElementRef;

  protected nativeElement;

  @HostBinding('class') clazz = 'phi-widget';

  @Input() autoFocus = false;

  @Input() id: string;
  @Input() tooltip: string;

  // @Input() required: boolean; // [required]="required"
  @Input() renderedEl: boolean;
  @Input() disabled: string;

  @Input() format: string;
  @Input() widgetLabel: string;

  @Input() minimum: string;
  @Input() maximum: string;
  @Input() defaultValue: string;

  @Input() styleClass: string;

  @Input() autocomplete: string;

  @Output() change: EventEmitter<WidgetEvent> = new EventEmitter<WidgetEvent>();

  @Output() mouseup: EventEmitter<WidgetEvent> = new EventEmitter<WidgetEvent>();

  inputType = 'text';

  protected _value: any = '';

  protected _converter: any = null;

  // Placeholders for the callbacks which are later providesd by the Control Value Accessor
  ctrl: AbstractControl;
  onValidatorCallback: () => void;
  onTouchedCallback: () => {};
  onChangeCallback: (_: any) => {};
  onMouseUpCallback: (_: any) => {};

  ngOnInit() {
    if (typeof this.renderedEl === 'undefined') {
      this.renderedEl = true;
    }
  }

  ngAfterViewInit() {
    if (this.input) {
      this.nativeElement = this.input.nativeElement;
    }
    if (this.autoFocus) {
      if (!this.input) {
        // console.log('WARNING: #input is missing in the widget. Autofocus will not work');
        return;
      }
      this.input.nativeElement.focus();
    }
  }

  @Input()
  set converter(converter) {
    this._converter = converter;
    if ('Integer Converter' === converter || 'Double Converter' === converter) {
      this.inputType = 'number';
    }
  }

  get value() {
    return this._value;
  }

  set value(val) {
    if (val !== this._value) {
      this._value = val;
      let convertedValue = this.valueConverter(val);

      if (this.onChangeCallback) {
        this.onChangeCallback(convertedValue);
      }
      if (this.onTouchedCallback) {
        this.onTouchedCallback();
      }
      this.change.emit( {value: convertedValue} );
    }
  }

  valueConverter(val) {
    let convertedValue;

    if (this._converter === 'Integer Converter') {
      convertedValue = +val; // parseInt(val);
    } else if (this._converter === 'Double Converter') {
      convertedValue = parseFloat(val);
    } else if (this._converter === 'Date Time Converter') { // Used by slider, extens
      convertedValue = new Date(+val);
    } else {
      convertedValue = val;
    }
    return convertedValue;
  }

  onChange(event) {
    event.stopPropagation();
    const eventVal = event.target.value;
    if (eventVal !== '') {
      this.value = eventVal;
    } else {
      this.value = null;
    }
  }

  onMouseUp(event) {
    event.stopPropagation();
    const eventVal = event.target.value;
    if (eventVal !== '') {
      this.value = eventVal;
    } else {
      this.value = null;
    }

    let convertedValue = this.valueConverter(this.value);

    if (this.onMouseUpCallback) {
      this.onMouseUpCallback(convertedValue);
    }
    this.mouseup.emit({ value: convertedValue });
  }

  // Set touched on blur
  onBlur() {
    if (this.onTouchedCallback) {
      this.onTouchedCallback();
    }
  }
  // From ControlValueAccessor interface
  writeValue(val) {
    if (val !== this._value) {
      if (val == null) {
        if (this.defaultValue) {
          this._value = this.defaultValue;
        } else {
          this._value = '';
        }
      } else {
        if (val instanceof Date && this._converter === 'Date Time Converter') { // Used by slider, extens
          this._value = val.getTime().toString();
        } else {
          this._value = val;
        }
      }
    }

    // Polyfill
    if (!TextBoxComponent.supportsDateInput && this.inputType === 'number') {
      if (this.nativeElement && !this.nativeElement.hasAttribute(`data-has-picker`)) {
        Picker.instance = new Picker();
        new InputPoly(this.nativeElement);
      }
    }
  }
  // From ControlValueAccessor interface
  registerOnChange(fn) {
    this.onChangeCallback = fn;
  }
  registerOnMouseUp(fn) {
    this.onMouseUpCallback = fn;
  }
  // From ControlValueAccessor interface
  registerOnTouched(fn: any) {
    this.onTouchedCallback = fn;
  }
  // From ControlValueAccessor interface
  setDisabledState(/*isDisabled: boolean*/): void {

  }

  // From Validator interface
  validate(c: AbstractControl): ValidationErrors {
    this.ctrl = c;
    return null;
  }
  // From Validator interface
  registerOnValidatorChange?(fn: () => void): void {
    this.onValidatorCallback = fn;
  };

  isValid(): boolean {
    if (this.ctrl) {
      return this.ctrl.disabled || !this.ctrl.touched || this.ctrl.valid;
    } else {
      return true;
    }
  }
}
