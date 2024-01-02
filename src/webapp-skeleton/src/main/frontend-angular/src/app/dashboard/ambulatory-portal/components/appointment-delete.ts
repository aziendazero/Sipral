import { Component, Injector, Output, EventEmitter } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { environment } from '../../../../environments/environment';
import { BaseForm } from '../../../widgets/form/base-form';
import { AppointmentActionService } from '../../../services/actions';
import { ViewManager } from '../../../services/view-manager';
import { CodeValue } from '../../../services/entities/data-types/code-value';

@Component({
selector: 'phi-appointment-delete',
templateUrl: './appointment-delete.html'
})
export class AppointmentDelete extends BaseForm  {

  grouperId : string;
  cleanCycle;
  reputWl;
  reasonCancCode: CodeValue;
  cancNote: string;

  appointmentId: number;
  status: 'cancel' | 'nullify' | 'cancelReactivate';

  @Output() delete : EventEmitter<any> = new EventEmitter();

  environment = environment;

  constructor(injector: Injector,
              private route: ActivatedRoute,
              private AppointmentAction: AppointmentActionService,
              private viewManager: ViewManager) {
    super(injector);

    this.appointmentId = +this.route.snapshot.params['id'];
    this.status = this.route.snapshot.params['status'];
    this.grouperId = this.route.snapshot.params['grouperId'];
    if (this.grouperId === 'none') {
      this.grouperId = null;
    }
  }

  onDelete() {
    if(this.cleanCycle){
      this.AppointmentAction.deleteAll(this.appointmentId, this.reasonCancCode ? this.reasonCancCode.id : '', this.cancNote).then(() =>
      this.delete.emit(null)
      );
    }
    else{
      this.AppointmentAction.changeStatus(this.appointmentId, this.status, this.reasonCancCode ? this.reasonCancCode.id : '', this.cancNote).then(() =>
      this.delete.emit(null)
    );
    }

  }

  onCancel() {
    this.viewManager.setPopupViewId(null);
  }


}
