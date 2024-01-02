import { Component } from '@angular/core';
import { ButtonComponent } from './button.component';

@Component({
  selector: 'phi-buttonedit',
  templateUrl: './button.component.html',
})
export class ButtonEditComponent extends ButtonComponent {

  constructor() {
    super();
    this.mnemonicName = 'EDIT';
    this.btnClass = 'fa fa-pencil-square-o fa-2x';
  }

}
