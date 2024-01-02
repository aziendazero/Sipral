import { Injectable, Injector } from '@angular/core';

import { BaseActionService } from './base-action.service';
import { FavoriteTab } from '../entities/favorite-tab';

@Injectable()
export class FavoriteTabActionService extends BaseActionService<FavoriteTab> {

  constructor(injectorz: Injector) {
    super(injectorz);
    this.entityName = 'FavoriteTab';
  }

}
