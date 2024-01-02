import { Injectable, Injector } from '@angular/core';
import { select } from '@angular-redux/store';
import {BaseActionService, dateReplacer} from './base-action.service';
import { Appointment } from '../entities/base-entity';
import { anyToString, reviver } from '../converters/any.converter';
import { dateToString } from '../converters/date.converter';
import { Datamodel } from '../datamodel/datamodel';
import { CodeValue } from '../entities/data-types/code-value';
import { Patient, Employee, ServiceDeliveryLocation } from '../entities/role';
import { AppointmentGrouper } from '../entities/base-entity/appointment-grouper';
import { environment } from '../../../environments/environment';

@Injectable()
export class AppointmentActionService extends BaseActionService<Appointment> {

  public static defaultColor = 16503551; // #FBD2FF

  private static AMBULATORY = '/ambulatory';
  private static STATUS = '/status';
  private static VCO_STATUS = '/vco_status';
  private static DETAILS = '/details';
  private static COPY = '/copy';
  private static PATIENT = '/patients';
  private static CREATE_REPEATED = '/createRepeated';

  private static fieldsToBeLoaded = [
    'patient',
    'serviceDeliveryLocation',
    'serviceDeliveryLocation.parent',
    'statusCode',
    'externalId',
    'performedProcedure',
    'procedure',
    'procedure.classCode',
    'procedure.levelCode',
    'procedureRequest.procedure.codeIcd9',
    'procedureRequest.procedure.statusCode',
    'procedureRequest.exemption',
    'author'
  ];

  @select(['conversation', 'Patient']) patient$;
  patient: Patient;

  @select(['config', 'employee']) employee$;
  employee: Employee;

  @select(['conversation', 'ServiceDeliveryLocationList']) serviceDeliveryLocationList$;
  sdLocs: Datamodel;

  constructor(injectorz: Injector) {
    super(injectorz);
    this.entityName = 'Appointment';
    this.entityUrl = 'appointments';

    this.patient$.subscribe((pat) => {
      this.patient = pat;
    });
    this.employee$.subscribe((emp) => {
      this.employee = emp;
    });
    this.serviceDeliveryLocationList$.subscribe((sdLocs) => {
      this.sdLocs = sdLocs;
    });
  }

  public getAll(id: number): Promise<Appointment> {
    return this.get(id, null, AppointmentActionService.fieldsToBeLoaded).then((app: Appointment) => {
      if (!app.text) {
        app.text = {};
      }
      return app;
    });
  }

  public injectAll(id: number): Promise<Appointment> {
    return this.inject(id, null, AppointmentActionService.fieldsToBeLoaded).then((app: Appointment) => {
      if (!app.text) {
        app.text = {};
      }
      return app;
    });
  }

  public newAppointment(sdlId: number, date: Date, procedure: string, status = 'planned', appGrp: AppointmentGrouper = null): Appointment {
    this.entity = {};

    this.entity.author = { internalId: this.employee.internalId, entityName: 'com.phi.entities.role.Employee'};

    this.entity.defaultDate = date;
    this.entity.insertCompleted = true;

    if ((procedure === 'CUP' || procedure === 'MACROPROC') && this.patient) {
      const patientProxy: Patient = {entityName: 'com.phi.entities.role.Patient'};
      patientProxy.internalId = this.patient.internalId;
      patientProxy.name = this.patient.name;
      this.entity.patient = patientProxy;
    }

    if (procedure !== 'NONE') { // KLINIK General activity has no procedure
      this.entity.procedure = {};
      this.entity.procedure.classCode = {code: procedure, codeSystemName: 'PHIDIC', domainName: 'ProcType'};
      this.entity.procedure.levelCode = {code: '15min', codeSystemName: 'PHIDIC', domainName: 'Length'};
    }

    if (procedure === 'CUP') {
      this.entity.color = AppointmentActionService.defaultColor;
      this.entity.procedureRequest = [{}];
      this.entity.procedureRequest[0].procedure = [];
    } else if (procedure === 'MACROPROC') {
      this.entity.procedure.levelCode.code = '30min';
    }

    if (sdlId) {
      let sdl: ServiceDeliveryLocation;
      if (this.sdLocs && this.sdLocs.entities) {
        sdl = this.sdLocs.entities.find((sdloc: ServiceDeliveryLocation) => sdloc.internalId === sdlId);
      }

      if (!sdl) {
        throw new Error('Agenda with id ' + sdlId + ' not found in loaded agendas ' + this.sdLocs.entities.map((a) => a.internalId));
      }

      this.entity.serviceDeliveryLocation = {
        internalId: sdlId,
        entityName: 'com.phi.entities.role.ServiceDeliveryLocation',
        name: {giv: sdl.name.giv, fam: sdl.name.fam},
        nurseVisibility: sdl.nurseVisibility,
        parent: {
          name: {giv: sdl.parent.name.giv, fam: sdl.parent.name.fam},
          internalId: sdl.parent.internalId,
          nurseVisibility: sdl.parent.nurseVisibility,
          area: sdl.parent.area
        }
      };
    }

    this.entity.statusCode = { code: status, codeSystemName: 'PHIDIC', domainName: 'AppointmentStatus' };
    this.entity.text = {};

    this.linkAppointmentGrouper(appGrp);

    this.conversationActions.put(this._entityName, this.entity);

    return this.entity;
  }

  linkAppointmentGrouper(appGrp: AppointmentGrouper) {
    if (appGrp) {
      this.entity.appointmentGrouper = { internalId: appGrp.internalId, entityName: 'com.phi.entities.baseEntity.AppointmentGrouper'};

      if (appGrp.appointment) {
        appGrp.appointment.push(this.entity);
      } else {
        appGrp.appointment = [ this.entity ];
      }
    }
  }

  /**
   * Convert server format:
   * "ECG@@1@@new&&TEST DETERIORAMENTO O SVILUPPO INTELLETTIVO@@1@@new&&VALUTAZIONE FUNZIONALE FUNZIONI CORTICALI SUP"
   * into array of strings
   * or take proc.code.trans or procedure
   * @param {string} procedure
   * @returns {Array<string>}
   */
  procedureToArray(appointmentRow: any): Array<any> { //
    let procedures: Array<any> = [];
    if (appointmentRow) {
      if (appointmentRow.proc && appointmentRow.proc.code && appointmentRow.proc.code.trans ) {
        return [{
          title: appointmentRow.proc.code.trans,
          status: ''
        }];
      } else if (appointmentRow.procedurerequest && appointmentRow.procedurerequest.procedure) {
        procedures = appointmentRow.procedurerequest.procedure.split('&&');
        procedures = procedures.map((proc: string) => {
          const totAndName = proc.split('@@');
          return {
            title: totAndName[1] + ' ' + totAndName[0],
            status: totAndName[2]
          };
        });
      } else if (appointmentRow.procedure ) {
        procedures = appointmentRow.procedure.split('@@');
        return [{
          title: procedures[0],
          status: ''
        }];
      }
    }
    return procedures;
  }


  /**
   * get Ambulatory Appointments
   */
  getAmbulatoryAppointments(date: Date, selectedStatuses, sdlocs: Array<any>, employeeRoleId: number,
    retrieveInternalActivity: Boolean = false, typeFilter: string = null, days: number = 1, page: number = 0): Promise<any> {

    if (!date || date.getFullYear() > 9999) { // 9999 max year supported by oracle
      return;
    }

    let parameters: String = '?';

    parameters += 'date=' + anyToString(date);

    if (employeeRoleId) {
      parameters += '&roleId=' + employeeRoleId;
    } else {
      sdlocs.map((sdl) => {
        parameters += '&id=' + sdl.id;
      });
    }

    if (selectedStatuses != null) {
      selectedStatuses.map((cv: CodeValue) => {
        parameters += '&status=' + cv.id;
      });
    }

    parameters += '&intact=' + retrieveInternalActivity;

    if (days > 1) {
      parameters += '&days=' + days;
    }

    if (typeFilter) {
      parameters += '&type-filter=' + typeFilter; // 'cup' or 'consulence'
    }

    return this.httpService.fetch(this.apiUrl + this.restBaseUrl + this.entityUrl + AppointmentActionService.AMBULATORY + '/' + page
      + parameters + '&cid=' + this.cid,
      {
        credentials: 'include'
      })
      .then(response => response.text())
      .then(text => JSON.parse(text, reviver) )
      .then(raw => {

        let dm: Datamodel = new Datamodel(raw || [], this.httpService);

        dm._pages.pop();
        dm._pages[0] = raw;
        dm.currentPageNumber = page;
        dm.nextUrl = this.restBaseUrl + this.entityUrl + AppointmentActionService.AMBULATORY + '/'
          + (page + 1) + parameters + '/'; // force next page

        this.conversationActions.put('AmbulatoryAppointmentList', dm);
      })
      .catch(error => {
        console.error('Error getAmbulatoryAppointments ' + date + ' ' + error.message);
        throw error;
      });
  }

  public getDetails(appId: number, retrieveInternalActivity: boolean = false): Promise<any> {

    let parameters = '?';
    parameters += 'intact=' + retrieveInternalActivity;

    return this.httpService.fetch(this.apiUrl + this.restBaseUrl + this.entityUrl + AppointmentActionService.DETAILS + '/'
      + appId + parameters + '&conversationId=' + this.cid,
      {
        credentials: 'include'
      })
      .then(response => response.text())
      .then(text => JSON.parse(text, reviver) )
      .then(raw => {
        return raw;
      });
  }

  public copyAppointment(appId: number, date: Date, sdlId: number): Promise<any> {

    let parameters = '?';
    parameters += 'appointmentId=' + appId + '&agendaId=' + sdlId;

    if (date != null) {
      parameters += '&date=' + anyToString(date);
    }

    return this.httpService.fetch(this.apiUrl + this.restBaseUrl + this.entityUrl + environment.appointmentAction.copy +
      '/' + parameters + '&conversationId=' + this.cid,
      {
        credentials: 'include'
      })
      .then(response => response.text())
      .then(text => JSON.parse(text, reviver) )
      .then(raw => {
        return raw;
      });
  }



  changeStatus(appInternalId: number, action: string, reasonCancCodeId: string = '', cancellationNote: string = '',cleanCycle:boolean=null): Promise<any> {

    const url = this.apiUrl + this.restBaseUrl + this.entityUrl + '/' + environment.appointmentAction.changeStatus
      + '/' + appInternalId + ';jsessionid=' + this.sid + '?cid=' + this.cid;

    let body = 'action=' + action;
    body = body + '&reasonCancCodeId=' + reasonCancCodeId;
    body = body + '&cancellationNote=' + cancellationNote;
    let form = new FormData();
    form.append('action', action);

    return this.httpService.fetch(url, {
      method: 'POST',
      body,
      headers: {'Content-Type': 'application/x-www-form-urlencoded'},
      credentials: 'include'
    })
    .then(response => response.text())
    .then(text => JSON.parse(text, reviver))
    .then(res => {
      if (res.locked) {
        this.vm.openAlertMessage(this.ts.instant('Warning'),
        res.locked + ' ' + res.lockingUser, false);
      } else if (res.status && res.status === 'error') {
        this.vm.openAlertMessage(this.ts.instant('Warning'),
        res.message, false);
    } else {
        if (this.entity) {
          return this.getAll(this.entity.internalId);
        }
      }
      return Promise.resolve(null);
    });

  }

  deleteAll(appInternalId: number, reasonCancCodeId: string = null, cancellationNote: string = null): Promise<any> {

    const url = this.apiUrl + this.restBaseUrl + this.entityUrl + '/deleteAll/' + appInternalId + ';jsessionid=' + this.sid + '?cid=' + this.cid;

    let body;
    if (reasonCancCodeId) {
      body = body + '&reasonCancCodeId=' + reasonCancCodeId
    }
    if (cancellationNote) {
      body = body + '&cancellationNote=' + cancellationNote
    }


    return this.httpService.fetch(url, {
      method: 'POST',
      body,
      headers: {'Content-Type': 'application/x-www-form-urlencoded'},
      credentials: 'include'
    })
    .then(response => response.text())
    .then(text => JSON.parse(text, reviver))
    .then(res => {
      if (res.locked) {
        this.vm.openAlertMessage(this.ts.instant('Warning'),
        res.locked + ' ' + res.lockingUser, false);
      }
    });
  }




  public getPatientAppointments(patientId: number, date: Date, agendaIds: Array<number>, orderBy = 'ASC'): Promise<any> {

    let parameters = '?';
    parameters += 'id=' + patientId;

    if (date != null) {
      parameters += '&date=' + anyToString(date);
    }

    if (agendaIds) {
      agendaIds.map((id: number) => {
        parameters += '&agendaId=' + id;
      });
    }

    parameters += '&orderBy=' + orderBy;

    return this.httpService.fetch(this.apiUrl + this.restBaseUrl + this.entityUrl + AppointmentActionService.PATIENT
      + ';jsessionid=' + this.sid + parameters + '&conversationId=' + this.cid,
      {
        credentials: 'include'
      })
      .then(response => response.text())
      .then(text => JSON.parse(text, reviver) )
      .then(raw => {
        this.conversationActions.put('PatientAppointmentList', new Datamodel(raw))
      })
      .catch(error => {
        console.error('Error getPatientAppointments ' + date + ' ' + error.message);
        throw error;
      });
    // 2018-03-29 12:32:48,781 ERROR [com.phi.rest.AppointmentRest] imperiale Error getting appointment list for patient id: 1526510
    // Caused by: java.sql.SQLException: ORA-01489: result of string concatenation is too long
    // 2018-03-29 12:32:48,833 ERROR [com.phi.rest.log.LogRest] imperiale [cid=68003]
    // Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36
    // Error: Uncaught (in promise): SyntaxError: Unexpected token E in JSON at position 0
    // SyntaxError: Unexpected token E in JSON at position 0

    // USE XML AGG?!?!
    // https://stackoverflow.com/questions/29776035/oracle-ora-01489-result-of-string-concatenation-is-too-long
  }

  /**
   * Created repeatable general activity for KLINIK
   * @param {string} frequency
   * @param {Date} endDate
   * @returns {Promise<any>}
   */
  public createRepeated(frequency: string, endDate: Date): Promise<any> {

    // Terrible format for retrocompatibility Flex dashboard
    let formData = 'endDate=' + encodeURIComponent(dateToString(endDate)) +
                   '&entity=' + encodeURIComponent(JSON.stringify(this.entity, dateReplacer)) +
                   '&frequency=' + encodeURIComponent(frequency);

    return this.httpService.fetch(this.apiUrl + this.restBaseUrl + this.entityUrl + AppointmentActionService.CREATE_REPEATED + '?cid=' + this.cid,
      {
        method: 'POST',
        body: formData,
        headers: new Headers({'Content-Type': 'application/x-www-form-urlencoded'}),
        credentials: 'include'
      })
      .then(response => response.text())
      .then(text => JSON.parse(text, reviver) )
      .then(raw => {
        return raw;
      });
  }

  public getColorUrlPath(urlPath: string): string{
    if (urlPath !==null && urlPath !== '') {
      return "green";
    } else {
      return "black";
    }
  }
}
