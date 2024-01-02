import { Component } from '@angular/core';
import { ButtonComponent } from './button.component';

@Component({
  selector: 'phi-buttonremove',
  templateUrl: './button.component.html',
})
export class ButtonRemoveComponent extends ButtonComponent {

  constructor() {
    super();
    this.mnemonicName = 'REMOVE';
    this.btnClass = 'fa fa-2x fa-minus';
  }

}
