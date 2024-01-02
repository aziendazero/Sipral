import { Injectable, Injector } from '@angular/core';
import { BaseActionService } from './base-action.service';
import { AppointmentGrouper } from '../entities/base-entity';

@Injectable()
export class AppointmentGrouperActionService extends BaseActionService<AppointmentGrouper> {

  constructor(injectorz: Injector) {
    super(injectorz);
    this.entityName = 'AppointmentGrouper';
  }

}
