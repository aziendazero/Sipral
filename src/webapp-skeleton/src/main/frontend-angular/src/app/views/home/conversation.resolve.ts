import { Injectable } from '@angular/core';
import { select } from '@angular-redux/store';
import { Resolve, ActivatedRouteSnapshot } from '@angular/router';
import { AppointmentActionService, AgendaAnnotationActionService } from '../../services/actions';
import { Appointment } from '../../services/entities/base-entity';
import { AgendaAnnotation } from '../../services/entities/act';
import { PrescriptionActionService } from '../../services/actions/prescription-action.service';
import { Prescription } from '../../services/entities/base-entity/prescription';
import { Datamodel } from '../../services/datamodel/datamodel';
import { ConversationActions } from '../../actions/conversation.actions';
import { PrescriptionDischargeActionService } from '../../services/actions/prescription-discharge-action.service';
import { PrescriptionGeneric } from '../../services/entities/base-entity/prescription-generic';

@Injectable()
export class ConversationResolve implements Resolve<any> {

  @select(['conversation', 'Appointment']) Appointment$;
  Appointment: Appointment;

  @select(['conversation', 'AgendaAnnotation']) AgendaAnnotation$;
  AgendaAnnotation: AgendaAnnotation;


  @select(['conversation', 'Prescription']) Prescription$;
  Prescription: Prescription;

  constructor(private AppointmentAction: AppointmentActionService,
              private AgendaAnnotationAction: AgendaAnnotationActionService,
              private PrescriptionAction: PrescriptionActionService,
              private PrescriptionDischargeAction: PrescriptionDischargeActionService,
              private conversationActions: ConversationActions) {
    this.Appointment$.subscribe(res => {
      this.Appointment = res;
    });
    this.AgendaAnnotation$.subscribe(res => {
      this.AgendaAnnotation = res;
    });
    this.Prescription$.subscribe(res => {
      this.Prescription = res;
    });
  }

  resolve(route: ActivatedRouteSnapshot) {

    if (route.params['appointment']) {
      if (route.params['appointment'] === 'new') {

        if (!this.Appointment || (this.Appointment && this.Appointment.internalId)) {

            let sdlId = +route.params['sdlId'];
            let date = new Date(+route.params['date']);

            this.AppointmentAction.newAppointment(sdlId, date, route.params['type'], route.params['status']);
        }
      } else {
        // If an appointment is in conversation but doesen't have all required connected objects loaded then injectall
        // Remove adding hateaos.
        if (!this.Appointment || this.Appointment && this.Appointment.internalId !== +route.params['appointment']
            || !this.Appointment.procedure || !this.Appointment.procedure.classCode || !this.Appointment.procedure.classCode.code || !this.Appointment.text) {
              if (route.params['isPopupDashboard'] && route.params['isPopupDashboard'] === 'true') {
                return this.AppointmentAction.getAll(route.params['appointment']);
              } else {
                return this.AppointmentAction.injectAll(route.params['appointment']);
              }
        }
      }
    }

    if (route.params['agendaannotation']) {
      if (route.params['agendaannotation'] === 'new') {

        // if (!this.AgendaAnnotation || (this.AgendaAnnotation && this.AgendaAnnotation.internalId)) {
        //
        //   return this.AgendaAnnotationAction.eject().then(() => {

            let sdlId = +route.params['sdlId'];
            let date = new Date(+route.params['date']);

            this.AgendaAnnotationAction.newAgendaAnnotation(sdlId, date);
        //   });
        // }
      }
      else if (route.params['agendaannotation'] === 'newemptyapp') {

        // if (!this.AgendaAnnotation || (this.AgendaAnnotation && this.AgendaAnnotation.internalId)) {
        //
        //   return this.AgendaAnnotationAction.eject().then(() => {

            let sdlId = +route.params['sdlId'];
            let date = new Date(+route.params['date']);

            this.AgendaAnnotationAction.newAgendaAnnotation(sdlId, date);
            this.AgendaAnnotationAction.entity.color = 16777215; // white
        //   });
        // }
      }

      else {
        if (!this.AgendaAnnotation || this.AgendaAnnotation && this.AgendaAnnotation.internalId !== +route.params['agendaannotation']) {
          return this.AgendaAnnotationAction.inject(route.params['agendaannotation'], null, ['serviceDeliveryLocation']);
        }
      }
    }

    if (route.params['prescription']) {
      if (route.params['prescription'] === 'new') {

      } else {
        if (!this.Prescription || this.Prescription && this.Prescription.internalId !== +route.params['prescription']) {
          return this.PrescriptionAction.inject(route.params['prescription'], null, ['statusCode', 'rootPrescription', 'code']).then((p: Prescription) => {

            if (p.statusCode.code === 'completed') { //Clone completed into new
              p = this.PrescriptionAction.clone(p);
              this.conversationActions.put('Prescription', p);
            }

            let dm: Datamodel = new Datamodel([p]);
            this.conversationActions.put('SelectedPrescriptionList', dm);
            return this.PrescriptionAction.readHistory(p);
          });
        }
      }
    }

    if (route.params['prescription-discharge']) {
      if (route.params['prescription-discharge'] === 'new') {

      } else {
        if (!this.Prescription || this.Prescription && this.Prescription.internalId !== +route.params['prescription-discharge']) {
          return this.PrescriptionDischargeAction.inject(+route.params['prescription-discharge'], null, ['statusCode', 'code']).then((p: PrescriptionGeneric) => {
            this.conversationActions.put('Prescription', p);

            if (p.statusCode.code === 'completed') { //Clone completed into new
              p = this.PrescriptionAction.clone(p);
              this.conversationActions.put('Prescription', p);
            }

            let dm: Datamodel = new Datamodel([p]);
            this.conversationActions.put('SelectedPrescriptionList', dm);

            return p;
          });
        }
      }
    }

  }

}
