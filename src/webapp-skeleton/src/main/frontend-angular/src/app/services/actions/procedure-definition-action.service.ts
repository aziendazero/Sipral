import { Injectable, Injector } from '@angular/core';

import { BaseActionService } from './base-action.service';
import { ProcedureDefinition } from '../entities/act/procedure-definition';

@Injectable()
export class ProcedureDefinitionActionService extends BaseActionService<ProcedureDefinition> {

  constructor(injectorz: Injector) {
    super(injectorz);
    this.entityName = 'ProcedureDefinition';
  }

}
