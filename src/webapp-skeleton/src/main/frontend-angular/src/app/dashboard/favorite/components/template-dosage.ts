import { Component, Injector } from '@angular/core';
import { select } from '@angular-redux/store';
import { BaseForm } from '../../../widgets/form/base-form';
import { Datamodel } from '../../../services/datamodel/datamodel';
import { TemplateDosageActionService } from '../../../services/actions';
import { ConversationActions } from '../../../actions/conversation.actions';
import { SelectItem } from '../../../services/datamodel/select-item';
import { ServiceDeliveryLocation } from '../../../services/entities/role';
import { ViewManager } from '../../../services/view-manager';

@Component({
selector: 'phi-template-dosage',
templateUrl: './template-dosage.html'
})
export class TemplateDosage extends BaseForm {

  @select(['conversation']) conversation$;

  ServiceDeliveryLocation: ServiceDeliveryLocation;
  TemplateDosageList: Datamodel;
  TemplateDosage; //: TemplateDosage;
  favoriteType: 'THERAPY' | 'ACTIVITY' = 'THERAPY';

  days: Array<SelectItem> =  [{label: '0', value: 0}, {label: '+1', value: 1}, {label: '+2', value: 2},
    {label: '+3', value: 3}, {label: '+4', value: 4}, {label: '+5', value: 5}, {label: '+6', value: 6}];

  constructor(injector: Injector,
              private TemplateDosageAction: TemplateDosageActionService,
              private conversationActions: ConversationActions,
              private viewManager: ViewManager) {
    super(injector);

    this.conversation$.subscribe(res => {
      this.ServiceDeliveryLocation = res.ServiceDeliveryLocation;
      this.TemplateDosageList = res.TemplateDosageList;
      this.TemplateDosage = res.TemplateDosage;
      this.favoriteType = res.favoriteType;
    });
  }

  ie(entity, conversationName): Promise<any> {
    if (!this.TemplateDosage || this.TemplateDosage.internalId !== entity.internalId) {
      if (entity.internalId) {
        return this.TemplateDosageAction.inject(entity.internalId);
      } else {
        this.conversationActions.put(conversationName, entity);
      }
    } else {
      if (entity.internalId) {
        return this.TemplateDosageAction.eject();
      } else {
        this.conversationActions.remove(conversationName);
      }
    }
  }

  addTemplate() {
    if (!this.TemplateDosageList) {
      this.conversationActions.put('TemplateDosageList', new Datamodel([]));
    }
    let templateDosage = this.TemplateDosageAction.newTemplateDosage(this.ServiceDeliveryLocation.internalId, this.favoriteType);
    this.TemplateDosageList.entities.push(templateDosage);
  }

  deleteTemplate(TemplateDosage) {
    const templateIndex = this.TemplateDosageList.entities.indexOf(TemplateDosage);
    if (templateIndex !== -1) {
      this.TemplateDosageList.entities.splice(templateIndex, 1);
    }
    this.TemplateDosageAction.entity = TemplateDosage;
    this.TemplateDosageAction.delete();
  }

  save(TemplateDosage) {
    if (TemplateDosage) {
      this.TemplateDosageAction.entity = TemplateDosage;
      this.TemplateDosageAction.create();
    }
    this.close();
  }

  close() {
    this.conversationActions.remove('TemplateDosage');
    this.viewManager.setPopupViewId(null);
  }

}
