import { CodeValue } from './../../../services/entities/data-types/code-value';
import { Component, Injector, Output, EventEmitter } from '@angular/core';
import { select } from '@angular-redux/store';
import { TranslateService } from '@ngx-translate/core';

import { BaseForm } from '../../../widgets/form/base-form';
import { PatientEncounter, LEPActivity, Nanda } from '../../../services/entities/act';
import { PatientEncounterActionService, LEPActivityActionService } from '../../../services/actions';
import { DictionaryManager } from '../../../services/dictionary-manager';
import { ConversationActions } from '../../../actions/conversation.actions';
import { SelectItem } from '../../../services/datamodel/select-item';
import { ViewManager } from '../../../services/view-manager';


@Component({
selector: 'phi-lep-tree',
templateUrl: './lep-tree.html'
})
export class LepTree extends BaseForm  {

  @select(['conversation']) conversation$;

  LEPActivity: LEPActivity;
  Nanda: Nanda;
  NandaInfo: CodeValue;
  PatientEncounter: PatientEncounter;

  favoriteConfiguration: boolean;

  @Output() select : EventEmitter<any> = new EventEmitter();

  constructor(injector: Injector,
              private PatientEncounterAction: PatientEncounterActionService,
              private LEPActivityAction: LEPActivityActionService,
              public DictionaryManager: DictionaryManager,
              private conversationActions: ConversationActions,
              private translateService: TranslateService,
              private viewManager: ViewManager) {
    super(injector);

    this.conversation$.subscribe(res => {
      this.LEPActivity = res.LEPActivity;
      this.Nanda = res.Nanda;
      this.PatientEncounter = res.PatientEncounter;
      this.favoriteConfiguration = res.favoriteConfiguration;
    });

    if (this.favoriteConfiguration || !this.Nanda) {
      this.typeChanged('all');
    } else {
      this.typeChanged('suggested');
    }
  }

  getAllOrFavorites() {
    let si: Array<SelectItem> = [];

    if (!this.favoriteConfiguration && this.Nanda) {
      si.push({value: 'suggested', label: this.translateService.instant('suggested')});
    }
    si.push({value: 'all', label: this.translateService.instant('all')});
    si.push({value: 'favorites', label: this.translateService.instant('favorites')});

    return si;
  }

  typeChanged(event: string) {
    let promise: Promise<any>;
    if (event === 'suggested' && this.Nanda) {
      promise = this.LEPActivityAction.getSuggestedLEP(this.Nanda.internalId);
    } else if (event === 'favorites') {
      const sdlId = this.PatientEncounterAction.getServiceDeliveryLocationId(this.PatientEncounter);
      promise = this.LEPActivityAction.getFavoriteLEP(sdlId);
    } else { // if (event.value === 'all') {
      promise = this.DictionaryManager.get('NANDA:K630119', true);
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

  info() {
    if (this.LEPActivity.nandaLep) {
      if (this.NandaInfo) {
        this.NandaInfo = null;
      } else {
        this.DictionaryManager.getById(this.LEPActivity.nandaLep.id).then((cv) => {
          this.NandaInfo = cv;
        });
      }
    }
  }

  lepChanged() {

  }

  onSelect() {
    this.select.emit(null);
  }

  ngOnDestroy() {
    this.conversationActions.remove('SuggestedLEPList');

    delete this.DictionaryManager.searchByName['NANDA:K630119'];
    this.DictionaryManager.searchResults['NANDA:K630119'] = [];
  }

  close() {
    this.viewManager.setPopupViewId(null);
  }

}
