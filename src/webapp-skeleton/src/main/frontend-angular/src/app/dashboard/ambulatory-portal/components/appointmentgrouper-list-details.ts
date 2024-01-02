import { Component, Injector } from '@angular/core';
import { select } from '@angular-redux/store';
import { BaseForm } from '../../../widgets/form/base-form';

@Component({
  selector: 'phi-appointmentgrouper-list-details',
  templateUrl: './appointmentgrouper-list-details.html'
})
export class AppointmentgrouperListDetails extends BaseForm  {

  @select(['conversation', 'AppointmentList']) AppointmentList$;
  AppointmentList;

  constructor(injector: Injector) {
    super(injector);
    this.AppointmentList$.subscribe(res => this.AppointmentList = res);
  }

}
