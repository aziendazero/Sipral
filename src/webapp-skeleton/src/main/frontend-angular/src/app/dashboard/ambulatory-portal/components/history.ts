import { Component, Injector } from '@angular/core';
import { select } from '@angular-redux/store';
import { BaseForm } from '../../../widgets/form/base-form';
import { PatientEncounterActionService } from '../../../services/actions/patient-encounter-action.service';
import { ProcessActions } from '../../../actions/process.actions';
@Component({
selector: 'phi-history',
templateUrl: './history.html'
})
export class History extends BaseForm  {

  historyProcess = 'MOD_Clinical_Document/CORE/PROCESSES/Create ambulatory report';

  @select(['conversation', 'PatientEncounterAdditional']) PatientEncounterAdditional$;
  PatientEncounterAdditional;

  constructor(injector: Injector,
              private patientEncounterAction: PatientEncounterActionService,
              private processActions: ProcessActions) {
    super(injector);
    this.PatientEncounterAdditional$.subscribe(res => this.PatientEncounterAdditional = res);
  }

  async ie(entity, conversationName): Promise<any> {
    await this.patientEncounterAction.inject(entity.encounter.internalid);
    await this.processActions.startProcess(this.historyProcess);
  }


}
