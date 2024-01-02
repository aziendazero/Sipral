import {Component, EventEmitter, Injector, OnInit, Output} from '@angular/core';
import { select } from '@angular-redux/store';
import { BaseForm } from '../../widgets/form/base-form';
import { FavoriteSection } from '../../services/entities/favorite-section';

import { FavoriteTab } from '../../services/entities/favorite-tab';
import { FavoriteSectionActionService } from '../../services/actions/favorite-section-action.service';
import { ViewManager } from '../../services/view-manager';

@Component({
  templateUrl: './favorite-section.html'
})
export class FavoriteSectionComponent extends BaseForm implements OnInit {

  @select(['conversation', 'FavoriteTab']) FavoriteTab$;
  FavoriteTab: FavoriteTab;
  @select(['conversation', 'FavoriteSection']) FavoriteSection$;
  FavoriteSection: FavoriteSection;
  @select(['conversation', 'column']) column$;
  column: number;

  @Output() save : EventEmitter<any> = new EventEmitter();

  viewManager: ViewManager;
  FavoriteSectionAction: FavoriteSectionActionService;

  constructor(injector: Injector) {
    super(injector);
    this.FavoriteTab$.subscribe(res => this.FavoriteTab = res);
    this.FavoriteSection$.subscribe(res => this.FavoriteSection = res);
    this.column$.subscribe(res => this.column = res);

    this.viewManager = injector.get(ViewManager);
    this.FavoriteSectionAction = injector.get(FavoriteSectionActionService);
  }

  ngOnInit() {
    // if (this.data) { //Create
    //   this.FavoriteSectionAction.newEntity();
    //   this.favoriteTab = this.data.FavoriteTab;
    //   this.column = this.data.column;
    // }
    if (this.FavoriteSection && ! this.FavoriteSection.sortOrder) {
      this.FavoriteSection.sortOrder = this.getMaxSortOrder();
    }
  }

  getMaxSortOrder() {
    const sectionsOfThisColumn: Array<FavoriteSection> = this.FavoriteTab.section.filter((section: FavoriteSection) =>
      section.columnIndex === this.column
    );

    return sectionsOfThisColumn.length + 1;
  }


  onSave() {
    if (!this.FavoriteSection.internalId) {

      this.FavoriteSection.columnIndex = this.column;
      this.FavoriteSection.tab = {internalId: this.FavoriteTab.internalId, entityName: 'com.phi.entities.FavoriteTab'};
      this.FavoriteSectionAction.create().then(() => {
        this.save.next(null);
        this.close();
      });

    } else {

      this.FavoriteSectionAction.entity = this.FavoriteSection;
      this.FavoriteSectionAction.update().then(() => {
        this.save.next(null);
        this.close();
      });

    }
  }

  close() {
    this.viewManager.setPopupViewId(null);
  }


}
