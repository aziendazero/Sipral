import { Component, Injector } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BaseForm } from '../../../widgets/form/base-form';
import { ViewManager } from '../../../services/view-manager';
import { AdtAction } from './../actions/adt-action.service';

@Component({
selector: 'phi-adt-cancel',
templateUrl: './adt-cancel.html'
})
export class AdtCancel extends BaseForm  {
  cancellationNote;

  encounterId: number = null;
  appointmentId: number = null;

  constructor(injector: Injector,
              private route: ActivatedRoute,
              private viewManager: ViewManager,
              private adtAction: AdtAction) {
    super(injector);

    this.encounterId = +this.route.snapshot.params['encounterId'];
    this.appointmentId = +this.route.snapshot.params['appointmentId'];
  }

  handleOk() {
    this.adtAction.changeStatus(this.encounterId, this.appointmentId, 'cancelled', this.cancellationNote).then(() =>
      this.viewManager.setPopupViewId(null)
    );
  }

  handleClose() {
    this.viewManager.setPopupViewId(null);
  }
}
