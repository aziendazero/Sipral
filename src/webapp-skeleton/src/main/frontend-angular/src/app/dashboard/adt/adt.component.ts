import { AuthGuard } from './../../services/auth-guard.service';
import { ElementRef } from '@angular/core';
import { PrimaVitalSignActionService } from './../../services/actions/prima-vital-sign-action.service';
import { ConfigActions } from '../../actions/config.action';
import { Component, Inject, OnInit, OnDestroy, HostBinding, Injector, ViewChild } from '@angular/core';
import { select } from '@angular-redux/store';
import { ConversationActions } from '../../actions/conversation.actions';
import { Config } from '../../store/config.reducer';
import { BaseActionService, PatientActionService, PatientEncounterActionService } from '../../services/actions';
import { AdtAction } from './actions/adt-action.service';
import { SelectItem } from '../../services/datamodel/select-item';
import { Patient } from '../../services/entities/role';
import { PatientEncounter } from '../../services/entities/act/patient-encounter';
import { CodeValue } from '../../services/entities/data-types/code-value';
import { DictionaryManager } from '../../services/dictionary-manager';
import { WidgetEvent } from '../../widgets/event/widget-event';
import { TranslateService } from '@ngx-translate/core';
import { BaseForm } from '../../widgets/form/base-form';
import { ViewManager } from '../../services/view-manager';
import { environment } from '../../../environments/environment';
import { Subscription } from 'rxjs/Subscription';
import { ProcessActions } from 'app/actions/process.actions';

@Component({
  templateUrl: './adt.html',
  styleUrls: ['./adt.component.scss']
})
export class AdtComponent extends BaseForm implements OnInit, OnDestroy {

  @select(['conversation']) conversation$;
  conversationSub: Subscription;

  @ViewChild('LayoutAdtResults')
  protected layoutAdtResults: ElementRef;

  searchVisible;
  peListVisible = 'dummyClassInvisibleSearch';
  detailsVisible;
  encounterTypes;
  PatientEncounterList;
  PatientEncounterAdditional;
  Patient: Patient;
  PatientEncounter: PatientEncounter;
  AdtInitialized: Boolean;
  TransferList;
  Transfer;
  EncounterHistoryList;
  Encounter;

  @select(['config']) config$;
  configSub: Subscription;
  config: Config;

  enabledSdlocs: Array<SelectItem>;
  environment = environment;

  @select(['process', 'cid']) cid$;
  cid: any;

  Param: Map<string, any>;

  @HostBinding('class') clazz = 'fullHeightWidth';

  bradenOrNorton = 'Norton'; // can be overwritten by environment.adt.bradenOrNorton
  showHistoricEncounters = 'true';
  EncounterType = 'IMP;DH;DSRG;DSRV;PRE;SAMB'.split(';');
  defaultSorting = 'SurnameName'; // parameterManager.getParameter('ADT','DefaultSorting');
  defaultOrder: 'ASC' | 'DESC' = 'ASC';
  currentSorting;
  currentOrder;
  reimbursement: Array<SelectItem>;

  constructor(injector: Injector,
    @Inject('apiUrl') private url,
    private processActions: ProcessActions,
    private conversationActions: ConversationActions,
    private translateService: TranslateService,
    public AdtAction: AdtAction,
    public authGuard: AuthGuard,
    private PatientAction: PatientActionService,
    private PatientEncounterAction: PatientEncounterActionService,
    private TransferAction: BaseActionService<any>,
    private configActions: ConfigActions,
    private dictionaryManager: DictionaryManager,
    private viewManager: ViewManager,
    private primaVitalSignAction: PrimaVitalSignActionService
  ) {

    super(injector);

    this.conversationActions.put('peListVisible', this.peListVisible);

    this.conversationSub = this.conversation$.subscribe(res => {
      this.searchVisible = res.searchVisible;
      this.peListVisible = res.peListVisible;
      this.detailsVisible = res.detailsVisible;
      this.encounterTypes = res.encounterTypes;
      this.reimbursement = res.reimbursement;
      this.PatientEncounterList = res.PatientEncounterList;
      this.PatientEncounterAdditional = res.PatientEncounterAdditional;
      this.Patient = res.Patient;
      this.PatientEncounter = res.PatientEncounter;
      this.AdtInitialized = res.AdtInitialized;
      this.TransferList = res.TransferList;
      this.Transfer = res.Transfer;
      this.EncounterHistoryList = res.EncounterHistoryList;
      this.Encounter = res.Encounter;
    });

    this.configSub = this.config$.subscribe((cfg: Config) => {
      this.config = cfg;
      this.Param = cfg.param;
      if (cfg.sdLocs) {
        this.enabledSdlocs = cfg.sdLocs
          .filter(sdl =>
            !this.environment.adt.onlyWardChildren
            && (sdl.type === 'WARD' || sdl.hybrid)
            || (this.environment.adt.onlyWardChildren && sdl.parent && sdl.parent.type === 'WARD')
          ).map((sdl) => ({
            label: sdl.text,
            value: +sdl.id,
          }));
      }
    });

    this.cid$.subscribe(res => this.cid = res);

    this.TransferAction.entityName = 'Transfer';

    if (environment.adt.bradenOrNorton) {
      this.bradenOrNorton = environment.adt.bradenOrNorton;
    }

  }

  ngOnInit() {
    // don't load adt for administrator
    const refresh =
      environment.adt.refreshOnInit
      || !this.Param['p.dashboard.adt.donotrefresh']
      || !this.Param['p.dashboard.adt.donotrefresh'].value;
    if (!this.AdtInitialized) { // avoid filter reset when returning to home
      this.resetFilters(refresh);
      this.conversationActions.put('AdtInitialized', true);
    } else {
      if (refresh) {
        this.refresh();
      }
    }

    this.configActions.put('isPortal', true);
  }

  ngOnDestroy() {
    this.conversationSub.unsubscribe();
    this.configSub.unsubscribe();
    this.conversationActions.remove('PatientEncounterList');
    this.configActions.remove('isPortal');
  }

  async resetFilters(refresh = true) {

    this.AdtAction.cleanRestrictions();

    // Add filter for selected SDLoc
    let wardsIds: Array<number> = [];
    if (this.enabledSdlocs) {
      wardsIds = this.enabledSdlocs.map((sdl: SelectItem) => (
        +sdl.value
      ));
    }
    if (wardsIds.length > 0) {
      this.AdtAction.equal['wards'] = wardsIds;
    } else {
      this.AdtAction.equal['wards'] = [];
    }

    if (environment.adt.status) {
      const statusCodes = await this.dictionaryManager.get(environment.adt.status);

      if (environment.adt.selectedStatuses) {
        this.AdtAction.equal['statusCode.id'] = statusCodes.filter((cv: CodeValue) =>
          environment.adt.selectedStatuses.indexOf(cv.code) !== -1
        );
      }
    } else {
      this.AdtAction.equal['statusCodes'] = environment.adt.selectedStatuses;
    }

    if (environment.adt.showAdtWard) {
      // Vco
      // ALL ROLES (except AMMINISTRATORE = 1 and MEDICO = 11 and SEGRETARIO = 28): only temporary
      if (this.config.employeeRoleCode !== '1' && this.config.employeeRoleCode !== '11' && this.config.employeeRoleCode !== '28') {
        this.AdtAction.equal['assigned'] = false;
      } else {
        this.AdtAction.equal['assigned'] = true;
      }
      // MEDICO = 11 and SEGRETARIO = 28: only assigned
      if (this.config.employeeRoleCode === '11' || this.config.employeeRoleCode === '28') {
        this.AdtAction.equal['temporary'] = false;
      } else {
        this.AdtAction.equal['temporary'] = true;
      }
      // ANESTESISTA DI SALA AND ANESTESISTA DI SALA DIRETTORE only surgicalVisFlag
      if (this.config.employeeRoleCode === '50' || this.config.employeeRoleCode === '51' || this.config.employeeRoleCode === '24') {
        this.AdtAction.equal['surgicalVisFlag'] = true;
      } else {
        this.AdtAction.equal['surgicalVisFlag'] = false;
      }

      const encounterTypes = [];
      this.AdtAction.equal['encounterTypes'] = [];

      for (let i = 0; i < this.EncounterType.length; i++) {
        let encounterType: SelectItem = { label: '', value: '' };
        if (this.EncounterType[i] === 'IMP') {
          encounterType.label = 'ORD';
        } else if (this.EncounterType[i] === 'SAMB') {
          encounterType.label = 'AMB*';
        } else {
          encounterType.label = this.EncounterType[i];
        }

        encounterType.value = this.EncounterType[i];
        if (this.EncounterType[i] !== 'PRE') {
          this.AdtAction.equal['encounterTypes'].push(encounterType.value);
        }
        encounterTypes.push(encounterType);
      }
      this.conversationActions.put('encounterTypes', encounterTypes);

      if (this.defaultSorting !== null && this.defaultSorting !== '') {
        this.AdtAction.equal['sorting'] = this.defaultSorting + this.defaultOrder;
        this.currentSorting = this.defaultSorting;
        this.currentOrder = this.defaultOrder;
      }

    } else {
      // Klinik

      this.reimbursement = [
        { label: this.translateService.instant('reimbursement'), value: 1 },
        { label: this.translateService.instant('without-reimbursement'), value: 2 },
        { label: this.translateService.instant('all'), value: 3 }
      ];
      this.conversationActions.put('reimbursement', this.reimbursement);

      const today = new Date();
      today.setHours(0, 0, 0, 0);
      this.AdtAction.equal['startDate'] = today;

      const tomorrow = new Date(today);
      tomorrow.setDate(tomorrow.getDate() + 1);
      this.AdtAction.equal['endDate'] = tomorrow;
    }
    await this.refresh(refresh);
  }

  encounterTypesChange(event: WidgetEvent) {
    if (event.element === 'PRE' && event.selected) {
      this.AdtAction.equal['encounterTypes'] = ['PRE'];
    } else if (event.element === 'PRE' && !event.selected) {
      this.AdtAction.equal['encounterTypes'] = ['IMP', 'DH', 'DSRG', 'DSRV', 'SAMB'];
    } else {
      if (this.AdtAction.equal['encounterTypes'].indexOf('PRE') !== -1) { // .includes('PRE')) {
        let tmp = Object.assign(this.AdtAction.equal['encounterTypes']).slice(); // clone array
        let index = tmp.indexOf('PRE');
        tmp.splice(index, 1);
        this.AdtAction.equal['encounterTypes'] = tmp;
      }
    }
  }

  refresh(refresh = true) {
    if (this.layoutAdtResults) {
      this.layoutAdtResults.nativeElement.scrollTop = 0;
    }
    if (environment.adt.showVitalSignAlert) {
      if (!this.AdtAction.equal['wards'] || this.AdtAction.equal['wards'].length === 0
        || this.AdtAction.equal['encounterTypes'].length === 0 || !(this.AdtAction.equal['assigned']
          || this.AdtAction.equal['temporary'])) {
        //this.viewManager.openAlertMessage(this.translateService.instant('Warning'),
        //  this.translateService.instant('ADT_Filter_Warning'), false);
      } else {
        // return this.authGuard.checkRelogin(this.isAdmin())
        //   .then(resp => {
        //     if (resp.reloginUrl) {
        //       this.authGuard.needRelogin(resp);
        //     } else {
              return this.primaVitalSignAction.countAlertedPrimaVitalSign()
                .then(hasElements => {
                  if (hasElements) {
                    if (this.config.menuVisible) {
                      this.configActions.toggleMenu();
                    }
                    this.processActions.startProcess('MOD_Medical_Obs/customer_VCO/PROCESSES/PRIMA_alert_list');
                  } else if (refresh) {
                    return this.AdtAction.refresh();
                //   }
                // });
            }
          });
      }
    } else {
      return this.AdtAction.refresh();
    }
  }

  clickApplyFilter(event) {
    let inputText = event.target;
    let inputId = event.currentTarget.id;
    if (inputId === 'TextBoxPatientSurname') {
      this.AdtAction.equal['patientSurname'] = inputText.value;
    } else if (inputId === 'TextBoxPatientName') {
      this.AdtAction.equal['patientName'] = inputText.value;
    }
    return this.refresh();
  }

  ie(entity, conversationName): Promise<any> {
    return this.injectEject(entity, conversationName);
  }

  injectEject(entity, conversationName) {

    if (conversationName === 'PatientEncounter') {

      if (this.PatientEncounter === undefined || this.PatientEncounter.internalId !== entity.internalid) {

        let additionals = null;
        if (environment.adt.additionals) {
          additionals = environment.adt.additionals;
        }

        return this.PatientEncounterAction.inject(entity.internalid, additionals, ['code.code', 'therapy', 'patient.language'])
          .then((patientEncounter) => {
            this.conversationActions.put('PatientEncounter', patientEncounter);
            if (this.detailsVisible) {
              this.PatientAction.getEncounterHistory(this.PatientEncounter.patient.internalId, this.showHistoricEncounters)
                .then((responseHistory => {
                  if (responseHistory !== null && environment.adt.transfers) {
                    this.getTransfers(entity.internalid);
                  }
                }));
            }
            return patientEncounter;
          });

      } else {
        if (environment.adt.menu) {
          this.viewManager.openMenu('adt.menu', entity.internalid, entity.statuscode, entity.appointment.internalid, entity.invoicingclosed === 1 || this.isAdmin(), entity.sdoclosed === 1);
        }
        return Promise.resolve(false);
      }
    } else {
      this.conversationActions.put('Encounter', { internalId: entity.internalid });
      this.getTransfers(entity.internalid);
    }
  }

  private injectAndExecute(entity: any, /*event,*/ callback) {
    if (this.PatientEncounter === undefined || this.PatientEncounter.internalId !== entity.internalid) {
      this.ie(entity, 'PatientEncounter').then(() => {
        callback();
      });
    } else {
      callback();
    }
  }

  printWorkList() {
    this.AdtAction.cleanRestrictions();
    this.AdtAction.equal['worklist'] = true;
    this.AdtAction.printReport(environment.adt.report);
    this.resetFilters();
  }

  startProcess(entity: any, process: String, event) {
    event.stopPropagation();
    const callback = () => {
      this.processActions.startProcess(process);
    };
    this.injectAndExecute(entity, /*event,*/ callback);
    if (this.config.menuVisible) {
      this.configActions.toggleMenu();
    }
  }

  startDashboard(entity: any, dashboard: String, event) {
    event.stopPropagation();
    const callback = () => {
      this.processActions.startProcess(dashboard);
    };
    this.injectAndExecute(entity, callback);
  }

  editNote(entity, event) {
    const callback = () => {
      this.viewManager.setPopupViewId('adt-note');
    };
    this.injectAndExecute(entity,  /*event,*/ callback);
  }


  viewAlarmDetails(entity, event, tipo) {
    const callback = () => {
      this.viewManager.setPopupViewId('adt-alarm-details')
        .then(() => {
          let result = '';
          if (this.PatientEncounter.code.code !== 'PRE') {
            if (this.PatientEncounter.visitNumber && this.PatientEncounter.visitNumber.extension) {
              result = this.PatientEncounter.visitNumber.extension;
            }
          } else if (this.PatientEncounter.preadmitNumber && this.PatientEncounter.preadmitNumber.extension) {
            result = this.PatientEncounter.preadmitNumber.extension;
          }

          if (result && result.length > 0) {
            return this.AdtAction.getAlarmDetails(result, tipo).then(dm => {
              this.conversationActions.put('TherapyAlarmList', dm);
            })
          } else {
            this.conversationActions.remove('TherapyAlarmList');
            return Promise.resolve(null);
          }

        });
    };
    this.injectAndExecute(entity, callback);
  }

  injectAndPrintReport(entity, event) {
    const callback = () => {
      const url = this.url + '/servlet/graficaReport.jr?solutionName=PHI_CI' +
        '&reportName=report2&patientId=' + this.Patient.internalId +
        '&encounterId=' + this.PatientEncounter.internalId +
        '&sdlId=' + this.PatientEncounter.assignedSDL.internalId +  // FIXME USE teporary or Assigned sdl
        '&cid=' + this.cid;

      this.viewManager.openIframe(url);
    };
    this.injectAndExecute(entity,  /*event,*/ callback);
  }

  printReport() {
    const url = this.url + '/swf/modules/ADT/reports/r_laserBand.seam?cid=' + this.cid;
    this.viewManager.openIframe(url);
  }

  toggleSearch() {
    this.conversationActions.put('searchVisible', !this.searchVisible);
    let peListDummy = 'dummyClass';
    if (this.searchVisible) {
      peListDummy += 'VisibleSearch'
    } else {
      peListDummy += 'InvisibleSearch'
    }
    this.conversationActions.put('peListVisible', peListDummy);
    //  this.AdtAction.refresh();
  }

  async toggleDetails() {
    if (this.PatientEncounter) {
      await this.PatientAction.getEncounterHistory(this.PatientEncounter.patient.internalId, this.showHistoricEncounters);
      if (environment.adt.transfers) {
        await this.getTransfers(this.PatientEncounter.internalId);
      }
    }
    this.conversationActions.put('detailsVisible', !this.detailsVisible);
  }

  public getTransfers(encounterId: number): void {
    // Clean old transfers
    this.conversationActions.remove('TransferList');

    this.TransferAction.cleanRestrictions();
    this.TransferAction.select.push('internalId');
    this.TransferAction.select.push('effectiveDate');
    this.TransferAction.select.push('SDLocFrom.name.giv');
    this.TransferAction.select.push('SDLocTo.name.giv');

    this.TransferAction.equal['patientEncounter.internalId'] = encounterId;
    this.TransferAction.equal['isActive'] = true;
    this.TransferAction.orderBy['effectiveDate'] = BaseActionService.ASC;
    this.TransferAction.read();
  }

  // Open barcode research panel
  searchPatientByBarcode(): void {
    if (this.PatientEncounter !== null) {
      this.viewManager.setPopupViewId('adt-barcode');
    }
  }

  getStatus(data) {
    let status = '';
    let encounterCode: string = data.code;
    if (data.code) {
      if (data.code === 'IMP') { // ORD
        encounterCode = 'ORD';
      }
      status = this.translateService.instant(encounterCode) + ' ' + this.translateService.instant(encounterCode + '_' + data.statuscode);

      if (encounterCode === 'PRE') {
        status = status + '\n' + this.translateService.instant('Priority') + ': ' + data.prioritycode +
          '\n' + this.translateService.instant('Intervention') + ': ' + data.intervention;
      }
    }
    return status;
  }

  encounterCodeFormatter(data) {
    let result: String = '';

    if (data.code !== null && data.code !== '') {
      if (data.code === 'IMP') {
        result = 'ORD';
      } else if (data.code === 'SAMB') {
        result = 'AMB*';
      } else {
        result = data.code;
      }
    }
    return result;
  }

  // Function to format encounter infos
  encounterInfoFormatter(data) {
    let result = '';
    if (data.code !== 'PRE') {
      if (data.visitnumber) {
        data.visitnumber = data.visitnumber.substring(data.visitnumber.lastIndexOf('_') + 1);
        result = data.visitnumber;
      } else if (data.nosografico) {
        result = data.nosografico; // Klinik
      }
    } else if (data.preadmitnumber) {
      result = data.preadmitnumber;
    }

    return result;
  }

  isAdmin() {
    return this.config.employeeRoleCode === '1';
  }

  isNurseOrCoordinator() {
    return this.isAdmin() || this.config.employeeRoleCode === '10' || this.config.employeeRoleCode === '4';
  }

  isDoctorOrDirector() {
    return this.isAdmin() || this.config.employeeRoleCode === '11' || this.config.employeeRoleCode === '6';
  }

  isObstetrician() {
    return this.isAdmin() || this.config.employeeRoleCode === '15';
  }

  isBrassScore(data) {
    return data.brassscore !== undefined && data.brassscore !== null;
  }

  isMedicalChosen(){
    return this.isAdmin() || this.config.employeeRoleCode === '11' || this.config.employeeRoleCode === '10' || this.config.employeeRoleCode === '30' || this.config.employeeRoleCode === '21' || this.config.employeeRoleCode === '4' || this.config.employeeRoleCode === '6' || this.config.employeeRoleCode === '12';
  }

  // Function to set the background color of the discharge column when usign Brass scale
  getNoBrassColor(data) {
    if (!this.isBrassScore(data) && this.isInTargetPopulation(data.patient.birthtime)) {
      return 'yellowBackGround'; // 0xFFFF00;
    } else {
      return ''; // 0xFFFFFF;
    }
  }

  // Function to switch Brass icon
  getBrassIcon(data) {
    if (data && this.isBrassScore(data)) {

      if ('VALIDATED' === data.nrcode) {
        return 'orange';
      }
      if ('TOREVIEW' === data.nrcode) {
        return 'red';
      }
      if ('OK' === data.nrcode) {
        return 'green';
      }
      if (data.brassscore > 10 && (!data.nrcode || 'TEMPORARY' === data.nrcode || 'CONFIRMED' === data.nrcode)) {
        return 'yellow orangeStroke';
      }
      if (data.brassscore <= 10 && (!data.nrcode || 'TEMPORARY' === data.nrcode || 'CONFIRMED' === data.nrcode)) {
        return 'white';
      }


    } else {
      return '';
    }
  }


  isNurseReportConfirmed(data) {
    return 'CONFIRMED' === data.nrcode;
  }

  isPainValue(data) {
    return data.painvalue !== undefined && data.painvalue !== null;
  }

  isMorseScore(data) {
    return data.morsescore !== undefined && data.morsescore !== null;
  }

  // Function to check if the patient is in target population for Brass scale
  isInTargetPopulation(dateOfBirth: Date): Boolean {
    return this.getYearsOld(dateOfBirth) > 65;
  }

  // Function to get the age of a patient
  getYearsOld(dateOfBirth: Date) {
    let now: Date = new Date();
    let age = Number(now.getFullYear()) - Number(dateOfBirth.getFullYear());
    if (dateOfBirth.getMonth() > now.getMonth() || (dateOfBirth.getMonth() === now.getMonth() && dateOfBirth.getDate() > now.getDate())) {
      age--;
    }
    return age;
  }

  checkingStatusSurgical(data){
    if (data.surgicalvisflag) {
      return 'green';
    } else {
      return 'grey';
    }
  }

  checkingStatusIcon(data) {
    let currentTime = new Date();

    if (data.availabilitytime === null) {
      return null;
    }

    // 24H after encounter create
    let after24H = new Date(data.availabilitytime.getFullYear(), data.availabilitytime.getMonth(), data.availabilitytime.getDate(),
      data.availabilitytime.getHours() + 24, data.availabilitytime.getMinutes());

    // 48H after encounter create
    let after48H = new Date(data.availabilitytime.getFullYear(), data.availabilitytime.getMonth(), data.availabilitytime.getDate(),
      data.availabilitytime.getHours() + 48, data.availabilitytime.getMinutes());

    // No Checking done yet
    if (data.checkingavailabilitytime == null) {
      if (currentTime <= after24H) {
        return null;
      }

      if (currentTime > after24H && currentTime <= after48H) {
        return 'yellow orangeStroke';
      }

      if (currentTime > after48H) {
        return 'red';
      }
    } else {
      // Checking done
      if (data.checkingavailabilitytime <= after24H) {
        return 'green';
      }

      if (data.checkingavailabilitytime > after24H) {
        return 'black';
      }
    }

    return null;
  }

  therapyAllarmiIcon(data) {

    if (data !== null && data !== undefined) {
      if (data === 0) {
        return 'grey';
      } else if (data === 1) {
        return 'green';
      } else if (data === 5) {
        return 'yellow orangeStroke';
      } else if (data === 10) {
        return 'red';
      }
    }
    return '';
  }

  // Function to check visibility of Braden/Norton cell
  bradenOrNortonVisibility(data) {
    if (this.bradenOrNorton === 'Norton') {
      if (data.nortonscore !== null && data.nortonscore !== -1) {
        return true;
      } else {
        return false;
      }
    } else if (this.bradenOrNorton === 'Braden') {
      if (data.bradenscore !== null && data.bradenscore !== -1) {
        return true;
      } else {
        return false;
      }
    }
  }

  getMorseIcon(data) {

    if (data.morsescore !== null && data.morsescore !== -1) {
      if (data.morsescore >= 0 && data.morsescore <= 24) {
        return 'green';
      }
      if (data.morsescore >= 25 && data.morsescore <= 50) {
        return 'yellow orangeStroke';
      }
      if (data.morsescore >= 51) {
        return 'red';
      }
    }
  }
  // Function to switch Braden/Norton value icon
  bradenOrNortonIcon(data) {
    if (this.bradenOrNorton === 'Norton') {
      if (data.nortonscore !== null && data.nortonscore !== -1) {
        if (data.nortonscore >= 4 && data.nortonscore <= 11) {
          return 'red';
        }
        if (data.nortonscore >= 12 && data.nortonscore <= 14) {
          return 'yellow orangeStroke';
        }
        if (data.nortonscore > 14) {
          return 'green';
        }
      }
    } else if (this.bradenOrNorton === 'Braden') {
      if (data.bradenscore >= 11) {
        return 'green';
      } else {
        return 'red';
      }
    }

    return null;
  }

  // Function to switch Braden/Norton value info
  bradenOrNortonInfo(data) {
    let result: String = '';
    if (this.bradenOrNorton === 'Norton') {
      if (data.nortonscore !== null && data.nortonscore !== -1) {
        if (data.nortonscore >= 4 && data.nortonscore <= 11) {
          result = 'Rischio elevato';
        }
        if (data.nortonscore >= 12 && data.nortonscore <= 14) {
          result = 'Rischio lieve';
        }
        if (data.nortonscore > 14) {
          result = 'Rischio nullo';
        }
      }
    } else if (this.bradenOrNorton === 'Braden') {
      if (data.bradenscore != null) {
        result = data.bradenscore;
      }
    }
    return result;
  }

  dischargeLetterClass(adt) {
    if (adt && adt.lettercount > 0) {
      return 'fa fa-envelope green';
    } else {
      return 'fa fa-envelope red';
    }
  }

  hospiceDataClass(adt) {
    if (adt && adt.hospicecount > 0) {
      return 'fa fa-file-text-o green';
    } else {
      return 'fa fa-file-text-o red';
    }
  }

  medHistoryClass(adt) {
    if (adt && adt.mhfamcount + adt.mhremcount + adt.mhproxcount > 0) {
      return 'fa fa-stethoscope green';
    } else {
      return 'fa fa-stethoscope red';
    }
  }

  medHistoryTooltip(adt) {
    if (adt && adt.mhfamcount + adt.mhremcount + adt.mhproxcount > 0) {
      return 'adtMhCompiled';
    } else {
      return 'adtMhNotCompiled';
    }
  }

  hasVisitNumber() {
    if (this.PatientEncounter && this.PatientEncounter.visitNumber && this.PatientEncounter.visitNumber.extension) {
      return true;
    } else {
      return false;
    }
  }

}
