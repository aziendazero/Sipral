import { Component, EventEmitter, Injector, Output, ViewChild } from '@angular/core';
import { BaseForm } from '../../../widgets/form/base-form';
import { ActivatedRoute } from '@angular/router';
import { AppointmentActionService } from '../../../services/actions/appointment-action.service';
import { TabbedPanelComponent } from '../../../widgets/tabbed-panel.component';

@Component({
  selector: 'phi-details',
  templateUrl: './details.html'
})
export class Details extends BaseForm  {

  @ViewChild('tpnldetails') tpnldetails: TabbedPanelComponent;

  manageInternalActivity = true; // Parametrize

  Appointment;

  InternalActivity;

  @Output() save : EventEmitter<any> = new EventEmitter();

  constructor(injector: Injector,
              private route: ActivatedRoute,
              private AppointmentAction: AppointmentActionService) {
    super(injector);

    this.AppointmentAction.getDetails(+route.snapshot.params.appointment, this.manageInternalActivity).then((details) => {
      if (details.appointment && details.appointment.length > 0) {
        this.Appointment = details.appointment[0];

        if (this.Appointment.appgroup && this.Appointment.appgroup.internalid) {
          this.AppointmentAction.cleanRestrictions();
          this.AppointmentAction.select.push('statusCode.code');
          this.AppointmentAction.select.push('defaultDate');
          this.AppointmentAction.select.push('serviceDeliveryLocation.name.giv');
          this.AppointmentAction.select.push('procedure.levelCode.score');

          this.AppointmentAction.equal['appointmentGrouper.internalId'] = this.Appointment.appgroup.internalid;
          this.AppointmentAction.equal['insertCompleted'] = true;
          this.AppointmentAction.equal['isActive'] = true;
          this.AppointmentAction.orderBy['defaultDate'] = 'ascending';
          this.AppointmentAction.read();
        }
      }
      if (details.internalActivity && details.internalActivity.length > 0) {
        this.InternalActivity = details.internalActivity[0];
      } else {
        this.tpnldetails.selectedTabIndex = 1;
      }
    });
  }

  onSave = () => {
    this.save.emit(null);
  }
}
