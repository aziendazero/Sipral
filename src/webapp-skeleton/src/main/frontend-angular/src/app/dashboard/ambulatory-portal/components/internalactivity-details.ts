import { Component, Injector, Input } from '@angular/core';
import { BaseForm } from '../../../widgets/form/base-form';

@Component({
  selector: 'phi-internalactivity-details',
  templateUrl: './internalactivity-details.html'
})
export class InternalactivityDetails extends BaseForm  {
  @Input()
  InternalActivity;

  constructor(injector: Injector) {
    super(injector);
  }

}
