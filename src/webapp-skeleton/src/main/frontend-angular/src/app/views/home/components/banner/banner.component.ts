import { Annotation } from './../../../../services/entities/act/annotation';
import { BannerActions } from './../../../../actions/banner.action';

import { EmployeeActionService } from '../../../../services/actions/employee-action.service';
import { Component,Inject } from '@angular/core';
import { select } from '@angular-redux/store';
import { Patient } from '../../../../services/entities/role/patient';
import { PatientEncounter } from '../../../../services/entities/act/patient-encounter';
import { DictionaryManager } from '../../../../services/dictionary-manager';
import { CodeValue } from '../../../../services/entities/data-types/code-value';
import { PatientActionService } from '../../../../services/actions';
import { ViewManager } from '../../../../services/view-manager';
import { TranslateService } from '@ngx-translate/core';
import { environment } from '../../../../../environments/environment';
import { ProcessActions } from '../../../../actions/process.actions';
import { Params, Router } from '@angular/router';
import { HttpService } from 'app/services';
import Phi from 'app/legacy/js';

@Component({
  selector: 'phi-banner',
  // changeDetection: ChangeDetectionStrategy.OnPush, // Le modifiche non vengono propagate veso giù ma solo in su!!!
  // Verrà aggiornato solo quando cambiano @Input (quando cambia reference ovvero dati immutable) -> aggiungi input
  // da aggiungere a tutti i componenti figli che hanno props
  templateUrl: './banner.component.html',
  styleUrls: ['./banner.component.scss']
})
export class BannerComponent {

  @select(['conversation', 'Patient']) Patient$;
  Patient: Patient;
  @select(['conversation', 'PatientEncounter']) PatientEncounter$;
  PatientEncounter: PatientEncounter;

  @select(['banner']) Banner$;
  Banner;

  @select(['config']) config$;
  isPortal;
  sid: any;

  @select(['process', 'cid']) cid$;
  cid: any;

  @select(['config', 'param']) param$;
  Param: Map<string, any>;

  environment = environment;
  gender;
  restBaseUrl = 'resource/rest/';
  // language;
  PatientNote:Annotation;
  consent;
  isolationvis: boolean;
  privacyvis: boolean;
  privacy;
  phi: Phi;

  constructor(@Inject('apiUrl') protected apiUrl,
              private dictionaryManager: DictionaryManager,
              private viewManager: ViewManager,
              private PatientAction: PatientActionService,
              private EmployeeAction: EmployeeActionService,
              private translateService: TranslateService,
              private processActions: ProcessActions,
              private BannerActions: BannerActions,
              private httpService: HttpService) {

    this.Patient$.subscribe((res) => {
      this.Patient = res;
      if (this.Patient) {
        if (this.Patient.genderCode) {
          this.dictionaryManager.getById(this.Patient.genderCode.id).then((cv: CodeValue) => this.gender = cv.currentTranslation);
        }
        if (environment.banner.showConsent) {
          // KLINIK
          if (!this.Patient.consent) {
            this.consent = false;
          } else if (this.Patient.consent.code === 'ND' || this.Patient.consent.code === 'NO') {
            this.consent = false;
          } else if (this.Patient.consentExpireDate && this.Patient.consentExpireDate.getTime() < new Date().getTime()) {
            this.consent = false;
          } else {
            this.consent = true;
          }
        }

      }
    });
    this.PatientEncounter$.subscribe((res) => {
      this.PatientEncounter = res;
    });
    this.Banner$.subscribe((res) =>
      this.Banner = res
    );
    this.config$.subscribe((res) => {
      this.isPortal = res.isPortal;


    } );

    this.param$.subscribe(param => {
      this.Param = param;


        if (param && param['p.banner.isolation']) {
          this.isolationvis = param['p.banner.isolation'].value;
        } else {
          this.isolationvis = false;
        }

        if (param && param['p.banner.showprivacy']) {
          this.privacyvis = param['p.banner.showprivacy'].value;
        } else {
          this.privacyvis = false;
        }
      });
      this.cid$.subscribe((res) => this.cid = res);

    this.config$.subscribe((res) =>
      this.sid = res.sid
    );

    this.phi = new Phi(this.cid);
  }


  getAllergy(): string {
    if (this.Banner['allergyIcon'] === 'images/allergy_icon.gif') {
      return 'allergy';
    } else if (this.Banner['allergyIcon'] === 'images/allergy_gen_icon.gif') {
      return 'allergy-generic';
    } else if (!this.Banner['allergyIcon'] || this.Banner['allergyIcon'] === 'images/noAllergy_icon.gif') {
      return 'no-allergy';
    }
  }

  getDiet(): string {
    if (this.Banner['dietIcon'] === 'images/okDiet_icon.png') {
      return 'diet-generic';
    } else if (this.Banner['dietIcon'] === 'images/noDiet_icon.png') {
      return 'no-diet';
    }
    return 'undefined_diet';
  }


    getCVC(): string {
    if (this.Banner['cvcIcon'] === 'images/cvc_gray_icon.png') {
      return 'cvc-gray';
    } else if (this.Banner['cvcIcon'] === 'images/cvc_orange_icon.png') {
      return 'cvc-orange';
    } else if ( this.Banner['cvcIcon'] === 'images/cvc_red_icon.png') {
      return 'cvc-red';
    }else if ( this.Banner['cvcIcon'] === 'images/cvc_green_icon.png') {
      return 'cvc-green';
    }
  }
  

  getIsolation(): string {
    if (this.Banner['isolationIcon'] === 'images/okIsolation_icon.png') {
      return 'isolation-generic';
    } else if (this.Banner['isolationIcon'] === 'images/noIsolation_icon.png') {
      return 'no-isolation';
    }
    return 'undefined_isolation';
  }
  getPrivacyColor(): string {
    if (this.Banner['privacy'] ==true) {
      return 'ok-privacy';
    } else if (this.Banner['privacy'] ==false)  {
      return 'no-privacy';
    }
    return '';
  }
  getManagementType(): string {
    if (this.Banner['managementType'] === 'images/amb.png') {
      return 'fa-user-md';
    } else if (this.Banner['managementType'] === 'images/ward.png') {
      return 'fa-bed';
    } else {
      return 'fa-user';
    }
  }
  getManagementColor(): string {
    if (this.Banner['managementColor'] === 'green') {
      return 'note';
    } else {
      return 'noNote';
    }
  }

  getManagementTooltip(): string {
    this.PatientNote = this.Banner['PatientNote'];
    if (this.PatientNote && this.PatientNote.text) {
      return this.PatientNote.text.string;
    } else {
      return '';
    }
  }

  privacyIcon(data) {

    if (data !== null && data !== undefined) {
      if (data ==true) {
        return 'fa-unlock fa-2x';
      } else if (data ==false) {
        return 'fa-lock fa-2x';
      } 
    }
    return '';
  }
  getManagementTypeDescription(): string {
    if (this.Banner['managementType'] === 'images/amb.png') {
      return 'Amb.'; //TODO: TRADURRE
    } else if (this.Banner['managementType'] === 'images/ward.png') {
      return this.translateService.instant('ward');
    } else {
      return this.translateService.instant('patient');
    }
  }

  startProcess(event: Event) {
    if (environment.banner.startProcess && this.isPortal) {
      this.processActions.startProcess(environment.banner.startProcess)
    }else {
      this.openDetails(event);
    }
  }
  editPatientNote(event: Event): void {

    // if(this.isPortal){
      event.stopPropagation();
      if(this.Banner['PatientNote']){
        this.viewManager.setPopupViewId('patient-note');
      }
    // }
  }
  openDetails(event: Event): void {
    event.stopPropagation();
    if (this.PatientEncounter && this.PatientEncounter.admitter)
      this.EmployeeAction.inject(this.PatientEncounter.admitter.internalId, null, null, 'BannerAdmitter')
        .then(() => this.injectReferrer());
    else {
      this.injectReferrer();
    }
  }

  injectReferrer(): void {
    if (this.PatientEncounter && this.PatientEncounter.referrer) {
      this.EmployeeAction.inject(this.PatientEncounter.referrer.internalId, null, null, 'BannerReferrer')
        .then(() => this.viewManager.setPopupViewId('banner-details'))
    } else {
      this.viewManager.setPopupViewId('banner-details');
    }
  }

  ejectPatient() {
    this.PatientAction.eject();
  }

  openPrivacy() {
    return this.httpService.fetch(this.apiUrl + this.restBaseUrl + 'banner/privacymanagement' +
      ';jsessionid=' + this.sid + '?conversationId=' + this.cid,
    {
      method: 'GET',
      headers: {'Content-Type': 'application/json'},
      credentials: 'include'
    })
    .then(response => response.text())
    .then((resp) => {

      if (resp) {
        let popUp = window.open(resp, 'Privacy Management', 'width=700,height=700,top=0, left=0,location=1,scrollbars=1,resizable=0');

        if (!popUp || popUp.closed || typeof popUp.closed === 'undefined') {
          alert('popup bloccato')
        } else {
          let timer = setInterval(() => {
            if (!popUp || popUp.closed || typeof popUp.closed === 'undefined') {
              clearInterval(timer);

              this.BannerActions.refresh();
            }
          }, 1000);
        }
      };
    });
  }

  openIeRepository() {
    return this.httpService.fetch(this.apiUrl + this.restBaseUrl + 'documents/ieRepositoryUrl' +
      ';jsessionid=' + this.sid + '?cid=' + this.cid,
    {
      method: 'GET',
      headers: {'Content-Type': 'application/json'},
      credentials: 'include'
    })
    .then(response => response.text())
    .then((resp) => {

      if (resp) {
        this.phi.openWindow(resp, null, null, 0.80, 0.80);
      } else {
        alert('Impossibile costruire la stringa di connessione');
      }
    });
  }

}
