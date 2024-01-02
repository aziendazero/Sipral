import { Component } from '@angular/core';
import { ButtonComponent } from './button.component';

@Component({
  selector: 'phi-buttoncancel',
  templateUrl: './button.component.html',
})
export class ButtonCancelComponent extends ButtonComponent {

  constructor() {
    super();
    this.mnemonicName = 'CANCEL';
    this.btnClass = 'fa fa-times fa-2x';
  }

}
