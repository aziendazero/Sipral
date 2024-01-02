import { Component, Injector } from '@angular/core';
import { select } from '@angular-redux/store';
import { BaseForm } from '../../widgets/form/base-form';
import { ErogationStatus } from '../nurse-activity/utils/erogation-status';


@Component({
  templateUrl: './erogation.html'
})
export class ErogationComponent extends BaseForm {
  @select(['conversation', 'ErogationHistoryList']) ErogationHistoryList$;
  ErogationHistoryList;
  @select(['conversation', 'Erogation']) Erogation$;
  Erogation;

  constructor(injector: Injector,
              public erogationStatus: ErogationStatus) {
    super(injector);
    this.ErogationHistoryList$.subscribe(res => this.ErogationHistoryList = res);
    this.Erogation$.subscribe(res => this.Erogation = res);
  }

  data: any;

}
