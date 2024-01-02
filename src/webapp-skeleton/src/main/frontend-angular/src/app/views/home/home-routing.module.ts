
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { HomeComponent } from './home.component';
import { ActivityPrescriber } from '../../dashboard/activity-prescriber/activity-prescriber';
import { AdtComponent } from '../../dashboard/adt/adt.component';
import { DrugPrescriberComponent } from '../../dashboard/drug-prescriber/drug-prescriber.component';
import { FavoriteComponent } from '../../dashboard/favorite/favorite.component';
import { NurseActivityComponent } from '../../dashboard/nurse-activity/nurse-activity.component';
import { FlexContainerRouterComponent } from '../../dashboard/flex-container/flex-container-router.component';
import { ConversationResolve } from './conversation.resolve';
import { HomeResolve } from './home.resolve';
import { HomeCanActivateService } from './home.can-activate.service';

import { PopupRouterComponent } from './components/popup/popup.router.component';
import { AlertMessageComponent } from './components/popup/components/alert-message.component';
import { ErrorMessageComponent } from './components/popup/components/error-message.component';
import { PopupMenuComponent } from '../../widgets/menu/popup-menu.component';
import { IframeComponent } from './components/popup/components/iframe.component';

import { AdtNoteComponent } from '../../dashboard/adt/adt-note.component';
import { PatientNoteComponent } from './components/banner/components/patient-note.component';
import { AdtAlarmDetails } from './../../dashboard/adt/components/adt-alarm-details';
import { AdtBarcodeComponent } from '../../dashboard/adt/adt-barcode.component';
import { AdtCancel } from '../../dashboard/adt/components/adt-cancel';

import { PrescriptionPharmacologic } from '../../dashboard/drug-prescriber/prescription-pharmacologic';
import { NeedsBasedComponent } from '../../dashboard/drug-prescriber/needs-based.component';
import { SearchMedicineComponent } from '../../dashboard/favorite/search-medicine.component';
import { MedicineDetail } from '../../dashboard/favorite/medicine/medicine-detail';
import { ErogationComponent } from '../../dashboard/drug-prescriber/erogation.component';
import { PrintReportComponent } from '../../dashboard/drug-prescriber/components/print-report.component';
import { VitalSignLast } from '../../dashboard/drug-prescriber/components/vital-sign-last';
import { BannerDetailsComponent } from './components/banner/components/banner-details.component';
import { Diagnosis } from '../../dashboard/activity-prescriber/components/diagnosis';
import { DiagnosisDelete } from '../../dashboard/activity-prescriber/components/diagnosis-delete';
import { Objective } from '../../dashboard/activity-prescriber/components/objective';
import { ObjectiveClose } from '../../dashboard/activity-prescriber/components/objective-close';
import { LepActivityEdit } from '../../dashboard/favorite/activity/lep-activity-edit';
import { LepActivityDelete } from '../../dashboard/activity-prescriber/components/lep-activity-delete';
import { FavoriteTabComponent } from '../../dashboard/favorite/favorite-tab.component';
import { TemplateDosage } from '../../dashboard/favorite/components/template-dosage';
import { LepTree } from '../../dashboard/favorite/activity/lep-tree';
import { FavoriteSectionComponent } from '../../dashboard/favorite/favorite-section.component';

import { NeedsBasedWarning } from '../../dashboard/nurse-activity/components/needs-based-warning';
import { NurseActivityPanel } from '../../dashboard/nurse-activity/components/nurse-activity-panel';
import { NurseActivityCancel } from '../../dashboard/nurse-activity/components/nurse-activity-cancel';
import { SubstanceAdministrationDetail } from '../../dashboard/nurse-activity/components/substance-administration-detail';
import { LepExecutionDetail } from '../../dashboard/nurse-activity/components/lep-execution-detail';
import { LepActivity } from '../../dashboard/nurse-activity/components/lep-activity';

import { AmbulatoryPortal } from '../../dashboard/ambulatory-portal/ambulatory-portal';
import { AppointmentDelete } from '../../dashboard/ambulatory-portal/components/appointment-delete';
import { AppointmentList } from '../../dashboard/ambulatory-portal/components/appointment-list';
import { Details } from '../../dashboard/ambulatory-portal/components/details';

import { AmbulatoryCalendar } from '../../dashboard/ambulatory-calendar/ambulatory-calendar';
import { AgendaAnnotationEdit } from '../../dashboard/ambulatory-calendar/components/agendaannotation-edit';
import { AppointmentEdit } from '../../dashboard/ambulatory-calendar/components/appointment-edit';
import { AppointmentMenu } from '../../dashboard/ambulatory-calendar/components/appointment-menu';
import { AgendaAnnotationMenu } from '../../dashboard/ambulatory-calendar/components/agendaannotation-menu';
import { CalendarMenu } from '../../dashboard/ambulatory-calendar/components/calendar-menu';
import { PatientSearch } from '../../dashboard/ambulatory-calendar/components/patient-search';
import { ProcedureDefinitonSearch } from '../../dashboard/ambulatory-calendar/components/procedure-definiton-search';
import { ServicedeliverylocationSearch } from '../../dashboard/ambulatory-calendar/components/servicedeliverylocation-search';

import { DrugPrescriberSimple } from '../../dashboard/drug-prescriber-simple/drug-prescriber-simple';

import { Ps } from '../../dashboard/ps/ps';

import { DoNotShowSecondaryOnRefreshGuard } from './components/popup/do-not-show-secondary-on-refresh.guard';
import { JsfContainerComponent } from '../../pageContainer/jsf-container.component';
import { FlexContainerCanActivateService } from '../../dashboard/flex-container/flex-container.can-activate.service';
import { environment } from '../../../environments/environment';
import { PsMenu } from '../../dashboard/ps/components/ps-menu';
import { ScrollPositionCanDeactivateService } from 'app/services';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: '', component: HomeComponent, canActivateChild: [HomeCanActivateService], resolve: { init: HomeResolve },
        children: [
          { path: '', redirectTo: environment.home, pathMatch: 'full' },
          // TODO use canActivate: [DashboardCanActivateService]  for users without dashboard
          {
            path: 'process/:path', // TODO: sei sicuro di uscire? canDeactivate: [CanDeactivateGuard],
            component: JsfContainerComponent // Remove to load in chunk
            // loadChildren: '../../pageContainer/jsf-container.module#JsfContainerModule' // Add to load in chunk
          },
          { path: 'dashboard/nurse-activity', component: NurseActivityComponent },
          { path: 'dashboard/nurse-activity/:patientEncounterId', component: NurseActivityComponent },

          { path: 'dashboard/activity-prescriber', component: ActivityPrescriber },

          { path: 'dashboard/adt', component: AdtComponent},

          { path: 'dashboard/ambulatory-portal', component: AmbulatoryPortal },

          { path: 'dashboard/ambulatory-calendar', component: AmbulatoryCalendar, canDeactivate: [ScrollPositionCanDeactivateService]},

          { path: 'dashboard/drug-prescriber', component: DrugPrescriberComponent },
          { path: 'dashboard/drug-prescriber-discharge', component: DrugPrescriberComponent },

          { path: 'dashboard/drug-prescriber-simple', component: DrugPrescriberSimple },

          // { path: 'dashboard/poc', component: Poc },
          { path: 'dashboard/poc', loadChildren: '../../dashboard/poc/poc.module#PocModule' },

          { path: 'dashboard/lab', loadChildren: '../../dashboard/lab-report/lab-report.module#LabReportModule' },

          { path: 'dashboard/favorite/:servideDeliveryLocationId/:type', component: FavoriteComponent },
          { path: 'dashboard/favorite/:servideDeliveryLocationId/:type/:configurationMode', component: FavoriteComponent },

          { path: 'dashboard/ps', component: Ps},

          // { path: 'flex/', component: FlexContainerComponent }, //, resolve: { flashVars: FlashVarsResolve } },
          { path: 'flex/:dashboardName', component: FlexContainerRouterComponent,
            canActivate: [FlexContainerCanActivateService]}, // resolve: { flashVars: FlashVarsResolve } }
          {
            path: 'error',
            component: ErrorMessageComponent,
            canActivate: [DoNotShowSecondaryOnRefreshGuard]
          }
        ]
      },
      {
        path: 'alert/:title/:message/:cancel/:ok/:okGoesBack/:cancelGoesBack/:modal',
        component: AlertMessageComponent,
        outlet: 'popup',
        canActivate: [DoNotShowSecondaryOnRefreshGuard]
      },
      {
        path: 'iframe/:src',
        component: IframeComponent,
        outlet: 'popup',
        canActivate: [DoNotShowSecondaryOnRefreshGuard]
      },
      {
        path: 'menu/:environmentPath/:encounterId/:statusCode/:appointmentId/:invoicingClosed/:sdoClosed',
        component: PopupMenuComponent,
        outlet: 'popup',
        canActivate: [DoNotShowSecondaryOnRefreshGuard]
      },
      {
        path: 'form',
        component: PopupRouterComponent,
        outlet: 'popup',
        canActivate: [DoNotShowSecondaryOnRefreshGuard],
        children: [
          { path: 'banner-details', component: BannerDetailsComponent},
          { path: 'patient-note', component: PatientNoteComponent },

          // Adt
          { path: 'adt-note', component: AdtNoteComponent },
          { path: 'adt-barcode', component: AdtBarcodeComponent },
          { path: 'adt-alarm-details', component: AdtAlarmDetails },
          { path: 'adt-cancel/:encounterId/:appointmentId', component: AdtCancel },

          // Drug prescriber
          { path: 'prescription', component: PrescriptionPharmacologic },
          { path: 'prescription-edit/:prescription', component: PrescriptionPharmacologic, resolve: { conversation: ConversationResolve } },
          { path: 'prescription-discharge-edit/:prescription-discharge', component: PrescriptionPharmacologic,
            resolve: { conversation: ConversationResolve }
          },
          { path: 'needs-based', component: NeedsBasedComponent },
          { path: 'search-medicine', component: SearchMedicineComponent },
          { path: 'medicine-detail', component: MedicineDetail },
          { path: 'erogation', component: ErogationComponent },
          { path: 'print-report', component: PrintReportComponent },
          { path: 'vital-sign-last', component: VitalSignLast },

          // Activity prescriber
          { path: 'diagnosis', component: Diagnosis},
          { path: 'diagnosis-delete', component: DiagnosisDelete},
          { path: 'objective', component: Objective },
          { path: 'objective-close', component: ObjectiveClose },
          { path: 'lep-activity-edit', component: LepActivityEdit },
          { path: 'lep-activity-delete', component: LepActivityDelete },

          // Favorite
          { path: 'favorite-tab', component: FavoriteTabComponent },
          { path: 'template-dosage', component: TemplateDosage },
          { path: 'lep-tree', component: LepTree },
          { path: 'favorite-section', component: FavoriteSectionComponent },

          // Nurse activity
          { path: 'needs-based-warning', component: NeedsBasedWarning },
          { path: 'nurse-activity', component: NurseActivityPanel },
          { path: 'nurse-activity-cancel', component: NurseActivityCancel },
          { path: 'substance-administration-detail', component: SubstanceAdministrationDetail },
          { path: 'lep-execution-detail', component: LepExecutionDetail },
          { path: 'lep-activity', component: LepActivity },

          // Ambulatory portal
          { path: 'details/:appointment', component: Details },
          { path: 'appointment-delete/:id/:status/:grouperId', component: AppointmentDelete },
          { path: 'appointment-list', component: AppointmentList },

          // Ambulatory calendar
          { path: 'agendaannotation-edit/:agendaannotation/:sdlId/:date', component: AgendaAnnotationEdit,
            resolve: { conversation: ConversationResolve }
          },
          { path: 'agendaannotation-edit/:agendaannotation', component: AgendaAnnotationEdit,
            resolve: { conversation: ConversationResolve }
          },
          { path: 'agendaannotation/:agendaannotation/:sdlId/:date', component: AgendaAnnotationEdit,
            resolve: { conversation: ConversationResolve }
          },
          { path: 'appointment/:appointment/:sdlId/:date/:status/:type/:patientFromConversation', component: AppointmentEdit,
            resolve: { conversation: ConversationResolve }
          },
          { path: 'appointment-edit/:appointment/:isPopupDashboard', component: AppointmentEdit, resolve: { conversation: ConversationResolve } },
          { path: 'appointment-menu/:appointment/:externalId/:status/:grouperStatus/:sdlId/:encounterId/:edit/:hasPatient/:grouperId/:hasReport/:classCode/:patientFromConversation',
            component: AppointmentMenu },
          { path: 'agendaannotation-menu/:agendaannotation', component: AgendaAnnotationMenu },
          { path: 'calendar-menu/:sdlId/:date/:patientFromConversation', component: CalendarMenu },
          { path: 'patient-search/:sdlId', component: PatientSearch },
          { path: 'procedure-definiton-search/:sdlId', component: ProcedureDefinitonSearch },
          { path: 'servicedeliverylocation-search', component: ServicedeliverylocationSearch },

          // Ps
          { path: 'ps-menu/:encounterId/:status', component: PsMenu },
        ]
      }

    ])
  ],
  exports: [RouterModule],
  providers: [ScrollPositionCanDeactivateService]
})
export class HomeRoutingModule { }
