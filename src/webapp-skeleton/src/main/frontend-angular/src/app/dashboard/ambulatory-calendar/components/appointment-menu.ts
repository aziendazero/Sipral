import { Appointment } from "./../../../services/entities/base-entity/appointment";
import { PatientEncounterActionService } from "./../../../services/actions/patient-encounter-action.service";
import { Component, Injector, OnDestroy } from "@angular/core";
import { select } from "@angular-redux/store";
import { ActivatedRoute } from "@angular/router";
import { BaseForm } from "../../../widgets/form/base-form";
import { AppointmentActionService } from "../../../services/actions/appointment-action.service";
import { ViewManager } from "../../../services/view-manager";
import { AppointmentDelete } from "../../ambulatory-portal/components/appointment-delete";
import { AmbulatoryPortal } from "../../ambulatory-portal/ambulatory-portal";
import { AmbulatoryCalendarAction } from "../actions/ambulatory-calendar-action.service";
import { AmbulatoryPortalActionService } from "../../ambulatory-portal/actions/ambulatory-portal-action.service";
import { Datamodel } from "../../../services/datamodel/datamodel";
import { ServiceDeliveryLocation } from "../../../services/entities/role/service-delivery-location";
import { Details } from "../../ambulatory-portal/components/details";
import { ProcessActions } from "../../../actions/process.actions";
import { TranslateService } from "@ngx-translate/core";
import { logError } from "../../../services/error/global-error-handler";
import { environment } from "../../../../environments/environment";

@Component({
  selector: "phi-appointment-menu",
  templateUrl: "./appointment-menu.html",
})
export class AppointmentMenu extends BaseForm implements OnDestroy {
  @select(["conversation", "ServiceDeliveryLocationList"])
  serviceDeliveryLocationList$;
  serviceDeliveryLocationListSub;
  ServiceDeliveryLocationList: Datamodel;

  appointmentId: number;
  externalId?: string;
  appointmentStatus: string;
  grouperId: number;
  grouperStatus: string;
  classCode: string;
  sdlId: number;
  encounterId: number;
  editMode: boolean = true;
  hasPatient: boolean = false;
  isAuthorized;
  sdlWaitingList: boolean;
  sdlCupOrCode: boolean;
  isPsychoOncology;
  isOncology;
  hasReport = false;
  private isCreatedByPHIESB = false;

  @select(["config", "param"]) param$;
  Param: Map<string, any>;

  environment = environment;

  patientFromConversation = false;

  constructor(
    injector: Injector,
    private processActions: ProcessActions,
    private route: ActivatedRoute,
    public AppointmentAction: AppointmentActionService,
    public PatientEncounterAction: PatientEncounterActionService,
    private viewManager: ViewManager,
    public AmbulatoryCalendarAction: AmbulatoryCalendarAction,
    private ambulatoryPortalAction: AmbulatoryPortalActionService,
    private translateService: TranslateService
  ) {
    super(injector);

    this.appointmentId = +this.route.snapshot.params["appointment"];
    this.appointmentStatus = this.route.snapshot.params["status"];
    this.externalId = this.route.snapshot.params["externalId"];
    this.grouperStatus = this.route.snapshot.params["grouperStatus"];
    this.grouperId = this.route.snapshot.params["grouperId"];
    if (this.grouperStatus === "none") {
      this.grouperStatus = null;
    }
    this.sdlId = +this.route.snapshot.params["sdlId"];
    this.encounterId = +this.route.snapshot.params["encounterId"];
    this.editMode = this.route.snapshot.params["edit"] === "true";
    this.hasPatient = this.route.snapshot.params["hasPatient"] === "true";
    this.hasReport = this.route.snapshot.params["hasReport"] === "true";
    this.classCode = this.route.snapshot.params["classCode"];
    this.patientFromConversation =
      this.route.snapshot.params["patientFromConversation"] === "true";

    this.serviceDeliveryLocationListSub =
      this.serviceDeliveryLocationList$.subscribe((res) => {
        this.ServiceDeliveryLocationList = res;
        if (
          this.ServiceDeliveryLocationList &&
          this.ServiceDeliveryLocationList.entities
        ) {
          const sdl: ServiceDeliveryLocation =
            this.ServiceDeliveryLocationList.entities.find(
              (sdLoc: ServiceDeliveryLocation) =>
                sdLoc.internalId === this.sdlId
            );
          if (!sdl) {
            logError(
              "Agenda with id " +
                this.sdlId +
                " not found in user enabled agendas " +
                this.ServiceDeliveryLocationList.entities.map(
                  (a) => a.internalId
                ),
              this.AppointmentAction.cid
            );
          } else {
            this.isOncology = this.AmbulatoryCalendarAction.isOncology(sdl);
            this.sdlWaitingList = sdl.waitingListSupported === true;
            this.isAuthorized = this.AmbulatoryCalendarAction.isAuthorized(sdl);
            this.sdlCupOrCode =
              !this.AmbulatoryCalendarAction.isAuthorized(sdl);
            this.isPsychoOncology =
              this.AmbulatoryCalendarAction.isPsychoOncology(sdl);
          }
        }
      });
    this.param$.subscribe((res) => (this.Param = res));
    this.AppointmentAction.getDetails(this.appointmentId).then((res) => {
      this.isCreatedByPHIESB =
        res && res.appointment &&
        !!res.appointment.length &&
        res.appointment[0].created != null &&
        res.appointment[0].created.by === "PHIESB";
    });
  }
  isCupCtrl() {
    if (!environment.ambulatoryCalendar.rootCtrl) {
      return this.externalId !== null && this.externalId !== "undefined";
    }
  }

  isPlanned() {
    return (
      this.appointmentStatus === "planned" ||
      this.appointmentStatus === "awaiting"
    ); // KLINIK
  }

  isAwaiting() {
    return this.appointmentStatus === "awaiting";
  }

  isCancelled(): boolean {
    return this.appointmentStatus === "cancelled";
  }

  isPlannedOrArrived(): boolean {
    return (
      this.appointmentStatus === "planned" ||
      this.appointmentStatus === "awaiting" ||
      this.appointmentStatus === "arrived"
    ); // KLINIK
  }

  isArrivedOrActive(): boolean {
    return (
      this.appointmentStatus === "arrived" ||
      this.appointmentStatus === "active"
    );
  }

  isAwaitingArrivedOrActive(): boolean {
    return (
      this.appointmentStatus === "awaiting" ||
      this.appointmentStatus === "arrived" ||
      this.appointmentStatus === "active"
    );
  }

  isErogated(): boolean {
    return (
      this.appointmentStatus === "completed" ||
      this.appointmentStatus === "reported"
    );
  }

  isActive(): boolean {
    return this.appointmentStatus === "active";
  }

  isGrouperNewOrCompleted(): boolean {
    return this.grouperStatus === "new" || this.grouperStatus === "completed";
  }

  isGrouperPlanned(): boolean {
    return this.grouperStatus === "planned";
  }

  isGrouperActive(): boolean {
    return this.grouperStatus === "active";
  }

  hasClassCode(): boolean {
    return environment.appointmentMenu.fakeClassCode || this.classCode !== "";
  }

  isDeleteDisabled() {
    let isDisabled = this.sdlCupOrCode;
    if (environment.appointmentMenu.disableDeleteByRole) {
      // VCO
      isDisabled =
        isDisabled ||
        (!this.isPlannedOrArrived() &&
          !this.AmbulatoryCalendarAction.isObstetrician() &&
          !(
            this.AmbulatoryCalendarAction.isPsychologist() &&
            this.isPsychoOncology
          )) ||
        // H00652004 ~ do not allow medics and directors to cancel an appointment if it came from CUP / PHIESB
        (this.AmbulatoryCalendarAction.isDirecMedic() &&
          this.isCreatedByPHIESB === true) ||
        // END H00652004
        this.AmbulatoryCalendarAction.isReadOnly();
    } else {
      // OTHERS
      isDisabled =
        isDisabled || !this.isPlannedOrArrived() || this.isReported();
    }
    return isDisabled;
  }

  isReported() {
    if (environment.appointmentMenu.reportedCheck) {
      // KLINIK - OMEGA
      return this.hasReport;
    } else {
      // OTHERS
      return false;
    }
  }

  showDetails() {
    if (!this.environment.appointmentMenu.showDetailsProcess) {
      this.viewManager
        .setPopupViewId("details", this.appointmentId.toString())
        .then((det: Details) =>
          det.save.subscribe(() => {
            this.refreshForm();
          })
        );
    } else {
      this.AppointmentAction.injectAll(this.appointmentId).then(
        (app: Appointment) => {
          if (app.patient) {
            this.viewManager
              .setPopupViewId(null)
              .then(() =>
                this.processActions.startProcess(
                  this.environment.appointmentMenu.showDetailsProcess
                )
              );
          } else {
            this.viewManager.setPopupViewId(
              "appointment-edit",
              this.appointmentId.toString(),
              "false"
            );
          }
        }
      );
    }
  }

  select() {
    this.AppointmentAction.injectAll(this.appointmentId).then(() => {
      this.viewManager.setPopupViewId(null);
    });
  }

  edit() {
    return this.AppointmentAction.getLockedStatus(this.appointmentId).then(
      (res: boolean) => {
        if (res) {
          return;
        } else {
          this.viewManager.setPopupViewId(
            "appointment-edit",
            this.appointmentId.toString(),
            this.patientFromConversation ? "true" : "false"
          );
        }
      }
    );
  }

  copy() {
    this.AmbulatoryCalendarAction.copyCalendarItem(
      this.appointmentId,
      "Appointment"
    );
    this.viewManager.setPopupViewId(null);
  }

  move() {
    return this.AppointmentAction.getLockedStatus(this.appointmentId).then(
      (res: boolean) => {
        if (res) {
          return;
        } else {
          this.AmbulatoryCalendarAction.moveCalendarItem(
            this.appointmentId,
            "Appointment"
          );
          this.viewManager.setPopupViewId(null);
        }
      }
    );
  }

  endCycle() {}

  arrived() {
    return this.AppointmentAction.getLockedStatus(this.appointmentId).then(
      (res: boolean) => {
        if (res) {
          return;
        } else {
          if (!this.environment.appointmentMenu.arrivedProcess) {
            this.AppointmentAction.changeStatus(
              this.appointmentId,
              "confirm"
            ).then(() => {
              this.viewManager.setPopupViewId(null);
              this.refreshForm();
            });
          } else {
            this.injectAndStartProcess(
              this.environment.appointmentMenu.arrivedProcess
            );
          }
        }
      }
    );
  }

  takeInCharge() {
    return this.AppointmentAction.getLockedStatus(this.appointmentId).then(
      (res: boolean) => {
        if (res) {
          return;
        } else {
          this.ambulatoryPortalAction
            .changeStatus(
              PatientEncounterActionService.ACTION_ACTIVE,
              this.encounterId,
              null,
              null,
              this.appointmentId
            )
            .then(() => {
              this.viewManager.setPopupViewId(null);
              this.refreshForm();
            });
        }
      }
    );
  }

  outpatientSummary() {
    this.injectAndStartProcess("MOD_Accounting/CORE/PROCESSES/Policy Mngt");
  }

  registry() {
    this.injectAndStartProcess("MOD_Patient/CORE/PROCESSES/View");
  }

  medicalHistory() {
    this.injectAndStartProcess(
      "MOD_Clinical_Document/CORE/PROCESSES/Create report during admission"
    );
  }

  history() {
    this.injectAndStartProcess(
      "MOD_Outpatients/CORE/PROCESSES/Appointment/HistoryAppointment"
    );
  }
  episodeList() {
    this.injectAndStartProcess(
      "MOD_Clinical_Document/CORE/PROCESSES/Search reports"
    );
  }

  invoice() {
    this.injectAndStartProcess(
      "MOD_Accounting/CORE/PROCESSES/Invoicing/Search Invoice"
    );
  }

  note() {
    this.injectAndStartProcess(
      "MOD_Outpatients/CORE/PROCESSES/Appointment/Search from enc"
    );
  }

  procedure() {
    this.injectAndStartProcess(
      "MOD_Outpatients/CORE/PROCESSES/Ambulatory Enc/Amb Encounter Active"
    );
  }

  erogate() {
    return this.AppointmentAction.getLockedStatus(this.appointmentId).then(
      (res: boolean) => {
        if (res) {
          return;
        } else {
          if (this.sdlWaitingList) {
            this.injectAndStartProcess(
              "MOD_Outpatients/customer_VCO/PROCESSES/AppointmentListForComplete"
            );
          } else {
            this.AppointmentAction.changeStatus(
              this.appointmentId,
              "complete"
            ).then(() => {
              this.viewManager.setPopupViewId(null);
              this.refreshForm();
            });
          }
        }
      }
    );
  }

  report() {
    if (environment.ambulatoryPortal.dblClickProcess) {
      this.injectAndStartProcess(environment.ambulatoryPortal.dblClickProcess);
    } else {
      this.injectAndStartProcess(
        this.Param["p.dashboard.appointment.process"].value
      );
    }
  }

  delete() {
    return this.AppointmentAction.getLockedStatus(this.appointmentId).then(
      (res: boolean) => {
        if (res) {
          return;
        } else {
          let grouperId = "none";
          if (environment.ambulatoryPortal.useGrouperId) {
            grouperId = this.grouperId.toString();
          }
          this.viewManager
            .setPopupViewId(
              "appointment-delete",
              this.appointmentId.toString(),
              "cancel",
              grouperId
            )
            .then((appDelete: AppointmentDelete) =>
              appDelete.delete.subscribe(() => {
                this.viewManager.setPopupViewId(null);
                this.refreshForm();
              })
            );
        }
      }
    );
  }

  reopen() {
    this.injectAndStartProcess(
      "MOD_Outpatients/CORE/PROCESSES/Ambulatory Enc/Amb Encounter Reopen"
    );
  }

  reputInWl() {
    this.viewManager.openAlertMessage(
      this.translateService.instant("Confirm"),
      this.translateService.instant("Are_you_sure_to_reput_in_wl"),
      true,
      true,
      "OK",
      "Cancel",
      true,
      () => {
        this.viewManager
          .setPopupViewId(
            "appointment-delete",
            this.appointmentId.toString(),
            "cancelReactivate",
            "none"
          )
          .then((appDelete: AppointmentDelete) =>
            appDelete.delete.subscribe(() => {
              this.viewManager.setPopupViewId(null);
              this.refreshForm();
            })
          );
      }
    );
  }

  cancel() {
    return this.AppointmentAction.getLockedStatus(this.appointmentId).then(
      (res: boolean) => {
        if (res) {
          return;
        } else {
          // if (this.Param['p.dashboard.appointment.nullifypopup'].value) { TODO: CUSTOMIZE FOR CUSTOMER!!!
          if (this.sdlWaitingList) {
            this.injectAndStartProcess(
              "MOD_Outpatients/customer_VCO/PROCESSES/AppointmentListForNullify"
            );
          } else {
            this.viewManager
              .setPopupViewId(
                "appointment-delete",
                this.appointmentId.toString(),
                "nullify",
                "none"
              )
              .then((appDelete: AppointmentDelete) =>
                appDelete.delete.subscribe(() => {
                  this.viewManager.setPopupViewId(null);
                  this.refreshForm();
                })
              );
          }
          /*} else {

        }*/
        }
      }
    );
  }

  generateReport() {
    if (this.sdlWaitingList) {
      this.injectAndStartProcess(
        "MOD_Outpatients/customer_VCO/PROCESSES/AppointmentListGenerate"
      );
    }
  }

  refreshForm() {
    if (this.viewManager.formComponent instanceof AmbulatoryPortal) {
      this.viewManager.formComponent.refresh();
    } else {
      this.AmbulatoryCalendarAction.refreshAmbularyCalendar(true);
    }
  }

  private async injectAndStartProcess(process: string) {
    if (this.encounterId) {
      await this.PatientEncounterAction.inject(this.encounterId);
    }
    if (this.appointmentId) {
      await this.AppointmentAction.injectAll(this.appointmentId);
    }
    await this.viewManager.setPopupViewId(null);
    await this.processActions.startProcess(process);
  }

  ngOnDestroy() {
    this.serviceDeliveryLocationListSub.unsubscribe();
  }
}
