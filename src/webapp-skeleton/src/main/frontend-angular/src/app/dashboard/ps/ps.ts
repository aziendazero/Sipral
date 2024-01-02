import {Component, Injector, OnDestroy, OnInit} from '@angular/core';
import {select} from '@angular-redux/store';
import {BaseForm} from '../../widgets/form/base-form';
import {PsAction} from './actions/ps-action.service';
import {ProcessActions} from '../../actions/process.actions';
import {Subscription} from 'rxjs/Subscription';
import {PatientEncounter} from '../../services/entities/act/patient-encounter';
import {Datamodel} from '../../services/datamodel/datamodel';
import {PatientEncounterActionService} from '../../services/actions/patient-encounter-action.service';
import {ConversationActions} from '../../actions/conversation.actions';
import {ViewManager} from '../../services/view-manager';
import { ConfigActions } from '../../actions/config.action';

@Component({
  selector: 'phi-ps',
  templateUrl: './ps.html',
  styleUrls: ['./ps.scss']
})
export class Ps extends BaseForm implements OnInit, OnDestroy {

  @select(['conversation']) conversation$;
  conversationSub: Subscription;
  PsList: Datamodel;
  PatientEncounter: PatientEncounter;
  Ps: PatientEncounter;


  searchVisible = false;
  showingRecent = false;

  constructor(injector: Injector,
              private conversationActions: ConversationActions,
              private processActions: ProcessActions,
              private viewManager: ViewManager,
              private configActions: ConfigActions,
              private patientEncounterAction: PatientEncounterActionService,
              public psAction: PsAction) {
    super(injector);

    this.conversationSub = this.conversation$.subscribe(res => {
      this.PsList = res.PsList;
      this.Ps = this.PatientEncounter = res.PatientEncounter;
    });
  }

  ngOnInit() {
    this.showOpen();

    this.configActions.put('isPortal', true);
  }

  ngOnDestroy() {
    this.conversationSub.unsubscribe();

    this.configActions.remove('isPortal');
  }

  toggleSearch() {
    this.searchVisible = !this.searchVisible;
  }

  cleanFilters() {
    this.psAction.cleanRestrictions();
    this.psAction.equal['statusCode.code'] = ['new', 'active', 'terminated'];
    this.psAction.refresh();
  }

  showOpen() {
    this.showingRecent = false;
    this.psAction.cleanRestrictions();
    this.psAction.equal['statusCode.code'] = ['new', 'active', 'terminated'];
    this.psAction.refresh();
  }

  showWaiting() {
    this.showingRecent = false;
    this.psAction.cleanRestrictions();
    this.psAction.equal['statusCode.code'] = ['new'];
    this.psAction.refresh();
  }

  showIncharge() {
    this.showingRecent = false;
    this.psAction.cleanRestrictions();
    this.psAction.equal['statusCode.code'] = ['active', 'terminated'];
    this.psAction.refresh();
  }

  showRecent() {
    this.showingRecent = true;
    const yesterday = new Date();
    yesterday.setHours(0, 0, 0, 0);
    yesterday.setDate(yesterday.getDate() - 1);

    this.psAction.cleanRestrictions();
    this.psAction.equal['statusCode.code'] = ['completed'];
    this.psAction.equal['dismissionDateStart'] = yesterday;
    this.psAction.refresh();
  }

  refresh() {
    this.psAction.refresh();
  }

  newPs() {
    this.processActions.startProcess('MOD_Hospital_Admission/CORE/PROCESSES/Admission');
  }

  report() {
    window.print();
  }

  ie(entity, conversationName): Promise<any> {
    if (!super.selected(entity, 'PatientEncounter')) {
      return this.patientEncounterAction.inject(entity.internalid /*entity.encounterId*/, ['history', 'procedures'], ['patient.language']).then(() =>
        this.conversationActions.put('Pa', {internalId: entity.internalid})
      );
    } else {
      this.viewManager.setPopupViewId('ps-menu', entity.internalid, entity.statuscode.code);
      return Promise.resolve(this.PatientEncounter);
    }
  }
}
