import { Component, Injector, ViewChild, forwardRef, OnInit } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { select } from '@angular-redux/store';
import { BaseForm } from '../../../widgets/form/base-form';
import { Datamodel } from '../../../services/datamodel/datamodel';
import { ConversationActions } from '../../../actions/conversation.actions';
import { WidgetEvent } from '../../../widgets/event/widget-event';

@Component({
selector: 'phi-dosage',
templateUrl: './dosage.html',
providers: [{ provide: NG_VALUE_ACCESSOR, useExisting: forwardRef(() => Dosage), multi: true }]
})
export class Dosage extends BaseForm implements ControlValueAccessor, OnInit {
  @select(['conversation']) conversation$;

  @ViewChild('form') form;

  DosageList: Datamodel;
  dayInterval = 1;

  constructor(injector: Injector, private conversationActions: ConversationActions) {
    super(injector);
    this.conversation$.subscribe(res => {
      this.DosageList = res.DosageList;
    });
  }
  ngOnInit() {
    this.form.valueChanges.subscribe(() => {
      if (this.form.valid && this.DosageList) {
        this.onChangeCallback(this.DosageList.entities);
      } else {
        this.onChangeCallback(null);
      }
    });
  }

  /**
   * DayInterval changed, update all dosages
   * @param dayInterval
   */
  dayIntervalChanged(event: WidgetEvent) {
    for (let dosage of this.DosageList.entities) {
      dosage.dayInterval = event.value;
    }
  }

  addDosage() {
    this.DosageList.entities.push( {dayInterval: this.dayInterval} );
  }

  removeDosage(dosage) {
    const dosageIndex = this.DosageList.entities.indexOf(dosage);
    if (dosageIndex !== -1) {
      this.DosageList.entities.splice(dosageIndex, 1);
    }
  }

  // ngOnDestroy() {
  //   this.conversationActions.remove('DosageList');
  // }

  // Placeholders for the callbacks which are later providesd by the Control Value Accessor
  onTouchedCallback: () => {};
  onChangeCallback: (_: any) => {};

  // From ControlValueAccessor interface
  writeValue(value) {
    if (value) {
      this.DosageList = new Datamodel(value);
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
}
