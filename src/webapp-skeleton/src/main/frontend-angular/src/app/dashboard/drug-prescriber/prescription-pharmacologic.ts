import {Component, Injector, ViewChild, OnInit, OnDestroy} from '@angular/core';
import { select } from '@angular-redux/store';
import { BaseForm } from '../../widgets/form/base-form';
import { PrescriptionActionService,
  PrescriptionDischargeActionService,
  TemplateDosageActionService,
  TherapyActionService,
  FavoriteProfileActionService } from '../../services/actions';
import { ConversationActions } from '../../actions/conversation.actions';
import { PrescriptionGeneric, Prescription, PrescriptionDischarge} from '../../services/entities/base-entity';
import { PatientEncounter, Therapy, Dosage } from '../../services/entities/act';
import { Subject } from 'rxjs/Subject';
import { ViewManager } from '../../services/view-manager';
import { DrugPrescriberComponent } from './drug-prescriber.component';
import { FavoriteComponent } from '../favorite/favorite.component';

@Component({
selector: 'phi-prescription-pharmacologic',
templateUrl: './prescription-pharmacologic.html'
})
export class PrescriptionPharmacologic extends BaseForm implements OnInit, OnDestroy  {
  @select(['conversation', 'favoriteConfiguration']) favoriteConfiguration$;
  favoriteConfiguration;
  @select(['conversation', 'FavoriteProfile']) FavoriteProfile$;
  FavoriteProfile;
  @select(['conversation', 'enableEndDate']) enableEndDate$;
  enableEndDate;
  // @select(['conversation', 'noDuration']) noDuration$;
  noDuration: boolean;
  @select(['conversation', 'Prescription']) Prescription$;
  Prescription// : Prescription;
  @select(['conversation', 'SelectedPrescriptionList']) SelectedPrescriptionList$;
  SelectedPrescriptionList;
  @select(['conversation', 'TemplateDosageList']) TemplateDosageList$;
  TemplateDosageList;
  @select(['conversation', 'showAdvanced']) showAdvanced$;
  showAdvanced;

  @select(['conversation', 'dischargeMode']) dischargeMode$;
  dischargeMode;

  @ViewChild('form') form;

  dateMidnight: Date;

  onSave: Subject<any> = new Subject();

  constructor(injector: Injector,
              public conversationActions: ConversationActions,
              public PrescriptionAction: PrescriptionActionService,
              private PrescriptionDischargeAction: PrescriptionDischargeActionService,
              private TherapyAction: TherapyActionService,
              private FavoriteProfileAction: FavoriteProfileActionService,
              public TemplateDosageAction: TemplateDosageActionService,
              private viewManager: ViewManager) {

    super(injector);
    this.favoriteConfiguration$.subscribe(res => this.favoriteConfiguration = res);
    this.FavoriteProfile$.subscribe(res => this.FavoriteProfile = res);
    this.enableEndDate$.subscribe(res => this.enableEndDate = res);
    // this.noDuration$.subscribe(res => this.noDuration = res);
    this.Prescription$.subscribe(res => this.Prescription = res);
    this.SelectedPrescriptionList$.subscribe(res => this.SelectedPrescriptionList = res);
    this.TemplateDosageList$.subscribe(res => this.TemplateDosageList = res);
    this.showAdvanced$.subscribe(res => this.showAdvanced = res);

    this.PatientEncounter$.subscribe(res =>
      this.PatientEncounter = res
    );
    this.PrescriptionHistoryList$.subscribe(res => this.PrescriptionHistoryList = res);
    this.dischargeMode$.subscribe(res => this.dischargeMode = res);

    this.dateMidnight = new Date(0);
    this.dateMidnight.setHours(0);
  }

  @select(['conversation', 'PatientEncounter']) PatientEncounter$;
  PatientEncounter: PatientEncounter;

  @select(['conversation', 'PrescriptionHistoryList']) PrescriptionHistoryList$;
  PrescriptionHistoryList;

  prescriptionTot = 1;
  prescriptionIndex = 0;

  eject = false;

  ngOnInit() {

    this.form.valueChanges.subscribe(() => {
      this.PrescriptionAction.errors = [];
      this.PrescriptionAction.warnings = [];
    });

    if (this.Prescription && !this.Prescription.duration) {
      this.noDuration = true;
    }

    if (this.PrescriptionHistoryList && this.PrescriptionHistoryList.entities) {
      this.prescriptionTot = this.PrescriptionHistoryList.entities.length;
      this.prescriptionIndex = this.PrescriptionHistoryList.entities.length - 1;
    }
  }

  ngOnDestroy() {
    if (this.eject) {
      this.PrescriptionAction.eject();
      this.FavoriteProfileAction.eject();
      this.conversationActions.remove('SelectedPrescriptionList');
      this.conversationActions.remove('PrescriptionHistoryList');
    }
  }

  ie(entity, conversationName): Promise<any> {
    if (!this[conversationName] || this[conversationName].internalId !== entity.internalId) {
      this.conversationActions.put(conversationName, entity);
    } else {
      this.conversationActions.put(conversationName, {quantity: {}}); // FIXME REMOVE object!!!
    }
    return Promise.resolve();
  }

  /**
   * History previous version
   */
  prev() {
    if (this.prescriptionIndex > 0) {
      this.prescriptionIndex--;
      const pRow = this.PrescriptionHistoryList.entities[this.prescriptionIndex];
      this.PrescriptionAction.inject(pRow.internalId, null, ['statusCode', 'code']);
    }
  }

  /**
   * History next version
   */
  next() {
    if (this.prescriptionIndex < this.PrescriptionHistoryList.entities.length - 1) {
      this.prescriptionIndex++;
      if (this.prescriptionIndex === this.PrescriptionHistoryList.entities.length -1) { // last version use in conv obj
        this.conversationActions.put('Prescription', this.SelectedPrescriptionList.entities[0]);
      } else {
        const pRow = this.PrescriptionHistoryList.entities[this.prescriptionIndex];
        this.PrescriptionAction.inject(pRow.internalId, null, ['statusCode', 'code']);
      }
    }
  }

  /**
   * CalPhrmHstryFrom on change
   */
  startDateChange() {
    this.PrescriptionAction.resetHighDate(this.Prescription);
  }

  /**
   * ChkBxPrscrptnEndDate on change
   */
  enableEndDateChange() {
    if (this.enableEndDate) {
      this.Prescription.period.high = new Date();
      this.Prescription.period.high.setSeconds(0);
    } else {
      this.Prescription.period.high = null;
    }
  }

  /**
   * Add medicine to profile
   */
  addMedicine() {
    // this.eject = false;
    this.viewManager.setPopupViewId('search-medicine');
  }

  removeMedicine() {
    const prescriptionIndex = this.SelectedPrescriptionList.entities.indexOf(this.Prescription);
    if (prescriptionIndex !== -1) {
      this.SelectedPrescriptionList.entities.splice(prescriptionIndex, 1);
      this.conversationActions.put('Prescription', this.SelectedPrescriptionList.entities[0]);
    }
  }

  indefinitelyChange() {
    if (this.noDuration) {
      delete this.Prescription.duration;
    }
  }

  /**
   * Save prescription and validate
   */
  saveAndValidate() {
    return this.save(true);
  }

  /**
   * Save prescription
   * @returns {any}
   */
  save(validate = false): Promise<any> {

    if (this.Prescription.prescriptionMedicine && this.Prescription.prescriptionMedicine.length > 0) {
      var dosage: Array<Dosage> = this.Prescription.prescriptionMedicine[0].dosage;
      PrescriptionActionService.orderDosage(dosage);
    }

    if(this.Prescription.code.code === 'INFU') {
      this.PrescriptionAction.fixDosages(this.Prescription);
    }

    if (this.PrescriptionAction.warnings.length === 0 && this.PrescriptionAction.errors.length === 0) {
      let noWarnOrErr = this.PrescriptionAction.checkInfos(this.SelectedPrescriptionList.entities, 0, this.favoriteConfiguration, this.dischargeMode, this.enableEndDate);

      if (!noWarnOrErr) {
        return Promise.resolve();
      }
    }

    if (this.favoriteConfiguration) {

      this.FavoriteProfileAction.entity = this.FavoriteProfile;

      this.FavoriteProfile.prescription = this.SelectedPrescriptionList.entities;

      return this.FavoriteProfileAction.create()
        // .then(() => this.FavoriteTabAction.read())
        .then(() => {

          if (this.viewManager.formComponent instanceof FavoriteComponent) {
            this.viewManager.formComponent.readFavoriteTabs();
          }

          this.close();
        });

    } else {

      return this.getOrCreateTherapy().then(() =>
        this.savePrescriptions(this.SelectedPrescriptionList.entities, validate).then(() => {

          if (this.viewManager.formComponent instanceof DrugPrescriberComponent) {
            this.viewManager.formComponent.refresh().then(() =>
              this.close()
            );
          } else {
            this.close();
          }

        })
      );

    }
  }

  /**
   * Get current therapy from PatientEncounter, if not found create a new therapy
   * @returns {Promise<Therapy>}
   */
  private getOrCreateTherapy(): Promise<Therapy> {
    let therapy:Therapy = this.PatientEncounter.therapy[0];
    if (!therapy) {

      therapy = {};
      therapy.patientEncounter = {internalId: this.PatientEncounter.internalId, entityName: 'com.phi.entities.act.PatientEncounter'};

      this.TherapyAction.entity = therapy;

      return this.TherapyAction.create().then((newTherapy) => {
        if (!this.PatientEncounter.therapy) {
          this.PatientEncounter.therapy = [];
        }
        this.PatientEncounter.therapy.push(newTherapy);
        this.conversationActions.put('PatientEncounter', this.PatientEncounter);
        //this.Prescription.therapy = {internalId: newTherapy.internalId, entityName: 'com.phi.entities.act.Therapy'};
        return newTherapy;
      });

    } else {
      return Promise.resolve(therapy);
    }
  }

  /**
   * Save a list of Prescriptions
   * @param prescriptionsToSave
   * @returns {any}
   */
  private savePrescriptions(prescriptionsToSave: Array<Prescription>, validate = false): Promise<any> {

    let savedPrescriptions: Array<Promise<any>> = prescriptionsToSave.map((pr: Prescription) => {
      pr.therapy = {internalId: this.PatientEncounter.therapy[0].internalId, entityName: 'com.phi.entities.act.Therapy'};
      pr.note = prescriptionsToSave[0].note;
      return this.savePrescription(pr, validate);
    });

    //When all done return
    return Promise.all(savedPrescriptions);
  }

  /**
   * Save a single Prescription
   * @param currPrescription
   * @returns {any}
   */
  private savePrescription(currPrescription: PrescriptionGeneric, validate = false): Promise<any> {

    if (!currPrescription.internalId) {
      // NEW
      if (!this.dischargeMode) {
        //if (currPrescription instanceof Prescription) {
        this.PrescriptionAction.entity = currPrescription;
        if (validate) {
          currPrescription.statusCode.code = 'active';
        }
        return this.PrescriptionAction.create().then((savedPr: Prescription) => {
          if(validate && savedPr.internalId) {
            return this.PrescriptionAction.changeStatus(savedPr.internalId, PrescriptionActionService.ACTION_VALIDATE, savedPr.period.low)
          }
        });
      } else {
      //} else if (currPrescription instanceof PrescriptionDischarge) {
        this.PrescriptionDischargeAction.entity = currPrescription as PrescriptionDischarge;
        this.PrescriptionDischargeAction.entity.statusCode.code = 'active';
        return this.PrescriptionDischargeAction.create();
      }

    } else if (currPrescription.statusCode.code === 'new') {

      this.PrescriptionAction.entity = currPrescription;
      return this.PrescriptionAction.update();

    } else if (currPrescription.statusCode.code === 'active') {

      if (!this.dischargeMode) {
      //if (currPrescription instanceof Prescription) {
        return this.PrescriptionAction.changeStatus(currPrescription.internalId,
          PrescriptionActionService.ACTION_INVALIDATE_AND_MODIFY, (currPrescription as Prescription).period.low)
          .then(() => {
            let clone = this.PrescriptionAction.clone(currPrescription);
            if (currPrescription.rootPrescription) {
              clone.rootPrescription = {
                internalId: currPrescription.rootPrescription.internalId,
                entityName: 'com.phi.entities.baseEntity.Prescription'
              };
            } else {
              clone.rootPrescription = {
                internalId: currPrescription.internalId,
                entityName: 'com.phi.entities.baseEntity.Prescription'
              };
            }
            this.PrescriptionAction.entity = clone;
            return this.PrescriptionAction.create().then((createdPrescription:Prescription) => {
              return this.PrescriptionAction.changeStatus(createdPrescription.internalId, PrescriptionActionService.ACTION_VALIDATE, createdPrescription.period.low);
            });
          });
      } else {
      //} else if (currPrescription instanceof PrescriptionDischarge) {
        this.PrescriptionDischargeAction.entity = currPrescription as PrescriptionDischarge;
        return this.PrescriptionDischargeAction.update();
      }
    }
  }

  close() {
    this.viewManager.setPopupViewId(null);
    this.eject = true;
  }

}
