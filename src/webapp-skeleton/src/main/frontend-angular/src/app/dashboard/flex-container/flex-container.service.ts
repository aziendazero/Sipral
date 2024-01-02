import {Inject, Injectable, NgZone} from '@angular/core';
import {select} from '@angular-redux/store';
import {BaseEntity} from '../../services/entities/base-entity/base-entity';
import {PatientEncounter} from '../../services/entities/act/patient-encounter';
import {Patient} from '../../services/entities/role/patient';
import {Router} from '@angular/router';
import {ProcessActions} from '../../actions/process.actions';
import {ConfigActions} from '../../actions/config.action';
import {ViewManager} from '../../services/view-manager';

@Injectable()
export class FlexContainerService {
  @select(['process', 'cid']) cid$;
  cid: string;
  @select(['process', 'current']) currentProcess$;
  currentProcess;

  @select(['conversation', 'Patient']) Patient$;
  Patient: Patient;
  @select(['conversation', 'PatientEncounter']) PatientEncounter$;
  PatientEncounter: PatientEncounter;
  @select(['conversation', 'Appointment']) Appointment$;
  Appointment: BaseEntity; // TODO: cast to appointment;

  nativeElement;
  flexInitialized = false;
  // dashboardName;

  constructor(
    private router: Router,
    private zone: NgZone,
    private processActions: ProcessActions,
    private ConfigActions: ConfigActions,
    @Inject('apiUrl') protected apiUrl,
    private viewManager: ViewManager
  ) {
    this.cid$.subscribe(res => this.cid = res);
    this.currentProcess$.subscribe((res) => this.currentProcess = res);

    this.Patient$.subscribe(res => this.Patient = res);
    this.PatientEncounter$.subscribe(res => this.PatientEncounter = res);
    this.Appointment$.subscribe(res => this.Appointment = res);

    window['flexCommunicator'] = this.flexCommunicator.bind(this);
  }

  varNameBCKUP;
  varValueBCKUP;

  flexCommunicator(modulePath, varName = null, varValue = null) {
    if (!modulePath) {
      console.log('flex-component: module path to open is not defined');
      return;
    }
    try {
      let moduleName = modulePath;
      if (modulePath.indexOf("swf/modules/") !== -1 && modulePath.indexOf(".swf") !== -1 ) { // adjust: moduleTarget = "swf/modules/" + moduleName + ".swf";
        moduleName = modulePath.substring(modulePath.indexOf("swf/modules/") + 12, modulePath.indexOf(".swf"));
      } else {
        modulePath = "swf/modules/" + moduleName + ".swf";
      }

      if (this.router.url.indexOf('flex') === -1 && this.router.url.indexOf('process') !== -1) { // you are coming from process!
        let newURL = "flex/" + moduleName;
        this.processActions.setDashboard(newURL);
      }
      console.log('flex-component: trying to open ' + modulePath);
      let jsonBanner = this.buildJsonBanner();
      if (!this.flexInitialized || (this.nativeElement as any).jsCommunicator === undefined || typeof ((this.nativeElement as any).jsCommunicator) != 'function') {
        if (this.nativeElement) {
          this.nativeElement.style.border = '1px solid #fff'; // force a style refresh
        }
        console.log('flex-component: flex non yet initialized');
      } else {
        if (this.varNameBCKUP || this.varValueBCKUP) {
          varName = this.varNameBCKUP;
          varValue = this.varValueBCKUP;
          this.varNameBCKUP = null;
          this.varValueBCKUP = null;
        }
        (this.nativeElement as any).jsCommunicator(modulePath, this.cid, jsonBanner, varName, varValue);
        this.processActions.setDashboard(null);
        console.log('flex-component: ok');
      }
    } catch (error) { // there is a catch in flex: when we call jsCommunicator, errors are catched inside flex!
      console.error(error.stack);
      this.viewManager.openErrorMessage('Error', error);
      fetch(this.apiUrl + 'resource/rest/logs/error',
        {
          method: 'POST',
          body: error.stack,
          headers: {'Content-Type': 'text/plain'},
          credentials: 'include'
        });
    }
  }

  buildJsonBanner(): string {
    let  jsonBanner = {};

    if (this.Patient) {
      jsonBanner['patientId'] = this.Patient.internalId;
      if (this.Patient.name) {
        jsonBanner['patientNameGiv'] = this.Patient.name.giv;
        jsonBanner['patientNameFam'] = this.Patient.name.fam;
      }
      if (this.Patient.birthTime && this.Patient.birthTime instanceof Date) {
        jsonBanner['birthTime'] = this.Patient.birthTime.getDay() + "/" + this.Patient.birthTime.getMonth() + "/" + this.Patient.birthTime.getFullYear();
      }
    }

    if (this.PatientEncounter) {
      jsonBanner['patientEncounterId'] = this.PatientEncounter.internalId;
      if (this.PatientEncounter.assignedSDL) {
        jsonBanner['assignedSDLId'] = this.PatientEncounter.assignedSDL.internalId;
      }
      if (this.PatientEncounter.temporarySDL) {
        jsonBanner['temporarySDLId'] = this.PatientEncounter.temporarySDL.internalId;
      }
      if (this.PatientEncounter.therapy && this.PatientEncounter.therapy.length > 0) {
        jsonBanner['therapyId'] = this.PatientEncounter.therapy[0].internalId;
      }

      jsonBanner['renderPrivacy'] = true; // TODO get from conversation
    }

    if (this.Appointment) {
      jsonBanner['appointmentId'] = this.Appointment.internalId;
    }

    /* TODO
    renderPrivacy
    datePrivacyFrom
    datePrivacyTo
    */

    return JSON.stringify(jsonBanner);
  }
}
