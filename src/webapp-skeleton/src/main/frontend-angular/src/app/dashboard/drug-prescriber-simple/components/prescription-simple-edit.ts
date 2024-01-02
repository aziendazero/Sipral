import { Component, forwardRef, Injector, Input } from '@angular/core';
import { NG_VALUE_ACCESSOR } from '@angular/forms';
import { BaseForm } from '../../../widgets/form/base-form';
import { PrescriptionSimple } from '../../../services/entities/base-entity';
import { Datamodel } from '../../../services/datamodel/datamodel';
import { ViewManager } from '../../../services/view-manager';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'phi-prescription-simple-edit',
  templateUrl: './prescription-simple-edit.html',
  styleUrls: ['./prescription-simple-edit.scss'],
  providers: [{ provide: NG_VALUE_ACCESSOR, useExisting: forwardRef(() => PrescriptionSimpleEdit), multi: true }]
})
export class PrescriptionSimpleEdit extends BaseForm  {

  PrescriptionSimpleList: Datamodel;

  // @ViewChild('form') form: NgForm;

  @Input()
  route: string;

  @Input()
  limitedSchedule: boolean = false;


  @Input()
  issuperuser: boolean = false;

  static maxPrescriptions = 20;

  constructor(injector: Injector,
              private viewManager: ViewManager,
              private translateService: TranslateService) {
    super(injector);
  }

  add() {
    if (this.PrescriptionSimpleList && this.PrescriptionSimpleList.entities && this.PrescriptionSimpleList.entities.length >= PrescriptionSimpleEdit.maxPrescriptions) {
      this.viewManager.openAlertMessage(this.translateService.instant('Warning'), this.translateService.instant('max-prescriptions-warning'), false);
    } else {

      const prescriptionSimple: PrescriptionSimple = {};
      if (this.route !== 'various') {
        prescriptionSimple.routeCode = {codeSystemName: 'PHIDIC', domainName: 'AdministrationRoute', code: this.route};
      }

      this.PrescriptionSimpleList.entities.push(prescriptionSimple);
      this.onChangeCallback(this.PrescriptionSimpleList);
    }
  }

  remove(index: number) {
    this.PrescriptionSimpleList.entities.splice(index,1);
    this.onChangeCallback(this.PrescriptionSimpleList);
  }

  /**
   * fixes: :
   * 1 aggiungi riga,
   * 2 cancelli penultima,
   * 3 insericsi nell ultima,
   * 4 aggiungi riga:
   * perdita modifiche
   * see: https://stackoverflow.com/questions/42322968/angular2-dynamic-input-field-lose-focus-when-input-changes
   * @param index
   * @param item
   * @returns {any}
   */
  trackByFn(index: any, item: any) {
    return index;
  }



  // Placeholders for the callbacks which are later providesd by the Control Value Accessor
  onTouchedCallback: () => {};
  onChangeCallback: (_: any) => {};

  // From ControlValueAccessor interface
  writeValue(value) {
    if (value) {
      this.PrescriptionSimpleList = value;
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
