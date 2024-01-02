import { Injector } from '@angular/core';

import { BaseActionService } from '../../services/actions/base-action.service';
import { BaseEntity } from '../../services/entities/base-entity/base-entity';

export class BaseForm {

  constructor(private injector: Injector) {}

  /**
   * Inject / eject entity
   * @param entity
   * @param conversationName
   * @returns {Promise<BaseEntity>}
     */
  ie(entity, conversationName, event=null): Promise<any> {
    const action: BaseActionService<BaseEntity> = this.injector.get(conversationName + 'Action'); // FIXME
    return action.inject(entity.internalId);
  }

  /**
   * Check if injected, if not inject
   * @param entity
   * @param conversationName
   * @returns {any}
     */
  inject(entity, conversationName): Promise<any> {
    if (this.selected(entity, conversationName)) {
      return Promise.resolve(this[conversationName]);
    } else {
      return this.ie(entity, conversationName);
    }
  }

  /**
   * Check if injected
   * @param entity
   * @param conversationName
   * @returns {boolean}
     */
  selected(entity, conversationName) {
    const injectedEntity = this[conversationName];
    if (!injectedEntity) {
      return false;
    }
    const sel = entity === injectedEntity
      || entity.internalId && (entity.internalId === injectedEntity.internalId)
      || entity.internalid && (entity.internalid === injectedEntity.internalId) // FIXME REMOVE
      || entity.internalid && (entity.internalid === injectedEntity.internalid); // FIXME REMOVE
    return sel;
  }

}
