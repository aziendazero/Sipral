import {Component, Injector} from '@angular/core';
import {BaseForm} from '../../../widgets/form/base-form';
import {ProcessActions} from '../../../actions/process.actions';
import {ViewManager} from '../../../services/view-manager';
import {PsAction} from '../actions/ps-action.service';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'phi-ps-menu',
  templateUrl: './ps-menu.html'
})
export class PsMenu extends BaseForm {

  encounterId: number;
  statusCode = '';

  constructor(injector: Injector,
              private route: ActivatedRoute,
              private viewManager: ViewManager,
              private processActions: ProcessActions,
              public psAction: PsAction) {
    super(injector);

    this.encounterId = +this.route.snapshot.params['encounterId'];
    this.statusCode = this.route.snapshot.params['status'];
  }

  registry() {
    this.startProcess('MOD_Patient/CORE/PROCESSES/View');
  }

  edit() {
    this.startProcess('MOD_Hospital_Admission/CORE/PROCESSES/View Data');
  }

  medicalHistory() {
    this.startProcess('MOD_Clinical_Document/CORE/PROCESSES/Create report during admission');
  }

  takeInCharge() {
    this.viewManager.setPopupViewId(null).then(() =>
      this.psAction.changeStatus(this.encounterId, 0, 'active')
    ).then(() =>
      this.psAction.refresh()
    );
  }

  outpatientSummary() {
    this.startProcess('MOD_Accounting/CORE/PROCESSES/Policy Mngt');
  }

  report() {
    this.startProcess('MOD_Clinical_Document/CORE/PROCESSES/Create ambulatory report');
  }

  closingData() {
    this.startProcess('MOD_Close_Encounter/CORE/PROCESSES/EncounterComplete');
  }

  reopen() {
    this.startProcess('MOD_Outpatients/CORE/PROCESSES/Emergency Enc/Emer Encounter Reopen');
  }

  closeEncounter() {
    this.startProcess('MOD_Close_Encounter/CORE/PROCESSES/EncounterComplete');
  }

  cancel() {
    this.startProcess('MOD_Close_Encounter/CORE/PROCESSES/Delete');
  }

  private startProcess(process: string) {
    this.viewManager.setPopupViewId(null).then(() =>
      this.processActions.startProcess(process)
    );

  }

}
