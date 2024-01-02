import { Component, Injector, Input, Output, EventEmitter, OnInit, ViewChild, forwardRef } from '@angular/core';
import {
  AbstractControl, ControlValueAccessor, NG_VALIDATORS, NG_VALUE_ACCESSOR,
  ValidationErrors
} from '@angular/forms';
import { select } from '@angular-redux/store';
import { BaseForm } from '../../../widgets/form/base-form';
import { PatientEncounter, Dosage } from '../../../services/entities/act';
import { SelectItem } from '../../../services/datamodel/select-item';
import { WidgetEvent } from '../../../widgets/event/widget-event';
import { PatientEncounterActionService, TemplateDosageActionService } from '../../../services/actions';


@Component({
  selector: 'phi-frequency',
  templateUrl: './frequency.html',
  providers: [{ provide: NG_VALUE_ACCESSOR, useExisting: forwardRef(() => Frequency), multi: true },
              { provide: NG_VALIDATORS, useExisting: forwardRef(() => Frequency), multi: true}]
})
export class Frequency extends BaseForm implements ControlValueAccessor, OnInit {
  @select(['conversation']) conversation$;

  @ViewChild('form') form;

  PatientEncounter: PatientEncounter;

  _dosages: Array<Dosage>;

  set dosages(value: Array<Dosage>) {
    if (value) {
      this._dosages = value;
      const sdlId = this.PatientEncounterAction.getServiceDeliveryLocationId(this.PatientEncounter);
      this.frqncy = this.TemplateDosageAction.getTemplateDosageId4Activity(this._dosages, sdlId);

      if (this.frqncy === TemplateDosageActionService.personalized.value) {
        this.showDosage = true;
      }
    }
  }

  get dosages() {
    return this._dosages;
  }

  @Input() frequencyDomain: Array<SelectItem>;

  @Input() label: string = 'label_CmbxFrequency';

  frqncy: string | number;
  showDosage = false;

  constructor(injector: Injector,
              private PatientEncounterAction: PatientEncounterActionService,
              private TemplateDosageAction: TemplateDosageActionService) {
    super(injector);

    this.conversation$.subscribe(res => {
      this.PatientEncounter = res.PatientEncounter;
    });
  }

  ngOnInit() {
    this.form.valueChanges.subscribe(() => {
      if (this.form.valid && this._dosages) {
        this.onChangeCallback(this._dosages);
      } else {
        if (this.onChangeCallback) {
          this.onChangeCallback(null);
        }
      }
    });
  }

  fillTemplateDosages(event: WidgetEvent) {
    if (TemplateDosageActionService.personalized.value == event.value) {
      if (!this._dosages) {
        this._dosages = [];
        //     this.conversationActions.put('DosageList', new Datamodel([]));
      }
      this.showDosage = true;
    } else {
      this._dosages.splice(0, this.dosages.length);
      this._dosages.push(...this.TemplateDosageAction.findAndClone(event.value, false));
      //this.conversationActions.put('DosageList', new Datamodel(this.TemplateDosageAction.findAndClone(event.value, false)));
      this.showDosage = false;
    }
    this.onChangeCallback(this._dosages);
  }

  toggleDosage() {
    this.showDosage = !this.showDosage;
  }


  // Placeholders for the callbacks which are later providesd by the Control Value Accessor
  onTouchedCallback: () => {};
  onChangeCallback: (_: any) => {};

  // From ControlValueAccessor interface
  writeValue(value) {
    if (value) {
      this.dosages = value;
    }
  }
  // From ControlValueAccessor interface
  registerOnChange(fn) {
    this.onChangeCallback = fn;
  }
  // From ControlValueAccessor interface
  registerOnTouched(fn: any) {
    this.onTouchedCallback = fn;
  }
  // From ControlValueAccessor interface
  setDisabledState(/*isDisabled: boolean*/): void {

  }

  ctrl: AbstractControl;
  onValidatorCallback: () => void;

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
      return !this.ctrl.touched || this.ctrl.valid;
    } else {
      return true;
    }
  }

}
