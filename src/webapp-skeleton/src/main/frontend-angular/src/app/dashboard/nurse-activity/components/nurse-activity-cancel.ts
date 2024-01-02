import { Component, Injector, Output, EventEmitter } from '@angular/core';
import { select } from '@angular-redux/store';
import { BaseForm } from '../../../widgets/form/base-form';
@Component({
selector: 'phi-nurse-activity-cancel',
templateUrl: './nurse-activity-cancel.html'
})
export class NurseActivityCancel extends BaseForm  {
  @select(['conversation', 'note']) note$;
  note;

  @Output() ok : EventEmitter<any> = new EventEmitter();

  constructor(injector: Injector) {
    super(injector);
    this.note$.subscribe(res => this.note = res);
  }

  save() {
    this.ok.emit(null);
  }
}
