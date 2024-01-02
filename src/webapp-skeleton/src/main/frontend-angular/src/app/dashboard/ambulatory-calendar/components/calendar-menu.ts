import { Config } from './../../../store/config.reducer';
import { Component, Injector, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { select } from '@angular-redux/store';
import { BaseForm } from '../../../widgets/form/base-form';
import { ViewManager } from '../../../services/view-manager';
import { AmbulatoryCalendarAction } from '../actions/ambulatory-calendar-action.service';
import { PatientActionService } from '../../../services/actions/patient-action.service';
import { Datamodel } from '../../../services/datamodel/datamodel';
import { ServiceDeliveryLocation } from '../../../services/entities/role/service-delivery-location';
import { TranslateService } from '@ngx-translate/core';
import { Patient } from '../../../services/entities/role/patient';
import { AgendaAnnotationEdit } from '../components/agendaannotation-edit';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'phi-calendar-menu',
  templateUrl: './calendar-menu.html'
})
export class CalendarMenu extends BaseForm implements OnDestroy {

  @select(['conversation', 'ServiceDeliveryLocationList']) serviceDeliveryLocationList$;
  serviceDeliveryLocationListSub;
  ServiceDeliveryLocationList: Datamodel;

  @select(['conversation', 'Patient']) patient$;
  patientSub;
  patient: Patient;

  @select(['config', 'employeeRoleCode']) employeeRoleCode$;
  employeeRoleCodeSub;
  employeeRoleCode: string;

  @select(['config']) config$;
  config: Config;

  sdlId;
  sdlWaitingList;
  isAuthorized;
  isOncology;
  isPsychoOncology;
  isDietologyC4;
  date;

  patientFromConversation = false;

  isCoordinator = false;

  param: Map<string, any>;

  environment = environment;

  constructor(injector: Injector,
    public AmbulatoryCalendarAction: AmbulatoryCalendarAction,
    private PatientAction: PatientActionService,
    private route: ActivatedRoute,
    private viewManager: ViewManager,
    private translateService: TranslateService) {
    super(injector);

    this.sdlId = +this.route.snapshot.params['sdlId'];
    this.date = new Date(+this.route.snapshot.params['date']);
    this.patientFromConversation = this.route.snapshot.params['patientFromConversation'] === 'true';


    this.serviceDeliveryLocationListSub = this.serviceDeliveryLocationList$.subscribe(res => {
      this.ServiceDeliveryLocationList = res;
      if (this.ServiceDeliveryLocationList && this.ServiceDeliveryLocationList.entities) {
        const sdl: ServiceDeliveryLocation = this.ServiceDeliveryLocationList.entities.find(
          (sdLoc: ServiceDeliveryLocation) => sdLoc.internalId === this.sdlId
        );
        this.sdlWaitingList = !!sdl.waitingListSupported || !!sdl.parent.waitingListSupported;
        this.isAuthorized = AmbulatoryCalendarAction.isAuthorized(sdl);
        this.isOncology = AmbulatoryCalendarAction.isOncology(sdl);
        this.isPsychoOncology = AmbulatoryCalendarAction.isPsychoOncology(sdl);
        this.isDietologyC4 = AmbulatoryCalendarAction.isDietologyC4(sdl);
      }
    });

    this.patientSub = this.patient$.subscribe(pat => {
      this.patient = pat;
    });

    this.employeeRoleCodeSub = this.employeeRoleCode$.subscribe(employeeRoleCode => {
      this.isCoordinator = employeeRoleCode === '4';
    });

    this.config$.subscribe((cfg: Config) => {
      this.config = cfg;
      this.param = cfg.param;
    });
  }

  ngOnDestroy() {
    this.serviceDeliveryLocationListSub.unsubscribe();
    this.patientSub.unsubscribe();
    this.employeeRoleCodeSub.unsubscribe();
  }

  async addAppointment() {

    if (environment.calendarMenu.addAppointmentProcess) {
      // KLINIK
      this.viewManager.setPopupViewId(null).then(() =>
        this.AmbulatoryCalendarAction.startNewAppointment(this.date, this.sdlId, environment.calendarMenu.addAppointmentProcess)
      );
    } else {
      // VCO
      let procedureClassCode = 'CUP';

      if (this.sdlWaitingList && !(this.param && this.param['p.dashboard.ambulatorycalendar.cuponly'])) {
        procedureClassCode = 'MACROPROC';
        if (this.patient) {
          await this.PatientAction.eject();
        }
      }

      if (this.patient && this.patient.name && procedureClassCode === 'CUP' && !this.patientFromConversation) {
        this.viewManager.openAlertMessage(
          this.translateService.instant('Confirm'),
          this.translateService.instant('appointment-for-pat') + ' ' + this.patient.name.fam + ' ' + this.patient.name.giv + ' ?',
          true,
          true,
          'OK',
          'No',
          true,
          () => {
            this.viewManager.setPopupViewId('appointment', 'new', this.sdlId, this.date.getTime().toString(),
              'planned', procedureClassCode, this.patientFromConversation.toString());
          },
          () => {
            this.PatientAction.eject().then(() =>
              this.viewManager.setPopupViewId('appointment', 'new', this.sdlId, this.date.getTime().toString(),
                'planned', procedureClassCode, this.patientFromConversation.toString())
            );
          }
        );
      } else {
        this.viewManager.setPopupViewId('appointment', 'new', this.sdlId, this.date.getTime().toString(), 'planned', procedureClassCode, this.patientFromConversation.toString());
      }
    }
  }

  addCycle() {
    this.viewManager.setPopupViewId(null).then(() =>
      this.AmbulatoryCalendarAction.startNewAppointment(this.date, this.sdlId, environment.calendarMenu.addCycleProcess)
    );


  }

  async addAppointmentNote() {
    /*
      if (this.note) {
        this.viewManager.setPopupViewId('agendaannotation-edit', this.note.internalId).then((aae: AgendaAnnotationEdit) =>
          aae.save.subscribe(() =>
            this.refresh(true)
          )
        );
      } else {*/

    this.viewManager.setPopupViewId('agendaannotation-edit', 'newemptyapp', this.sdlId, this.date.getTime().toString()).then(
        (aae: AgendaAnnotationEdit) => aae.save.subscribe(() => this.refresh(true))
    );
    // }

  }


  refresh(force = false): Promise<any> {
    return this.AmbulatoryCalendarAction.refreshAmbularyCalendar(force);
  }
  addIndirectProc() {
    this.viewManager.setPopupViewId('appointment', 'new', this.sdlId, this.date.getTime().toString(), 'planned', 'INDIRECT', 'false');
  }
  addActivity() {
    this.viewManager.setPopupViewId('appointment', 'new', this.sdlId, this.date.getTime().toString(), 'planned', 'GENERAL', 'false');
  }
  addActivityWithoutProcedure() { // KLINIK
    this.viewManager.setPopupViewId('appointment', 'new', this.sdlId, this.date.getTime().toString(), 'awaiting', 'NONE', 'false');
  }
  addCoordination() {
    this.viewManager.setPopupViewId('appointment', 'new', this.sdlId, this.date.getTime().toString(), 'planned', 'COORDINATION', 'false');
  }

  printColumn() {
    this.AmbulatoryCalendarAction.equal['reportingUdId'] = this.sdlId;
    this.AmbulatoryCalendarAction.equal['reportingDate'] = new Date(this.date);
    this.AmbulatoryCalendarAction.equal['reportingDate'].setHours(0, 0, 0, 0);

    this.AmbulatoryCalendarAction.printReport('swf/modules/ambulatoryCalendar/reports/dayAgendaReport.seam');
  }

}
