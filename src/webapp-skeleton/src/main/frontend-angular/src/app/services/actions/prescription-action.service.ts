import { Injectable, Injector } from '@angular/core';
import { select } from '@angular-redux/store';
import { TranslateService } from '@ngx-translate/core';

import { BaseActionService } from './base-action.service';
import { reviver } from '../converters/any.converter';
import { dateWithoutTime, dateToString } from '../converters/date.converter';
import { Datamodel } from '../datamodel/datamodel';

import { Medicine, Prescription, PrescriptionMedicineGeneric } from '../entities/base-entity';
import { Employee } from '../entities/role';
import { Therapy, Dosage } from '../entities/act';
import { PrescriptionGeneric } from '../entities/base-entity/prescription-generic';


@Injectable()
export class PrescriptionActionService extends BaseActionService<Prescription> {

  private static STATUS = '/status';
  public static CHANGE_STATUS = 'CHANGE_STATUS';
  public static ACTION_VALIDATE = 'validate';
  public static ACTION_INVALIDATE = 'invalidate';
  public static ACTION_INVALIDATE_AND_MODIFY = 'invalidate_and_modify';

  private static MINUTES_PER_HOUR = 60;

  private dateMidnight: Date;

  @select(['config', 'employee']) employee$;
  employee: Employee;
  @select(['config', 'sid']) sid$;
  sid: string;

  @select(['config', 'employeeRoleCode']) employeeRoleCode$;
  employeeRoleCode: string;

  translateService: TranslateService;

  constructor(injectorz: Injector) {
    super(injectorz);
    this.entityName = 'Prescription';
    this.employee$.subscribe(res => this.employee = res);
    this.sid$.subscribe(res => this.sid = res);
    this.employeeRoleCode$.subscribe(res => this.employeeRoleCode = res);

    this.translateService = injectorz.get(TranslateService);

    this.dateMidnight = new Date(0);
    this.dateMidnight.setHours(0);
  }

  public inject(id: number, additionals: Array<any> = null, loads: Array<any> = null, conversationName = null): Promise<Prescription> {
    return super.inject(id, additionals, loads, conversationName).then((prescription: Prescription) => {

      if (prescription) {

        if (prescription.period && prescription.period.low) {
          prescription.period.low = new Date();
          prescription.period.low.setSeconds(0);
        }

        // FIXME remove when widgets will create paths
        if (!prescription.quantity) {
          prescription.quantity = {};
        }
        if (prescription.prescriptionMedicine && prescription.prescriptionMedicine.length > 0) {
          prescription.prescriptionMedicine.map((pm: PrescriptionMedicineGeneric) => {
            if (!pm.dosage) {
              pm.dosage = [];
            }
          });
        }
        // END FIXME remove when widgets will create paths

        if (prescription.prescriptionMedicine
          && prescription.prescriptionMedicine.length >= 1 && prescription.prescriptionMedicine[0].dosage
          && prescription.prescriptionMedicine[0].dosage.length >= 1) {
          if (prescription.extemporaneous) {
            prescription.quantity = prescription.prescriptionMedicine[0].dosage[0].quantity;
          }
        }

        if (prescription && prescription.prescriptionMedicine && prescription.prescriptionMedicine.length > 0) {
          const dosage: Array<Dosage> = prescription.prescriptionMedicine[0].dosage;
          PrescriptionActionService.orderDosage(dosage);
        }
      }
      return prescription;
    });
  }

  public static orderDosage(dosage: Array<Dosage>) {
    if (dosage) {
      dosage.sort((a: Dosage, b: Dosage) => {
        if (!a.daytime || !b.daytime) {
          return 0;
        }
        return a.daytime.getTime() - b.daytime.getTime();
      });
    }
  }

  public resetHighDate(prescription: Prescription) {
    if (prescription.extemporaneous || prescription.urgent) {
      prescription.period.high = new Date(prescription.period.low);
      prescription.period.high.setMinutes(prescription.period.high.getMinutes() + 1);
    } else {
      prescription.period.high = null;
    }
  }

  public changeStatus(prescriptionId: number, action: string, changeDate: Date): Promise<any> {

    return this.httpService.fetch(this.apiUrl + this.restBaseUrl + this.entityUrl + PrescriptionActionService.STATUS
       + '/' + prescriptionId + '/' + dateToString(changeDate)
    // FIXME //DateUtil.toISO8601(changeDate)
      + ';jsessionid=' + this.sid + '?cid=' + this.cid,
      {
        method: 'POST',
        body: 'action=' + action,
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        credentials: 'include'
      })
      .then(response => response.text())
      .then(text => JSON.parse(text, reviver) )
      .then(raw => {
        return {type: PrescriptionActionService.CHANGE_STATUS, data: raw, receivedAt: Date.now()};
      })
      .catch(error => {
        console.error('Error changeStatus ' + this.entity + ' ' + error.message);
        throw error;
      });
  }

  /**
   * Return Prescription history: all prescription with the same rootPrescription
   * @param p
   * @returns {any}
   */
  public readHistory(p: Prescription): Promise<Datamodel>  {
    if (p.rootPrescription) {
      this.equal['rootPrescription.internalId'] = p.rootPrescription.internalId;
      this.orderBy['internalId'] = 'ascending';
      return this.read('PrescriptionHistoryList').then((dm) => {
        // Add root prescription at the beginning of history
        dm.entities.unshift(p.rootPrescription);
        return dm;
      });
    } else {
      this.conversationActions.remove('PrescriptionHistoryList');
      return new Promise<any>((resolve, reject) => {
        resolve();
    })
    }
  }

  /**
   * Create a new prescription
   * @param therapy
   * @param medicine
   * @param code
   * @returns {Prescription}
   */
  public newPrescription(therapy: Therapy, medicine: Medicine, code = 'PHARMA'): any {
    this.entity = {};

    //this.entity.dayInterval = 1;
    this.entity.quantity = {};

    if (code === 'INFU') {
      this.entity.infusionSpeed = {unit: 'ml/h'};
      this.entity.infusionDuration = {unit: 'h'};
      this.entity.quantity.unit = 'ml';
    }

    if (therapy) {
      this.entity.therapy = {internalId: therapy.internalId, entityName: 'com.phi.entities.act.Therapy'};
    }

    this.entity.prescriptionMedicine = [];

    const prescriptionMedicine: PrescriptionMedicineGeneric = {};
    prescriptionMedicine.dosage = [];
    // prescriptionMedicine.dosage.push({quantity: {}, daytime: this.dateMidnight });
    if (medicine) {
      prescriptionMedicine.medicine = medicine; // PROXY
      prescriptionMedicine.medicine.entityName = 'com.phi.entities.baseEntity.Medicine'; // Will not be persisted

      if (medicine.routeCode && medicine.routeCode.relationsPhi && medicine.routeCode.relationsPhi.length > 0) {
        this.entity.routeCode = medicine.routeCode.relationsPhi[0];
      }

      if (medicine.quantityPerBoxUnit && medicine.quantityPerBoxUnit.relationsPhi && medicine.quantityPerBoxUnit.relationsPhi.length > 0) {
        this.entity.quantity.unit = medicine.quantityPerBoxUnit.relationsPhi[0].code;
      }
    }
    this.entity.prescriptionMedicine.push(prescriptionMedicine);

    this.entity.author = { internalId: this.employee.internalId, entityName: 'com.phi.entities.role.Employee' }; // PROXY
    this.entity.authorRole = { codeSystemName: 'ROLES', domainName: 'EmployeeFunction', code: this.employeeRoleCode };

    this.entity.code = {codeSystemName: 'PHIDIC', domainName: 'PrescriptionType', code};
    this.entity.statusCode = { codeSystemName: 'PHIDIC', domainName: 'StatusCodes', code: 'new' };
    this.entity.modified = false;

    this.entity.continuous = false;
    this.entity.needsBased = false;
    this.entity.extemporaneous = false;
    this.entity.urgent = false;

    this.entity.period = { low: new Date() };
    this.entity.period.low.setSeconds(0);

    return this.entity;
  }

  /**
   * Clone Prescription, from FavoriteProfile or updating active prescription
   * @param prescription
   * @returns {Prescription}
   */
  public clone(prescription: PrescriptionGeneric): PrescriptionGeneric {
    const clone: Prescription = Object.assign({}, prescription);

    delete clone.internalId;
    delete clone.createdBy;
    delete clone.creationDate;
    delete clone.version;

    delete clone.validityPeriod;
    delete clone.substanceAdministration;

    delete clone.profile;
    delete clone['proxyString']; //TODO: remove serverside?

    clone.modified = false;

    clone.statusCode = { codeSystemName: 'PHIDIC', domainName: 'StatusCodes', code: 'new' };

    clone.author = { entityName: 'com.phi.entities.role.Employee', internalId: this.employee.internalId};
    clone.authorRole = { codeSystemName: 'ROLES', domainName: 'EmployeeFunction', code: this.employeeRoleCode };

    delete clone.cancelledBy;
    delete clone.cancelledByRole;

    delete clone.rootPrescription;

    if (!clone.quantity) {
      clone.quantity = {};
    }

    if (prescription.prescriptionMedicine) {
      for (let i = 0; i < prescription.prescriptionMedicine.length; i++) {
        delete prescription.prescriptionMedicine[i].internalId;
        delete prescription.prescriptionMedicine[i].createdBy;
        delete prescription.prescriptionMedicine[i].creationDate;
        delete prescription.prescriptionMedicine[i].version;
        delete prescription.prescriptionMedicine[i].prescription;

        delete prescription.prescriptionMedicine[i]['proxyString']; //TODO: remove serverside?

        if (prescription.prescriptionMedicine[i].dosage) {
          for (let j = 0; j < prescription.prescriptionMedicine[i].dosage.length; j++) {
            delete prescription.prescriptionMedicine[i].dosage[j].internalId;
            delete prescription.prescriptionMedicine[i].dosage[j].createdBy;
            delete prescription.prescriptionMedicine[i].dosage[j].creationDate;
            delete prescription.prescriptionMedicine[i].dosage[j].version;

            delete prescription.prescriptionMedicine[i].dosage[j]['proxyString']; //TODO: remove serverside?
          }
        } else {
          prescription.prescriptionMedicine[i].dosage = [];
        }
      }
    }

    return clone;
  }

  /**
   * Add dosage to prescriptionMedicine
   * @param prescription
   * @param prescriptionMedicine
   */
  addDosage(prescription: Prescription, prescriptionMedicine: PrescriptionMedicineGeneric) {

    if (!prescriptionMedicine.dosage) {
      prescriptionMedicine.dosage = [];
    }

    let dosage: Dosage = {};

    // if  personalized
    if (prescription.needsBased === false && prescription.continuous === false
      && prescription.extemporaneous === false && prescription.urgent === false) {
      dosage.daytime = this.dateMidnight;
      if (prescriptionMedicine.dosage.length > 0 && prescriptionMedicine.dosage[0].dayInterval) {
        dosage.dayInterval = prescriptionMedicine.dosage[0].dayInterval;
      } else {
        dosage.dayInterval = 1;
      }

    }

    dosage.quantity = {};

    if (prescription.code.code === 'PHARMA') {
      dosage.quantity.value = prescription.quantity.value || 1;
      dosage.quantity.unit = prescription.quantity.unit;
    }

    prescriptionMedicine.dosage.push(dosage);
  }

  /**
   * Add medicine to prescription
   * Add to first prescriptionMedicine if has no medicine else create a new PrescriptionMedicine
   * @param prescription
   * @param medicine
   */
  public addPrescriptionMedicine(prescription: Prescription, medicine: Medicine) {
    if (!prescription.prescriptionMedicine) {
      prescription.prescriptionMedicine = [];
    }

    let prescriptionMedicine: PrescriptionMedicineGeneric;

    if (prescription.prescriptionMedicine.length === 1 && !prescription.prescriptionMedicine[0].medicine) {
      prescriptionMedicine = prescription.prescriptionMedicine[0];
    } else {
      prescriptionMedicine = {};
      prescription.prescriptionMedicine.push(prescriptionMedicine);
    }

    if (!prescriptionMedicine.dosage || prescriptionMedicine.dosage.length === 0) {
      this.addDosage(prescription, prescriptionMedicine);
    }

    prescriptionMedicine.medicine = medicine;
    prescriptionMedicine.medicine.entityName = 'com.phi.entities.baseEntity.Medicine'; // Will not be persisted
  }

  /**
   * Set dosages of all PrescriptionMedicines same as Prescription.prescriptionMedicine[0]
   * Used for infusions
   * @param p
   * @returns {Prescription}
     */
  public fixDosages(p: Prescription): Prescription {
    if (p && p.prescriptionMedicine && p.prescriptionMedicine.length > 0 && p.prescriptionMedicine[0].dosage) {

      p.prescriptionMedicine.map((pm, pmIndex) => {
        //Set all dosages quantity to quantity of first dosage
        pm.dosage.map((d) => {
          d.quantity = pm.dosage[0].quantity;
        });

        if (pmIndex > 0) {
          //Copy dosages from first prescriptionMedicine, mantaining quantity
          let quantity = pm.dosage[0].quantity;
          pm.dosage = p.prescriptionMedicine[0].dosage.map((d) => {
            let cloneDosage: Dosage = {quantity};
            cloneDosage.dayInterval = d.dayInterval;
            cloneDosage.daytime = d.daytime;
            return cloneDosage;
          });
        }
      });

    }
    return p;
  }

  warnings = [];
  errors = [];

  /**
   * Function to check the infos contained in the Prescription before saving
   * Same as MDashboard\src\com\phi\ui\PharmacologicPanel.mxml checkInfos
   * and MDashboard\src\com\phi\ui\InfusionPanel.mxml, merged
   * @param prescriptions
   * @param prescriptionMinutesGapFromNow
   * @param configurationMode
   * @param dischargeMode
   * @param endDateChecked
   * @returns {any} warnings and errors
   */
  public checkInfos(prescriptions: Array<Prescription>, prescriptionMinutesGapFromNow: number, configurationMode = false, dischargeMode = false, endDateChecked = false): any {
    let currentTime = new Date();

    let affectedPrescriptionLabels;
    // let dosage: Dosage;
    let acceptedDate:Date;
    // let prescription: PrescriptionGeneric;

    // filterDosages(
    let dsg:Array<Dosage> = [];
    if (prescriptions[0].prescriptionMedicine && prescriptions[0].prescriptionMedicine.length > 0 && prescriptions[0].prescriptionMedicine[0].dosage != null) {
      dsg = prescriptions[0].prescriptionMedicine[0].dosage.filter((d:Dosage) => d.daytime);
    }

    this.warnings = [];
    this.errors = [];

    if (configurationMode) {
      // Same Medicines in Profile
      for (let i = 0; i < prescriptions.length; i++) {
        const firstMedicine:Medicine = prescriptions[i].prescriptionMedicine[0].medicine as Medicine;
        for (let j = i + 1; j < prescriptions.length; j++) {
          const medicine:Medicine = prescriptions[j].prescriptionMedicine[0].medicine as Medicine;
          if (firstMedicine.internalId == medicine.internalId) {
            this.errors.push(this.translateService.instant('Same_medicament_Warning'));
            break;
          }
        }
      }
    }

    if (!configurationMode) {
      if (!dischargeMode) {
        // startDate == endDate
        let sameDate = false;
        if (endDateChecked && (dateWithoutTime(prescriptions[0].period.low).getTime() === dateWithoutTime(prescriptions[0].period.high).getTime())) {
          this.warnings.push(this.translateService.instant('Same_date_Warning'));
          sameDate = true;
        }

        // startDate > endDate
        if (sameDate) {
          if (endDateChecked && (prescriptions[0].period.low.getHours() * PrescriptionActionService.MINUTES_PER_HOUR + prescriptions[0].period.low.getMinutes())
            >= (prescriptions[0].period.high.getHours() * PrescriptionActionService.MINUTES_PER_HOUR + prescriptions[0].period.high.getMinutes()))
            this.errors.push(this.translateService.instant('Wrong_time_Warning'));
        }

        let isurgent = false;
        for (let prescription of prescriptions) {
          if (prescription.urgent) {
            isurgent = true;
            break;
          }
        }

        // startDate < now + prescriptionGap - 5 min
        acceptedDate = new Date(currentTime.getFullYear(), currentTime.getMonth(), currentTime.getDate(), currentTime.getHours(), currentTime.getMinutes() + prescriptionMinutesGapFromNow - 5);
        if (!isurgent && (!prescriptions[0].period.low || prescriptions[0].period.low.getTime() < acceptedDate.getTime()))
          this.errors.push(this.translateService.instant('To_soon_Warning') + ' ' + prescriptionMinutesGapFromNow + ' ' + this.translateService.instant('minute/s') + '!');
        /*
         // When modifying: startDate < now + 1 h - 5 min
         acceptedDate = new Date(currentTime.fullYear, currentTime.month, currentTime.date, currentTime.hours + 1, currentTime.minutes - 5);
         if (!isurgent && editingValidatedPrescription && startDate.selectedDate.time < acceptedDate.time)
         this.errors.push(this.translateService.instant('To_soon_modifying_Warning') + ' 60 ' + getLocalizedText('minute/s') + '!');
         */
      }

      // No note if medicine is not in PTA
      if (!dischargeMode && !prescriptions[0].note) {
        for (let prescription of prescriptions) {
          for (let prescriptionMedicine of prescription.prescriptionMedicine) { //Loop prescriptionMedicine for INFU
            if (prescriptionMedicine.medicine && !prescriptionMedicine.medicine.reference) {
              this.errors.push(this.translateService.instant('No_note_PTA_Warning'));
              break;
            }
          }
        }
      }
    }

    //Add infu
    if (prescriptions[0].code.code === 'INFU') {
      // No solution
      let noSolution: Boolean = false;
      if (!prescriptions[0].infusionTypeCode) {
        noSolution = true;
      }
      // No components
      let noComponents: Boolean = false;
      // if (filteredComponents.length == 0)
      let pm: Array<PrescriptionMedicineGeneric> = prescriptions[0].prescriptionMedicine.filter((pm: PrescriptionMedicineGeneric) => pm.medicine);
      if (pm.length === 0) {
        noComponents = true;
      }

      if (noSolution) {
        if (configurationMode)
          this.warnings.push(this.translateService.instant('No_solution_Warning'));
        else if (noComponents)
          this.errors.push(this.translateService.instant('No_solution_or_components_Warning'));
      }

      // No quantity/speed/duration
      if (prescriptions[0].quantity == null || prescriptions[0].infusionSpeed == null || prescriptions[0].infusionDuration == null || isNaN(prescriptions[0].quantity.value) || isNaN(prescriptions[0].infusionSpeed.value) || isNaN(prescriptions[0].infusionDuration.value)) {
        if (configurationMode)
          this.warnings.push(this.translateService.instant('No_quantity_speed_duration_Warning'));
        else
          this.errors.push(this.translateService.instant('No_quantity_speed_duration_Warning'));
      }

      // No dosages
      if (dsg.length === 0 && !prescriptions[0].continuous && !prescriptions[0].needsBased) {
        if (configurationMode)
          this.warnings.push(this.translateService.instant('No_dosage_infu_Warning'));
        else
          this.errors.push(this.translateService.instant('No_dosage_infu_Warning'));
      }

    } else if (prescriptions[0].code.code === 'PHARMA') {

      // No dosages in Repeated Prescription
      this.validate('No_dosage_pharma_Warning', prescriptions, configurationMode,  (p) =>
        !p.needsBased && !p.extemporaneous && (!p.prescriptionMedicine[0].dosage || p.prescriptionMedicine[0].dosage.length == 0));

      // Different UM in dosages
      affectedPrescriptionLabels = '';
      for (let prescription of prescriptions) {
        if (prescription.prescriptionMedicine[0].dosage.length > 0) {
          let prevDosage:Dosage = prescription.prescriptionMedicine[0].dosage[0] as Dosage;
          for (let dosage of prescription.prescriptionMedicine[0].dosage) {
            if (prevDosage.quantity != null && dosage.quantity != null && prevDosage.quantity.unit != null && dosage.quantity.unit != null && prevDosage.quantity.unit != dosage.quantity.unit) {
              affectedPrescriptionLabels += prescription.prescriptionMedicine[0].medicine.name.giv + ', ';
              break;
            }
            prevDosage = dosage;
          }
        }
      }
      if (affectedPrescriptionLabels != '') {
        affectedPrescriptionLabels = affectedPrescriptionLabels.substr(0, affectedPrescriptionLabels.length - 2);
        this.warnings.push(this.translateService.instant('Different_UM_Warning') + ' (' + affectedPrescriptionLabels + ')');
      }
    }

    // Incomplete dosages in Repeated Prescription
    affectedPrescriptionLabels = '';
    for (let prescription of prescriptions) {
      if (!prescription.needsBased && !prescription.extemporaneous && !prescription.urgent && prescription.prescriptionMedicine[0].dosage.length > 0) {
        for (let prescriptionMedicine of prescription.prescriptionMedicine) {
          if (prescription.code.code === 'INFU' && !prescriptionMedicine.medicine ) {
            continue; // Skip infusion fake prescriptionMedicine
          }
          for (let dosage of prescriptionMedicine.dosage) {
            if (!dosage.daytime || isNaN(dosage.dayInterval) || !dosage.quantity || !dosage.quantity.value || isNaN(dosage.quantity.value) || !dosage.quantity.unit) {
              affectedPrescriptionLabels += prescriptionMedicine.medicine.name.giv + ', ';
              break;
            }
          }
        }
      }
    }
    if (affectedPrescriptionLabels != '') {
      affectedPrescriptionLabels = affectedPrescriptionLabels.substr(0, affectedPrescriptionLabels.length - 2);
      if (configurationMode) {
        this.warnings.push(this.translateService.instant('Incomplete_dosage_Warning') + ' (' + affectedPrescriptionLabels + ')');
      } else {
        this.errors.push(this.translateService.instant('Incomplete_dosage_Warning') + ' (' + affectedPrescriptionLabels + ')');
      }
    }

    // No threshold in asNeedes Prescription
    this.validate('No_threshold_Warning', prescriptions, configurationMode,  (p) =>
      !dischargeMode && p.needsBased && p.systolicPressure == null && p.diastolicPressure == null
      && p.temperature == null && p.glycemia == null && p.diuresis == null
      && p.pain == null && p.heartRate == null && p.spo2 == null
      && p.other == null && !p.glySecondaryProtocol);

    if (prescriptions[0].code.code === 'PHARMA') {
      // No Quantity in asNeeded Prescription
      this.validate('No_quantity_as_needed_Warning', prescriptions, configurationMode,  (p) =>
        p.needsBased && (p.prescriptionMedicine[0].dosage.length == 0 || p.prescriptionMedicine[0].dosage[0].quantity == null
        || isNaN(p.prescriptionMedicine[0].dosage[0].quantity.value) || p.prescriptionMedicine[0].dosage[0].quantity.unit == null));

      // No MaxQuantity in asNeeded Prescription
      affectedPrescriptionLabels = '';
      for (let prescription of prescriptions) {
        if (prescription.needsBased && !dischargeMode && ((prescription as Prescription).maximumQuantity24h == null || isNaN((prescription as Prescription).maximumQuantity24h.value) || (prescription as Prescription).maximumQuantity24h.unit == null))
          affectedPrescriptionLabels += prescription.prescriptionMedicine[0].medicine.name.giv + ', ';
      }
      if (affectedPrescriptionLabels != '') {
        affectedPrescriptionLabels = affectedPrescriptionLabels.substr(0, affectedPrescriptionLabels.length - 2);
        this.warnings.push(this.translateService.instant('No_max_quantity_Warning') + ' (' + affectedPrescriptionLabels + ')');
      }

      // No MinGap in asNeeded Prescription
      affectedPrescriptionLabels = '';
      for (let prescription of prescriptions) {
        if (prescription.needsBased && !dischargeMode && (prescription as Prescription).hoursGap == null && (prescription as Prescription).minsGap == null)
          affectedPrescriptionLabels += prescription.prescriptionMedicine[0].medicine.name.giv + ', ';
      }
      if (affectedPrescriptionLabels != '') {
        affectedPrescriptionLabels = affectedPrescriptionLabels.substr(0, affectedPrescriptionLabels.length - 2);
        this.warnings.push(this.translateService.instant('No_min_gap_Warning') + ' (' + affectedPrescriptionLabels + ')');
      }
    } else if (prescriptions[0].code.code === 'INFU') {
      // Overlapped dosages
      if (prescriptions[0].infusionDuration != null && !isNaN(prescriptions[0].infusionDuration.value)) {
        if (dsg.length != 0) {
          let previousDosage: Dosage = dsg[0];
          for (let index: number = 1; index < dsg.length; index++) {
            let nextDosage:Dosage = dsg[index] as Dosage;
            let duration: number = prescriptions[0].infusionDuration.value;
            if (prescriptions[0].infusionDuration.unit === 'h') {
              duration = duration * 60;
            }
            let endTime: Date = new Date(previousDosage.daytime.getTime());
            endTime.setMinutes(endTime.getMinutes() + duration);

            if (endTime.getTime() > nextDosage.daytime.getTime()) {
              this.errors.push(this.translateService.instant('Overlapped_dosages_Warning'));
              break;
            }
            previousDosage = nextDosage;
          }
        }
      }
    }

    // No admin route
    this.validate('No_administration_route_Warning', prescriptions, configurationMode,  (p) => p.routeCode == null);

    if (prescriptions[0].code.code === 'PHARMA') {
      // No Quantity in extemporaneous or urgen Prescription
      this.validate('No_quantity_extemporaneous_Warning', prescriptions, configurationMode,  (p) =>
      (p.extemporaneous || p.urgent) && (p.prescriptionMedicine[0].dosage.length == 0
        || p.prescriptionMedicine[0].dosage[0].quantity == null || isNaN(p.prescriptionMedicine[0].dosage[0].quantity.value)
        || p.prescriptionMedicine[0].dosage[0].quantity.unit == null));
    }

    if (this.warnings.length > 0 || this.errors.length > 0) {
      return false;
    } else {
      return true;
    }
  }

  validate(message: string, prescriptions: Array<Prescription>, configurationMode: boolean, filterFunction: Function ) {

    let errOrWarn = false;
    let affectedPrescriptionLabels = '';

    if (prescriptions[0].code.code === 'PHARMA') {
      affectedPrescriptionLabels = ' (';
      prescriptions.map((p:Prescription) => {
        if (filterFunction(p)) {
          affectedPrescriptionLabels += p.prescriptionMedicine[0].medicine.name.giv + ', ';
        }
      });
      if (affectedPrescriptionLabels != ' (') {
        affectedPrescriptionLabels = affectedPrescriptionLabels.substr(0, affectedPrescriptionLabels.length - 2);
        affectedPrescriptionLabels += ')';
        errOrWarn = true;
      }
    } else if (prescriptions[0].code.code === 'INFU') {
      errOrWarn = filterFunction(prescriptions[0]);
    }

    if (errOrWarn) {
      if (configurationMode) {
        this.warnings.push(this.translateService.instant(message) + affectedPrescriptionLabels);
      } else {
        this.errors.push(this.translateService.instant(message) + affectedPrescriptionLabels);
      }
    }
  }

}
