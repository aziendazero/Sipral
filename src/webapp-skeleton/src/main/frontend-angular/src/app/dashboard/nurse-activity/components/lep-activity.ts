import { Component, Injector, Output, EventEmitter } from '@angular/core';
import { select } from '@angular-redux/store';
import { TranslateService } from '@ngx-translate/core';
import { BaseForm } from '../../../widgets/form/base-form';
import { DictionaryManager } from '../../../services/dictionary-manager';
import { ViewManager } from '../../../services/view-manager';
import { LEPActivity, PatientEncounter } from '../../../services/entities/act';
import { PatientEncounterActionService, NandaActionService } from '../../../services/actions';

@Component({
selector: 'phi-lep-activity',
templateUrl: './lep-activity.html'
})
export class LepActivity extends BaseForm  {

  @select(['conversation']) conversation$;
  LEPActivity: LEPActivity;
  PatientEncounter: PatientEncounter;

  @Output() create : EventEmitter<any> = new EventEmitter();

  constructor(injector: Injector,
              private PatientEncounterAction: PatientEncounterActionService,
              private NandaAction: NandaActionService,
              private translateService: TranslateService,
              public DictionaryManager: DictionaryManager,
              private viewManager: ViewManager) {
    super(injector);

    this.conversation$.subscribe(res => {
      this.LEPActivity = res.LEPActivity;
      this.PatientEncounter = res.PatientEncounter
    });

    this.typeChanged( 'all' );
  }

  getAllOrFavorites() {
    return [{value: 'all', label: this.translateService.instant('all')},
            {value: 'favorites', label: this.translateService.instant('favorites')}];
  }

  typeChanged(value: string) {
    let promise: Promise<any>;
    if (value === 'all') {
      promise = this.DictionaryManager.get('NANDA:K630119', true);
    } else if (value === 'favorites') {
      const sdlId = this.PatientEncounterAction.getServiceDeliveryLocationId(this.PatientEncounter);
      promise = this.NandaAction.getFavoriteNanda(sdlId);
    }
    promise.then((domain) =>
      this.DictionaryManager.searchResults['NANDA:K630119'] = domain
    );
  }

  search() {
    if (this.DictionaryManager.searchByName['NANDA:K630119'] === null) {
      this.typeChanged('all');
    } else {
      this.DictionaryManager.search();
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

  close() {
    this.viewManager.setPopupViewId(null);
  }
}
