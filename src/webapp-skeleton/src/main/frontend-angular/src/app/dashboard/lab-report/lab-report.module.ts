import { NgModule } from '@angular/core';

import { LabReport } from './lab-report';

import { routing } from "./lab-report.routing";
import { SharedModule } from '../../shared/shared.module';
import { LisAction } from './actions/lis-action.service';


@NgModule({
  imports:      [
    SharedModule,
    routing
  ],
  declarations: [
    LabReport
  ],
  exports: [

  ],
  providers: [
    LisAction
  ],
  entryComponents: [

  ]
})
export class LabReportModule { }
