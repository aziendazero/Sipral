import { NgModule } from '@angular/core';
import { ChartsModule } from 'ng2-charts';
import { SharedModule } from '../../shared/shared.module';

import { Poc } from './poc';
import { PocAction } from './actions/poc-action.service';
import { VitalSignActionService } from '../../services/actions';
import { routing } from "./poc.routing";


@NgModule({
  imports:      [
    SharedModule,
    ChartsModule,
    routing
  ],
  declarations: [
    Poc
  ],
  exports: [

  ],
  providers: [
    PocAction,
    VitalSignActionService
  ],
  entryComponents: [

  ]
})
export class PocModule { }
