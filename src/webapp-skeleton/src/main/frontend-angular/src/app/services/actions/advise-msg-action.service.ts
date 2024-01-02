import { Injectable, Injector } from '@angular/core';

import { BaseActionService } from './base-action.service';
import { AdviseMsg } from '../entities/base-entity';

@Injectable()
export class AdviseMsgActionService extends BaseActionService<AdviseMsg> {

  private static READED = '/readed';

  constructor(injectorz: Injector) {
    super(injectorz);
    this.entityName = 'AdviseMsg';
  }

  setAsRead(): Promise<any> {

    const url = this.apiUrl + this.restBaseUrl + this.entityUrl + AdviseMsgActionService.READED
      + ';jsessionid=' + this.sid + '?conversationId=' + this.cid;

    return this.httpService.fetch(url, { method: 'POST', credentials: 'include' })
      .then(response => {
        return true;
      });

  }

}
