import { ConfigActions } from './../../actions/config.action';
import { Component, Injector, OnInit, OnDestroy, HostBinding } from '@angular/core';
import { Router, ActivatedRoute, UrlSegment } from '@angular/router';
import { select } from '@angular-redux/store';
import { TranslateService } from '@ngx-translate/core';
import { PrescriptionActionService,
  PrescriptionDischargeActionService,
  PatientActionService,
  PatientEncounterActionService,
  TemplateDosageActionService } from '../../services/actions/.';
import { DrugPrescriberAction } from './actions/drug-prescriber-action.service';
import { Datamodel } from '../../services/datamodel/datamodel';
import { ConversationActions } from '../../actions/conversation.actions';
import { Prescription } from '../../services/entities/base-entity';
import { PrescriptionGeneric } from '../../services/entities/base-entity/prescription-generic';
import { PrescriptionDischarge } from '../../services/entities/base-entity/prescription-discharge';
import { VitalSignActionService } from '../../services/actions/viital-sign-action.service';
import { BaseForm } from '../../widgets/form/base-form';
import { ViewManager } from '../../services/view-manager';
import { ParameterManager } from '../../services/parameter-manager';
import { ProcessActions } from 'app/actions/process.actions';


@Component({
  templateUrl: './drug-prescriber.html',
  styleUrls: ['./drug-prescriber.component.scss']
})
export class DrugPrescriberComponent extends BaseForm implements OnInit, OnDestroy {

  private static DAY_IN_MILLISECONDS = 24 * 60 * 60 * 1000;

  @select(['conversation', 'PrescriptionList']) PrescriptionList$;
  PrescriptionList;
  @select(['conversation', 'Prescription']) Prescription$;
  Prescription;
  @select(['conversation', 'Patient']) Patient$;
  Patient;
  @select(['conversation', 'PatientEncounter']) PatientEncounter$;
  PatientEncounter;
  @select(['conversation', 'PrescriptionAdditionalAllergy']) PrescriptionAdditionalAllergy$;
  PrescriptionAdditionalAllergy;

  @HostBinding('class') clazz = 'fullHeightWidth';

  @select(['conversation', 'dischargeMode']) dischargeMode$;
  dischargeMode;

  @select(['config', 'menuVisible']) menuVisible$;
  menuVisible: boolean;

  copyMode = false;
  selectUnselectAll = false;

  allPrescriptions: Array<any> = null;

  showNew = true;
  showActive = true;
  showCompleted = false;

  newPrescriptions = 0;
  activePrescriptions = 0;
  completedPrescriptions = 0;

  constructor(injector: Injector,
              private router: Router,
              private processActions: ProcessActions,
              private route: ActivatedRoute,
              private viewManager: ViewManager,
              private conversationActions: ConversationActions,
              private translateService: TranslateService,
              private drugPrescriberAction: DrugPrescriberAction,
              private patientAction: PatientActionService,
              private PatientEncounterAction: PatientEncounterActionService,
              private PrescriptionAction: PrescriptionActionService,
              private PrescriptionDischargeAction: PrescriptionDischargeActionService,
              private templateDosagesAction: TemplateDosageActionService,
              private VitalSignAction: VitalSignActionService,
              private parameterManager: ParameterManager,
              private configActions: ConfigActions) {

    super(injector);

    this.PrescriptionList$.subscribe(res => this.PrescriptionList = res);
    this.Prescription$.subscribe(res => this.Prescription = res);
    this.Patient$.subscribe(res => this.Patient = res);
    this.PatientEncounter$.subscribe(res => this.PatientEncounter = res);
    this.PrescriptionAdditionalAllergy$.subscribe(res => this.PrescriptionAdditionalAllergy = res);
    this.dischargeMode$.subscribe(res => this.dischargeMode = res);
    this.menuVisible$.subscribe(res => this.menuVisible = res);
  }

  ngOnInit() {

    if ( this.route.snapshot.url.find((url: UrlSegment) => url.path === 'drug-prescriber-discharge') ) {
      this.conversationActions.put('dischargeMode', true);
    } else {
      this.conversationActions.put('dischargeMode', false);
    }

    this.route.queryParams.subscribe(params => {

      if (params['copyMode']) {
        this.copyMode = true;
      } else {
        this.copyMode = false;
      }

      if (this.PatientEncounter) {
        this.readTherapy(this.PatientEncounter);
      } else {
        this.router.navigate(['/dashboard', 'adt']);
      }

    });
  }

  ngOnDestroy() {
    this.conversationActions.remove('Prescription');
    this.conversationActions.remove('PrescriptionList');
    this.conversationActions.remove('PrescriptionAdditionalAllergy');
  }

  readTherapy(patientEncounter) {
    if (patientEncounter.therapy != null && patientEncounter.therapy.length > 0) {
      this.drugPrescriberAction.equal['therapyId'] = patientEncounter.therapy[0].internalId;
      if (this.dischargeMode) {
        this.drugPrescriberAction.equal['prescriptionDischarge'] = true;
      } else {
        delete this.drugPrescriberAction.equal['prescriptionDischarge'];
      }
      this.drugPrescriberAction.init().then((dataz) => {
        if (dataz) {
          this.allPrescriptions = dataz.entities;
          this.filterPrescriptions();
        } else {
          this.allPrescriptions = null;
        }
        if (this.PrescriptionAdditionalAllergy && this.PrescriptionAdditionalAllergy.length === 1) {
          if (this.PrescriptionAdditionalAllergy[0].allergy === 0) {
            this.viewManager.openAlertMessage(this.translateService.instant('Warning'),
              this.translateService.instant('No_allergy_Warning'), false);
          }
        }
      }).then(() =>
        this.readTemplateDosages()
      );
    } else {
      this.patientAction.countAllergies(this.Patient.internalId).then((tot) => {
        if (tot === 0) {
          this.viewManager.openAlertMessage(this.translateService.instant('Warning'),
            this.translateService.instant('No_allergy_Warning'), false);
        }
      });
      this.drugPrescriberAction.cleanRestrictions();
      this.drugPrescriberAction.cleanResults();
      this.conversationActions.remove('PrescriptionAdditionalAllergy');
    }
  }

  filterPrescriptions() {
    this.newPrescriptions = 0;
    this.activePrescriptions = 0;
    this.completedPrescriptions = 0;

    if (this.allPrescriptions) {
      this.PrescriptionList = new Datamodel(this.allPrescriptions.filter(prescription => {

        switch (prescription.status) {
          case 'new':
            this.newPrescriptions++;
            return (this.showNew);
          case 'active':
            this.activePrescriptions++;
            return (this.showActive);
          case 'completed':
            this.completedPrescriptions++;
            return (this.showCompleted);
        }
        return false;

      }));
    }
  }

  refresh(): Promise<any> {
    if (this.drugPrescriberAction.equal['therapyId']) {
      return this.drugPrescriberAction.refresh().then((dataz) => {
        if (dataz) {
          this.allPrescriptions = dataz.entities;
          this.filterPrescriptions();
        } else {
          this.allPrescriptions = null;
        }
      });
    }
  }

  ie(entity, conversationName): Promise<any> {
    if (!this.dischargeMode) {
      if (!this.Prescription || this.Prescription && this.Prescription.internalId !== entity.internalid) {
        return this.PrescriptionAction.inject(entity.internalid, null, ['statusCode', 'rootPrescription', 'code']);
      }
    } else {
      return this.PrescriptionDischargeAction.inject(entity.internalid, null, ['statusCode', 'code']).then((p) => {
        this.conversationActions.put(conversationName, p);
        return p;
      });
    }
  }

  showAdministrationHistory(prescriptionRow) {
    this.inject(prescriptionRow, 'Prescription').then((prescription: Prescription) => {
      let id;
      if (prescription.rootPrescription) {
        id = prescription.rootPrescription.internalId;
      } else {
        id = prescription.internalId;
      }
      this.drugPrescriberAction.getAdministrationHistory(id).then(() => {
        this.viewManager.setPopupViewId('erogation');
      });
    });

  }


  /**
   * Edit prescription
   * @param prescriptionRow
   */
  edit(prescriptionRow) {
    if (!this.dischargeMode) {
      this.viewManager.setPopupViewId('prescription-edit', prescriptionRow.internalid);
    } else {
      this.viewManager.setPopupViewId('prescription-discharge-edit', prescriptionRow.internalid);
    }
  }

  /**
   * Validate a single prescription
   * @param prescriptionRow
   */
  validate(prescriptionRow) {
    this.inject(prescriptionRow, 'Prescription').then((prescription: Prescription) => {
      this.PrescriptionAction.entity = prescription;
      return this.PrescriptionAction.changeStatus(prescription.internalId,
          PrescriptionActionService.ACTION_VALIDATE, prescription.period.low)
        .then(() => this.PrescriptionAction.eject())
        .then(() => this.refresh())
    });
  }

  delete(prescriptionRow) {
    this.inject(prescriptionRow, 'Prescription').then((prescription: PrescriptionGeneric) => {
      if (!this.dischargeMode) {
        if (prescription.statusCode.code === 'new') {
          this.PrescriptionAction.entity = prescription;
          this.PrescriptionAction.delete().then(() => this.refresh());
        } else if (prescription.statusCode.code === 'active') {
          this.PrescriptionAction.changeStatus(prescription.internalId, PrescriptionActionService.ACTION_INVALIDATE, new Date()).then(() =>
            this.refresh()
          );
        }
      } else {
        this.PrescriptionDischargeAction.entity = prescription as PrescriptionDischarge;
        this.PrescriptionDischargeAction.delete().then(() => this.refresh());
      }
    });
  }

  /**
   * Read TemplateDosages
   */
  public readTemplateDosages(/* event:Event = null*/): void {
    const sdlId = this.PatientEncounterAction.getServiceDeliveryLocationId(this.PatientEncounter);
    if (sdlId != null) {
      this.templateDosagesAction.equal['isActive'] = true;
      this.templateDosagesAction.equal['serviceDeliveryLocation.internalId'] = sdlId;
      this.templateDosagesAction.equal['code.code'] = 'THERAPY';
      this.templateDosagesAction.orderBy['title'] = 'ascending';
      this.templateDosagesAction.read();
    }
  }

  printReport() {
    this.viewManager.setPopupViewId('print-report');
  }

  /**
   * Validate all prescriptions in state new
   */
  validateAllPrescription() {
    if (this.allPrescriptions) {
      let changeStatuses: Array<Promise<any>> = this.allPrescriptions
        .filter(prescription => prescription.status === 'new')
        .map((prescription: any) => this.PrescriptionAction.changeStatus(
          prescription.internalid, PrescriptionActionService.ACTION_VALIDATE, prescription.startdate));

      // When all done refresh
      Promise.all(changeStatuses).then(() => this.refresh());
    }
  }

  vitalSign() {
    this.VitalSignAction.getLastVitalSign(this.PatientEncounter.internalId).then(() =>
    this.viewManager.setPopupViewId('vital-sign-last')
  );
  }

  searchPatient() {
    this.router.navigate(['/dashboard', 'adt']);
  }

  openFavoriteTherapies() {
    const sdlId = this.PatientEncounterAction.getServiceDeliveryLocationId(this.PatientEncounter);
    this.router.navigate(['/dashboard', 'favorite', sdlId, 'therapy']);
  }

  configFavoriteTherapies() {
    const sdlId = this.PatientEncounterAction.getServiceDeliveryLocationId(this.PatientEncounter);
    this.router.navigate(['/dashboard', 'favorite', sdlId, 'therapy', 'configuration']);
  }

  /**
   * Discharge therapy
   * activate copyMode from terapia in ricovero
   */
  activateCopyMode() {
    this.router.navigate(['/dashboard', 'drug-prescriber'], { queryParams: { id: this.PatientEncounter.internalId, copyMode: true}});
  }

  /**
   * Discharge therapy
   * close copyMode from terapia in ricovero
   */
  closeCopyMode() {
    this.router.navigate(['/dashboard', 'drug-prescriber-discharge'], { queryParams: { id: this.PatientEncounter.internalId}});
  }

  /**
   * Starts DischargeLetter process
   * check parameter 'p.dashboard.drugprescriber.dischargeletterprocess'
   * bz:      'MOD_Inpatient/CORE/PROCESSES/DischargeLetter'
   * default: 'MOD_Inpatient/customer_VCO/PROCESSES/DL_Switcher'
   */
  startDischargeLetterProcess() {
    const process = this.parameterManager.getParameter('p.dashboard.drugprescriber.dischargeletterprocess', 'value');
    if (process) {
      this.startProcess(process);
    } else {
      // default VCO process
      this.startProcess('MOD_Inpatient/customer_VCO/PROCESSES/DL_Switcher');
    }
  }

  /**
   * Discharge therapy
   * start process discharge letter
   */
  startProcess(process: string) {
    if (this.menuVisible) {
      this.configActions.toggleMenu();
    }
    this.processActions.startProcess(process);
  }

  /**
   * Copy mode
   * select/unselect all prescription
   */
  selectAll() {
    this.selectUnselectAll = !this.selectUnselectAll;
    if (this.PrescriptionList) {
      for (let row of this.PrescriptionList.entities) {
        if (row.code === 'PHARMA') {
          row.isSelected = this.selectUnselectAll;
        }
      }
    }
  }

  /**
   * Copy mode
   * copy selected prescriptions to discharge therapy
   */
  copyToPrescriptionDischarge(): void {
    if (this.PrescriptionList) {
      let selectedIds: Array<number> = [];
      for (let row of this.PrescriptionList.entities) {
        if (row.isSelected) {
          selectedIds.push(row.internalid);
        }
      }

      if (selectedIds.length !== 0) {
        this.drugPrescriberAction.copyPrescriptionsToPrescriptionDischarges(selectedIds, true).then(() => this.closeCopyMode());
      }
    }
  }

  // Function to format the start and the end date
  public getDuration(startDate: Date, endDate: Date): string {
    if (!startDate) {
      return '';
    }
    let checkDate: Date = new Date();
    checkDate.setSeconds(0);
    checkDate.setMilliseconds(0);

    startDate.setSeconds(0);
    startDate.setMilliseconds(0);

    if (endDate != null) {
      endDate.setSeconds(0);
      endDate.setMilliseconds(0);
    }

    let result = '';
    if (endDate == null) {
      result += ' (?)';
    } else {
      result += ' (' + Math.ceil(((endDate.getTime() - startDate.getTime())
        / DrugPrescriberComponent.DAY_IN_MILLISECONDS)).toString() + ' gg)';
    }

    return result;
  }

  // Function to format the start and the end date
  public getClass(startDate: Date, endDate: Date): string {
    if (!startDate) {
      return '';
    }
    let checkDate: Date = new Date();
    checkDate.setSeconds(0);
    checkDate.setMilliseconds(0);

    startDate.setSeconds(0);
    startDate.setMilliseconds(0);

    if (endDate != null) {
      endDate.setSeconds(0);
      endDate.setMilliseconds(0);
    }

    let color: string;
    if (checkDate.getTime() >= startDate.getTime()) {
      if (endDate === null || checkDate.getTime() < endDate.getTime()) {
        color = 'started'; // '#009900';
      } else {
        color = 'ended'; // '#990000';
      }
    } else {
      color = 'notstarted'; // '#999999';
    }
    return color;
  }

  public getStatusClass(status: string): string {
    if (status === 'new') {
      return 'fa-hourglass blue';
    } else if (status === 'active') {
      return 'fa-square green';
    } else if (status === 'completed') {
      return 'fa-check-square-o green';
    }
  }

}
