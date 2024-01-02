import { Component, Injector } from '@angular/core';
import { select } from '@angular-redux/store';
import { BaseForm } from '../../../widgets/form/base-form';
import { Datamodel } from '../../../services/datamodel/datamodel';

@Component({
selector: 'phi-vital-sign-last',
templateUrl: './vital-sign-last.html'
})
export class VitalSignLast extends BaseForm  {
  @select(['conversation']) conversation$;

  VitalSignList: Datamodel;

  constructor(injector: Injector) {
    super(injector);
    this.conversation$.subscribe(res => {
      this.VitalSignList = res.VitalSignList;
    });
  }

}
