import { Component, Injector, Output, EventEmitter } from '@angular/core';
import { select } from '@angular-redux/store';
import { BaseForm } from '../../../widgets/form/base-form';
import { ObjectiveNanda } from '../../../services/entities/act';
import {ViewManager} from '../../../services/view-manager';

@Component({
selector: 'phi-objective-close',
templateUrl: './objective-close.html'
})
export class ObjectiveClose extends BaseForm  {
  @select(['conversation']) conversation$;

  ObjectiveNanda: ObjectiveNanda;
  CarePlan;

  date = new Date();

  @Output() save : EventEmitter<any> = new EventEmitter();

  constructor(injector: Injector,
              private viewManager: ViewManager) {
    super(injector);
    this.conversation$.subscribe(res => {
      this.ObjectiveNanda = res.ObjectiveNanda;
      this.CarePlan = res.CarePlan;
    });
  }

  onSave() {
    this.save.emit(null);
  }

  close() {
    this.viewManager.setPopupViewId(null);
  }

}
