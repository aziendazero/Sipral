import { Injectable, Injector } from '@angular/core';

import { BaseActionService } from './base-action.service';
import { FavoriteProfile } from '../entities/favorite-profile';
import { FavoriteSection } from '../entities/favorite-section';

@Injectable()
export class FavoriteProfileActionService extends BaseActionService<FavoriteProfile> {

  constructor(injectorz: Injector) {
    super(injectorz);
    this.entityName = 'FavoriteProfile';
  }

  public newFavoriteProfile(favoriteSection: FavoriteSection): FavoriteProfile {
    this.entity = {};

    this.entity.section = { internalId: favoriteSection.internalId, entityName: 'com.phi.entities.FavoriteSection' };
    this.entity.sortOrder = favoriteSection.profile.length + 1;

    this.conversationActions.put(this._entityName, this.entity);
    return this.entity;
  }

}
