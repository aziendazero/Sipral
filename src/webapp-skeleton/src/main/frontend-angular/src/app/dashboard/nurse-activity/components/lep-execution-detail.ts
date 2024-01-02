import { Component, Injector } from '@angular/core';
import { select } from '@angular-redux/store';
import { BaseForm } from '../../../widgets/form/base-form';
@Component({
selector: 'phi-lep-execution-detail',
templateUrl: './lep-execution-detail.html'
})
export class LepExecutionDetail extends BaseForm  {
  @select(['conversation', 'NurseActivity']) NurseActivity$;
  NurseActivity;
  @select(['conversation', 'LEPExecution']) LEPExecution$;
  LEPExecution;

  constructor(injector: Injector) {
    super(injector);
    this.NurseActivity$.subscribe(res => this.NurseActivity = res);
    this.LEPExecution$.subscribe(res => this.LEPExecution = res);
  }
}
