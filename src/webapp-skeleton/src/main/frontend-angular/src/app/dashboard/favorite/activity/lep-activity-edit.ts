import { Component, Injector, Output, EventEmitter } from '@angular/core';
import { select } from '@angular-redux/store';
import { BaseForm } from '../../../widgets/form/base-form';
import { TemplateDosageActionService } from '../../../services/actions/template-dosage-action.service';
import { Datamodel } from '../../../services/datamodel/datamodel';
import { LEPActivity, Nanda } from '../../../services/entities/act';
import { DictionaryManager } from '../../../services/dictionary-manager';
import { FavoriteProfile } from '../../../services/entities/favorite-profile';
import { ViewManager } from '../../../services/view-manager';


@Component({
selector: 'phi-lep-activity-edit',
templateUrl: './lep-activity-edit.html'
})
export class LepActivityEdit extends BaseForm  {

  @select(['conversation']) conversation$;
  LEPActivityList: Datamodel;
  FavoriteProfile: FavoriteProfile;
  favoriteConfiguration: boolean;

  @Output() addActivity : EventEmitter<any> = new EventEmitter();
  @Output() save : EventEmitter<any> = new EventEmitter();

  info = {};

  constructor(injector: Injector,
              public TemplateDosageAction: TemplateDosageActionService,
              private DictionaryManager: DictionaryManager,
              private viewManager: ViewManager) {
    super(injector);

    this.conversation$.subscribe(res => {
      this.LEPActivityList = res.LEPActivityList;
      this.FavoriteProfile = res.FavoriteProfile;
      this.favoriteConfiguration = res.favoriteConfiguration;
    });
  }
  onAddActivity() {
    this.addActivity.emit(null);
  }

  onRemoveActivity(LEPActivity: LEPActivity) {
    const idx = this.LEPActivityList.entities.indexOf(LEPActivity);
    if (idx !== -1) {
      this.LEPActivityList.entities.splice(idx, 1);
    }
  }

  infoActivity(LEPActivity: LEPActivity) {
    if (LEPActivity.nandaLep) {
      if (!this.info[LEPActivity.nandaLep.id]) {
        this.DictionaryManager.getById(LEPActivity.nandaLep.id).then((cv) => {
          this.info[LEPActivity.nandaLep.id] = cv;
          // LEPActivity.nandaLep = cv;
        });
      } else {
        delete this.info[LEPActivity.nandaLep.id];
      }
    }
  }

  enableEndDateChange(LEPActivity: LEPActivity) {
    if (!LEPActivity.effectiveDate.high) {
      LEPActivity.effectiveDate.high = LEPActivity.effectiveDate.low;
    } else {
      LEPActivity.effectiveDate.high = null;
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

  /**
   * Get nanda title
   * @returns {any}
   */
  getNandaTitle() {
      if (this.LEPActivityList && this.LEPActivityList.entities.length > 0 && this.LEPActivityList.entities[0].nanda) {
        let nanda: Nanda = this.LEPActivityList.entities[0].nanda;
        if (nanda.nandaDiag) {
          return nanda.progNumber + ' - ' + nanda.nandaDiag.currentTranslation;
        }
        return nanda.progNumber + ' - ' + nanda.titleDiag;
      }
    return '';
  }

  onSave() {
    this.save.emit(null);
  }

  close() {
    this.viewManager.setPopupViewId(null);
  }
}
