import { Component } from '@angular/core';
import { ButtonComponent } from './button.component';

@Component({
  selector: 'phi-buttonadd',
  templateUrl: './button.component.html',
})
export class ButtonAddComponent extends ButtonComponent {

  constructor() {
    super();
    this.mnemonicName = 'ADD';
    this.btnClass = 'fa fa-2x fa-plus';
  }

}
