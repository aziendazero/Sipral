import { Component, OnDestroy, Injector, EventEmitter } from '@angular/core';
import { select } from '@angular-redux/store';
import { TranslateService } from '@ngx-translate/core';
import { MedicineActionService } from '../../services/actions/medicine-action.service';
import { PrescriptionActionService } from '../../services/actions/prescription-action.service';
import { ConversationActions } from '../../actions/conversation.actions';
import { Datamodel } from '../../services/datamodel/datamodel';
import { BaseForm } from '../../widgets/form/base-form';
import { Medicine } from '../../services/entities/base-entity';
import { Location } from '@angular/common';
import { ViewManager } from '../../services/view-manager';

@Component({
  templateUrl: './search-medicine.html',
  styleUrls: ['./search-medicine.scss']
})
export class SearchMedicineComponent extends BaseForm implements OnDestroy {
  @select(['conversation', 'MedicineList']) MedicineList$;
  MedicineList;
  @select(['conversation', 'Medicine']) Medicine$;
  Medicine;
  @select(['conversation', 'SelectedPrescriptionList']) SelectedPrescriptionList$;
  SelectedPrescriptionList;
  @select(['conversation', 'Prescription']) prescription$;
  prescription;
  @select(['conversation', 'PatientEncounter']) patientEncounter$;
  patientEncounter;
  @select(['conversation', 'favoriteConfiguration']) favoriteConfiguration$;
  favoriteConfiguration;

  extendedSearch = false;

  onSelect: EventEmitter<any> = new EventEmitter();

  eject = true;

  constructor(injector: Injector,
              private translateService: TranslateService,
              public conversationActions: ConversationActions,
              private location: Location,
              private viewManager: ViewManager,
              public MedicineAction: MedicineActionService,
              private PrescriptionAction: PrescriptionActionService) {

    super(injector);

    this.MedicineList$.subscribe(res => this.MedicineList = res);
    this.Medicine$.subscribe(res => this.Medicine = res);
    this.prescription$.subscribe((pr) => this.prescription = pr);
    this.patientEncounter$.subscribe((enc) => this.patientEncounter = enc);
    this.SelectedPrescriptionList$.subscribe((res) => this.SelectedPrescriptionList = res);
    this.favoriteConfiguration$.subscribe((res) => this.favoriteConfiguration = res);

    this.resetRestrictions();

  }

  ngOnDestroy() {
    if (this.eject) {
      this.conversationActions.remove('Medicine');
      this.conversationActions.remove('MedicineList');
    }
  }

  searchMedicine() {

    delete this.MedicineAction.equal["isActive"];
    delete this.MedicineAction.equal["reference"];
    delete this.MedicineAction.equal["atcCode.code"];

    if (this.extendedSearch) {
      delete this.MedicineAction.equal['reference'];
      delete this.MedicineAction.notEqual['usageType.code'];
    } else {
      this.MedicineAction.equal['reference'] = true;
      this.MedicineAction.notEqual['usageType.code'] = 'C';
    }

    this.MedicineAction.read();
  }

  resetRestrictions() {
    this.MedicineAction.cleanRestrictions();

    this.MedicineAction.select.push('internalId');
    this.MedicineAction.select.push('name.giv');
    this.MedicineAction.select.push('substance.name.giv');
    this.MedicineAction.select.push('manufacturer.name.giv');
    this.MedicineAction.select.push('reference');
    this.MedicineAction.select.push('atcCode.code');
  }

  ie(entity, conversationName, event): Promise<any> {
    if (event.target instanceof HTMLAnchorElement) {
      return;
    }
    if (!entity.reference) {
      this.viewManager.openAlertMessage(
        this.translateService.instant('Confirm'),
        this.translateService.instant('No_PTA_Warning'),
        true,
        true,
        'OK',
        'Cancel',
        true,
        () => {
          this.injectMedicineAndCreatePrescription(entity);
        }
      );
    } else {
      return this.injectMedicineAndCreatePrescription(entity);
    }
  }

  injectMedicineAndCreatePrescription(entity) {
    const load: Array<string> = ['routeCode.relationsPhi', 'quantityPerBoxUnit.relationsPhi'];

    return this.MedicineAction.inject(entity.internalId, null, load).then(
      (medicine => {

        if (this.favoriteConfiguration) {
          if (!this.prescription || this.prescription.code.code === 'PHARMA') { // Pharma
            if (this.SelectedPrescriptionList == null) {
              this.SelectedPrescriptionList = new Datamodel([]);
              this.conversationActions.put('SelectedPrescriptionList', this.SelectedPrescriptionList);
            }
            this.prescription = this.PrescriptionAction.newPrescription(null, medicine);
            this.SelectedPrescriptionList.entities.push(this.prescription);
          } else { // Infu
            this.PrescriptionAction.addPrescriptionMedicine(this.prescription, medicine);
          }
        } else { //Not profile: maybe SelectedPrescriptionList is useless
          if (!this.prescription) { // Pharma
            this.prescription = this.PrescriptionAction.newPrescription(this.patientEncounter.therapy[0], medicine);
            // this.conversation['Prescription'] = prescription;
            this.conversationActions.put('Prescription', this.prescription);
            let dm:Datamodel = new Datamodel([this.prescription]);
            this.conversationActions.put('SelectedPrescriptionList', dm);
          } else { // Infu
            this.PrescriptionAction.addPrescriptionMedicine(this.prescription, medicine);
          }
        }

        this.viewManager.setPopupViewId('prescription');
      })
    );
  }

  getMedicineDetails(Medicine) {

    let load: Array<string> = [
      'substance',
      'manufacturer',
      'ssnCode',
      'terapeuticGroup',
      'pharmaceuticFormCode',
      'productClassification',
      'temperatureStorageCode',
      'expiringCode',
      'recipeCode',
      'box',
      'usageType',
      'atcCode',
      'quantityPerBoxUnit'
    ];

    this.MedicineAction.inject(Medicine.internalId, null, load).then(() => {
      this.eject = false;
      this.viewManager.setPopupViewId('medicine-detail');
    });
  }

  // Function to search alternatives
  searchAlternatives(medicine: Medicine) {

    this.conversationActions.remove('MedicineList');

    this.resetRestrictions();

    this.MedicineAction.equal["isActive"] = true;
    this.MedicineAction.equal["reference"] = true;
    this.MedicineAction.equal["atcCode.code"] = medicine.atcCode.code;

    this.MedicineAction.read();
  }

  close() {
    this.location.back();
  }

}
