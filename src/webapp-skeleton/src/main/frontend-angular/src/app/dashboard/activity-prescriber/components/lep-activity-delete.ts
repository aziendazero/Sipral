import { Component, Injector, Output, EventEmitter } from '@angular/core';
import { select } from '@angular-redux/store';
import { BaseForm } from '../../../widgets/form/base-form';
import { Config } from '../../../store/config.reducer';
import { Employee } from '../../../services/entities/role';
import { LEPActivity } from '../../../services/entities/act';

@Component({
selector: 'phi-lep-activity-delete',
templateUrl: './lep-activity-delete.html'
})
export class LepActivityDelete extends BaseForm  {
  
  @Output() delete : EventEmitter<any> = new EventEmitter();
  
    @select(['conversation']) conversation$;
    @select(['config']) config$;
  
    LEPActivity: LEPActivity;
    Employee: Employee;
  
    constructor(injector: Injector) {
      super(injector);
  
      this.conversation$.subscribe(res => {
        this.LEPActivity = res.LEPActivity;
      });
  
      this.config$.subscribe( (cfg: Config) => {
        this.Employee = cfg.employee;
      });
  
      if (!this.LEPActivity.cancellationDate) {
        this.LEPActivity.cancellationDate = new Date();
      }
  
      if (!this.LEPActivity.cancelledBy) {
        this.LEPActivity.cancelledBy = this.Employee;
      }
    }
  
    onDelete() {
      this.delete.emit(null);
    }

}
