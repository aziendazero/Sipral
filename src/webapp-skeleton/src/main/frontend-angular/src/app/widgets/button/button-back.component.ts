import { Component } from '@angular/core';
import { ButtonComponent } from './button.component';

@Component({
  selector: 'phi-buttonback',
  templateUrl: './button.component.html',
})
export class ButtonBackComponent extends ButtonComponent {

  constructor() {
    super();
    this.mnemonicName = 'BACK';
    this.btnClass = 'fa fa-arrow-left fa-2x';
    // this.addStyle('fa fa-refresh fa-2x');
  }

}
