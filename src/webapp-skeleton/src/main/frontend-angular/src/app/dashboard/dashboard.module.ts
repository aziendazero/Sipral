
import { NgModule } from '@angular/core';


import { DashboardBaseAction } from './dashboard-base-action.service';
import { AnnotationActionService } from './../services/actions/annotation-action.service';

import { NurseActivityAction } from './nurse-activity/actions/nurse-activity-action.service';
import { NurseActivityComponent } from './nurse-activity/nurse-activity.component';
import { ErogationStatus } from './nurse-activity/utils/erogation-status';

import { ActivityPrescriber } from './activity-prescriber/activity-prescriber';
import { Diagnosis } from './activity-prescriber/components/diagnosis';
import { DiagnosisDelete } from './activity-prescriber/components/diagnosis-delete';
import { Objective } from './activity-prescriber/components/objective';
import { ObjectiveClose } from './activity-prescriber/components/objective-close';
import { LepActivityDelete } from './activity-prescriber/components/lep-activity-delete';

import { CarePlanAction } from './activity-prescriber/actions/care-plan-action.service';

import { AdtComponent } from './adt/adt.component';
import { AdtNoteComponent } from './adt/adt-note.component';

import { AdtAlarmDetails } from './adt/components/adt-alarm-details';
import { AdtBarcodeComponent } from './adt/adt-barcode.component';
import { AdtCancel } from './adt/components/adt-cancel';

import { AdtAction } from './adt/actions/adt-action.service';
import { DrugPrescriberAction } from './drug-prescriber/actions/drug-prescriber-action.service';
import { DrugPrescriberComponent } from './drug-prescriber/drug-prescriber.component';
import { PrescriptionPharmacologic } from './drug-prescriber/prescription-pharmacologic';
import { PrescriptionEdit } from './drug-prescriber/components/prescription-edit';

import { ErogationComponent } from './drug-prescriber/erogation.component';
import { NeedsBasedComponent } from './drug-prescriber/needs-based.component';
import { PrintReportComponent } from './drug-prescriber/components/print-report.component';
import { VitalSignLast } from './drug-prescriber/components/vital-sign-last';

import { FavoriteComponent } from './favorite/favorite.component';
import { FavoriteTabComponent } from './favorite/favorite-tab.component';
import { FavoriteSectionComponent } from './favorite/favorite-section.component';
import { MedicineDetail } from './favorite/medicine/medicine-detail';
import { SearchMedicineComponent } from './favorite/search-medicine.component';
import { Dosage } from './favorite/components/dosage';
import { Frequency } from './favorite/components/frequency';
import { TemplateDosage } from './favorite/components/template-dosage';
import { LepTree } from './favorite/activity/lep-tree'
import { LepActivityEdit } from './favorite/activity/lep-activity-edit'

import { NurseActivityPanel } from './nurse-activity/components/nurse-activity-panel';
import { LepActivity } from './nurse-activity/components/lep-activity';
import { NeedsBasedWarning } from './nurse-activity/components/needs-based-warning';
import { LepExecutionDetail } from './nurse-activity/components/lep-execution-detail';
import { SubstanceAdministrationDetail } from './nurse-activity/components/substance-administration-detail';
import { NurseActivityCancel } from './nurse-activity/components/nurse-activity-cancel';

import { DosageValidator } from './favorite/directives/dosage-validator';

import {
  AdviseMsgActionService,
  AgendaAnnotationActionService,
  AmbulatoryDiaryActionService,
  AmbulatoryReportActionService,
  BaseActionService,
  AppointmentActionService,
  AppointmentGrouperActionService,
  ClinicalDiaryActionService,
  EmployeeActionService,
  EmployeeRoleActionService,
  FavoriteProfileActionService,
  FavoriteSectionActionService,
  FavoriteTabActionService,
  InternalActivityActionService,
  LEPActivityActionService,
  LEPExecutionActionService,
  MedicineActionService,
  NandaActionService,
  ObjectiveNandaActionService,
 
  PatientActionService,
  PatientEncounterActionService,
  PrescriptionActionService,
  PrescriptionDischargeActionService,
  PrimaVitalSignActionService,
  ProcedureDefinitionActionService,
  ServiceDeliveryLocationActionService,
  SubstanceAdministrationActionService,
  TemplateDosageActionService,
  TherapyActionService,
  TherapySimpleActionService,
  VitalSignActionService
} from '../services/actions';

// Legacy flex dashboards
import { FlexContainerComponent } from './flex-container/flex-container.component';
import { FlashVarsResolve } from './flex-container/flash-vars.resolve';
import { FlexContainerCanActivateService } from './flex-container/flex-container.can-activate.service';
import { FlexContainerRouterComponent } from './flex-container/flex-container-router.component';
import { FlexContainerService } from './flex-container/flex-container.service';

import { SharedModule } from '../shared/shared.module';

import { AmbulatoryPortal } from './ambulatory-portal/ambulatory-portal';
import { AppointmentDelete } from './ambulatory-portal/components/appointment-delete';
import { AppointmentDetails } from './ambulatory-portal/components/appointment-details';
import { AppointmentList } from './ambulatory-portal/components/appointment-list';
import { AppointmentgrouperDetails } from './ambulatory-portal/components/appointmentgrouper-details';
import { AppointmentgrouperListDetails } from './ambulatory-portal/components/appointmentgrouper-list-details';
import { Details } from './ambulatory-portal/components/details';
import { InternalactivityDetails } from './ambulatory-portal/components/internalactivity-details';

import { AmbulatoryCalendar } from './ambulatory-calendar/ambulatory-calendar';
import { AgendaAnnotationEdit } from './ambulatory-calendar/components/agendaannotation-edit';
import { AppointmentEdit } from './ambulatory-calendar/components/appointment-edit';
import { AppointmentMenu } from './ambulatory-calendar/components/appointment-menu';
import { AgendaAnnotationMenu } from './ambulatory-calendar/components/agendaannotation-menu';
import { CalendarMenu } from './ambulatory-calendar/components/calendar-menu';
import { PatientSearch } from './ambulatory-calendar/components/patient-search';
import { ProcedureDefinitonSearch } from './ambulatory-calendar/components/procedure-definiton-search';
import { ServicedeliverylocationSearch } from './ambulatory-calendar/components/servicedeliverylocation-search';
import { AmbulatoryCalendarAction } from './ambulatory-calendar/actions/ambulatory-calendar-action.service';

import { DrugPrescriberSimple } from './drug-prescriber-simple/drug-prescriber-simple';
import { PrescriptionSimpleEdit } from './drug-prescriber-simple/components/prescription-simple-edit';
import { AmbulatoryPortalActionService } from './ambulatory-portal/actions/ambulatory-portal-action.service';
import { History } from './ambulatory-portal/components/history';
import { ProcedureList } from './ambulatory-portal/components/procedure-list';
import { Ps } from './ps/ps';
import { PsMenu } from './ps/components/ps-menu';
import { PsAction } from './ps/actions/ps-action.service';
import { TimeBandActionService } from 'app/services/actions/time-band-action.service';

@NgModule({
  imports: [
    SharedModule
  ],
  declarations: [
    AdtNoteComponent,
    AdtAlarmDetails,
    AdtBarcodeComponent,
    AdtCancel,
    ActivityPrescriber,
    Diagnosis,
    DiagnosisDelete,
    Objective,
    ObjectiveClose,
    LepActivityDelete,
    AdtComponent,
    DrugPrescriberComponent,
    PrescriptionPharmacologic,
    PrescriptionEdit,
    ErogationComponent,
    FavoriteComponent,
    FavoriteSectionComponent,
    FavoriteTabComponent,
    NeedsBasedComponent,
    NurseActivityComponent,
    PrintReportComponent,
    VitalSignLast,
    MedicineDetail,
    SearchMedicineComponent,
    Dosage,
    Frequency,
    TemplateDosage,
    LepTree,
    LepActivityEdit,
    NurseActivityPanel,
    LepActivity,
    NeedsBasedWarning,
    LepExecutionDetail,
    SubstanceAdministrationDetail,
    NurseActivityCancel,

    FlexContainerComponent,
    FlexContainerRouterComponent,

    DosageValidator,

    AmbulatoryPortal,
    AppointmentDelete,
    AppointmentDetails,
    AppointmentList,
    AppointmentgrouperDetails,
    AppointmentgrouperListDetails,
    Details,
    InternalactivityDetails,
    History,
    ProcedureList,

    AmbulatoryCalendar,
    AgendaAnnotationEdit,
    AgendaAnnotationMenu,
    AppointmentEdit,
    AppointmentMenu,
    CalendarMenu,
    PatientSearch,
    ProcedureDefinitonSearch,
    ServicedeliverylocationSearch,

    DrugPrescriberSimple,
    PrescriptionSimpleEdit,

    Ps,
    PsMenu
  ],
  exports: [
    AmbulatoryCalendar,
    FlexContainerComponent
  ],
  providers: [
    AdviseMsgActionService,
    AgendaAnnotationActionService,
    AmbulatoryDiaryActionService,
    AmbulatoryReportActionService,
    AdtAction,
    BaseActionService,
    ClinicalDiaryActionService,
    EmployeeActionService,
    EmployeeRoleActionService,
    FavoriteProfileActionService,
    FavoriteTabActionService,
    FavoriteSectionActionService,
    InternalActivityActionService,
    LEPActivityActionService,
    LEPExecutionActionService,
    MedicineActionService,
    NandaActionService,
    ObjectiveNandaActionService,
    AnnotationActionService,
    PatientActionService,
    PatientEncounterActionService,
    PrescriptionActionService,
    PrimaVitalSignActionService,
    PrescriptionDischargeActionService,
    ProcedureDefinitionActionService,
    ServiceDeliveryLocationActionService,
    SubstanceAdministrationActionService,
    TemplateDosageActionService,
    TherapyActionService,
    TherapySimpleActionService,
    TimeBandActionService,
    NurseActivityAction,
    ErogationStatus,
    DashboardBaseAction,
    CarePlanAction,
    DrugPrescriberAction,
    VitalSignActionService,
    AppointmentActionService,
    AppointmentGrouperActionService,
    AmbulatoryCalendarAction,
    AmbulatoryPortalActionService,

    PsAction,

    FlexContainerService,
    FlashVarsResolve,
    FlexContainerCanActivateService
  ]
})
export class DashboardModule { }
