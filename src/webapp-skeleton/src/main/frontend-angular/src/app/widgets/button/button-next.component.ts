import { Component } from '@angular/core';
import { ButtonComponent } from './button.component';

@Component({
  selector: 'phi-buttonnext',
  templateUrl: './button.component.html',
})
export class ButtonNextComponent extends ButtonComponent {

  constructor() {
    super();
    this.mnemonicName = 'NEXT';
    this.btnClass = 'fa fa-arrow-right fa-2x';
    // this.addStyle('fa fa-refresh fa-2x');
  }

}
