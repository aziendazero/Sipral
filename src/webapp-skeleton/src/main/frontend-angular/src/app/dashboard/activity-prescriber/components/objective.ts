import { Component, Injector, Output, EventEmitter } from '@angular/core';
import { select } from '@angular-redux/store';
import { BaseForm } from '../../../widgets/form/base-form';
import { PatientEncounter, ObjectiveNanda, Nanda } from '../../../services/entities/act';
import { Employee } from '../../../services/entities/role';
import { Config } from '../../../store/config.reducer';
import { PatientEncounterActionService, TemplateDosageActionService } from '../../../services/actions';
import { ConversationActions } from '../../../actions/conversation.actions';
import {ViewManager} from '../../../services/view-manager';
import { LEPActivity } from '../../../services/entities/act/lep-activity';

@Component({
selector: 'phi-objective',
templateUrl: './objective.html'
})
export class Objective extends BaseForm  {

  @select(['conversation']) conversation$;
  @select(['config']) config$;

  Nanda: Nanda;
  ObjectiveNanda: ObjectiveNanda;
  Employee: Employee;
  PatientEncounter: PatientEncounter;

  enableTimeLimit = true;
  frqncyz;
  showDosage = false;
  now;

  @Output() create : EventEmitter<any> = new EventEmitter();

  constructor(injector: Injector,
              protected PatientEncounterAction: PatientEncounterActionService,
              public TemplateDosageAction: TemplateDosageActionService,
              private conversationActions: ConversationActions,
              private viewManager: ViewManager) {

    super(injector);

    this.conversation$.subscribe(res => {
      this.Nanda = res.Nanda;
      this.ObjectiveNanda = res.ObjectiveNanda;
      this.PatientEncounter = res.PatientEncounter;
    });
    this.config$.subscribe( (cfg: Config) => {
      this.Employee = cfg.employee;
    });
    this.now = new Date();

    this.readTemplateDosages();
  }

  /**
   * Read TemplateDosages
   */
  public readTemplateDosages(): void {
    const sdlId = this.PatientEncounterAction.getServiceDeliveryLocationId(this.PatientEncounter);
    if (sdlId != null) {
      this.TemplateDosageAction.equal['isActive'] = true;
      this.TemplateDosageAction.equal['serviceDeliveryLocation.internalId'] = sdlId;
      this.TemplateDosageAction.equal['code.code'] = 'ACTIVITY';
      this.TemplateDosageAction.orderBy['title'] = 'ascending';
      this.TemplateDosageAction.read();
    }
  }

  supportNumberChanged(LEPActivity: LEPActivity, value: number) {
    if (value > 0) {
      if (!LEPActivity.supportRole) {
        LEPActivity.supportRole = {codeSystemName: 'ROLES', domainName: 'EmployeeFunction', code: '10'}; // Nurse
      }
    } else {
      delete LEPActivity.supportRole;
    }
  }

  save() {
    this.create.emit(null);
  }

  ngOnDestroy() {
    this.conversationActions.remove('TemplateDosageList');
  }

  close() {
    this.viewManager.setPopupViewId(null);
  }
}
