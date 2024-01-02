import { Component, Injector, Input} from '@angular/core';
import { BaseForm } from '../../../widgets/form/base-form';

@Component({
  selector: 'phi-appointmentgrouper-details',
  templateUrl: './appointmentgrouper-details.html'
})
export class AppointmentgrouperDetails extends BaseForm  {

  @Input()
  Appointment;

  constructor(injector: Injector) {
    super(injector);
  }

}
