import {Component, EventEmitter, Injector, Input, OnDestroy, Output} from '@angular/core';
import { select } from '@angular-redux/store';
import { BaseForm } from '../../../widgets/form/base-form';
import { AppointmentActionService } from '../../../services/actions/appointment-action.service';
import { ViewManager } from '../../../services/view-manager';
@Component({
  selector: 'phi-appointment-list',
  templateUrl: './appointment-list.html',
  styles: ['.has-footer{ max-height: calc(100vh - 150px);}']
})
export class AppointmentList extends BaseForm implements OnDestroy {
  @select(['conversation', 'PatientAppointmentList']) PatientAppointmentList$;
  patientAppointmentListSub;
  PatientAppointmentList;

  @select(['conversation', 'Appointment']) Appointment$;
  appointmentSub;
  Appointment;

  @Output() select : EventEmitter<any> = new EventEmitter();

  @Input()
  showFooter = true;

  constructor(injector: Injector,
              public AppointmentAction: AppointmentActionService,
              private viewManager: ViewManager) {
    super(injector);
    this.patientAppointmentListSub = this.PatientAppointmentList$.subscribe(res => this.PatientAppointmentList = res);
    this.appointmentSub = this.Appointment$.subscribe(res => this.Appointment = res);
  }

  ngOnDestroy() {
    this.patientAppointmentListSub.unsubscribe();
    this.appointmentSub.unsubscribe();
  }

  ie(entity, conversationName): Promise<any> {
    return this.AppointmentAction.injectAll(entity.internalid);
  }

  selectOnCalendar() {
    this.select.emit(this.Appointment);
    this.viewManager.setPopupViewId(null);
  }

  close() {
    this.viewManager.setPopupViewId(null);
  }


}
