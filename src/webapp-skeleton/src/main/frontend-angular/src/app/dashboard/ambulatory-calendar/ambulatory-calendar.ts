import { InternalActivityActionService } from "./../../services/actions/internal-activity-action.service";
import { AgendaAnnotation } from "./../../services/entities/act/agenda-annotation";
import { AgendaAnnotationActionService } from "./../../services/actions/agenda-annotation-action.service";
import { CalendarItem } from "./model/calendar-item";
import {
  AfterViewInit,
  Component,
  ElementRef,
  Inject,
  Injector,
  Input,
  OnDestroy,
  OnInit,
  ViewChild,
} from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { Location } from "@angular/common";
import { select } from "@angular-redux/store";
import { BaseForm } from "../../widgets/form/base-form";
import { AppointmentActionService } from "../../services/actions";
import { ConversationActions } from "../../actions/conversation.actions";
import { Appointment } from "../../services/entities/base-entity/appointment";
import { ViewManager } from "../../services/view-manager";
import { color2hex } from "../../services/converters/any.converter";
import { DateFormatPipe } from "../../services/converters/date-format.pipe";
import { DictionaryManager } from "../../services/dictionary-manager";
import { CodeValuePhi } from "../../services/entities/data-types/code-value-phi";
import { PatientSearch } from "./components/patient-search";
import { Patient } from "../../services/entities/role/patient";
import { ServicedeliverylocationSearch } from "./components/servicedeliverylocation-search";
import { ServiceDeliveryLocationActionService } from "../../services/actions/service-delivery-location-action.service";
import { ServiceDeliveryLocation } from "../../services/entities/role/service-delivery-location";
import { AppointmentList } from "../ambulatory-portal/components/appointment-list";
import { AmbulatoryCalendarAction } from "./actions/ambulatory-calendar-action.service";
import { AgendaAnnotationEdit } from "./components/agendaannotation-edit";
import { TranslateService } from "@ngx-translate/core";
import { ConfigActions } from "../../actions/config.action";
import { AppointmentGrouperActionService } from "../../services/actions/appointment-grouper-action.service";
import { AppointmentGrouper } from "../../services/entities/base-entity/appointment-grouper";
import { EmployeeRoleActionService } from "../../services/actions/employee-role-action.service";
import { environment } from "../../../environments/environment";
import { Procedure } from "../../services/entities/act/procedure";
import { ProcessActions } from "../../actions/process.actions";
import { SaveScrollOnDeactivate } from "app/services";
import { Datamodel } from "app/services/datamodel/datamodel";
import { Subscription } from "rxjs";
import { logError } from "../../services/error/global-error-handler";

@Component({
  selector: "phi-ambulatory-calendar",
  templateUrl: "./ambulatory-calendar.html",
  styleUrls: ["./ambulatory-calendar.scss"],
})
export class AmbulatoryCalendar
  extends BaseForm
  implements OnInit, AfterViewInit, OnDestroy, SaveScrollOnDeactivate
{
  private static heightPixelPerMinute =
    environment.ambulatoryCalendar.pixelPerMinute;
  public rowHeight =
    AmbulatoryCalendarAction.minutesPerRow *
    AmbulatoryCalendar.heightPixelPerMinute;

  highLightPatient: string;

  @ViewChild("calendar")
  protected calendar: ElementRef;

  @ViewChild("calendarElement")
  protected calendarElement: ElementRef;

  @ViewChild("appointmentElement")
  protected appointmentElement: ElementRef;

  @select(["conversation", "ServiceDeliveryLocationList"])
  public serviceDeliveryLocationList$;
  public serviceDeliveryLocationListSub: Subscription;
  public serviceDeliveryLocationList: Datamodel;

  @select(["conversation", "movingAppointment"]) movingAppointment$;
  movingAppointmentSub;
  movingAppointment: CalendarItem;

  @select(["conversation", "movingReason"]) movingReason$;
  movingReasonSub;
  movingReason: "INSERT" | "MOVE" | "COPY";

  @select(["conversation", "Appointment"]) Appointment$;
  appInConvSub;
  AppInConv: Appointment;

  scrollIdentifier;

  get scrollPosition() {
    return this.calendar.nativeElement.scrollTop;
  }

  @select(["conversation", "CalendarScrollPosition"]) scrollPosition$;
  scrollPositionSub;
  previousScrollPosition;

  currentCal;
  currentApp;

  environment = environment;

  selectedSdlIds: Array<number> = [];
  selectedSdl: Array<ServiceDeliveryLocation> = [];

  hiddenInlineCalendar = true;

  timer;
  delay = 500;
  prevent = false;

  editOnlyPatientId: number = null;

  @select(["process", "cid"]) cid$;
  cid: any;

  @Input()
  set patientFromConversation(val) {
    if (val === "true") {
      if (this.AppointmentAction.patient) {
        this.editOnlyPatientId = this.AppointmentAction.patient.internalId;
        this.highLightPatient = this.AppointmentAction.patient.name.fam;
      } else {
        throw new Error("No patient in conversation!");
      }
    }
  }
  constructor(
    injector: Injector,
    @Inject("apiUrl") private url,
    private route: ActivatedRoute,
    private location: Location,
    public ambulatoryCalendarAction: AmbulatoryCalendarAction,
    private AgendaAnnotationAction: AgendaAnnotationActionService,
    public AppointmentAction: AppointmentActionService,
    public AppointmentGrouperAction: AppointmentGrouperActionService,
    public ServiceDeliveryLocationAction: ServiceDeliveryLocationActionService,
    private employeeRoleAction: EmployeeRoleActionService,
    private conversationActions: ConversationActions,
    private configActions: ConfigActions,
    private viewManager: ViewManager,
    private processActions: ProcessActions,
    private dictionaryManager: DictionaryManager,
    private datePipe: DateFormatPipe,
    private internalActivityAction: InternalActivityActionService,
    private translateService: TranslateService
  ) {
    super(injector);

    this.scrollIdentifier = "Calendar";

    this.cid$.subscribe((res) => (this.cid = res));

    this.movingAppointmentSub = this.movingAppointment$.subscribe((res) => {
      this.movingAppointment = res;
      if (this.movingAppointment) {
        this.initMovingCalendarItem();
        if (this.movingReason === "MOVE") {
          const appElement: HTMLElement = document.getElementById(
            this.movingAppointment.internalId.toString()
          );
          if (appElement) {
            appElement.style.display = "none";
          }
        }
      }
    });

    this.movingReasonSub = this.movingReason$.subscribe((res) => {
      this.movingReason = res;
    });

    this.scrollPositionSub = this.scrollPosition$.subscribe((res) => {
      this.previousScrollPosition = res;
    });

    if (environment.adt.bracialetReport) {
      this.appInConvSub = this.Appointment$.subscribe((res) => {
        this.AppInConv = res;
      });
    }

    this.serviceDeliveryLocationListSub =
      this.serviceDeliveryLocationList$.subscribe((res) => {
        this.serviceDeliveryLocationList = res;
      });
  }

  ngOnInit() {
    this.configActions.put("isPortal", true);

    // if queryParam insert is present: coming from MOD_Outpatients/CORE/PROCESSES/NewAppointment.jpdl.xml process
    // witch creates an Appointment with insertCompleted = false.
    // Continue creation by selecting date and sdl
    const appIdToBeInserted: number =
      +this.route.snapshot.queryParams["insert"];

    // if queryParam insertFromGrouper is present: coming from MOD_Outpatients/CORE/PROCESSES/Waiting_List.jpdl.xml process
    // update AppointmentGrouper and create connected appointment
    const appGrouperIdToBeInserted: number =
      +this.route.snapshot.queryParams["insertFromGrouper"];

    if (appIdToBeInserted || appGrouperIdToBeInserted) {
      this.conversationActions.put("movingReason", "INSERT");
    }

    if (this.environment.ambulatoryCalendar.columns === 0) {
      this.ambulatoryCalendarAction.agendaColumns =
        this.howManyAgendasCanDisplay();
    }

    this.ambulatoryCalendarAction.initAmbularyCalendar().then(() => {
      if (appIdToBeInserted) {
        this.AppointmentAction.injectAll(appIdToBeInserted).then(
          (appToBeInserted: Appointment) => {
            this.conversationActions.put("movingAppointment", appToBeInserted);

            if (
              this.movingAppointment &&
              this.movingAppointment.serviceDeliveryLocation
            ) {
              this.ambulatoryCalendarAction.setUds(
                this.ServiceDeliveryLocationAction.ServiceDeliveryLocationList
                  .entities
              );
              this.ambulatoryCalendarAction.goToAgenda(
                this.movingAppointment.serviceDeliveryLocation
              );
            }
          }
        );
      } else if (appGrouperIdToBeInserted) {
        this.AppointmentGrouperAction.inject(appGrouperIdToBeInserted).then(
          (appGrouperToBeInserted: AppointmentGrouper) => {
            this.conversationActions.put(
              "movingAppointment",
              this.AppointmentAction.newAppointment(
                null,
                null,
                "MACROPROC",
                "planned",
                appGrouperToBeInserted
              )
            ); // levelcode 30min

            delete appGrouperToBeInserted["proxyString"];
            appGrouperToBeInserted.statusCode = {
              code: "planned",
              codeSystemName: "PHIDIC",
              domainName: "AppGroupStatus",
            };

            this.movingAppointment.procedure.code = appGrouperToBeInserted.code;
            this.movingAppointment.isIndirect = false;

            const agendaz: Array<ServiceDeliveryLocation> =
              this.ServiceDeliveryLocationAction.filterByParent(
                appGrouperToBeInserted.serviceDeliveryLocation.internalId
              );
            this.ambulatoryCalendarAction.setUds(agendaz);
            this.ambulatoryCalendarAction.refreshAmbularyCalendar(true);
          }
        );
      }
      if (this.calendar.nativeElement.scrollTo) {
        // IE11 doesn't support
        if (this.previousScrollPosition) {
          this.calendar.nativeElement.scrollTo({
            top: this.previousScrollPosition,
          });
        }
      }
    });
  }

  ngAfterViewInit() {
    if (this.movingReason === "INSERT") {
      this.initMovingCalendarItem();
    }
  }

  ngOnDestroy() {
    this.conversationActions.remove("AppointmentList");
    this.conversationActions.remove("AgendaAnnotationList");
    this.conversationActions.remove("ServiceDeliveryLocationList");
    this.configActions.remove("isPortal");

    this.movingAppointmentSub.unsubscribe();
    this.movingReasonSub.unsubscribe();
    this.scrollPositionSub.unsubscribe();
    if (environment.adt.bracialetReport) {
      this.appInConvSub.unsubscribe();
    }
  }

  refresh(force = false): Promise<any> {
    return this.ambulatoryCalendarAction.refreshAmbularyCalendar(force);
  }

  prevAgenda() {
    this.ambulatoryCalendarAction.prevAgenda();
  }

  nextAgenda() {
    this.ambulatoryCalendarAction.nextAgenda();
  }

  currDay() {
    if (this.ambulatoryCalendarAction.selectedDate) {
      const curr = new Date(
        this.ambulatoryCalendarAction.selectedDate.getTime()
      );
      curr.setHours(0, 0, 0, 0);
      this.conversationActions.put("selectedDate", curr);
      this.refresh();
    }
  }

  prevDay() {
    if (this.ambulatoryCalendarAction.selectedDate) {
      const offset = this.ambulatoryCalendarAction.weekViewOfSdl ? 7 : 1;
      const prev = new Date(
        this.ambulatoryCalendarAction.selectedDate.getTime()
      );
      prev.setDate(prev.getDate() - offset);
      this.conversationActions.put("selectedDate", prev);
      this.refresh();
    }
  }

  nextDay() {
    if (this.ambulatoryCalendarAction.selectedDate) {
      const offset = this.ambulatoryCalendarAction.weekViewOfSdl ? 7 : 1;
      const next = new Date(
        this.ambulatoryCalendarAction.selectedDate.getTime()
      );
      next.setDate(next.getDate() + offset);
      this.conversationActions.put("selectedDate", next);
      this.refresh();
    }
  }

  today() {
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    this.conversationActions.put("selectedDate", today);
    this.refresh();
  }

  touchStartX;
  touchStartY;
  touchStartTime;
  touchThreshold = 150; //required min distance traveled to be considered swipe
  touchAllowedTime = 200; // maximum time allowed to travel that distance

  touchstart(event: TouchEvent) {
    this.touchStartX = event.changedTouches[0].pageX;
    this.touchStartY = event.changedTouches[0].pageY;
    this.touchStartTime = new Date().getTime();
  }

  touchend(event: TouchEvent) {
    let distX = event.changedTouches[0].pageX - this.touchStartX; // dist traveled by finger while in contact with surface
    let distY = event.changedTouches[0].pageY - this.touchStartY;
    let elapsedTime = new Date().getTime() - this.touchStartTime; // get time elapsed

    if (elapsedTime <= this.touchAllowedTime) {
      if (Math.abs(distX) >= this.touchThreshold && Math.abs(distY) <= 100) {
        // horizontal swipe
        if (distX < 0) {
          // this.nextDay();
          this.nextAgenda();
        } else {
          // this.prevDay();
          this.prevAgenda();
        }
      }
    }
  }

  minAgendaColumnWidth = 190;

  resize(event: Event) {
    if (this.environment.ambulatoryCalendar.columns === 0) {
      const newNumberOfAgendas = this.howManyAgendasCanDisplay();
      const currentNumberOfAgendas =
        this.ambulatoryCalendarAction.agendaTo -
        this.ambulatoryCalendarAction.agendaFrom +
        1;

      if (newNumberOfAgendas !== currentNumberOfAgendas) {
        this.ambulatoryCalendarAction.agendaColumns = newNumberOfAgendas;
        let newAgendaTo =
          this.ambulatoryCalendarAction.agendaFrom + newNumberOfAgendas - 1;
        if (newAgendaTo <= this.ambulatoryCalendarAction.agendas.length - 1) {
          this.conversationActions.put("agendaTo", newAgendaTo);
        } else {
          let newAgendaFrom =
            this.ambulatoryCalendarAction.agendaTo - newNumberOfAgendas + 1;
          if (newAgendaFrom > 0) {
            this.conversationActions.put("agendaFrom", newAgendaFrom);
          }
        }
        this.ambulatoryCalendarAction.refreshAmbularyCalendar();
      }
    }
  }

  howManyAgendasCanDisplay(): number {
    const calendarWidth = window.innerWidth - 20; // this.calendar.nativeElement.offsetWidth;
    const newNumberOfAgendas =
      (calendarWidth - (calendarWidth % this.minAgendaColumnWidth)) /
      this.minAgendaColumnWidth;
    return newNumberOfAgendas;
  }

  printWorkList() {
    this.ambulatoryCalendarAction.printWorkList();

    // this.ambulatoryCalendarAction.cleanRestrictions();
    // this.ambulatoryCalendarAction.equal['worklist'] = true;
    // this.ambulatoryCalendarAction.equal['reportingDate'] = this.ambulatoryCalendarAction.selectedDate;
    // this.ambulatoryCalendarAction.printReport(environment.ambulatoryCalendar.report);
  }

  wardFilter() {
    this.viewManager
      .setPopupViewId("servicedeliverylocation-search")
      .then((sdlSrc: ServicedeliverylocationSearch) =>
        sdlSrc.select.subscribe((sdLocs: Array<ServiceDeliveryLocation>) => {
          if (environment.ambulatoryCalendar.saveFavoriteSdl) {
            this.employeeRoleAction.saveFavoriteSdl(
              sdLocs.map((sdl: ServiceDeliveryLocation) => sdl.internalId),
              true
            ); // KLINIK
          }
          this.ambulatoryCalendarAction.setUds(sdLocs);
          this.ambulatoryCalendarAction.refreshAmbularyCalendar(true);
        })
      );
  }

  isHighlighted(calItem: CalendarItem): boolean {
    if (
      this.highLightPatient &&
      calItem.patient &&
      calItem.patient.name &&
      calItem.patient.name.giv &&
      calItem.patient.name.fam
    ) {
      return (
        this.highLightPatient
          .toLowerCase()
          .indexOf(calItem.patient.name.giv.toLowerCase()) !== -1 ||
        this.highLightPatient
          .toLowerCase()
          .indexOf(calItem.patient.name.fam.toLowerCase()) !== -1
      );
    }
    return true;
  }

  showCancelled() {
    this.ambulatoryCalendarAction.showCancelled =
      !this.ambulatoryCalendarAction.showCancelled;
    this.refresh(true);
  }

  search() {
    this.viewManager
      .setPopupViewId("patient-search", "all")
      .then((patSrc: PatientSearch) =>
        patSrc.select.subscribe((pat: Patient) => {
          if (
            this.ServiceDeliveryLocationAction.ServiceDeliveryLocationList &&
            this.ServiceDeliveryLocationAction.ServiceDeliveryLocationList
              .entities
          ) {
            return this.AppointmentAction.getPatientAppointments(
              pat.internalId,
              null,
              this.ServiceDeliveryLocationAction.ServiceDeliveryLocationList.entities.map(
                (ud: ServiceDeliveryLocation) => ud.internalId
              ),
              "DESC"
            ).then(() => {
              this.viewManager
                .setPopupViewId("appointment-list")
                .then((appLst: AppointmentList) =>
                  appLst.select.subscribe((app: Appointment) => {
                    // Reset location filter to show appointments also of currently filtered sdl
                    this.ambulatoryCalendarAction.setUds(
                      this.ServiceDeliveryLocationAction
                        .ServiceDeliveryLocationList.entities
                    );

                    if (app.serviceDeliveryLocation) {
                      const appDay = new Date(app.defaultDate);
                      appDay.setHours(0, 0, 0, 0);
                      this.conversationActions.put("selectedDate", appDay);

                      this.ambulatoryCalendarAction.goToAgenda(
                        app.serviceDeliveryLocation
                      );
                    } else {
                      this.viewManager.openAlertMessage(
                        this.translateService.instant("Warning"),
                        "Attenzione, non sei autorizzato a vedere l agenda: " +
                          app.serviceDeliveryLocation.name.giv,
                        false
                      );
                    }
                  })
                );
            });
          }
        })
      );
  }

  onChangeSdl(event) {
    const id = +event.target.value;
    let currentSdl = this.ambulatoryCalendarAction.list.headers.find(
      (hdr) => hdr.id === id
    );
    if (event.target.checked) {
      this.selectedSdlIds.push(id);
      this.selectedSdl.push(currentSdl);
    } else {
      this.selectedSdlIds.splice(this.selectedSdlIds.indexOf(id), 1);
      this.selectedSdl.splice(this.selectedSdl.indexOf(currentSdl), 1);
    }
    /*this.selectedSdl = this.ambulatoryCalendarAction.list.headers.filter((hdr) =>
      this.selectedSdlIds.indexOf(hdr.id) !== -1
    );*/
  }

  showWeek(sdl: Array<any>) {
    if (!this.ambulatoryCalendarAction.weekViewOfSdl) {
      this.conversationActions.put("weekViewOfSdl", sdl);
      this.refresh(true);
    }
  }

  exitWeekView() {
    this.conversationActions.remove("weekViewOfSdl");
    this.refresh(true);
  }

  editNote(hdr: any) {
    if (this.ambulatoryCalendarAction.isSuperUser()) {
      if (hdr.note) {
        this.viewManager
          .setPopupViewId("agendaannotation-edit", hdr.note.internalId)
          .then((aae: AgendaAnnotationEdit) =>
            aae.save.subscribe(() => this.refresh(true))
          );
      } else {
        const date = this.getDateTime(hdr.date, 0, 0);
        this.viewManager
          .setPopupViewId(
            "agendaannotation",
            "new",
            hdr.id,
            date.getTime().toString()
          )
          .then((aae: AgendaAnnotationEdit) =>
            aae.save.subscribe(() => this.refresh(true))
          );
      }
    }
  }

  moveSdlLeft(sdlId: number) {
    this.moveSdl(sdlId, false);
  }

  moveSdlRight(sdlId: number) {
    this.moveSdl(sdlId, true);
  }

  private moveSdl(sdlId: number, right: boolean) {
    const agendas: Array<ServiceDeliveryLocation> =
      this.ambulatoryCalendarAction.agendas;
    const agendaIndex: number = agendas.findIndex(
      (sdl: ServiceDeliveryLocation) => sdl.internalId === sdlId
    );
    let agenda2Index = agendaIndex;
    let agenda2: ServiceDeliveryLocation;

    if (right && agendaIndex < agendas.length - 1) {
      // NEXT
      agenda2 = agendas[++agenda2Index];
    } else if (!right && agendaIndex > 0) {
      // PREV
      agenda2 = agendas[--agenda2Index];
    }
    // swap Array elements
    const temp = agendas[agendaIndex];
    agendas[agendaIndex] = agendas[agenda2Index];
    agendas[agenda2Index] = temp;

    if (agenda2) {
      this.employeeRoleAction.moveSdl(sdlId, right, agenda2.internalId);
    }

    this.refresh(true);
  }

  calendarClick(ev: MouseEvent, hdr, hour: number, minute: number) {
    if (ev.button === 0) {
      ev.stopPropagation();

      const date = this.getDateTime(hdr.date, hour, minute);

      if (!this.movingAppointment) {
        this.viewManager.setPopupViewId(
          "calendar-menu",
          hdr.id,
          date.getTime().toString(),
          this.editOnlyPatientId ? "true" : "false"
        );
      } else {
        this.saveMovingCalendarItem(date, hdr.id);
      }
    }
  }

  appointmentClick(ev: MouseEvent, calItem: CalendarItem) {
    if (ev.button === 0) {
      ev.stopPropagation();

      if (!this.movingAppointment) {
        if (
          !this.editOnlyPatientId ||
          (this.editOnlyPatientId &&
            calItem.patient &&
            this.editOnlyPatientId === calItem.patient.internalId)
        ) {
          if (calItem.type === "Appointment") {
            if (this.environment.ambulatoryCalendar.dblClickProcess) {
              this.timer = setTimeout(() => {
                if (!this.prevent) {
                  const grouperStatusCode =
                    calItem.appointmentGrouper &&
                    calItem.appointmentGrouper.statusCode &&
                    calItem.appointmentGrouper.statusCode.code
                      ? calItem.appointmentGrouper.statusCode.code
                      : "none";
                  this.viewManager.setPopupViewId(
                    "appointment-menu",
                    calItem.internalId.toString(),
                    calItem.externalId,
                    calItem.statusCode,
                    grouperStatusCode,
                    calItem.serviceDeliveryLocation.internalId.toString(),
                    calItem.encounterId === null
                      ? "none"
                      : calItem.encounterId.toString(),
                    "true",
                    calItem.patient.internalId === null ? "false" : "true",
                    calItem.appointmentGrouper
                      ? calItem.appointmentGrouper.toString()
                      : "none",
                    "false",
                    calItem.procedure &&
                      calItem.procedure.classCode &&
                      calItem.procedure.classCode.code
                      ? calItem.procedure.classCode.code
                      : undefined,
                    this.editOnlyPatientId ? "true" : "false"
                  );
                }
                this.prevent = false;
              }, this.delay);
            } else {
              const grouperStatusCode =
                calItem.appointmentGrouper &&
                calItem.appointmentGrouper.statusCode &&
                calItem.appointmentGrouper.statusCode.code
                  ? calItem.appointmentGrouper.statusCode.code
                  : "none";
              this.viewManager.setPopupViewId(
                "appointment-menu",
                calItem.internalId.toString(),
                calItem.externalId,
                calItem.statusCode,
                grouperStatusCode,
                calItem.serviceDeliveryLocation.internalId.toString(),
                calItem.encounterId === null
                  ? "none"
                  : calItem.encounterId.toString(),
                "true",
                calItem.patient.internalId === null ? "false" : "true",
                calItem.appointmentGrouper
                  ? calItem.appointmentGrouper.toString()
                  : "none",
                "false",
                calItem.procedure &&
                  calItem.procedure.classCode &&
                  calItem.procedure.classCode.code
                  ? calItem.procedure.classCode.code
                  : undefined,
                this.editOnlyPatientId ? "true" : "false"
              );
            }
          } else if (calItem.type === "Note") {
            if (calItem.note) {
              this.viewManager.setPopupViewId(
                "agendaannotation-menu",
                calItem.internalId.toString()
              );
            }
          }
        }
      } else {
        this.saveMovingCalendarItem(
          calItem.date,
          calItem.serviceDeliveryLocation.internalId
        );
      }
    }
  }

  async appointmentDblClick(calItem: CalendarItem) {
    if (this.environment.ambulatoryCalendar.dblClickProcess) {
      // KLINIK
      clearTimeout(this.timer);
      this.prevent = true;

      await this.AppointmentAction.injectAll(calItem.internalId);
      if (calItem.patient.internalId) {
        this.processActions.startProcess(
          this.environment.appointmentMenu.showDetailsProcess
        );
      } else {
        this.viewManager.setPopupViewId(
          "appointment-edit",
          calItem.internalId.toString(),
          "false"
        );
      }
    }
  }

  initMovingCalendarItem() {
    if (this.calendarElement && this.appointmentElement) {
      this.calendarElement.nativeElement.addEventListener(
        "mousemove",
        this.moveCalendarItem
      );
    }
  }

  moveCalendarItem = (e: MouseEvent) => {
    this.appointmentElement.nativeElement.style.left = e.pageX + 5 + "px";
    this.appointmentElement.nativeElement.style.top = e.pageY + 5 + "px";
  };

  stopMovingCalendarItem() {
    this.calendarElement.nativeElement.removeEventListener(
      "mousemove",
      this.moveCalendarItem
    );
  }

  /**
   * Save appointment after move, copy or continue creation from MOD_Outpatients/CORE/PROCESSES/NewAppointment.jpdl.xml process
   */
  saveMovingCalendarItem(date: Date, agendaId: number) {
    const sdl: ServiceDeliveryLocation =
      this.ambulatoryCalendarAction.agendas.find(
        (sdloc: ServiceDeliveryLocation) => sdloc.internalId === agendaId
      );
    if (!this.ambulatoryCalendarAction.isAuthorized(sdl)) {
      return; // locked agenda
    }

    if (this.movingReason === "MOVE" || this.movingReason === "INSERT") {
      if (this.movingAppointment.type === "Note") {
        if (this.ambulatoryCalendarAction.isNoteAuthorized(sdl)) {
          this.AgendaAnnotationAction.entity.availabilityTime = date;
          this.AgendaAnnotationAction.entity.serviceDeliveryLocation = {
            internalId: agendaId,
            entityName: "com.phi.entities.role.ServiceDeliveryLocation",
          };
        }
      } else {
        this.AppointmentAction.entity.defaultDate = date;
        this.AppointmentAction.entity.serviceDeliveryLocation = {
          internalId: agendaId,
          entityName: "com.phi.entities.role.ServiceDeliveryLocation",
        };
      }

      if (
        this.movingReason === "INSERT" ||
        !this.movingAppointment.type ||
        (this.movingAppointment.type && this.movingAppointment.type !== "Note")
      ) {
        // Continue creation by selecting date and sdl from MOD_Outpatients/CORE/PROCESSES/NewAppointment.jpdl.xml process
        this.movingAppointment.status = {
          codeSystemName: "PHIDIC",
          domainName: "AppointmentStatus",
          code: "planned",
        };
        if (
          this.movingAppointment.procedure &&
          !this.movingAppointment.procedure.levelCode
        ) {
          this.movingAppointment.procedure.levelCode = {
            code: "30min",
            codeSystemName: "PHIDIC",
            domainName: "Length",
          };
        }
        this.movingAppointment.insertCompleted = true;
        this.location.replaceState("dashboard/ambulatory-calendar");
      }
      if (
        this.movingAppointment.type &&
        this.movingAppointment.type === "Note"
      ) {
        if (this.ambulatoryCalendarAction.isNoteAuthorized(sdl)) {
          this.AgendaAnnotationAction.create().then(() => {
            this.refresh(true);
          });
        }
      } else {
        // si passa environment.appointmentAction.update perch� qui siamo sicuri che l'appuntamento gi� esista
        this.AppointmentAction.create(environment.appointmentAction.update).then(() => {
          if (this.AppointmentGrouperAction.entity) {
            this.AppointmentGrouperAction.create().then(() => {
              this.AppointmentGrouperAction.entity = null;
              this.updateInternalActivitySdlAndrefresh(
                this.AppointmentAction.entity.internalId
              );
            });
          } else {
            this.updateInternalActivitySdlAndrefresh(
              this.AppointmentAction.entity.internalId
            );
          }
        });
      }
    } else {
      // COPY
      if (this.movingAppointment.type === "Appointment") {
        this.AppointmentAction.copyAppointment(
          this.movingAppointment.internalId,
          date,
          agendaId
        ).then(() => this.refresh(true));
      } else if (this.movingAppointment.type === "Note") {
        if (this.ambulatoryCalendarAction.isNoteAuthorized(sdl)) {
          this.AgendaAnnotationAction.copyAgendaAnnotation(
            this.movingAppointment,
            date,
            agendaId
          );
          this.AgendaAnnotationAction.create().then(() => this.refresh(true));
        }
      }
    }
    this.conversationActions.remove("movingReason");
    this.conversationActions.remove("movingAppointment");
    this.stopMovingCalendarItem();
  }

  dragstart(ev: DragEvent, calItem: CalendarItem) {
    if (ev.target instanceof HTMLDivElement) {
      this.currentCal = calItem.type;
      ev.dataTransfer.setData("text", calItem.internalId.toString());
      ev.dataTransfer.setData("type", calItem.type);
      if (calItem.type === "Appointment") {
        if (
          !calItem.statusCode ||
          calItem.statusCode === "awaiting" ||
          calItem.statusCode === "planned"
        ) {
          ev.dataTransfer.effectAllowed = "copyMove";
        } else {
          ev.dataTransfer.effectAllowed = "copy";
        }
      }
      if (calItem.type === "Note") {
        ev.dataTransfer.effectAllowed = "copyMove";
      }
      // Set drag image offset to 0,0:
      if (ev.dataTransfer.setDragImage) {
        // IE11 doesn't support
        ev.dataTransfer.setDragImage(ev.target.parentElement, 0, 0);
      }
    }
  }

  dragover(ev: DragEvent) {
    if (ev.currentTarget instanceof HTMLTableCellElement) {
      const td: HTMLTableCellElement = ev.currentTarget;
      const sdlId = +td.dataset.sdl;
      const sdl: ServiceDeliveryLocation =
        this.ambulatoryCalendarAction.agendas.find(
          (sdloc: ServiceDeliveryLocation) => sdloc.internalId === sdlId
        );
      if (this.currentCal === "Note") {
        if (this.ambulatoryCalendarAction.isNoteAuthorized(sdl)) {
          // if (this.ambulatoryCalendarAction.isSdlWaitingList(sdl)) {   && sdl.waitingListSupported
          ev.preventDefault(); // allow dropping
        }
      } else {
        if (this.ambulatoryCalendarAction.isAuthorized(sdl)) {
          ev.preventDefault(); // allow dropping
        }
      }
      if (ev.ctrlKey) {
        // show right mouse icon
        ev.dataTransfer.dropEffect = "copy";
      } else {
        ev.dataTransfer.dropEffect = "move";
      }
    }
  }

  drop(ev: DragEvent) {
    ev.preventDefault();
    const calItemId: number = +ev.dataTransfer.getData("text");
    const caltype: string = ev.dataTransfer.getData("type");
    if (calItemId && ev.currentTarget instanceof HTMLTableCellElement) {
      const td: HTMLTableCellElement = ev.currentTarget;
      const sdlId = +td.dataset.sdl;
      const time = +td.dataset.time;
      let day: Date;
      if (time) {
        day = new Date(time);
      }

      const tbody = td.parentElement.parentElement;
      const offset: number = ev.clientY - tbody.getBoundingClientRect().top;
      const minutes: number = offset / AmbulatoryCalendar.heightPixelPerMinute;
      const minutesRounded =
        AmbulatoryCalendarAction.startHour * 60 +
        Math.trunc(minutes / AmbulatoryCalendarAction.minutesPerRow) *
          AmbulatoryCalendarAction.minutesPerRow;

      const date = this.getDateTime(
        day,
        Math.floor(minutesRounded / 60),
        minutesRounded % 60
      );

      if (caltype === 'Appointment') {
        return this.AppointmentAction.getLockedStatus(calItemId)
        .then((res:boolean) => {
          if (res) {
            return;
          } else {
            this.AppointmentAction.getAll(calItemId).then((app: Appointment) => {
              if (ev.ctrlKey) { // clone
                td.appendChild(document.getElementById(calItemId.toString()).cloneNode(true));
                this.AppointmentAction.copyAppointment(app.internalId, date, sdlId).then(() =>
                  this.refresh(true)
                );
              } else { // move
                td.appendChild(document.getElementById(calItemId.toString()));
                app.defaultDate = date;
                app.serviceDeliveryLocation.internalId = sdlId;
                this.AppointmentAction.create(app.internalId ? environment.appointmentAction.update : environment.appointmentAction.create).then(() =>
                  this.updateInternalActivitySdlAndrefresh(app.internalId)
                );
              }
            });
          }
        });
      }
      if (caltype === "Note") {
        this.AgendaAnnotationAction.inject(calItemId, null, [
          "lengthCode",
        ]).then((agenda: AgendaAnnotation) => {
          if (ev.ctrlKey) {
            // clone
            td.appendChild(
              document.getElementById(calItemId.toString()).cloneNode(true)
            );
            this.AgendaAnnotationAction.copyAgendaAnnotationFromAgenda(
              agenda,
              date,
              sdlId
            );
            this.AgendaAnnotationAction.create().then(() => this.refresh(true));
          } else {
            // move
            td.appendChild(document.getElementById(calItemId.toString()));
            agenda.availabilityTime = date;
            agenda.serviceDeliveryLocation.internalId = sdlId;
            this.AgendaAnnotationAction.create().then(() => this.refresh(true));
          }
        });
      }
    }
  }

  initialiseResize(ev) {
    ev.stopPropagation();

    window.addEventListener("mousemove", this.startResizing, true);
    window.addEventListener("mouseup", this.stopResizing, true);
    this.currentApp = ev.target.parentElement;
  }

  startResizing = (ev) => {
    ev.stopPropagation();

    if (this.currentApp) {
      const height: number =
        ev.clientY - this.currentApp.getBoundingClientRect().top;
      const minutes: number = height / AmbulatoryCalendar.heightPixelPerMinute;
      let minutesPerRow = environment.ambulatoryCalendar.minutesPerRowShort;
      let minutesRounded =
        Math.round(minutes / minutesPerRow) * minutesPerRow || minutesPerRow;
      if (environment.ambulatoryCalendar.useRead && minutesRounded > 30) {
        if (minutesRounded > 540) {
          minutesPerRow = environment.ambulatoryCalendar.minutesPerRowLong;
        } else {
          minutesPerRow = environment.ambulatoryCalendar.minutesPerRowMedium;
        }
        minutesRounded =
          Math.round(minutes / minutesPerRow) * minutesPerRow || minutesPerRow;
      }
      // console.log('minutes: ' + minutesRounded);
      const snapHeight: number =
        minutesRounded * AmbulatoryCalendar.heightPixelPerMinute;

      this.currentApp.style.height = snapHeight + "px";
    }
  };

  stopResizing = (ev) => {
    ev.stopPropagation();

    const calItemId: number = +this.currentApp.id;
    const calItemNote = this.currentApp.classList.contains("calItemTypeNote");
    const height: number = +this.currentApp.style.height.substring(
      0,
      this.currentApp.style.height.length - 2
    );
    const minutes: number = height / AmbulatoryCalendar.heightPixelPerMinute;

    if (environment.ambulatoryCalendar.useRead) {
      // VCO
      const h: number = Math.floor(minutes / 60);
      const m: number = minutes % 60;
      const code = (h !== 0 ? h + "h" : "") + (m !== 0 ? m + "min" : "");

      this.dictionaryManager
        .get("PHIDIC:Length")
        .then((domain: Array<CodeValuePhi>) => {
          const cv: CodeValuePhi = domain.find((cval: CodeValuePhi) => {
            // return minutesRoundedUp === cv.score; // FIXME: dictionary manager doesent fetch score, so change server!
            return cval.code === code;
          });

        if (cv) {
          if (calItemNote) {
            this.AgendaAnnotationAction.inject(calItemId).then((aan: AgendaAnnotation) => {
              if (aan.lengthCode.id !== cv.id) {
                aan.lengthCode = cv;
                this.AgendaAnnotationAction.create().then(
                  () => this.refresh(true)
                );
              }
            });
          } else {
            this.AppointmentAction.getAll(calItemId).then((app: Appointment) => {
              if (app.procedure.levelCode.id !== cv.id) {
                app.procedure.levelCode = cv;
                this.AppointmentAction.create(app.internalId ? environment.appointmentAction.update : environment.appointmentAction.create).then(
                  () => this.refresh(true)
                );
              }
            });
          }
        }
      });
    } else {
      this.AppointmentAction.getAll(calItemId).then((app: Appointment) => {
        // KLINIK
        app.duration = minutes;
        this.AppointmentAction.create(app.internalId ? environment.appointmentAction.update : environment.appointmentAction.create).then(
          () => this.refresh(true)
        );
      });
    }

    window.removeEventListener("mousemove", this.startResizing, true);
    window.removeEventListener("mouseup", this.stopResizing, true);

    this.currentApp = null;
    this.currentCal = null;
  };

  private getDateTime(day: Date, hour: number, minute: number): Date {
    let date = null;
    if (this.ambulatoryCalendarAction.weekViewOfSdl) {
      date = new Date(day);
    } else {
      date = new Date(this.ambulatoryCalendarAction.selectedDate);
    }
    date.setHours(hour, minute);
    return date;
  }

  get startWorkHour() {
    return AmbulatoryCalendarAction.startWorkHour;
  }

  get endWorkHour() {
    return AmbulatoryCalendarAction.endWorkHour;
  }

  getColumnTitle(hdr) {
    return hdr.parent ? hdr.parent.text : "";
  }

  getColor(calItem: CalendarItem): string {
    if (calItem) {
      if (calItem.statusCode === "cancelled") {
        return "red";
      } else if (calItem.color) {
        return color2hex(calItem.color.toString());
      } else {
        if (
          environment.ambulatoryCalendar.defaultDurationColor &&
          (calItem.duration ||
            !calItem.procedure ||
            !calItem.procedure.classCode)
        ) {
          // duration only in KLINIK
          return "#6dbdd6";
        } else {
          if (calItem.procedure && calItem.procedure.classCode) {
            if (calItem.procedure.classCode.code === "MACROPROC") {
              return "#D2F8FF";
            } else if (calItem.procedure.classCode.code === "INDIRECT") {
              return "#C8F7AD";
            } else if (calItem.procedure.classCode.code === "GENERAL") {
              return "#FFEBA9";
            } else if (calItem.procedure.classCode.code === "CUP") {
              return "#FBD2FF";
            } else if (calItem.procedure.classCode.code === "COORDINATION") {
              return "#FFCA73";
            } else {
              return "#FFFFFF";
            }
          }
        }
      }
    }
  }

  getHeader(calItem: CalendarItem) {
    const endDate: Date = new Date(calItem.date);
    endDate.setMinutes(endDate.getMinutes() + calItem.duration);
    return (
      this.datePipe.transform(calItem.date, "shortTime") +
      " - " +
      this.datePipe.transform(endDate, "shortTime")
    );
  }

  getTitle(calItem: CalendarItem) {
    const type: String = calItem.type;
    if (type === "Appointment") {
      let title = this.getHeader(calItem) + "\n";
      if (calItem.patient.name.fam) {
        title += calItem.patient.name.fam + " ";
      }
      if (calItem.patient.name.giv) {
        title += calItem.patient.name.giv + "\n";
      }
      title += this.datePipe.transform(calItem.patient.birthTime, "shortDate");
      if (calItem.note) {
        title += "\n" + calItem.note;
      }
      if (environment.ambulatoryCalendar.showPerformedProcedure) {
        title +=
          "\n" +
          calItem.performedProcedure.map((p: Procedure) => p.text).join(", ");
      }
      return title;
    } else {
      return calItem.note;
    }
  }

  getTranslation(code: string) {
    return this.translateService.instant(code);
  }

  getTop(groupOffset: number = 0, appOffset: number = 0) {
    return (groupOffset + appOffset) * AmbulatoryCalendar.heightPixelPerMinute;
  }

  getHeight(minutes: number) {
    return minutes * AmbulatoryCalendar.heightPixelPerMinute;
  }

  getWidth(maxCollision) {
    return 100 / maxCollision - 3 / maxCollision;
  }

  getLeft(maxCollision, index) {
    let multiplier = index % maxCollision;
    /*   if (index === maxCollision) {
      multiplier++;
    }*/
    let left =
      (100 / maxCollision) * multiplier - (3 / maxCollision) * multiplier;
    if (left < 0) {
      return 0;
    } else {
      return left;
    }
  }

  isPlanned(statusCode) {
    return (
      statusCode &&
      (statusCode === "planned" ||
        statusCode === "awaiting" ||
        statusCode === "arrived")
    ); // KLINIK
  }

  isCup(externalId) {
    if (environment.ambulatoryCalendar.rootCtrl) {
      // VCO
      return externalId && "CUP" === externalId;
    } else {
      // KLINIK
      return externalId != undefined;
    }
  }
  isCupCtrl(externalId) {
    if (!environment.ambulatoryCalendar.rootCtrl) {
      return externalId != undefined;
    }
  }
  toggleInlineCalendar() {
    this.hiddenInlineCalendar = !this.hiddenInlineCalendar;
  }

  updateInternalActivitySdlAndrefresh(appointmentId) {
    if (environment.ambulatoryCalendar.updateInternalActivitySdl) {
      // VCO
      this.internalActivityAction
        .updateSdl(appointmentId)
        .then(() => this.refresh(true));
    } else {
      // OTHERS
      this.refresh(true);
    }
  }

  printReport() {
    const url =
      this.url + "/swf/modules/ADT/reports/r_laserBand.seam?cid=" + this.cid;
    this.viewManager.openIframe(url);
  }

  public isOfTypeC4(calItem): boolean {
    if (
      calItem.serviceDeliveryLocation &&
      calItem.serviceDeliveryLocation.internalId &&
      this.serviceDeliveryLocationList &&
      this.serviceDeliveryLocationList.entities
    ) {
      const sdl: ServiceDeliveryLocation =
        this.serviceDeliveryLocationList.entities.find(
          (sdLoc: ServiceDeliveryLocation) =>
            sdLoc.internalId === calItem.serviceDeliveryLocation.internalId
        );
      if (!sdl) {
        return false;
      } else {
        return this.ambulatoryCalendarAction.isOfTypeC4(sdl);
      }
    }
  }

  public isDraggable(calItem) {
    return (
      !this.isOfTypeC4(calItem) &&
      (this.ambulatoryCalendarAction.isSuperUser() ||
        this.ambulatoryCalendarAction.isProNurse() ||
        this.ambulatoryCalendarAction.isObstetrician ||
        this.ambulatoryCalendarAction.isOss()) &&
      this.isPlanned(calItem.statusCode) &&
      !this.isCup(
        environment.ambulatoryCalendar.rootCtrl
          ? calItem.externalIdRoot
          : calItem.externalId
      )
    );
  }
}
