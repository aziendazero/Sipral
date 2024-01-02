import {Component, EventEmitter, Injector, Output} from '@angular/core';
import { select } from '@angular-redux/store';
import { BaseForm } from '../../widgets/form/base-form';

import { FavoriteTabActionService } from '../../services/actions';
import { FavoriteTab } from '../../services/entities/favorite-tab';
import { Datamodel } from '../../services/datamodel/datamodel';
import { ViewManager } from '../../services/view-manager';
import {ConversationActions} from '../../actions/conversation.actions';

@Component({
  templateUrl: './favorite-tab.html'
})
export class FavoriteTabComponent extends BaseForm {
  @select(['conversation', 'FavoriteTabList']) FavoriteTabList$;
  FavoriteTabList: Datamodel;
  @select(['conversation', 'FavoriteTab']) FavoriteTab$;
  FavoriteTab: FavoriteTab;

  data: any;

  @Output() closeEvent : EventEmitter<any> = new EventEmitter();

  constructor(injector: Injector,
              private FavoriteTabAction: FavoriteTabActionService,
              private viewManager: ViewManager,
              private conversationActions: ConversationActions) {

    super(injector);

    this.FavoriteTab$.subscribe(res => this.FavoriteTab = res);
    this.FavoriteTabList$.subscribe(res => this.FavoriteTabList = res);
  }

  onSave() {

      this.FavoriteTabAction.entity = this.FavoriteTab;
      this.FavoriteTabAction.create().then(() => {
        this.closeEvent.emit(null);
        this.close()
      });
  }

  close() {
    this.conversationActions.remove('FavoriteTab');
    this.viewManager.setPopupViewId(null);
    this.closeEvent.emit(null);
  }


}
