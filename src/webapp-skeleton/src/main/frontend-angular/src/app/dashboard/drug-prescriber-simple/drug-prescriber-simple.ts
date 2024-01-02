import { Component, Injector, ViewChild } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { select } from '@angular-redux/store';
import { TranslateService } from '@ngx-translate/core';
import { BaseForm } from '../../widgets/form/base-form';
import { ConversationActions } from '../../actions/conversation.actions';
import { TherapySimpleActionService } from '../../services/actions';
import { PatientEncounter } from '../../services/entities/act';
import { TherapySimple, PrescriptionSimple } from '../../services/entities/base-entity';
import { Datamodel } from '../../services/datamodel/datamodel';
import { ViewManager } from '../../services/view-manager';
import { PatientEncounterActionService } from '../../services/actions/patient-encounter-action.service';
import { ServiceDeliveryLocationActionService } from '../../services/actions/service-delivery-location-action.service';

@Component({
  selector: 'phi-drug-prescriber-simple',
  templateUrl: './drug-prescriber-simple.html',
  styleUrls: ['./drug-prescriber-simple.scss']
})
export class DrugPrescriberSimple extends BaseForm  {

  static urlReport = '/swf/modules/DrugPrescriber/reports/r_therapySimple.seam';
  static urlReportUTIC = '/swf/modules/DrugPrescriber/reports/r_therapyUTIC.seam';
  static limitedScheduleWardSpecialization: Array<string> = ['08','49','50'];
// ['AMMINISTRATIVO', 'DIRETTORE',  'MEDICO', 'PROGRAMMATORE'];
public static superUserRoleCodes = ['1', '6', '11', '16'];

  @ViewChild('form') form: NgForm;
  @select(['config', 'employeeRoleCode']) employeeRoleCode$;
  employeeRoleCode: string;
  @select(['conversation', 'PatientEncounter']) PatientEncounter$;
  patientEncounterSub;
  PatientEncounter: PatientEncounter;
  @select(['conversation', 'TherapySimple']) TherapySimple$;
  therapySimpleSub;
  TherapySimple: TherapySimple;

  PrescriptionOral: Datamodel;
  PrescriptionIntraSub: Datamodel;
  PrescriptionEndo: Datamodel;
  PrescriptionVarious: Datamodel;

  somethingChanged = false;

  limitedSchedule = false;
  issuperuser=false
  
  constructor(injector: Injector,
              private router: Router,
              private TherapySimpleAction: TherapySimpleActionService,
              private PatientEncounterAction: PatientEncounterActionService,
              private ServiceDeliveryLocationAction: ServiceDeliveryLocationActionService,
              private viewManager: ViewManager,
              private conversationActions: ConversationActions,
              private translateService: TranslateService) {
    super(injector);
    
 this.employeeRoleCode$.subscribe(res => this.employeeRoleCode = res);
    this.patientEncounterSub = this.PatientEncounter$.subscribe(res => {
      this.PatientEncounter = res;
    });
    this.therapySimpleSub = this.TherapySimple$.subscribe(res => {
      if (res) {
        this.TherapySimple = res;
        this.filterPrescriptionSimple();
      } else {
        this.TherapySimple = {};
      }
    });
    if(DrugPrescriberSimple.superUserRoleCodes.indexOf(this.employeeRoleCode) !== -1){
      this.issuperuser=true;
    }
  }
 
  ngOnInit() {

    this.form.valueChanges.subscribe(() => {
      if (this.form.pristine) {
        this.somethingChanged = false;
      } else {
        this.somethingChanged = true;
      }
    });

    if (this.PatientEncounter) {
      const sdlId = this.PatientEncounterAction.getServiceDeliveryLocationId(this.PatientEncounter);
      this.ServiceDeliveryLocationAction.cleanRestrictions();
      this.ServiceDeliveryLocationAction.select.push('specialization.code');
      this.ServiceDeliveryLocationAction.equal['internalId'] = sdlId;
      this.ServiceDeliveryLocationAction.read().then((dm: Datamodel) => {
        if (dm && dm.entities && dm.entities.length > 0 && dm.entities[0].specialization) {
          const wardSpecialization = dm.entities[0].specialization.code;
          if (DrugPrescriberSimple.limitedScheduleWardSpecialization.indexOf(wardSpecialization) == -1) { // not .includes(...)) {
            this.limitedSchedule = true;
          }
        }
        this.TherapySimpleAction.equal['patientEncounter.internalId'] = this.PatientEncounter.internalId;
        this.refresh();
      });

    } else {
      this.router.navigate(['/dashboard', 'adt']);
    }
  }

  ngOnDestroy() {
    this.patientEncounterSub.unsubscribe();
    this.therapySimpleSub.unsubscribe();
    this.conversationActions.remove('TherapySimple');
  }

  refresh(): Promise<any> {

    this.TherapySimple = {};

    return this.TherapySimpleAction.read().then((dm: Datamodel) => {
      if (dm.entities && dm.entities.length > 0) {
        this.conversationActions.put('TherapySimple', dm.entities[0]);
        this.filterPrescriptionSimple();
      } else {
        this.conversationActions.put('TherapySimple', this.TherapySimpleAction.newTherapySimple(this.PatientEncounter));
      }
    });
  }

  filterPrescriptionSimple() {

    this.PrescriptionOral = new Datamodel([]);
    this.PrescriptionIntraSub = new Datamodel([]);
    this.PrescriptionEndo = new Datamodel([]);
    this.PrescriptionVarious = new Datamodel([]);

    if (this.TherapySimple.prescriptionSimple) {
      this.TherapySimple.prescriptionSimple.map((ps: PrescriptionSimple) => {
        if (ps.routeCode) {
          if (ps.routeCode.id === '2.16.840.1.113883.3.20.888.2323.3.0_V0') { //OS
            this.PrescriptionOral.entities.push(ps);
          } else if (ps.routeCode.id === '2.16.840.1.113883.3.20.888.2323.1.0_V0') { //IMU
            this.PrescriptionIntraSub.entities.push(ps);
          } else if (ps.routeCode.id === '2.16.840.1.113883.3.20.888.2323.0.3_V0') { //EV
            this.PrescriptionEndo.entities.push(ps);
          }
        } else {
          this.PrescriptionVarious.entities.push(ps);
        }
      });
    }
  }

  cancel() {

    this.refresh().then((dm) => {
      this.form.form.markAsPristine();
      this.somethingChanged = false;
    });

  }

  save() {

    this.TherapySimple.editDate = new Date();

    this.TherapySimple.prescriptionSimple = [];
    this.TherapySimple.prescriptionSimple = this.TherapySimple.prescriptionSimple.concat(this.PrescriptionOral.entities);
    this.TherapySimple.prescriptionSimple = this.TherapySimple.prescriptionSimple.concat(this.PrescriptionIntraSub.entities);
    this.TherapySimple.prescriptionSimple = this.TherapySimple.prescriptionSimple.concat(this.PrescriptionEndo.entities);
    this.TherapySimple.prescriptionSimple = this.TherapySimple.prescriptionSimple.concat(this.PrescriptionVarious.entities);

    this.TherapySimpleAction.entity = this.TherapySimple;

    this.TherapySimpleAction.create().then((dm) => {
      this.form.form.markAsPristine();
      this.somethingChanged = false;
    });

  }

  printSimple() {
    this.printReport(DrugPrescriberSimple.urlReport);
  }

  printUtic() {
    this.printReport(DrugPrescriberSimple.urlReportUTIC);
  }

  private printReport(url) {
    if (this.somethingChanged) {
      this.viewManager.openAlertMessage(
        this.translateService.instant('Warning'),
        this.translateService.instant('print-warning'),
        true,
        true,
        'OK',
        'Cancel',
        true,
        () => {
          this.save();
        }
      );

    } else {
      this.TherapySimpleAction.inject(this.TherapySimple.internalId).then(() => {
        this.viewManager.openReport(url);
      });
    }
  }

}
