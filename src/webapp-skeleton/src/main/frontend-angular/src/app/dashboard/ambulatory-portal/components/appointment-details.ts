import { Component, EventEmitter, Injector, Input, Output } from '@angular/core';
import { BaseForm } from '../../../widgets/form/base-form';
import { AppointmentActionService } from '../../../services/actions/appointment-action.service';
import { ViewManager } from '../../../services/view-manager';


@Component({
selector: 'phi-appointment-details',
templateUrl: './appointment-details.html'
})
export class AppointmentDetails extends BaseForm  {

  @Input()
  Appointment; // result of query, not real Appointment entity

  @Output() save : EventEmitter<any> = new EventEmitter();

  constructor(injector: Injector,
              private viewManager: ViewManager,
              public AppointmentAction: AppointmentActionService) {
    super(injector);

  }

  onSave() {
    this.AppointmentAction.injectAll(this.Appointment.internalid).then(() => {

      if (!this.AppointmentAction.entity.text) {
        this.AppointmentAction.entity.text = {};
      }
      this.AppointmentAction.entity.text.string = this.Appointment.text.string;
      this.AppointmentAction.create().then(() => {
        this.viewManager.setPopupViewId(null);
        this.save.emit(null);
      });
    });
  }

}
