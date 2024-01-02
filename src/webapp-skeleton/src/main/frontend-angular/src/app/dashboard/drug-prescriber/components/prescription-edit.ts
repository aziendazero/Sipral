import { Component, forwardRef, Injector, Input, OnInit, ViewChild } from '@angular/core';
import {
  AbstractControl, ControlValueAccessor, NG_VALIDATORS, NG_VALUE_ACCESSOR,
  ValidationErrors
} from '@angular/forms';
import { select } from '@angular-redux/store';
import { Prescription, PrescriptionMedicineGeneric } from '../../../services/entities/base-entity';
import { Dosage, PatientEncounter } from '../../../services/entities/act';
import { ServiceDeliveryLocation } from '../../../services/entities/role';
import {
  PrescriptionActionService, PatientEncounterActionService,
  TemplateDosageActionService, ServiceDeliveryLocationActionService
} from '../../../services/actions';
import { BaseForm } from '../../../widgets/form/base-form';
import { SelectItem } from '../../../services/datamodel/select-item';
import { ViewManager } from '../../../services/view-manager';

@Component({
  selector: 'phi-prescription-edit',
  templateUrl: './prescription-edit.html',
  providers: [{ provide: NG_VALUE_ACCESSOR, useExisting: forwardRef(() => PrescriptionEdit), multi: true },
              { provide: NG_VALIDATORS, useExisting: forwardRef(() => PrescriptionEdit), multi: true}]
})
export class PrescriptionEdit extends BaseForm implements ControlValueAccessor, OnInit {

  @select(['conversation', 'Prescription']) Prescription$;
  Prescription: Prescription;

  @select(['conversation', 'PatientEncounter']) PatientEncounter$;
  PatientEncounter: PatientEncounter;

  @Input()
  favoriteConfiguration;

  @Input()
  dischargeMode;

  periods: Array<SelectItem> =  [{label: '1', value: 1}, {label: '2', value: 2}, {label: '3', value: 3},
    {label: '4', value: 4}, {label: '5', value: 5}, {label: '6', value: 6}, {label: '7', value: 7},
    {label: '14', value: 14}, {label: '21', value: 21}, {label: '28', value: 28}];

  days: Array<SelectItem> =  [{label: '0', value: 0}, {label: '+1', value: 1}, {label: '+2', value: 2},
    {label: '+3', value: 3}, {label: '+4', value: 4}, {label: '+5', value: 5}, {label: '+6', value: 6}];

  speedUMcodes: Array<SelectItem> =  [{label: 'h', value: 'ml/h'}, {label: 'min', value: 'ml/min'}];
  timeUMcodes: Array<SelectItem> =  [{label: 'h', value: 'h'}, {label: 'min', value: 'min'}];

  showAdvanced: boolean;

  frqncyz: any; // string | number;

  dayInterval = 1;

  @ViewChild('form') form;

  // Placeholders for the callbacks which are later providesd by the Control Value Accessor
  onTouchedCallback: () => {};
  onChangeCallback: (_: any) => {};

  ctrl: AbstractControl;
  onValidatorCallback: () => void;

  constructor(injector: Injector,
              private PrescriptionAction: PrescriptionActionService,
              private PatientEncounterAction: PatientEncounterActionService,
              public TemplateDosageAction: TemplateDosageActionService,
              private ServiceDeliveryLocationAction: ServiceDeliveryLocationActionService,
              private viewManager: ViewManager) {
    super(injector);

    this.Prescription$.subscribe(res => {
      this.Prescription = res;
      this.frqncyz = this.getFrqncyz(this.Prescription );
      if (this.Prescription && this.Prescription.prescriptionMedicine && this.Prescription.prescriptionMedicine.length > 0
        && this.Prescription.prescriptionMedicine[0].dosage != null && this.Prescription.prescriptionMedicine[0].dosage.length > 0
        && this.Prescription.prescriptionMedicine[0].dosage[0].dayInterval) {
        this.dayInterval = this.Prescription.prescriptionMedicine[0].dosage[0].dayInterval;
      } else {
        this.dayInterval = 1;
      }
    });
    this.PatientEncounter$.subscribe(res => this.PatientEncounter = res);
  }

  ngOnInit() {
    this.form.valueChanges.subscribe(() =>
        this.onChangeCallback(this.Prescription)
    );
  }

  /**
   * Calculate value of frequency comboBox
   * @param p
   * @returns {string|number}
   */
  getFrqncyz(p: Prescription):  string | number {
    let frqncyz: string | number;
    if (p) {
      if (p.needsBased === true) {
        frqncyz = TemplateDosageActionService.asNeeded.value;
      } else if (p.continuous === true) {
        frqncyz = TemplateDosageActionService.continuous.value;
      } else if (p.extemporaneous === true) {
        frqncyz = TemplateDosageActionService.extemporaneous.value;
      } else if (p.urgent === true) {
        frqncyz = TemplateDosageActionService.urgent.value;
      } else if (p.prescriptionMedicine && p.prescriptionMedicine.length > 0 && p.prescriptionMedicine[0].dosage
        && p.prescriptionMedicine[0].dosage.length > 0
        && p.prescriptionMedicine[0].dosage[0].daytime /*&& p.prescriptionMedicine[0].dosage[0].quantity
        && p.prescriptionMedicine[0].dosage[0].quantity.value*/) {
        const sdlId = this.PatientEncounterAction.getServiceDeliveryLocationId(this.PatientEncounter);
        frqncyz = this.TemplateDosageAction.getTemplateDosageId4Therapy(p.prescriptionMedicine[0].dosage, sdlId);

        if (frqncyz === TemplateDosageActionService.personalized.value) {
          this.showAdvanced = true;
        }
      }
    }
    return frqncyz;
  }

  /**
   * Domain of prescription route combo box Prescription.routeCode
   * @returns {any}
   */
  getRoute(): string {
    if (this.Prescription.code.code === 'PHARMA') {
      return 'PHIDIC:AdministrationRoute';
    } else {
      return 'PHIDIC:AdministrationRoute:EVC,EVMP,EVP,IMU,SC,ENPEG,ENSNG';
    }
  }

  medicineName() {
    let name = '';
    if (this.Prescription && this.Prescription.prescriptionMedicine && this.Prescription.prescriptionMedicine[0]
      && this.Prescription.prescriptionMedicine[0].medicine && this.Prescription.prescriptionMedicine[0].medicine.name) {
      name = this.Prescription.prescriptionMedicine[0].medicine.name.giv;
      if (this.Prescription.prescriptionMedicine[0].medicine.substance
        && this.Prescription.prescriptionMedicine[0].medicine.substance.length > 0) {
        name += '(' + this.Prescription.prescriptionMedicine[0].medicine.substance[0].name.giv + ')';
      }
    }
    return name;
  }

  /**
   * BtnPrscrtnPhrmDosageAdd: add dosage to first prescriptionMedicine
   */
  addDosage() {
    this.PrescriptionAction.addDosage(this.Prescription, this.Prescription.prescriptionMedicine[0]);
  }

  /**
   * BtnPrscrtnPhrmDosageRemove: remove dosage
   * @param dosage
   */
  removeDosage(dosage) {
    const dosageIndex = this.Prescription.prescriptionMedicine[0].dosage.indexOf(dosage);
    if (dosageIndex !== -1) {
      if (this.Prescription.code.code === 'PHARMA') {
        this.Prescription.prescriptionMedicine[0].dosage.splice(dosageIndex, 1);
      } else { // INFU
        if (this.Prescription.prescriptionMedicine[0].dosage.length === 1) {
          // no se pol
        } else {
          if (dosageIndex === 0) {
            this.Prescription.prescriptionMedicine[0].dosage[dosageIndex + 1].quantity =
              this.Prescription.prescriptionMedicine[0].dosage[0].quantity;
          }
          this.Prescription.prescriptionMedicine[0].dosage.splice(dosageIndex, 1);
        }
      }
    }
  }


  /**
   * TxtPrscrtnPhrmQty and CbxPrscrtnPhrmQty on change
   * Copy values to all dosages
   */
  updateDosages() {

    if (!this.Prescription.prescriptionMedicine[0].dosage) {
      this.Prescription.prescriptionMedicine[0].dosage = [];
    }
    if (this.Prescription.prescriptionMedicine[0].dosage.length === 0) {
      this.addDosage();
    }

    if (this.Prescription.code.code === 'PHARMA') {
      for (let dosage of this.Prescription.prescriptionMedicine[0].dosage) {
        if (!dosage.quantity) {
          dosage.quantity = {};
        }
        if (this.Prescription.quantity.value) {
          dosage.quantity.value = this.Prescription.quantity.value;
        }
        if (this.Prescription.quantity.unit) {
          dosage.quantity.unit = this.Prescription.quantity.unit;
        }
      }
    }
  }

  checkDosages() {
    let value;
    let unit;

    for (let dosage of this.Prescription.prescriptionMedicine[0].dosage) {
      if (!value && dosage.quantity.value) {
        value = dosage.quantity.value;
      }
      if (dosage.quantity.value && value && dosage.quantity.value !== value) {
        this.Prescription.quantity.value = null;
      }
      if (!unit && dosage.quantity.unit) {
        unit = dosage.quantity.unit;
      }
      if (dosage.quantity.unit && unit && dosage.quantity.unit !== unit) {
        this.Prescription.quantity.unit = null;
      }
    }
  }

  renderDosages(): boolean {
    return (this.frqncyz === 'personalized' || !isNaN(this.frqncyz)) && this.showAdvanced;
  }

  /**
   * DayInterval changed, update all dosages
   * @param $event
   */
  dayIntervalChanged() {
    for (let dosage of this.Prescription.prescriptionMedicine[0].dosage) {
      dosage.dayInterval = this.dayInterval;
    }
  }

  dayIntervalSetup() {
    for (let dosage of this.Prescription.prescriptionMedicine[0].dosage) {
      if (!dosage.dayInterval) {
        dosage.dayInterval = this.dayInterval;
      }
    }
  }

  /**
   * Frequency combobox (TxtPrscrtnPhrmFrqncy) on change
   * Clone TemplateDosage and assign to prescriptionMedicine.dosage
   * @param frqncyz
   */
  fillTemplateDosages(frqncyz) {

    this.Prescription.needsBased = false;
    this.Prescription.continuous = false;
    this.Prescription.extemporaneous = false;
    this.Prescription.urgent = false;

    if (!isNaN(frqncyz)) { // Template dosage

      this.Prescription.prescriptionMedicine[0].dosage =
        this.TemplateDosageAction.findAndClone(frqncyz, this.Prescription.code.code === 'PHARMA');
      if (this.Prescription.prescriptionMedicine[0].dosage && this.Prescription.prescriptionMedicine[0].dosage.length > 0) {
        this.dayInterval = this.Prescription.prescriptionMedicine[0].dosage[0].dayInterval;
      }
    } else if (frqncyz === TemplateDosageActionService.personalized.value) {
      this.showAdvanced = true;
      this.dayIntervalSetup();
    } else {
      this.showAdvanced = false;

      if (frqncyz === TemplateDosageActionService.asNeeded.value) {
        this.Prescription.needsBased = true;
      } else {
        this.dayIntervalSetup();
        if (frqncyz === TemplateDosageActionService.continuous.value) {
          this.Prescription.continuous = true;
        } else if (frqncyz === TemplateDosageActionService.extemporaneous.value) {
          this.Prescription.extemporaneous = true;
        } else if (frqncyz === TemplateDosageActionService.urgent.value) {
          this.Prescription.urgent = true;
        }
      }

      if (this.Prescription.code.code === 'PHARMA') {
        this.updateDosages();
      }
    }

    if (!this.dischargeMode) {
      this.PrescriptionAction.resetHighDate(this.Prescription);
    }
  }

  configNeedsBased() {

    if (this.favoriteConfiguration) {

      this.viewManager.setPopupViewId('needs-based');

    } else {

      const sdlId = this.PatientEncounterAction.getServiceDeliveryLocationId(this.PatientEncounter);
      this.ServiceDeliveryLocationAction.inject(sdlId).then((serviceDeliveryLocation: ServiceDeliveryLocation) => {
        this.viewManager.setPopupViewId('needs-based');
      });

    }
  }

  renderAdvanced() {
    return this.frqncyz === 'personalized' || !isNaN(this.frqncyz);
  }

  toggleAdvanced() {
    this.showAdvanced = !this.showAdvanced;
  }

  /**
   * Infusion: update speed or duration when quantity changes
   */
  quantityChange(): void {
    if (this.Prescription.quantity.value && this.Prescription.infusionSpeed.value) {
      this.Prescription.infusionDuration.value =
        Math.round(((this.Prescription.quantity.value / this.Prescription.infusionSpeed.value) * 100)) / 100;
    } else if (this.Prescription.quantity.value && this.Prescription.infusionDuration.value) {
      this.Prescription.infusionSpeed.value =
        Math.round(((this.Prescription.quantity.value / this.Prescription.infusionDuration.value) * 100)) / 100;
    }
  }

  /**
   * Infusion: update quantity or duration when speed changes
   */
  speedChange(): void {
    if (this.Prescription.infusionSpeed.value && this.Prescription.quantity.value) {
      this.Prescription.infusionDuration.value =
        Math.round(((this.Prescription.quantity.value / this.Prescription.infusionSpeed.value) * 100)) / 100;
    } else if (this.Prescription.infusionSpeed.value && this.Prescription.infusionDuration.value) {
      this.Prescription.quantity.value =
        Math.round(((this.Prescription.infusionDuration.value * this.Prescription.infusionSpeed.value) * 100)) / 100;
    }
  }

  /**
   * Infusion: update quantity or speed when duration changes
   */
  durationChange(): void {
    if (this.Prescription.infusionDuration.value && this.Prescription.quantity.value) {
      this.Prescription.infusionSpeed.value =
        Math.round(((this.Prescription.quantity.value / this.Prescription.infusionDuration.value) * 100)) / 100;
    } else if (this.Prescription.infusionDuration.value && this.Prescription.infusionSpeed.value) {
      this.Prescription.quantity.value =
        Math.round(((this.Prescription.infusionDuration.value * this.Prescription.infusionSpeed.value) * 100)) / 100;
    }
  }

  /**
   * Infusion: Remove solution
   */
  removeSolution() {
    this.Prescription.infusionTypeCode = null;
  }

  /**
   * Add medicine to infusion
   */
  addPrescriptionMedicine() {
    // this.eject = false;
    this.viewManager.setPopupViewId('search-medicine');
  }

  /**
   * Infusion: Remove medicine
   * @param PrescriptionMedicine
   */
  removePrescriptionMedicine(prescriptionMedicine: PrescriptionMedicineGeneric) {
    if (this.Prescription.prescriptionMedicine.length === 1) {
      delete this.Prescription.prescriptionMedicine[0].medicine;
      this.Prescription.prescriptionMedicine[0].dosage[0].quantity = {};
    } else {
      const pmIndex = this.Prescription.prescriptionMedicine.indexOf(prescriptionMedicine);
      if (pmIndex !== -1) {
        if (pmIndex === 0) {
          let dsgsNoQty: Array<Dosage> = prescriptionMedicine.dosage.map((d: Dosage) => {
            return { dayInterval: d.dayInterval, daytime: d.daytime};
          });
          dsgsNoQty[0].quantity =  this.Prescription.prescriptionMedicine[pmIndex + 1].dosage[0].quantity;
          this.Prescription.prescriptionMedicine[pmIndex + 1].dosage = dsgsNoQty;
        }
        this.Prescription.prescriptionMedicine.splice(pmIndex, 1);
      }
    }
  }


  // From ControlValueAccessor interface
  writeValue(value) {
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
