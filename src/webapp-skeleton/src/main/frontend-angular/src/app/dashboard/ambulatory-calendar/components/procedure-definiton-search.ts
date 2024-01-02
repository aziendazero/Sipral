import { TranslateService } from '@ngx-translate/core';
import { Component, EventEmitter, Injector, OnDestroy, Output } from '@angular/core';
import { Location } from '@angular/common';
import { select } from '@angular-redux/store';
import { BaseForm } from '../../../widgets/form/base-form';
import { ProcedureDefinition } from '../../../services/entities/act';
import { ProcedureDefinitionActionService } from '../../../services/actions';
import { ConversationActions } from '../../../actions/conversation.actions';
import { Datamodel } from '../../../services/datamodel/datamodel';
import { ActivatedRoute } from '@angular/router';
import { color2hex } from '../../../services/converters/any.converter';

@Component({
selector: 'phi-procedure-definiton-search',
templateUrl: './procedure-definiton-search.html'
})
export class ProcedureDefinitonSearch extends BaseForm implements OnDestroy {
  @select(['conversation', 'ProcedureDefinitionList']) ProcedureDefinitionList$;
  procedureDefinitionListSub;
  ProcedureDefinitionList: Datamodel;

  ProcedureDefinition: ProcedureDefinition;

  @Output() select: EventEmitter<any> = new EventEmitter();

  color2hex = color2hex;

  constructor(injector: Injector,
              private route: ActivatedRoute,
              private location: Location,
              private ProcedureDefinitionAction: ProcedureDefinitionActionService,
              private conversationActions: ConversationActions,
              private translateService: TranslateService) {
    super(injector);

    const sdlId = this.route.snapshot.params['sdlId'];

    this.procedureDefinitionListSub = this.ProcedureDefinitionList$.subscribe(
      res => {
        this.ProcedureDefinitionList = res;
        if (this.ProcedureDefinitionList && this.ProcedureDefinitionList.entities) {
          this.ProcedureDefinitionList.entities.forEach((proc: ProcedureDefinition) => {
            if (this.translateService.store.currentLang === 'it') {
              proc.codeIcd9.currentTranslation = proc.codeIcd9['langIt'];
            } else if (this.translateService.store.currentLang === 'de') {
              proc.codeIcd9.currentTranslation = proc.codeIcd9['langDe'];
            } else if (this.translateService.store.currentLang === 'en') {
              proc.codeIcd9.currentTranslation = proc.codeIcd9['langEn'];
            }
            if (!proc.codeIcd9.currentTranslation) {
              proc.codeIcd9.currentTranslation = proc.codeIcd9['displayName'];
            }
          });
        }
      }
    );

    this.ProcedureDefinitionAction.cleanRestrictions();
    this.ProcedureDefinitionAction.filterBySdl = false;
    this.ProcedureDefinitionAction.readPageSize = 0; // NOT paged because paginatione onScroll not work on some browsers.
    this.ProcedureDefinitionAction.equal['procedureSDL.internalId'] = sdlId;
    this.ProcedureDefinitionAction.select.push('internalId');
    this.ProcedureDefinitionAction.select.push('text');
    this.ProcedureDefinitionAction.select.push('color');
    this.ProcedureDefinitionAction.select.push('codeIcd9.id');
    this.ProcedureDefinitionAction.select.push('codeIcd9.code');
    this.ProcedureDefinitionAction.select.push('codeIcd9.displayName');
    this.ProcedureDefinitionAction.select.push('codeIcd9.langIt');
    this.ProcedureDefinitionAction.select.push('codeIcd9.langDe');
    this.ProcedureDefinitionAction.select.push('codeIcd9.langEn');
    this.ProcedureDefinitionAction.select.push('regionalCodeIcd9');
    this.ProcedureDefinitionAction.select.push('defaultLength.id');
    this.ProcedureDefinitionAction.select.push('defaultLength.code');
    this.ProcedureDefinitionAction.select.push('defaultLength.parent.displayName');
    this.ProcedureDefinitionAction.select.push('defaultLength.codeSystem.name');
    this.ProcedureDefinitionAction.select.push('defaultLength.score');
    this.ProcedureDefinitionAction.orderBy['codeIcd9.langIt'] = 'ascending';
    this.ProcedureDefinitionAction.read();
  }

  ngOnDestroy() {
    this.procedureDefinitionListSub.unsubscribe();
    this.conversationActions.remove('ProcedureDefinitionList');
  }

  ie(entity, conversationName): Promise<any> {
    return this.ProcedureDefinitionAction.inject(
       entity.internalId
       , null, [
        'codeIcd9',
        'regionalCodeIcd9',
        'defaultLength'
      ]).then((pd: ProcedureDefinition) => this.ProcedureDefinition = pd).then(() => Promise.resolve(this.ProcedureDefinition));
    /*
    this.ProcedureDefinition = entity;
    return Promise.resolve(entity);
    */
  }

  onSelect() {
    this.select.emit(this.ProcedureDefinition);
    this.location.back();
  }

  onCancel() {
    this.location.back();
  }
}
