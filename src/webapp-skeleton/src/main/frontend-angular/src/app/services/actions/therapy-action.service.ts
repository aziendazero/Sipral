import { Injectable, Injector } from '@angular/core';

import { BaseActionService } from './base-action.service';
import { Therapy } from '../entities/act';

@Injectable()
export class TherapyActionService extends BaseActionService<Therapy> {

  constructor(injectorz: Injector) {
    super(injectorz);
    this.entityName = 'Therapy';
    this.entityUrl = 'therapies';
  }

}
