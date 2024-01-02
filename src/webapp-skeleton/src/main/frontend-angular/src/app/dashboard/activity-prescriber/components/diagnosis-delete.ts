import { Component, Injector, Output, EventEmitter } from '@angular/core';
import { select } from '@angular-redux/store';
import { BaseForm } from '../../../widgets/form/base-form';
import { Config } from '../../../store/config.reducer';
import { Employee } from '../../../services/entities/role/employee';
import { Nanda } from '../../../services/entities/act/nanda';

@Component({
selector: 'phi-diagnosis-delete',
templateUrl: './diagnosis-delete.html'
})
export class DiagnosisDelete extends BaseForm  {

  @Output() delete : EventEmitter<any> = new EventEmitter();

  @select(['conversation']) conversation$;
  @select(['config']) config$;

  Nanda: Nanda;
  Employee: Employee;

  constructor(injector: Injector) {
    super(injector);

    this.conversation$.subscribe(res => {
      this.Nanda = res.Nanda;
    });

    this.config$.subscribe( (cfg: Config) => {
      this.Employee = cfg.employee;
    });

    if (!this.Nanda.cancellationDate) {
      this.Nanda.cancellationDate = new Date();
    }

    if (!this.Nanda.cancelledBy) {
      this.Nanda.cancelledBy = this.Employee;
    }
  }

  onDelete() {
    this.delete.emit(null);
  }
}
