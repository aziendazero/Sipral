import { Component, Injector, Output, EventEmitter } from '@angular/core';
import { select } from '@angular-redux/store';
import { BaseForm } from '../../../widgets/form/base-form';
@Component({
  selector: 'phi-needs-based-warning',
  templateUrl: './needs-based-warning.html',
  styles: [ '#lyt-systolic, #lyt-diastolic, #lyt-temperature, #lyt-glycemia, ' +
            '#lyt-diuresis, #lyt-pain, #lyt-heartrate, #lyt-spo2 {justify-content: space-between;}'],
})
export class NeedsBasedWarning extends BaseForm  {
  @select(['conversation', 'asNeededDetailsData']) asNeededDetailsData$;
  asNeededDetailsData;

  @Output() ok : EventEmitter<any> = new EventEmitter();

  constructor(injector: Injector) {
    super(injector);
    this.asNeededDetailsData$.subscribe(res => this.asNeededDetailsData = res);
  }

  openAdminPopUp() {
    this.ok.emit(null);
  }
}
