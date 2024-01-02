import { Component, Injector, Output, EventEmitter } from '@angular/core';
import { select } from '@angular-redux/store';
import { TranslateService } from '@ngx-translate/core';
import { BaseForm } from '../../../widgets/form/base-form';
import { DictionaryManager } from '../../../services/dictionary-manager';
import { PatientEncounterActionService, NandaActionService } from '../../../services/actions';
import { PatientEncounter, Nanda } from '../../../services/entities/act';
import { Employee } from '../../../services/entities/role';
import { CodeValue } from '../../../services/entities/data-types/code-value';
import { ConversationActions } from '../../../actions/conversation.actions';
import { Config } from '../../../store/config.reducer';
import { SelectItem } from '../../../services/datamodel/select-item';
import { WidgetEvent } from '../../../widgets/event/widget-event';
import { ViewManager } from '../../../services/view-manager';

@Component({
selector: 'phi-diagnosis',
templateUrl: './diagnosis.html'
})
export class Diagnosis extends BaseForm  {

  @select(['config']) config$;

  Employee: Employee;

  @select(['conversation']) conversation$;

  PatientEncounter: PatientEncounter;
  Nanda: Nanda;
  NandaInfo: CodeValue;
  NandaConsequenceInfo: CodeValue;
  NANDAEtiologicFactors: Array<CodeValue>;
  NANDASymptoms: Array<CodeValue>;
  NANDARiskFactors: Array<CodeValue>;

  CheckingResources: Array<SelectItem>;

  @select(['config', 'param']) param$;
  Param : Map<string, any>;

  now;
  showResources = false;
  showConsequence = false;

  @Output() create : EventEmitter<any> = new EventEmitter();

  constructor(injector: Injector,
              private translateService: TranslateService,
              private PatientEncounterAction: PatientEncounterActionService,
              private NandaAction: NandaActionService,
              public DictionaryManager: DictionaryManager,
              private viewManager: ViewManager,
              private conversationActions: ConversationActions) {

    super(injector);

    this.config$.subscribe( (cfg: Config) => {
      this.Employee = cfg.employee;
    });

    this.conversation$.subscribe(res => {
      this.PatientEncounter = res.PatientEncounter;
      this.Nanda = res.Nanda;
      this.NANDAEtiologicFactors = res.NANDAEtiologicFactors;
      this.NANDASymptoms = res.NANDASymptoms;
      this.NANDARiskFactors = res.NANDARiskFactors;
      this.CheckingResources = res.CheckingResources;
    });

    if (this.Nanda.statusCode.code == 'NANDACODED') {
      this.typeChanged( 'suggested' );
    }

    this.now = new Date();

    this.param$.subscribe(res => this.Param = res);
  }

  getAllOrFavorites() {
    return [{value: 'suggested', label: this.translateService.instant('suggested')},
            {value: 'all', label: this.translateService.instant('all')},
            {value: 'favorites', label: this.translateService.instant('favorites')}];
  }

  typeChanged(value: string) {
    let promise: Promise<any>;
    if (value === 'suggested') {
      promise = this.NandaAction.getSuggestedNandaAndCheckingResources(this.PatientEncounter.internalId);
    } else if (value === 'all') {
      promise = this.DictionaryManager.get('NANDA:NANDAStruktur', true);
    } else if (value === 'favorites') {
      const sdlId = this.PatientEncounterAction.getServiceDeliveryLocationId(this.PatientEncounter);
      promise = this.NandaAction.getFavoriteNanda(sdlId);
    }
    promise.then((domain) =>
      this.DictionaryManager.searchResults['NANDA:NANDAStruktur'] = domain
    );
  }

  diagChanged() {
    this.NandaAction.getNANDAFactors(this.Nanda.nandaDiag.id);
  }

  infoNanda() {
    if (this.Nanda.nandaDiag) {
      if (this.NandaInfo) {
        this.NandaInfo = null;
      } else {
        this.DictionaryManager.getById(this.Nanda.nandaDiag.id).then((cv) => {
          this.NandaInfo = cv;
        });
      }
    }
  }

  infoNandaConsequence() {
      if (this.Nanda.consequenceDiag) {
        if (this.NandaConsequenceInfo) {
          this.NandaConsequenceInfo = null;
        } else {
          this.DictionaryManager.getById(this.Nanda.consequenceDiag.id).then((cv) => {
            this.NandaConsequenceInfo = cv;
          });
        }
      }
    }

  toggleResource() {
    this.showResources = !this.showResources;
  }

  resourcesChanged(event: WidgetEvent) {
    if (event.selected) {
      this.Nanda.resources = this.Nanda.resources + '\n' + event.element;
    } else {
      this.Nanda.resources = this.Nanda.resources.replace(event.element, '');
    }
  }

  toggleConsequenceDiag() {
    this.showConsequence = !this.showConsequence;
  }

  cnsqncChanged(event: WidgetEvent) {
    this.showConsequence = false;
  }

  removeConsequenceDiag() {
    delete this.Nanda.consequenceDiag;
    this.NandaConsequenceInfo = null;
  }

  riskChanged(event: WidgetEvent) {
    if (event.value) {
      this.Nanda.riskCode.code = 'ATRISK';
    } else {
      this.Nanda.riskCode.code = 'NOTATRISK';
    }
  }

  search() {
    if (this.DictionaryManager.searchByName['NANDA:NANDAStruktur'] === null) {
      this.typeChanged('all');
    } else {
      this.DictionaryManager.search();
    }
  }


  save() {
    this.create.emit(null);
  }

  ngOnDestroy() {
    this.conversationActions.remove('NANDAEtiologicFactors');
    this.conversationActions.remove('NANDASymptoms');
    this.conversationActions.remove('NANDARiskFactors');

    this.conversationActions.remove('FavoriteNandaList');
    this.conversationActions.remove('SuggestedNandaList');
    this.conversationActions.remove('CheckingResources');

    delete this.DictionaryManager.searchByName['NANDA:NANDAStruktur'];
    this.DictionaryManager.searchResults['NANDA:NANDAStruktur'] = [];
  }

  close() {
    this.viewManager.setPopupViewId(null);
  }
}
