import { Component } from '@angular/core';
import { ButtonComponent } from './button.component';

@Component({
  selector: 'phi-buttonok',
  templateUrl: './button.component.html',
})
export class ButtonOkComponent extends ButtonComponent {

  constructor() {
    super();
    this.mnemonicName = 'OK';
    this.btnClass = 'fa fa-check fa-2x';
  }

}
