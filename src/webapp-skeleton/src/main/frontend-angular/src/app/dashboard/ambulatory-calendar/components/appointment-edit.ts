import { Component, Injector, OnDestroy } from '@angular/core';
import { select } from '@angular-redux/store';
import { TranslateService } from '@ngx-translate/core';
import { DateFormatPipe } from '../../../services/converters/date-format.pipe';
import { BaseForm } from '../../../widgets/form/base-form';
import { AppointmentActionService, ProcedureDefinitionActionService } from '../../../services/actions';
import { Appointment } from '../../../services/entities/base-entity';
import { ViewManager } from '../../../services/view-manager';
import { PatientSearch } from './patient-search';
import { Patient } from '../../../services/entities/role';
import { ConversationActions } from '../../../actions/conversation.actions';
import { ProcedureDefinitonSearch } from './procedure-definiton-search';
import { Procedure, ProcedureDefinition } from '../../../services/entities/act';
import { CodeValue } from '../../../services/entities/data-types/code-value';
import { Datamodel } from '../../../services/datamodel/datamodel';
import { SelectItem } from '../../../services/datamodel/select-item';
import { AmbulatoryCalendar } from '../ambulatory-calendar';
import { WidgetEvent } from '../../../widgets/event/widget-event';
import { AppointmentGrouperActionService } from '../../../services/actions/appointment-grouper-action.service';
import { InternalActivityActionService } from '../../../services/actions/internal-activity-action.service';
import { environment } from '../../../../environments/environment';
import { ActivatedRoute } from '@angular/router';
import { AmbulatoryCalendarAction } from '../actions/ambulatory-calendar-action.service';
import { DataService } from 'app/services/data.service';
import { Subscription } from 'rxjs';

@Component({
selector: 'phi-appointment-edit',
templateUrl: './appointment-edit.html'
})
export class AppointmentEdit extends BaseForm implements OnDestroy {
  @select(['conversation', 'Appointment']) Appointment$;
  appointmentSub;
  Appointment: Appointment;

  @select(['conversation', 'internalactivity']) internalactivity$;
  internalactivitySub;

  appointmentId: number;

  eject = false;

  ticketDetailsDomain: string;

  indirectProcedures: Array<SelectItem>;

  procedureDefinitionList: Array<ProcedureDefinition>;
  procedureDefinitionColorList: Array<ProcedureDefinition>;
  procedureDefinitionId: number;
  // completed = false;

  internalActivityMandatory = false;
  internalactivities: Array<SelectItem> = [];
  internalactivity = null;

  // KLINIK
  repeat: boolean;
  frequency: string;
  endDate: Date;

  duration: Array<SelectItem> = [
    {label: '15 min', value: 15},
    {label: '30 min', value: 30},
    {label: '45 min', value: 45},
    {label: '1 h', value: 60},
    {label: '1 h 15 min', value: 75},
    {label: '1 h 30 min', value: 90},
    {label: '2 h', value: 120}
  ];

  frequencies: Array<SelectItem> = [
    {label: this.translateService.instant('everydays'), value: 'daily'},
    {label: this.translateService.instant('workdays'), value: 'workingDays'},
    {label: this.translateService.instant('everyweek'), value: 'weekly'},
    {label: this.translateService.instant('everymonth'), value: 'monthly'}
  ];
  // KLINIK end

  environment = environment;

  patientFromConversation = false;

  private ignoreDeadPatients: boolean = false;
  private subscriptions: Subscription;

  constructor(
    injector: Injector,
    private viewManager: ViewManager,
    private ambulatoryCalendarAction: AmbulatoryCalendarAction,
    private AppointmentAction: AppointmentActionService,
    private AppointmentGrouperAction: AppointmentGrouperActionService,
    private ProcedureDefinitionAction: ProcedureDefinitionActionService,
    private internalActivityAction: InternalActivityActionService,
    private conversationActions: ConversationActions,
    private translateService: TranslateService,
    private datePipe: DateFormatPipe,
    private route: ActivatedRoute,
    private dataService: DataService
  ) {
    super(injector);

    this.subscriptions = new Subscription();
    this.patientFromConversation =
      this.route.snapshot.params['patientFromConversation'] === 'true';

    this.appointmentSub = this.Appointment$.subscribe((res) => {
      this.Appointment = res;
      if (this.Appointment) {
        if (
          this.Appointment.procedureRequest &&
          this.Appointment.procedureRequest.length !== 0 &&
          this.Appointment.procedureRequest[0].exemption
        ) {
          this.loadTicketDetails(
            this.Appointment.procedureRequest[0].exemption
          );
        }
        if (
          this.Appointment.procedure &&
          this.Appointment.procedure.classCode &&
          this.Appointment.procedure.classCode.code !== 'CUP'
        ) {
          this.ProcedureDefinitionAction.cleanRestrictions();
          // 'INDIRECT' or 'GENERAL' or 'MACROPROC'
          this.ProcedureDefinitionAction.equal['classCode.code'] =
            this.Appointment.procedure.classCode.code;
          this.ProcedureDefinitionAction.select.push('text');
          this.ProcedureDefinitionAction.select.push('code.id');
          this.ProcedureDefinitionAction.select.push('code.displayName');
          this.ProcedureDefinitionAction.select.push('code.langIt');
          this.ProcedureDefinitionAction.select.push('code.langDe');

          if (this.Appointment.procedure.classCode.code === 'MACROPROC') {
            this.ProcedureDefinitionAction.equal['procedureSDL.internalId'] =
              this.Appointment.serviceDeliveryLocation.parent.internalId;
          }

          // not for klinik:
          if (environment.appointmentEdit.loadProcedureDefinitions) {
            this.ProcedureDefinitionAction.read().then((dm: Datamodel) => {
              this.procedureDefinitionList = dm.entities;
              if (dm && dm.entities) {
                this.indirectProcedures = dm.entities.map(
                  (pd: ProcedureDefinition) => {
                    if (pd.code && pd.code.langIt) {
                      return { label: pd.code.langIt, value: pd.internalId };
                    } else if (pd.text) {
                      return { label: pd.text, value: pd.internalId };
                    }
                  }
                );
              }
            });
          }
        }

        // IF SDL MNEMONIC IS VBFISIOC4 OR DOFISIOC4 use createdHandlerWithInternalActivity
        if (
          this.Appointment &&
          this.Appointment.serviceDeliveryLocation &&
          this.Appointment.serviceDeliveryLocation.parent &&
          ([
            'DOFISIOC4',
            'VBFISIOC4',
            'DODIETETICAC4',
            'VBDIETETICAC4',
            '209_DOCONSPSICOC4',
            '209_VBCONSPSICOC4',
          ].includes(this.Appointment.serviceDeliveryLocation.name.fam) ||
            (this.Appointment.serviceDeliveryLocation.nurseVisibility &&
              this.Appointment.serviceDeliveryLocation.parent.nurseVisibility))
          /*this.Appointment.serviceDeliveryLocation.parent.name.giv === 'Ambulatorio RRF per interni (C4) - Verbania' ||
            this.Appointment.serviceDeliveryLocation.parent.name.giv === 'Ambulatorio RRF per interni (C4) - Domodossola' ||*/
          //&& (this.Appointment.serviceDeliveryLocation.parent.name.giv === 'Consulenze Stomaterapiste'||
          //this.Appointment.serviceDeliveryLocation.parent.name.giv === 'Consulenze Diabetologia' ||
          //this.Appointment.serviceDeliveryLocation.parent.name.giv === 'Consulenze UPRI'||
          //this.Appointment.serviceDeliveryLocation.parent.name.giv === 'Consulenze Dialisi peritoneali'||
          //this.Appointment.serviceDeliveryLocation.parent.name.giv === 'Consulenze WOUND CARE')
        ) {
          this.internalActivityMandatory = true;
          this.loadInternalActivities();
        }
      }
    });

    this.internalactivitySub = this.internalactivity$.subscribe(
      (res) => (this.internalactivity = res)
    );
  }

  ngOnInit() {
    this.subscriptions.add(
      this.dataService.currentIgnoreDeadPatientsOption$.subscribe(
        (o) => (this.ignoreDeadPatients = o)
      )
    );
  }

  ngOnDestroy() {
    if (this.eject) {
      this.conversationActions.remove('Appointment');
      this.conversationActions.remove('internalactivity');
    }
    this.appointmentSub.unsubscribe();
    this.internalactivitySub.unsubscribe();
    this.subscriptions.unsubscribe();
  }

  getFormTitle() {
    if (
      this.Appointment &&
      this.Appointment.procedure &&
      this.Appointment.procedure.classCode
    ) {
      const code = this.Appointment.procedure.classCode.code;
      return this.translateService.instant(code);
    }
  }

  addPatient() {
    let sdlId = 'all';
    if (this.Appointment.procedure.classCode.code === 'MACROPROC') {
      sdlId =
        this.Appointment.serviceDeliveryLocation.parent.internalId.toString();
    }
    this.dataService.setIgnoreDeadPatientsOption(true);
    this.viewManager
      .setPopupViewId('patient-search', sdlId)
      .then((patSrc: PatientSearch) =>
        patSrc.select.subscribe((pat: Patient) => {
          const patientProxy: Patient = {
            entityName: 'com.phi.entities.role.Patient',
          };
          patientProxy.internalId = pat.internalId;
          patientProxy.name = pat.name;
          this.Appointment.patient = patientProxy;

          if (
            this.Appointment.procedure.classCode.code === 'MACROPROC' &&
            this.AppointmentGrouperAction.entity != null
          ) {
            this.AppointmentAction.linkAppointmentGrouper(
              this.AppointmentGrouperAction.entity
            );
          }
        })
      );
  }

  loadInternalActivities(): Promise<Array<SelectItem>> {
    if (this.Appointment.patient) {
      return this.internalActivityAction
        .getInternalActivities(
          this.Appointment.patient.internalId,
          this.Appointment.serviceDeliveryLocation.internalId
        )
        .then((iaLst: Datamodel) => {
          if (iaLst && iaLst.entities && iaLst.entities.length > 0) {
            this.internalactivities = iaLst.entities.map((ia) => ({
              label:
                ia.sdl +
                ' ' +
                this.datePipe.transform(ia.defaultdate, 'short') +
                ' ' +
                (ia.procedure ? ia.procedure : ''),
              value: ia.intactid,
            }));
            return this.internalactivities;
          }
        });
    }
  }

  internalActivityChanged() {
    if (this.internalactivity) {
      this.conversationActions.put('internalactivity', this.internalactivity);
    }
  }

  loadTicketDetails(ticket: CodeValue) {
    if (ticket) {
      if (ticket.code === 'Ticket4') {
        this.ticketDetailsDomain = 'PHIDIC:PATOLOGIA';
      } else if (ticket.code === 'Ticket7') {
        this.ticketDetailsDomain = 'PHIDIC:INVALIDITA';
      } else if (ticket.code === 'Ticket11') {
        this.ticketDetailsDomain = 'PHIDIC:PATOLOGIACONDIAGNOSI';
      } else if (ticket.code === 'Ticket12') {
        this.ticketDetailsDomain = 'PHIDIC:CONAUTOCERTIFICAZIONE';
      } else if (ticket.code === 'Ticket13') {
        this.ticketDetailsDomain = 'PHIDIC:MALATTIERARE';
      } else if (ticket.code === 'Ticket14') {
        this.ticketDetailsDomain = 'PHIDIC:STATOSOCIALE';
      } else {
        this.ticketDetailsDomain = null;
      }
    } else {
      this.ticketDetailsDomain = null;
    }
  }

  /**
   * !CUP single procedure
   */
  appProcedureChanged(ev: WidgetEvent) {
    const procDef: ProcedureDefinition = this.procedureDefinitionList.find(
      (pd: ProcedureDefinition) => (pd.internalId = ev.value)
    );
    if (procDef) {
      if (this.Appointment.procedure.classCode.code === 'MACROPROC') {
        this.Appointment.procedure.code = {
          entityName: 'com.phi.entities.dataTypes.CodeValuePhi',
          id: procDef.code.id,
        };
      }
      this.Appointment.procedure.text = procDef.text;
    } else {
      this.Appointment.procedure.code = null;
      this.Appointment.procedure.text = null;
    }
  }

  /**
   * CUP multi procedure
   */
  addProcedure() {
    this.viewManager
      .setPopupViewId(
        'procedure-definiton-search',
        this.Appointment.serviceDeliveryLocation.internalId.toString()
      )
      .then((procDefSrc: ProcedureDefinitonSearch) =>
        procDefSrc.select.subscribe((procDef: ProcedureDefinition) => {
          const procedure: Procedure = {};
          procedure.classCode = {
            code: 'CUP',
            codeSystemName: 'PHIDIC',
            domainName: 'ProcType',
          };
          procedure.codeIcd9 = procDef.codeIcd9;
          procedure.regionalCodeIcd9 = procDef.regionalCodeIcd9;
          procedure.quantity = 1;
          procedure.statusCode = {
            code: 'new',
            codeSystemName: 'PHIDIC',
            domainName: 'StatusCodes',
          };

          this.Appointment.procedureRequest[0].procedure.push(procedure);

          if (
            !this.Appointment.color ||
            this.Appointment.color === AppointmentActionService.defaultColor
          ) {
            if (procDef.color) {
              this.Appointment.color = procDef.color;
            } else {
              this.Appointment.color = AppointmentActionService.defaultColor;
            }
          }

          /*
        if (procDef.color) {
          if (this.Appointment.color === AppointmentActionService.defaultColor) {
            this.Appointment.color = procDef.color;
          } else {
            this.Appointment.color = AppointmentActionService.defaultColor;
          }
        }
        */

          let procLength = procDef.defaultLength;
          if (procLength && procLength.score) {
            if (
              !this.Appointment.procedure.levelCode ||
              !this.Appointment.procedure.levelCode.score ||
              procLength.score > this.Appointment.procedure.levelCode.score
            ) {
              this.Appointment.procedure.levelCode = {
                code: procLength.code,
                codeSystemName: 'PHIDIC',
                domainName: 'Length',
              };
            }
          }
        })
      );
  }

  /**
   * CUP multi procedure
   */
  removeProcedure(procedure: Procedure) {
    const pIdx =
      this.Appointment.procedureRequest[0].procedure.indexOf(procedure);
    this.Appointment.procedureRequest[0].procedure.splice(pIdx, 1);

    if (this.Appointment.procedureRequest[0].procedure.length > 0) {
      let firstProc = this.Appointment.procedureRequest[0].procedure[0];
      return this.readProcDefForDelete(
        firstProc.codeIcd9,
        firstProc.regionalCodeIcd9
      ).then(() => {
        if (
          this.procedureDefinitionColorList.length > 0 &&
          this.procedureDefinitionColorList[0].color
        ) {
          this.Appointment.color = this.procedureDefinitionColorList[0].color;
        } else {
          this.Appointment.color = AppointmentActionService.defaultColor;
        }
      });
    } else {
      this.Appointment.color = AppointmentActionService.defaultColor;
    }
  }

  readProcDefForDelete(codeIcd9, regionalCodeIcd9?) {
    const sdlId = this.Appointment.serviceDeliveryLocation.internalId;

    // let procs = this.Appointment.procedureRequest[0].procedure;
    if (codeIcd9) {
      /*
      let procsIds: Array<String> = [];
      for (let j = 0; j < procs.length; j++) {
        procsIds.push(procs[j].codeIcd9.id);
      }*/
      this.ProcedureDefinitionAction.cleanRestrictions();
      this.ProcedureDefinitionAction.readPageSize = 0;
      this.ProcedureDefinitionAction.equal['procedureSDL.internalId'] = sdlId;
      this.ProcedureDefinitionAction.equal['codeIcd9.id'] = codeIcd9.id;
      if (regionalCodeIcd9) {
        this.ProcedureDefinitionAction.equal['regionalCodeIcd9.id'] =
          regionalCodeIcd9.id;
      }
      this.ProcedureDefinitionAction.select.push('internalId');
      this.ProcedureDefinitionAction.select.push('text');
      this.ProcedureDefinitionAction.select.push('color');
      this.ProcedureDefinitionAction.select.push('codeIcd9.id');
      this.ProcedureDefinitionAction.select.push('regionalCodeIcd9.id');
      return this.ProcedureDefinitionAction.read().then((dm: Datamodel) => {
        this.procedureDefinitionColorList = dm.entities;
      });
    }
  }

  setStatus(ev) {
    if (ev.value) {
      this.Appointment.statusCode.code = 'completed';
    }
  }

  invalidPatient() {
    if (
      this.Appointment.procedure &&
      this.Appointment.procedure.classCode.code !== 'GENERAL'
    ) {
      return !this.Appointment.patient;
    } else {
      return false;
    }
  }

  invalidProcedure() {
    if (
      this.Appointment.procedure &&
      this.Appointment.procedure.classCode.code === 'CUP'
    ) {
      return (
        !this.Appointment.procedureRequest ||
        this.Appointment.procedureRequest.length === 0 ||
        !this.Appointment.procedureRequest[0].procedure ||
        this.Appointment.procedureRequest[0].procedure.length === 0
      );
    } else {
      // INDIRECT or GENERAL or COORDINATION
      return false;
    }
  }

  onSave() {
    if (!this.invalidPatient() && !this.invalidProcedure()) {
      if (!this.repeat) {

        this.AppointmentAction.create(this.Appointment.internalId ? environment.appointmentAction.update : environment.appointmentAction.create).then((app: Appointment) => {
          this.eject = true;

          if (this.AppointmentGrouperAction.entity) {
            this.AppointmentGrouperAction.create().then(() =>
              this.AppointmentGrouperAction.entity = null
            );
          }

          if (this.internalActivityMandatory) {
            this.internalActivityAction.copyInternalActivities(this.Appointment.internalId, this.internalactivity );
          }
          this.viewManager.setPopupViewId(null);
          this.ambulatoryCalendarAction.refreshAmbularyCalendar(true);
        });
      } else {
        // KLINIK repeatable general activity
        this.AppointmentAction.createRepeated(this.frequency, this.endDate).then(() => {
          this.viewManager.setPopupViewId(null);
          this.ambulatoryCalendarAction.refreshAmbularyCalendar(true);
        });
      }
    }
  }

  close() {
    this.eject = true;
    this.viewManager.setPopupViewId(null);
  }
}
