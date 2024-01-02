import { Injectable, Injector } from '@angular/core';

import { BaseActionService } from './base-action.service';
import { Medicine } from '../entities/base-entity';

@Injectable()
export class MedicineActionService extends BaseActionService<Medicine> {

  constructor(injectorz: Injector) {
    super(injectorz);
    this.entityName = 'Medicine';
  }

}
