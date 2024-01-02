import { Component, Inject, Injector, OnDestroy, OnInit } from "@angular/core";
import { Router } from "@angular/router";
import { NgRedux, select } from "@angular-redux/store";
import { IAppState } from "../../store/index";
import { Subscription } from "rxjs/Subscription";
import { BaseForm } from "../../widgets/form/base-form";
import {
  AppointmentActionService,
  EmployeeActionService,
} from "../../services/actions";
import { Appointment } from "../../services/entities/base-entity";
import { Config } from "../../store/config.reducer";
import { Datamodel } from "../../services/datamodel/datamodel";
import { ConversationActions } from "../../actions/conversation.actions";
import { CodeValue } from "../../services/entities/data-types/code-value";
import { DictionaryManager } from "../../services/dictionary-manager";
import { ViewManager } from "../../services/view-manager";
import { TranslateService } from "@ngx-translate/core";
import { ConfigActions } from "../../actions/config.action";
import { SelectItem } from "../../services/datamodel/select-item";
import { AmbulatoryReportActionService } from "../../services/actions/ambulatory-report-action.service";
import { AmbulatoryDiaryActionService } from "../../services/actions/ambulatory-diary-action.service";
import { environment } from "../../../environments/environment";
import { AmbulatoryPortalActionService } from "./actions/ambulatory-portal-action.service";
import { PatientEncounterActionService } from "../../services/actions/patient-encounter-action.service";
import { PatientEncounter } from "../../services/entities/act/patient-encounter";
import { EmployeeRoleActionService } from "../../services/actions/employee-role-action.service";
import { ProcessActions } from "../../actions/process.actions";
import { color2hex } from "../../services/converters/any.converter";
import { Employee } from "app/services/entities/role";

@Component({
  selector: "phi-ambulatory-portal",
  templateUrl: "./ambulatory-portal.html",
  styleUrls: ["./ambulatory-portal.scss"],
})
export class AmbulatoryPortal extends BaseForm implements OnInit, OnDestroy {
  @select(["conversation"]) conversation$;
  conversationSub: Subscription;

  Appointment: Appointment;
  PatientEncounter: PatientEncounter;
  AmbulatoryAppointmentList: Datamodel;
  AmbulatoryAppointmentInChargeList: Datamodel;
  PatientAppointmentList: Datamodel;

  AmbulatoryReportList: Datamodel;
  AmbulatoryDiaryList: Datamodel;

  surname = "";
  name = "";
  selectedModality = null;
  selectedEmployee: SelectItem = null;
  employees: Array<SelectItem> = [];

  selectedDate: Date;
  selectedDateEnd: Date;
  selectedStatuses: Array<CodeValue>;
  type: "all" | "cup" | "consulence";
  previous: boolean;

  @select(["config"]) config$;
  configSub: Subscription;
  config: Config;
  sdLocs: Array<any>;

  @select(["config", "employeeRoleId"]) employeeRoleId$;
  employeeRoleId: number;

  searchVisible: boolean;
  detailsVisible: boolean;

  appointmentStatuses: Array<CodeValue>;

  showAmbulatoryReport = false;
  showAmbulatoryDiary = false;

  sentBy: Array<CodeValue>;
  selectedSentBy: Array<CodeValue>;

  environment = environment;

  types: Array<SelectItem> = [
    { label: this.translateService.instant("all"), value: "all" },
    { label: "CUP", value: "cup" },
    { label: this.translateService.instant("advice"), value: "consulence" },
  ];

  timer;
  delay = 500;
  prevent = false;

  @select(["process", "cid"]) cid$;
  cid: any;

  @select(["conversation", "Appointment"]) Appointment$;
  appInConvSub;
  AppInConv: Appointment;

  constructor(
    injector: Injector,
    @Inject("apiUrl") private url,
    private router: Router,
    public ambulatoryPortalAction: AmbulatoryPortalActionService,
    public AppointmentAction: AppointmentActionService,
    public AmbulatoryReportAction: AmbulatoryReportActionService,
    public AmbulatoryDiaryAction: AmbulatoryDiaryActionService,
    private patientEncounterAction: PatientEncounterActionService,
    private employeeAction: EmployeeActionService,
    private employeeRoleAction: EmployeeRoleActionService,
    public dictionaryManager: DictionaryManager,
    private conversationActions: ConversationActions,
    private configActions: ConfigActions,
    private viewManager: ViewManager,
    private translateService: TranslateService,
    private processActions: ProcessActions,
    private ngRedux: NgRedux<IAppState>
  ) {
    super(injector);

    this.conversationSub = this.conversation$.subscribe((res) => {
      this.Appointment = res.Appointment;
      this.PatientEncounter = res.PatientEncounter;
      this.AmbulatoryAppointmentList = res.AmbulatoryAppointmentList;
      this.AmbulatoryAppointmentInChargeList =
        res.AmbulatoryAppointmentInChargeList;
      this.PatientAppointmentList = res.PatientAppointmentList;

      this.AmbulatoryReportList = res.AmbulatoryReportList;
      this.AmbulatoryDiaryList = res.AmbulatoryDiaryList;

      this.selectedStatuses = res.ambulatoryPortalStatuses;
      this.selectedSentBy = res.ambulatoryPortalSentBy;
      this.type = res.ambulatoryPortalType || "all";
      this.previous = res.ambulatoryPortalPrevious || false;
    });

    this.cid$.subscribe((res) => (this.cid = res));

    this.configSub = this.config$.subscribe((cfg: Config) => {
      this.config = cfg;
      this.sdLocs = this.config.sdLocs;
      this.employeeRoleId$.subscribe((res) => (this.employeeRoleId = res));

      if (cfg.param) {
        if (cfg.param["p.dashboard.ambulatoryportal.showAmbulatoryReport"]) {
          this.showAmbulatoryReport =
            cfg.param[
              "p.dashboard.ambulatoryportal.showAmbulatoryReport"
            ].value;
        } else {
          this.showAmbulatoryReport = false;
        }
        if (cfg.param["p.dashboard.ambulatoryportal.showAmbulatoryDiary"]) {
          this.showAmbulatoryDiary =
            cfg.param["p.dashboard.ambulatoryportal.showAmbulatoryDiary"].value;
        } else {
          this.showAmbulatoryDiary = false;
        }
      }
    });
    if (environment.adt.bracialetReport) {
      this.appInConvSub = this.Appointment$.subscribe((res) => {
        this.AppInConv = res;
      });
    }
  }

  async ngOnInit() {
    const conversation = this.ngRedux.getState().conversation;

    this.selectedDate = conversation.ambulatoryPortalDate;
    this.selectedDateEnd = conversation.ambulatoryPortalDateEnd;
    this.detailsVisible = conversation.ambulatoryPortalDetailsVisible;

    if (!this.selectedDate) {
      this.selectedDate = new Date();
      this.selectedDate.setHours(0);
      this.selectedDate.setMinutes(0);
      this.selectedDate.setSeconds(0);
      this.conversationActions.put("ambulatoryPortalDate", this.selectedDate);
    }
    if (!this.selectedDateEnd) {
      this.selectedDateEnd = new Date(this.selectedDate.getTime());
      this.conversationActions.put(
        "ambulatoryPortalDateEnd",
        this.selectedDateEnd
      );
    }

    this.employees = await this.employeeAction.getByEmployeeRoleCode("11");

    const domainPromises: Array<Promise<Array<CodeValue>>> = [];

    domainPromises.push(
      this.dictionaryManager.get(environment.ambulatoryPortal.status)
    );

    if (environment.ambulatoryPortal.sentBy) {
      domainPromises.push(
        this.dictionaryManager.get(environment.ambulatoryPortal.sentBy)
      );
    }

    Promise.all(domainPromises).then(async ([statusDomain, sentByDomain]) => {
      this.appointmentStatuses = statusDomain;

      if (!this.selectedStatuses) {
        this.statusChanged(
          statusDomain.filter(
            (cv: CodeValue) =>
              environment.ambulatoryPortal.selectedStatuses.indexOf(cv.code) !==
              -1
          )
        );
      }

      this.sentBy = sentByDomain;

      if (!this.selectedSentBy) {
        this.sentByChanged(sentByDomain);
      }

      // Filter sdlocy by employeeRole.favoriteSdl
      if (environment.ambulatoryCalendar.saveFavoriteSdl) {
        const favoriteSdlocIds: Array<number> =
          await this.employeeRoleAction.getFavoriteSdl();
        this.sdLocs = this.config.sdLocs.filter(
          (sdLoc) => favoriteSdlocIds.indexOf(+sdLoc.id) !== -1
        );
      }

      this.refresh();
    });

    this.configActions.put("isPortal", true);
  }

  ngOnDestroy() {
    this.conversationSub.unsubscribe();
    this.configSub.unsubscribe();

    this.conversationActions.remove("AmbulatoryAppointmentList");
    this.conversationActions.remove("AppointmentList");
    this.conversationActions.remove("PatientAppointmentList");

    this.configActions.remove("isPortal");
    if (environment.adt.bracialetReport) {
      this.appInConvSub.unsubscribe();
    }
  }

  refresh() {
    this.conversationActions.put("ambulatoryPortalDate", this.selectedDate);
    this.conversationActions.put(
      "ambulatoryPortalDateEnd",
      this.selectedDateEnd
    );
    if (!this.selectedStatuses || this.selectedStatuses.length === 0) {
      this.viewManager.openAlertMessage(
        this.translateService.instant("Warning"),
        this.translateService.instant("at_least_one_status_must_be_selected"),
        false
      );
    } else if (!this.sdLocs || this.sdLocs.length === 0) {
      this.viewManager.openAlertMessage(
        this.translateService.instant("Warning"),
        this.translateService.instant(
          "at_least_one_clinic_department_must_be_selected"
        ),
        false
      );
    } else {
      this.conversationActions.remove("AmbulatoryAppointmentList");
      this.conversationActions.remove("PatientAppointmentList");
      if (!environment.ambulatoryPortal.useRead) {
        // VCO
        this.AppointmentAction.getAmbulatoryAppointments(
          this.selectedDate,
          this.selectedStatuses,
          this.sdLocs,
          null,
          true,
          this.type !== "all" ? this.type : null,
          this.type === "consulence" && this.previous ? 30 : null
        );
        this.searchVisible = false;
      } else {
        // KLINIK
        this.ambulatoryPortalAction.refreshBy(
          this.name,
          this.surname,
          this.sdLocs,
          this.selectedDate,
          this.selectedDateEnd,
          this.selectedSentBy,
          this.selectedStatuses,
          this.selectedModality,
          this.selectedEmployee
        );
      }
    }
  }

  ie(entity, conversationName): Promise<any> {
    if (this.environment.ambulatoryPortal.dblClickProcess) {
      this.timer = setTimeout(() => {
        if (!this.prevent) {
          return this.rowClick(entity);
        }
        this.prevent = false;
      }, this.delay);
    } else {
      return this.rowClick(entity);
    }
  }

  rowClick(entity, showMenu = true): Promise<any> {
    if (!this.detailsVisible) {
      this.toggleDetails();
    }
    if (!environment.ambulatoryPortal.useRead) {
      // VCO
      if (
        !this.Appointment ||
        this.Appointment.internalId !== entity.internalid
      ) {
        return this.AppointmentAction.injectAll(entity.internalid).then(
          (app: Appointment) => {
            const promises: Array<Promise<any>> = [];
            promises.push(
              this.AppointmentAction.getPatientAppointments(
                app.patient.internalId,
                null,
                [app.serviceDeliveryLocation.internalId]
              )
            );

            if (this.showAmbulatoryReport) {
              promises.push(
                this.AmbulatoryReportAction.getByPatient(
                  app.patient.internalId,
                  app.serviceDeliveryLocation.internalId
                )
              );
            }

            if (this.showAmbulatoryDiary) {
              promises.push(
                this.AmbulatoryDiaryAction.getByPatient(
                  app.patient.internalId,
                  app.serviceDeliveryLocation.internalId
                )
              );
            }

            return Promise.all(promises);
          }
        );
      } else {
        if (showMenu) {
          this.viewManager.setPopupViewId(
            "appointment-menu",
            entity.internalid,
            this.Appointment.externalId,
            entity.statuscode.code,
            entity.appointmentgrouper.statuscode || "none",
            entity.sdl.internalid,
            "none",
            "false",
            "true",
            "none",
            "false",
            entity.proc.classcode && entity.proc.classcode.code
              ? entity.procedure.classCode.code
              : undefined,
            "false"
          );
        }
        return Promise.resolve(this.Appointment);
      }
    } else {
      // KLINIK
      if (!super.selected(entity, "PatientEncounter")) {
        return this.patientEncounterAction
          .inject(
            entity.internalid /*entity.encounterId*/,
            ["history", "procedures"],
            ["patient.language"]
          )
          .then(() =>
            this.conversationActions.put("Appointment", {
              internalId: entity.appointment.internalid,
            })
          );
      } else {
        if (showMenu) {
          this.viewManager.setPopupViewId(
            "appointment-menu",
            entity.appointment.internalid ? entity.appointment.internalid : 0,
            entity.appointment.externalId,
            entity.statuscode.code === "new"
              ? "awaiting"
              : entity.statuscode.code,
            "none",
            entity.sdl.id,
            entity.internalid,
            "false",
            "true",
            "none",
            entity.report.internalid ? "true" : "false",
            undefined,
            "false"
          );
        }
        return Promise.resolve(this.Appointment);
      }
    }
  }

  async rowDblClick(appointmentRow, changeStatus = false) {
    if (this.environment.ambulatoryPortal.dblClickProcess) {
      // KLINIK

      clearTimeout(this.timer);
      this.prevent = true;

      if (changeStatus) {
        await this.ambulatoryPortalAction.changeStatus(
          PatientEncounterActionService.ACTION_ACTIVE,
          appointmentRow.internalid,
          null,
          null,
          appointmentRow.appointment.internalid
            ? appointmentRow.appointment.internalid
            : 0
        );
      }
      await this.patientEncounterAction.inject(
        appointmentRow.internalid,
        ["history", "procedures"],
        ["patient.language"]
      );
      if (
        appointmentRow.appointment &&
        appointmentRow.appointment.internalid > 0
      ) {
        await this.AppointmentAction.injectAll(
          appointmentRow.appointment.internalid
        );
      }
      await this.startProcess(
        this.environment.ambulatoryPortal.dblClickProcess
      );
    }
  }

  selected(row, conversationName) {
    if (!environment.ambulatoryPortal.useRead) {
      // VCO
      return super.selected(row, conversationName);
    } else {
      // KLINIK
      return super.selected(row.appointment, conversationName);
    }
  }

  prevDay() {
    if (this.selectedDate) {
      this.selectedDate = new Date(
        this.selectedDate.setDate(this.selectedDate.getDate() - 1)
      );
      this.selectedDateEnd = new Date(
        this.selectedDateEnd.setDate(this.selectedDateEnd.getDate() - 1)
      );
      this.refresh();
    }
  }

  nextDay() {
    if (this.selectedDate) {
      this.selectedDate = new Date(
        this.selectedDate.setDate(this.selectedDate.getDate() + 1)
      );
      this.selectedDateEnd = new Date(
        this.selectedDateEnd.setDate(this.selectedDateEnd.getDate() + 1)
      );
      this.refresh();
    }
  }

  startProcess(process: string) {
    this.processActions.startProcess(process);
  }

  toggleSearch() {
    this.searchVisible = !this.searchVisible;
  }

  toggleDetails() {
    this.detailsVisible = !this.detailsVisible;
    this.conversationActions.put(
      "ambulatoryPortalDetailsVisible",
      this.detailsVisible
    );
  }

  sdLocsChanged(selectedSdLocs: Array<any>) {
    if (environment.ambulatoryCalendar.saveFavoriteSdl) {
      if (selectedSdLocs && selectedSdLocs.length > 0) {
        this.employeeRoleAction.saveFavoriteSdl(
          selectedSdLocs.map((sdl) => +sdl.id),
          false
        ); // KLINIK
      }
    }
  }

  statusChanged(selectedStatuses: Array<CodeValue>) {
    this.conversationActions.put("ambulatoryPortalStatuses", selectedStatuses);
  }

  sentByChanged(selectedSentBy: Array<CodeValue>) {
    this.conversationActions.put("ambulatoryPortalSentBy", selectedSentBy);
  }

  typeChanged(type: string) {
    this.conversationActions.put("ambulatoryPortalType", type);
  }

  previousChanged(previous: boolean) {
    this.conversationActions.put("ambulatoryPortalPrevious", previous);
  }

  delayClass(appointmentDate: Date, arrivalDate: Date): string {
    if (appointmentDate && arrivalDate) {
      const yellowDelay = 1200000; // 20 min in ms
      const redDelay = 2700000; // 45 min in ms

      let dateToCheck: Date = arrivalDate;

      if (appointmentDate.getTime() > arrivalDate.getTime()) {
        dateToCheck = appointmentDate;
      }

      const now = new Date();

      if (now.getTime() > dateToCheck.getTime() + redDelay) {
        return "date-red";
      } else if (now.getTime() > dateToCheck.getTime() + yellowDelay) {
        return "date-orange";
      }
      return "";
    }
  }

  dateClass(appointmentRow): string {
    if (appointmentRow && appointmentRow.sdl && appointmentRow.sdl.parent) {
      const area = appointmentRow.sdl.parent.area;
      if (
        area === "C2" ||
        area === "C2ANESTHESIA" ||
        area === "C2ENDOSCOPY" ||
        area === "SWITCHC2"
      ) {
        return "date-red";
      }
    }
  }

  getStatus(code: string) {
    return this.translateService.instant("app-" + code);
  }

  getLocationColor(appointmentRow): string {
    if (appointmentRow.locationcolor) {
      return color2hex(appointmentRow.locationcolor.toString());
    }
  }

  printReport() {
    const url =
      this.url + "/swf/modules/ADT/reports/r_laserBand.seam?cid=" + this.cid;
    this.viewManager.openIframe(url);
  }
}
