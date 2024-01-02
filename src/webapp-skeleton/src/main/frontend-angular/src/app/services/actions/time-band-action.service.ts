import { Injectable, Injector } from '@angular/core';
import { select } from '@angular-redux/store';

import { BaseActionService } from './base-action.service';
import { TimeBand } from '../entities/base-entity/time-band';
import { Datamodel } from '../datamodel/datamodel';

@Injectable()
export class TimeBandActionService extends BaseActionService<TimeBand> {

  @select(['conversation', 'TimeBandList']) TimeBandList$;
  TimeBandList: Datamodel;

  constructor(injectorz: Injector) {
    super(injectorz);
    this.entityName = 'TimeBand';
    this.entityUrl = 'timebands';

    this.TimeBandList$.subscribe((res) => this.TimeBandList = res);
  }

  loadTimeBands(agendasId: Array<number>): Promise<Datamodel> {
    this.cleanRestrictions();
    this.readPageSize = 0;

    this.equal['serviceDeliveryLocation.internalId'] = agendasId;

    return this.read();
  }

}
