import { environment } from '../../../../environments/environment';
import { CalendarItem } from './../model/calendar-item';
import { AgendaAnnotation } from './../../../services/entities/act/agenda-annotation';
import { ServiceDeliveryLocation } from './../../../services/entities/role/service-delivery-location';
import { Injectable, Inject, Output, EventEmitter } from '@angular/core';
import { Router } from '@angular/router';
import { NgRedux, select } from '@angular-redux/store';
import { DashboardBaseAction } from '../../dashboard-base-action.service';
import { ConversationActions } from '../../../actions/conversation.actions';
import { HttpService } from '../../../services/http.service';
import { AppointmentActionService } from '../../../services/actions/appointment-action.service';
import { AgendaAnnotationActionService } from '../../../services/actions/agenda-annotation-action.service';
import { ServiceDeliveryLocationActionService } from '../../../services/actions/service-delivery-location-action.service';
import { Datamodel } from '../../../services/datamodel/datamodel';

import { Appointment } from '../../../services/entities/base-entity';

import { DateFormatPipe } from '../../../services/converters/date-format.pipe';
import { logError } from '../../../services/error/global-error-handler';
import { TranslateService } from '@ngx-translate/core';
import { ProcessActions } from '../../../actions/process.actions';
import { encodeValue } from '../../../services/actions/base-action.service';
import { dateToString } from '../../../services/converters/date.converter';
import { SelectItem } from '../../../services/datamodel/select-item';
import { EmployeeActionService } from '../../../services/actions/employee-action.service';
import { TimeBandActionService } from 'app/services/actions/time-band-action.service';
import { TimeBand, timeBandDays } from 'app/services/entities/base-entity/time-band';
import { CalendarRow } from '../model/calendar-row';
import { DictionaryManager } from 'app/services';

let Holidays = require('date-holidays');

@Injectable()
export class AmbulatoryCalendarAction extends DashboardBaseAction {
  // KLINIK NEW APPOINTMENT
  static START_NEW_APPOINTMENT = '/startNewAppointment';

  // ['AMMINISTRATIVO', 'DIETISTA', 'DIRETTORE', 'FISIOTERAPISTA / LOGOPEDISTA', 'MEDICO', 'TECNICO', PROGRAMMATORE', 'SEGRETARIO', 'INFERMIERE SPECIALIZZATO'];
  public static superUserRoleCodes = [
    '1',
    '5',
    '6',
    '8',
    '9',
    '11',
    '16',
    '28',
    '30',
  ];

  // ['COORDINATORE', 'EDUCATORE',, 'SEGRETARIO DI REPARTO [SOLA LETTURA]', 'STUDENTE'];
  public static reportingDisabledRoleCodes = [
    '4',
    '7',
    '10',
    '12' /*, 28 */,
    '31',
  ];

  //['MEDICO','DIRETTORE']
  public static proDireMedCodes = ['11', '6'];

  // ['COORDINATORE', 'INFERMIERE PROFESSIONISTA']
  public static proNurseCodes = ['4', '10'];

  // ['OSS']
  public static ossCodes = ['12'];

  // ['INFERMIERE PROFESSIONISTA','OSS']
  public static ossNurseCodes = ['10', '12'];

  // ['INFERMIERE SPECIALIZZATO']
  public static specNurseCodes = ['1', '30'];

  // ['OSTETRICA']
  public static obstetrician = ['15'];

  // ['SEGRETARIO DI REPARTO [SOLA LETTURA]']
  public static readOnly = ['28'];

  // ['PSICOLOGO']
  public static psychologist = ['17'];

  // ['RADIOLOGO']
  public static radiologist = ['radio'];

  // ['DIETISTA']
  public static dietologist = ['5'];

  // ['ASSISTENTE SOCIALE']
  public static socialWorker = ["3"];

  public agendaColumns = environment.ambulatoryCalendar.columns;
  public static startHour = environment.ambulatoryCalendar.startHour;
  public static endHour = environment.ambulatoryCalendar.endHour;
  public static startWorkHour = environment.ambulatoryCalendar.startWorkHour;
  public static endWorkHour = environment.ambulatoryCalendar.endWorkHour;
  public static minutesPerRow = environment.ambulatoryCalendar.minutesPerRow;

  public manageAppointmentIfInternal = false;

  @select(['conversation', 'selectedDate']) selectedDate$;
  selectedDate: Date;
  private dateFrom: Date;
  private dateTo: Date;
  public selectedDateHolidayName: string;

  public showCancelled = false;

  @select(['conversation', 'agendas']) agendas$;
  agendas: Array<ServiceDeliveryLocation>;
  @select(['conversation', 'agendaFrom']) agendaFrom$;
  agendaFrom: number;
  @select(['conversation', 'agendaTo']) agendaTo$;
  agendaTo: number;
  @select(['conversation', 'agendaTot']) agendaTot$;
  agendaTot: number;

  @select(['conversation', 'weekViewOfSdl']) weekViewOfSdl$;
  weekViewOfSdl: Array<any>;

  @select(['conversation', 'physiotherapists']) physiotherapists$;
  physiotherapists: Array<SelectItem>;

  @select(['config', 'employeeRoleCode']) employeeRoleCode$;
  employeeRoleCode: string;

  @select(['config', 'param']) param$;
  param: any;

  private appointments: Array<Appointment>;
  private appointmentBySdl: any;
  private agendaAnnotations: Array<AgendaAnnotation> = [];

  private calendarItems: Array<CalendarItem>;

  public list: Datamodel;

  public physiotherapist: SelectItem;

  @Output() selectedSdl: EventEmitter<any> = new EventEmitter();

  constructor(
    @Inject('apiUrl') protected apiUrl,
    protected conversationActions: ConversationActions,
    protected dictionaryManager: DictionaryManager,
    protected router: Router,
    protected httpService: HttpService,
    private appointmentAction: AppointmentActionService,
    private agendaAnnotationAction: AgendaAnnotationActionService,
    private serviceDeliveryLocationAction: ServiceDeliveryLocationActionService,
    private timeBandAction: TimeBandActionService,
    private employeeAction: EmployeeActionService,
    private datePipe: DateFormatPipe,
    private translateService: TranslateService,
    private processActions: ProcessActions,
    private redux: NgRedux<any>
  ) {
    super(apiUrl, conversationActions, dictionaryManager, router, httpService);

    this.dashboardName = 'AmbulatoryCalendar';
    this.dashboardUrl = 'ambulatorycalendar';

    this.employeeRoleCode$.subscribe((res) => (this.employeeRoleCode = res));
    this.param$.subscribe((param) => {
      this.param = param;
      if (
        param &&
        param['p.dashboard.ambulatorycalendar.manageAppointmentIfInternal']
      ) {
        this.manageAppointmentIfInternal =
          param[
            'p.dashboard.ambulatorycalendar.manageAppointmentIfInternal'
          ].value;
      } else {
        this.manageAppointmentIfInternal = false;
      }
    });

    this.selectedDate$.subscribe((res) => (this.selectedDate = res));

    this.agendas$.subscribe((res) => (this.agendas = res));
    this.agendaFrom$.subscribe((res) => (this.agendaFrom = res));
    this.agendaTo$.subscribe((res) => (this.agendaTo = res));
    this.agendaTot$.subscribe((res) => (this.agendaTot = res));
    this.weekViewOfSdl$.subscribe((res) => (this.weekViewOfSdl = res));
    this.physiotherapists$.subscribe((res) => (this.physiotherapists = res));
  }

  public isSuperUser = () =>
    AmbulatoryCalendarAction.superUserRoleCodes.indexOf(
      this.employeeRoleCode
    ) !== -1;

  public isProNurse = () =>
    AmbulatoryCalendarAction.proNurseCodes.indexOf(this.employeeRoleCode) !==
    -1;

  public isOss = () =>
    AmbulatoryCalendarAction.ossCodes.indexOf(this.employeeRoleCode) !== -1;

  public isSpecNurse = () =>
    AmbulatoryCalendarAction.specNurseCodes.indexOf(this.employeeRoleCode) !==
    -1;

  public isObstetrician = () =>
    AmbulatoryCalendarAction.obstetrician.indexOf(this.employeeRoleCode) !== -1;

  public isPsychologist = () =>
    AmbulatoryCalendarAction.psychologist.indexOf(this.employeeRoleCode) !== -1;

  public isReadOnly = () =>
    AmbulatoryCalendarAction.readOnly.indexOf(this.employeeRoleCode) !== -1;

  public reportingEnabled = () =>
    AmbulatoryCalendarAction.reportingDisabledRoleCodes.indexOf(
      this.employeeRoleCode
    ) === -1;

  public isOssNurse = () =>
    AmbulatoryCalendarAction.ossNurseCodes.indexOf(this.employeeRoleCode) !==
    -1;

  public isRadiologist = () =>
    AmbulatoryCalendarAction.radiologist.indexOf(this.employeeRoleCode) !== -1;

  public isDietologist = () =>
    AmbulatoryCalendarAction.dietologist.indexOf(this.employeeRoleCode) !== -1;

  public isSocialWorker = () =>
    AmbulatoryCalendarAction.socialWorker.indexOf(this.employeeRoleCode) !== -1;

  public isDirecMedic = () =>
    AmbulatoryCalendarAction.proDireMedCodes.indexOf(this.employeeRoleCode) !== -1;

  /**
   * Check if Appointments overlaps date
   * if overlaps return offset else return null
   * @returns {number} offset in minutes
   */
  private static getOffset(a: CalendarItem, date: Date): number {
    const aEnd: Date = new Date(a.date);
    aEnd.setMinutes(aEnd.getMinutes() + a.duration);

    if (a.date.getTime() === date.getTime()) {
      // start date equal?
      return a['offset'] || 0;
    } else if (aEnd.getTime() > date.getTime()) {
      // previous end date > current start?
      return (a['offset'] || 0) + (date.getTime() - a.date.getTime()) / 60000;
    }
    return null;
  }

  /**
   * Authorized to create appointment in AGENDA
   * @param {ServiceDeliveryLocation} sdl
   * @returns {boolean}
   */
  isAuthorized(sdl: ServiceDeliveryLocation): boolean {
    return !(
      sdl.g2Strt0 !== null ||
      (!this.manageAppointmentIfInternal &&
        sdl.parent.intActSupported &&
        !(
          ['RRF'].includes(sdl.parent.area.code) ||
          [
            'DODIETETICAC4',
            'VBDIETETICAC4',
            '209_DOCONSPSICOC4',
            '209_VBCONSPSICOC4',
          ].includes(sdl.name.fam)
        ) &&
        !(
          sdl.nurseVisibility &&
          sdl.parent.nurseVisibility &&
          this.isSpecNurse()
        ))
      //&& (sdl.name.fam !== 'DIALISIPERITONEALI' || !(sdl.name.fam === 'DIALISIPERITONEALI' && this.isSpecNurse()))
      //&& (sdl.name.fam !== 'INFDIABETOLOGIA' || !(sdl.name.fam === 'INFDIABETOLOGIA' && this.isSpecNurse()))
      //&& (sdl.name.fam !== 'WCARE' || !(sdl.name.fam === 'WCARE' && this.isSpecNurse()))
      //&& (sdl.name.fam !== 'UPRI' || !(sdl.name.fam === 'UPRI' && this.isSpecNurse()))
      //&& (sdl.name.fam !== 'STOMAT' || !(sdl.name.fam === 'STOMAT' && this.isSpecNurse()))
    );
  }

  isOncology(sdl: ServiceDeliveryLocation): Boolean {
    return (
      sdl &&
      sdl.parent &&
      sdl.parent.area &&
      sdl.parent.area.code === 'ONCOLOGY'
    );
  }

  isPsychoOncology(sdl: ServiceDeliveryLocation): Boolean {
    return sdl && this.isOncology(sdl) && sdl.psychoOncology;
  }

  isSdlWaitingList(sdl: ServiceDeliveryLocation): Boolean {
    return sdl.waitingListSupported || sdl.parent.waitingListSupported; // sdl.parent.area.code === 'RRF' && this.isSuperUser());
  }

  isNoteAuthorized(sdl: ServiceDeliveryLocation): Boolean {
    return this.isSdlWaitingList(sdl) || this.isOncology(sdl);
  }

  isDietologyC4(sdl: ServiceDeliveryLocation): Boolean {
    return (
      sdl &&
      sdl.name &&
      ['DODIETETICAC4', 'VBDIETETICAC4'].includes(sdl.name.fam)
    );
  }

  public isOfTypeC4(sdl: ServiceDeliveryLocation): boolean {
    // parent_sdl con SERVICE_DELIVERY_LOCATION.INT_ACT_SUPPORTED = 1 (tutte le agende C4)
    return sdl && sdl.parent && sdl.parent.intActSupported === true;
  }

  isLock(sdl: ServiceDeliveryLocation): boolean {
    return !this.isSuperUser() || !this.isAuthorized(sdl);
  }

  public async initAmbularyCalendar(): Promise<any> {
    if (!this.selectedDate) {
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      this.conversationActions.put('selectedDate', today);
    }

    if (environment.ambulatoryCalendar.useRead) {
      // VCO
      return this.serviceDeliveryLocationAction
        .loadUds()
        .then((dm: Datamodel) => {
          if (!this.agendas) {
            let uds = dm.entities;
            uds.sort(
              (a: ServiceDeliveryLocation, b: ServiceDeliveryLocation) => {
                if (a.ordering) {
                  if (b.ordering) {
                    return a.ordering - b.ordering;
                  } else {
                    return -1;
                  }
                } else if (b.ordering) {
                  return 1;
                } else {
                  return 0;
                }
              }
            );
            this.setUds(uds, false);
          }
          return this.refreshAmbularyCalendar(true);
        });
    } else {
      // KLINIK
      this.equal['startDate'] = this.selectedDate;
      this.equal['endDate'] = this.selectedDate;
      return this.init().then(async () => {
        const agendas: Array<any> =
          this.redux.getState().conversation
            .AmbulatoryCalendarAdditionalAgendas;
        if (agendas) {
          const selected = [];
          const all = agendas.map((agenda) => {
            const sdl: ServiceDeliveryLocation = {
              internalId: agenda.internalid,
              waitingListSupported: null,
              g2Strt0: null,
              name: { giv: agenda.name },
              parent: {
                internalId: agenda.parentid,
                name: { giv: agenda.parentname },
                waitingListSupported: null,
              },
            };
            if (agenda.isSelected) {
              selected.push(sdl);
            }
            return sdl;
          });

          this.conversationActions.put(
            'ServiceDeliveryLocationList',
            new Datamodel(all)
          );
          this.setUds(selected, false);
        }

        const physiotherapists: any =
          await this.employeeAction.getByEmployeeRoleCode('8'); // get physiotherapist
        this.conversationActions.put('physiotherapists', physiotherapists);

        return this.refreshAmbularyCalendar(true);
      });
    }
  }

  private getVisibleAgendas(): Promise<Datamodel> {
    if (environment.ambulatoryCalendar.timeBands) {
      const visibleAgendas = this.agendas.slice(
        this.agendaFrom,
        this.agendaTo + 1
      );
      if (visibleAgendas.length > 0) {
        const visibleAgendaIds = visibleAgendas.map((a) => a.internalId);

        return this.timeBandAction
          .loadTimeBands(visibleAgendaIds)
          .then((timeBands: Datamodel) => {
            timeBands.entities.forEach((timeBand: TimeBand) => {
              const agendaColumn = visibleAgendas.find(
                (va) =>
                  va.internalId === timeBand.serviceDeliveryLocation.internalId
              );
              if (!agendaColumn.timeBands) {
                agendaColumn.timeBands = [];
              }
              agendaColumn.timeBands.push(timeBand);
            });
            return timeBands;
          });
      } else {
        return Promise.resolve(null);
      }
    } else {
      return Promise.resolve(null);
    }
  }

  public setUds(uds: Array<any>, resetFromTo: boolean = true) {
    if (uds) {
      this.setAgendas(
        0,
        (uds.length < this.agendaColumns ? uds.length : this.agendaColumns) - 1,
        uds.length,
        resetFromTo
      );
    } else {
      this.setAgendas(0, 0, 0, resetFromTo);
    }
    this.conversationActions.put('agendas', uds);

    return this.getVisibleAgendas().then(() =>
      this.refreshAmbularyCalendar(true)
    );
  }

  private setAgendas(
    from: number,
    to: number,
    tot: number,
    resetFromTo: boolean = true
  ) {
    if (resetFromTo || !this.agendaFrom) {
      this.conversationActions.put('agendaFrom', from);
    }
    if (resetFromTo || !this.agendaTo) {
      this.conversationActions.put('agendaTo', to);
    }
    this.conversationActions.put('agendaTot', tot);
  }

  public refreshAmbularyCalendar(force = false): Promise<any> {
    if (
      this.selectedDate &&
      this.selectedDate.getFullYear() <= 9999 &&
      this.agendaTot !== 0
    ) {
      if (environment.ambulatoryCalendar.colorizeHolidays) {
        let h = new Holidays('IT');
        let hd = h.isHoliday(this.selectedDate);
        if (hd) {
          this.selectedDateHolidayName = hd.name;
        } else {
          this.selectedDateHolidayName = '';
        }
      }

      this.dateFrom = new Date(this.selectedDate);
      if (this.weekViewOfSdl) {
        // monday before
        this.dateFrom.setDate(
          this.dateFrom.getDate() -
            (this.selectedDate.getDay() === 0
              ? 7
              : this.selectedDate.getDay()) +
            1
        );
      }

      this.dateTo = new Date(this.dateFrom);
      if (this.weekViewOfSdl) {
        // saturday after
        this.dateTo.setDate(this.dateTo.getDate() + 6);
      }
      this.dateTo.setHours(23, 59, 59, 99);

      const agendaIds = [];
      if (this.weekViewOfSdl) {
        agendaIds.push(...this.weekViewOfSdl.map((sdl) => sdl.id));
      } else {
        for (let i = this.agendaFrom; i <= this.agendaTo; i++) {
          if (this.agendas[i]) {
            agendaIds.push(this.agendas[i].internalId);
          } else {
            throw new Error(
              'Agenda not in range: agenda from: ' +
                this.agendaFrom +
                ' agenda to ' +
                this.agendaTo +
                ', agendas: ' +
                this.agendas.map((a) => a.internalId)
            );
          }
        }
      }

      if (
        !force &&
        this.appointmentAction.greater['defaultDate'] &&
        this.appointmentAction.greater['defaultDate'].getTime() ===
          this.dateFrom.getTime()
      ) {
        return Promise.resolve(this.buildHoursDatamodel());
      } else {
        if (environment.ambulatoryCalendar.useRead) {
          // VCO
          this.appointmentAction.cleanRestrictions();
          this.appointmentAction.readPageSize = 0; // not paged!
          this.appointmentAction.filterBySdl = false;

          this.appointmentAction.select.push('televisit');
          this.appointmentAction.select.push('urlPath');
          this.appointmentAction.select.push('color');
          this.appointmentAction.select.push('externalId');
          this.appointmentAction.select.push('defaultDate');
          this.appointmentAction.select.push('text.string');
          this.appointmentAction.select.push('patient.internalId');
          this.appointmentAction.select.push('patient.birthTime');
          this.appointmentAction.select.push('patient.name.fam');
          this.appointmentAction.select.push('patient.name.giv');
          this.appointmentAction.select.push('procedure.internalId');
          // this.appointmentAction.select.push('procedure.code.langIt');
          // this.appointmentAction.select.push('procedure.code.langDe');
          this.appointmentAction.select.push('procedure.classCode.code');
          this.appointmentAction.select.push('procedure.levelCode.score');
          if (environment.ambulatoryCalendar.showPerformedProcedure) {
            this.appointmentAction.select.push('procedure.text');
          }
          // this.appointmentAction.select.push('author.name.fam');
          // this.appointmentAction.select.push('author.name.giv');
          this.appointmentAction.select.push(
            'serviceDeliveryLocation.internalId'
          );
          this.appointmentAction.select.push('statusCode.code');
          this.appointmentAction.select.push(
            'appointmentGrouper.statusCode.code'
          );
          this.appointmentAction.select.push('patientEncounter.internalId');
          this.appointmentAction.select.push('insertCompleted');

          this.appointmentAction.equal['insertCompleted'] = true;
          if (!this.showCancelled) {
            this.appointmentAction.notEqual['statusCode.code'] = 'cancelled';
          }

          this.appointmentAction.greater['defaultDate'] = this.dateFrom;
          this.appointmentAction.less['defaultDate'] = this.dateTo;

          this.appointmentAction.equal['serviceDeliveryLocation.internalId'] =
            agendaIds;

          return this.appointmentAction.read().then((apps: Datamodel) => {
            this.appointments = apps.entities;

            if (this.appointments) {
              this.appointments.map((a: Appointment) => {
                if (a.procedure && a.procedure.code) {
                  if (this.translateService.store.currentLang === 'it') {
                    a.procedure.code.currentTranslation =
                      a.procedure.code['langIt'];
                  } else if (this.translateService.store.currentLang === 'de') {
                    a.procedure.code.currentTranslation =
                      a.procedure.code['langDe'];
                  } else if (this.translateService.store.currentLang === 'en') {
                    a.procedure.code.currentTranslation =
                      a.procedure.code['langEn'];
                  }
                }
              });
            }

            return this.agendaAnnotationAction
              .readByDateAndAgenda(this.dateFrom, this.dateTo, agendaIds)
              .then((ann: Datamodel) => {
                // NO KLINIK
                this.agendaAnnotations = ann.entities;

                // creare lista calendar item loopando tutti due insieme

                this.calendarItems = this.appointments.map((a: Appointment) =>
                  CalendarItem.newFromAppointment(a)
                );
                this.agendaAnnotations
                  .filter((a: AgendaAnnotation) => a.color)
                  .map((a: AgendaAnnotation) => {
                    this.calendarItems.push(CalendarItem.newFromAgenda(a));
                  });

                if (this.agendas && apps && this.calendarItems) {
                  this.appointmentBySdl = this.buildCollisionDatamodel();
                  return this.buildHoursDatamodel();
                }
              });
          });
        } else {
          // KLINIK uses Dashboard action
          this.cleanRestrictions();
          this.equal['startDate'] = this.dateFrom;
          this.equal['endDate'] = this.dateTo;
          this.equal['agenda'] = agendaIds.join();
          if (this.physiotherapist) {
            this.equal['physiotherapist'] = this.physiotherapist.value;
          }
          if (!this.showCancelled) {
            this.equal['statusCode'] = 'cancelled';
          }
          return this.refresh().then((dm: Datamodel) => {
            if (dm && dm.entities) {
              // Convert server format to Appointment
              this.appointments = dm.entities.map((row: any) => ({
                internalId: row.internalid,

                serviceDeliveryLocation: { internalId: row.location },
                patient: {
                  birthTime: row.patient.birthtime,
                  internalId: row.patient.internalid,
                  name: {
                    fam: row.patient.surname,
                    giv: row.patient.name,
                  },
                },
                physiotherapist: {
                  internalId: row.therapist.internalid,
                  name: {
                    fam: row.therapist.surname,
                    giv: row.therapist.name,
                  },
                },
                surgeon: {
                  internalId: row.surgeon ? row.surgeon.internalid : null,
                  name: {
                    fam: row.surgeon ? row.surgeon.surname : null,
                    giv: row.surgeon ? row.surgeon.name : null,
                  },
                },
                appointmentGrouper: row.appointmentgrouper,
                defaultDate: row.time,
                externalId: row.externalid ? row.externalid : null,
                statusCode: {
                  codeSystemName: 'PHIDIC',
                  domainName: 'AppointmentStatus',
                  code: row.status,
                },
                // {code: row.status}, // row.statusId not used
                text: { string: row.note },
                color: row.color,
                duration: row.duration, // FIXME use procedure.levelcode or not?
                // visitType: {code: row.visitType, id: row.visitTypeId} // TODO: not used at VCO
                patientEncounter: { internalId: row.encounterid },
                performedProcedure: row.procedures
                  ? row.procedures
                      .split(',')
                      .map((procText: string) => ({ text: procText }))
                  : [],
                // createdBy: createdBy, // TODO: not used at VCO
                // isIndirect: row.isindirect // TODO: not used at VCO
                visitType: {
                  codeSystemName: 'PHIDIC',
                  domainName: 'VisitType',
                  code: row.visittype,
                },
                diagnosis: row.diagnosis,
                anesthesia: {
                  codeSystemName: 'PHIDIC',
                  domainName: 'VisitType',
                  currentTranslation: row.anesthesia,
                },
              }));
            } else {
              this.appointments = [];
            }

            this.calendarItems = this.appointments.map((a: Appointment) =>
              CalendarItem.newFromAppointment(a)
            );

            if (this.agendas && this.calendarItems) {
              this.appointmentBySdl = this.buildCollisionDatamodel();
              return this.buildHoursDatamodel();
            }
          });
        }
      }
    } else {
      this.list = null;
      return Promise.resolve(null);
    }
  }

  public buildCollisionDatamodel(): any {
    const appBySdl = {};

    this.agendas.map((ud: ServiceDeliveryLocation) => {
      appBySdl[ud.internalId] = this.calendarItems.filter(
        (calItem: CalendarItem) =>
          ud.internalId === calItem.serviceDeliveryLocation.internalId
      );

      appBySdl[ud.internalId].sort(
        (a: CalendarItem, b: CalendarItem) =>
          a.date.getTime() - b.date.getTime()
      );

      // using for instead of map to be able to remove elements while looping, and decrement i
      for (let i = 0; i < appBySdl[ud.internalId].length; i++) {
        const app: CalendarItem = appBySdl[ud.internalId][i];

        const appEnd: Date = new Date(app.date);
        appEnd.setMinutes(appEnd.getMinutes() + app.duration);

        if (i === 0) {
          appBySdl[ud.internalId][i] = [app];
          appBySdl[ud.internalId][i].max = 1;
        } else {
          const prevGroup: Array<CalendarItem> = appBySdl[ud.internalId][i - 1];

          let collideWithPrev = false;
          let maxCollisions = 1;
          prevGroup.map((prevApp: CalendarItem) => {
            let offset = AmbulatoryCalendarAction.getOffset(prevApp, app.date);
            if (offset !== null) {
              if (!collideWithPrev) {
                collideWithPrev = true;
                app['offset'] = offset;
                appBySdl[ud.internalId][i - 1].push(app);
                appBySdl[ud.internalId].splice(i, 1);
                i--; // decrement i since one element was removed
              }
              maxCollisions++;
            } else {
              if (!(appBySdl[ud.internalId][i] instanceof Array)) {
                appBySdl[ud.internalId][i] = [app];
                appBySdl[ud.internalId][i].max = 1;
              }
            }
          });

          if (appBySdl[ud.internalId][i].max < maxCollisions) {
            appBySdl[ud.internalId][i].max = maxCollisions;
          }
        }
      }
    });
    return appBySdl;
  }

  public buildHoursDatamodel(): Datamodel {
    const rows = [];

    const HourList: Datamodel = new Datamodel(rows);

    if (this.dateFrom && this.dateTo) {
      if (this.weekViewOfSdl) {
        let h = new Holidays('IT');

        HourList.headers = [];
        for (
          let dayOfWeek = new Date(this.dateFrom);
          dayOfWeek.getTime() <= this.dateTo.getTime();
          dayOfWeek.setDate(dayOfWeek.getDate() + 1)
        ) {
          this.weekViewOfSdl.map((sdl, index) => {
            let isHoliday = false;
            let dayName = '';
            if (environment.ambulatoryCalendar.colorizeHolidays) {
              let hd = h.isHoliday(dayOfWeek);
              if (hd) {
                dayName = hd.name;
                isHoliday = true;
              }
              if (!isHoliday) {
                isHoliday =
                  dayOfWeek.getDay() === 0 || dayOfWeek.getDay() === 6;
              }
            }

            const header = {
              text: this.datePipe.transform(dayOfWeek, 'shortDayDate'),
              id: +sdl.id,
              date: new Date(dayOfWeek),
              note: this.agendaAnnotations.find(
                (aa: AgendaAnnotation) =>
                  aa.serviceDeliveryLocation.internalId === sdl.id &&
                  aa.availabilityTime.getTime() === dayOfWeek.getTime()
              ),
              weekViewMultiSdl: false,
              isHoliday: isHoliday,
              dayName: dayName,
              timeBands: sdl.timeBands,
            };

            HourList.headers.push(header);

            if (!(index % this.weekViewOfSdl.length)) {
              HourList.headersFiltered.push(header);
            } else {
              header.weekViewMultiSdl = true;
            }
          });
        }
      } else {
        HourList.headers = this.agendas
          .slice(this.agendaFrom, this.agendaTo + 1)
          .map((sdl: ServiceDeliveryLocation) => ({
            text: sdl.name.giv,
            id: sdl.internalId,
            lock: !this.isSuperUser() || !this.isAuthorized(sdl),
            waitingList:
              sdl.waitingListSupported || sdl.parent.waitingListSupported,
            parent: { text: sdl.parent.name.giv },
            /*note: sdl['agendaAnnotation']*/
            note: this.agendaAnnotations.find(
              (aa: AgendaAnnotation) =>
                aa.serviceDeliveryLocation.internalId === sdl.internalId &&
                aa.availabilityTime.getTime() === this.selectedDate.getTime()
            ),
            timeBands: sdl.timeBands,
          }));
        HourList.headersFiltered = HourList.headers;
      }

      const start: Date = new Date(0);
      start.setHours(AmbulatoryCalendarAction.startHour);

      const end: Date = new Date(0);
      end.setHours(AmbulatoryCalendarAction.endHour);

      for (
        let frm = start;
        frm.getTime() < end.getTime();
        frm.setMinutes(
          frm.getMinutes() + AmbulatoryCalendarAction.minutesPerRow
        )
      ) {
        const to = new Date(frm);
        to.setMinutes(to.getMinutes() + AmbulatoryCalendarAction.minutesPerRow);

        const row: Array<Date | CalendarRow> = [new Date(frm)];

        HourList.headers.map((sdl, i) => {
          // check timeBands
          let enabled = 0;
          let color = '';
          if (sdl.timeBands && sdl.timeBands.length > 0) {
            sdl.timeBands.map((timeBand: TimeBand) => {
              let date = this.dateFrom;
              if (this.weekViewOfSdl) {
                date = sdl.date;
              }

              if (
                date.getTime() >= timeBand.startDate.getTime() &&
                date.getTime() <= timeBand.endDate.getTime() &&
                timeBand[timeBandDays[date.getDay()]]
              ) {
                // date inside timeband's dates

                if (
                  to.getTime() > timeBand.startTime.getTime() &&
                  frm.getTime() < timeBand.startTime.getTime()
                ) {
                  // table cell before timeband
                  enabled =
                    ((timeBand.startTime.getTime() - frm.getTime()) * 100) /
                    (to.getTime() - frm.getTime());
                  color = timeBand.color;
                } else if (
                  frm.getTime() >= timeBand.startTime.getTime() &&
                  to.getTime() <= timeBand.endTime.getTime()
                ) {
                  // cell overlapping timeband
                  enabled = 100;
                  color = timeBand.color;
                } else if (
                  frm.getTime() < timeBand.endTime.getTime() &&
                  to.getTime() > timeBand.endTime.getTime()
                ) {
                  // cell after timeband
                  enabled =
                    (((to.getTime() - timeBand.endTime.getTime()) * 100) /
                      (to.getTime() - frm.getTime())) *
                    -1;
                  color = timeBand.color;
                }

                // if (enabled !== 0) { // if enabled check closures
                //   if (sdl.closures && sdl.closures.length > 0) {
                //     sdl.closures.map((closure: AgendaClosureDto) => {
                //       if (date.getTime() >= closure.startDate.getTime() &&
                //           date.getTime() <=  closure.endDate.getTime()) { // date inside closure's dates
                //         enabled = 0;
                //       }
                //     });
                //   }
                // }
              }
            });
          } else {
            enabled = 100;
          }

          row[i + 1] = {
            enabled,
            color,
          };

          const appList4currSdl = this.appointmentBySdl[sdl.id];

          if (appList4currSdl) {
            appList4currSdl.map((appOrGroup: Array<CalendarItem>) => {
              let currApp = appOrGroup[0];

              let appDateAbsolute = new Date(0);
              appDateAbsolute.setHours(
                currApp.date.getHours(),
                currApp.date.getMinutes()
              );

              if (
                appDateAbsolute.getTime() >= frm.getTime() &&
                appDateAbsolute.getTime() < to.getTime()
              ) {
                if (
                  (this.weekViewOfSdl &&
                    currApp.date.getDay() === sdl.date.getDay()) ||
                  (!this.weekViewOfSdl &&
                    currApp.date.getDay() === this.selectedDate.getDay())
                ) {
                  const offset = new Date(
                    currApp.date.getTime() - frm.getTime()
                  ).getMinutes();
                  row[i + 1] = {
                    items: appOrGroup,
                    offset,
                    enabled,
                    color,
                  };
                }
              }
            });
          }
        });
        rows.push(row);
      }
    }
    this.list = HourList;
    return HourList;
  }

  /**
   * Call rest method /startNewAppointment and then execute process CreateNewAppointment
   * Used by KLINIK to create new appointment
   * @param {Date} defaultDate
   * @param sdlId
   * @returns {Promise<any>}
   */
  startNewAppointment(defaultDate: Date, sdlId, process): Promise<any> {
    const parameters: string =
      '/defaultDate=' +
      encodeValue(dateToString(defaultDate)) +
      ';serviceDeliveryLocation.internalId=' +
      sdlId;

    return this.httpService
      .fetch(
        this.buildUrl(
          this.apiUrl +
            this.restBaseUrl +
            this.dashboardUrl +
            AmbulatoryCalendarAction.START_NEW_APPOINTMENT +
            parameters
        ),
        {
          method: 'GET',
          credentials: 'include',
        }
      )
      .then((response) => {
        this.processActions.startProcess(process);
      })
      .catch((error) => {
        console.error(
          'Error startNewAppointment rest method ' +
            defaultDate +
            ' ' +
            sdlId +
            ' ' +
            error.message
        );
        throw error;
      });
  }

  prevAgenda(): Promise<any> {
    if (this.agendaFrom > 0) {
      let from = this.agendaFrom - this.agendaColumns;
      if (from < 0) {
        from = 0;
      }
      this.conversationActions.put('agendaFrom', from);
      this.conversationActions.put('agendaTo', from + this.agendaColumns - 1);
      return this.getVisibleAgendas().then(() =>
        this.refreshAmbularyCalendar(true)
      );
    }
  }

  nextAgenda(): Promise<any> {
    if (this.agendaTo < this.agendaTot - 1) {
      this.conversationActions.put(
        'agendaFrom',
        this.agendaFrom + this.agendaColumns
      );
      if (this.agendaTo + this.agendaColumns < this.agendaTot - 1) {
        this.conversationActions.put(
          'agendaTo',
          this.agendaTo + this.agendaColumns
        );
      } else {
        this.conversationActions.put('agendaTo', this.agendaTot - 1);
      }
      return this.getVisibleAgendas().then(() =>
        this.refreshAmbularyCalendar(true)
      );
    }
  }

  goToAgenda(serviceDeliveryLocation: ServiceDeliveryLocation) {
    const sdl: ServiceDeliveryLocation = this.agendas.find(
      (s: ServiceDeliveryLocation) =>
        s.internalId === serviceDeliveryLocation.internalId
    );
    if (sdl) {
      const selectedSdls = this.agendas.filter(
        (sdlToSel: ServiceDeliveryLocation) =>
          sdlToSel.internalId == sdl.internalId
      );
      this.selectedSdl.emit(selectedSdls);
      const sdlindex: number = this.agendas.indexOf(sdl);
      this.conversationActions.put(
        'agendaFrom',
        sdlindex - (sdlindex % this.agendaColumns)
      );
      const to = this.agendaFrom + this.agendaColumns - 1;
      this.conversationActions.put(
        'agendaTo',
        to <= this.agendas.length - 1 ? to : this.agendas.length - 1
      );

      this.refreshAmbularyCalendar(true).then(() => {
        this.conversationActions.put('weekViewOfSdl', [
          {
            id: sdl.internalId,
            text: sdl.name.giv,
          },
        ]);
        this.buildHoursDatamodel();
      });
    } else {
      logError(
        'Agenda with id ' +
          serviceDeliveryLocation.internalId +
          ' not found in user enabled agendas ' +
          this.agendas.map((a) => a.internalId),
        this.cid
      );
    }
  }

  copyCalendarItem(calItemId: number, type: 'Appointment' | 'Note') {
    if (type === 'Appointment') {
      this.appointmentAction.getAll(calItemId).then((app: Appointment) => {
        this.conversationActions.put('movingReason', 'COPY');
        this.conversationActions.put(
          'movingAppointment',
          CalendarItem.newFromAppointment(app)
        );
      });
    }
    if (type === 'Note') {
      this.agendaAnnotationAction
        .inject(calItemId, null, ['lengthCode'])
        .then((agenda: AgendaAnnotation) => {
          this.conversationActions.put('movingReason', 'COPY');
          this.conversationActions.put(
            'movingAppointment',
            CalendarItem.newFromAgenda(agenda)
          );
        });
    }
  }

  moveCalendarItem(calItemId: number, type: 'Appointment' | 'Note') {
    if (type === 'Appointment') {
      this.appointmentAction.getAll(calItemId).then((app: Appointment) => {
        this.conversationActions.put('movingReason', 'MOVE');
        this.conversationActions.put(
          'movingAppointment',
          CalendarItem.newFromAppointment(app)
        );
      });
    }
    if (type === 'Note') {
      this.agendaAnnotationAction
        .inject(calItemId, null, ['lengthCode'])
        .then((agenda: AgendaAnnotation) => {
          this.conversationActions.put('movingReason', 'MOVE');
          this.conversationActions.put(
            'movingAppointment',
            CalendarItem.newFromAgenda(agenda)
          );
        });
    }
  }

  isHoliday(hdr) {
    if (environment.ambulatoryCalendar.colorizeHolidays) {
      if (this.weekViewOfSdl) {
        return (
          hdr.isHoliday ||
          (hdr.date && (hdr.date.getDay() === 0 || hdr.date.getDay() === 6))
        );
      } else {
        return (
          (this.selectedDate &&
            (this.selectedDate.getDay() === 0 ||
              this.selectedDate.getDay() === 6)) ||
          this.selectedDateHolidayName
        );
      }
    } else {
      return false;
    }
  }

  printWorkList() {
    const next = new Date(this.selectedDate.getTime());
    next.setDate(next.getDate() + 1);

    const agendaIds = [];
    if (this.weekViewOfSdl) {
      agendaIds.push(...this.weekViewOfSdl.map((sdl) => sdl.id));
    } else {
      for (let i = 0; i < this.agendas.length; i++) {
        if (this.agendas[i]) {
          agendaIds.push(this.agendas[i].internalId);
        } else {
          throw new Error(
            'Agenda not in range: agendas: ' +
              this.agendas.map((a) => a.internalId)
          );
        }
      }
    }

    window.open(
      this.apiUrl +
        this.buildUrl(
          'report?solutionName=PHI_KLINIK&reportName=common//jasper//ambulatory-work-list' +
            '&dateFrom=' +
            this.selectedDate.getTime() +
            '&dateTo=' +
            next.getTime() +
            '&agendaIds=' +
            agendaIds
        )
    );
  }
}
