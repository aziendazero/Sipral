import { Injectable, Injector } from '@angular/core';

import { BaseActionService } from './base-action.service';
import { FavoriteSection } from '../entities/favorite-section';

@Injectable()
export class FavoriteSectionActionService extends BaseActionService<FavoriteSection> {

  constructor(injectorz: Injector) {
    super(injectorz);
    this.entityName = 'FavoriteSection';
  }

}
