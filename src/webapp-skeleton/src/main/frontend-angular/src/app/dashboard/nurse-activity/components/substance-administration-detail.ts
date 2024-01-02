import { Component, Injector } from '@angular/core';
import { select } from '@angular-redux/store';
import { BaseForm } from '../../../widgets/form/base-form';
@Component({
selector: 'phi-substance-administration-detail',
templateUrl: './substance-administration-detail.html'
})
export class SubstanceAdministrationDetail extends BaseForm  {
  @select(['conversation', 'NurseActivity']) NurseActivity$;
  NurseActivity;
  @select(['conversation', 'SubstanceAdministration']) SubstanceAdministration$;
  SubstanceAdministration;

  constructor(injector: Injector) {
    super(injector);
    this.NurseActivity$.subscribe(res => this.NurseActivity = res);
    this.SubstanceAdministration$.subscribe(res => this.SubstanceAdministration = res);
  }
}
