import { Component, Injector } from '@angular/core';
import { select } from '@angular-redux/store';
import { BaseForm } from '../../../widgets/form/base-form';
@Component({
selector: 'phi-procedure-list',
templateUrl: './procedure-list.html'
})
export class ProcedureList extends BaseForm  {
  @select(['conversation', 'PatientEncounterAdditional']) PatientEncounterAdditional$;
  PatientEncounterAdditional;

  constructor(injector: Injector) {
    super(injector);
    this.PatientEncounterAdditional$.subscribe(res => this.PatientEncounterAdditional = res);
  }

}
