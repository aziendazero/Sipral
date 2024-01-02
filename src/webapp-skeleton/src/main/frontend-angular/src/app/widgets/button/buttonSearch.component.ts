import { Component } from '@angular/core';
import { ButtonComponent } from './button.component';

@Component({
  selector: 'phi-buttonsearch',
  templateUrl: './button.component.html',
})
export class ButtonSearchComponent extends ButtonComponent {

  constructor() {
    super();
    this.mnemonicName = 'SEARCH';
    this.btnClass = 'fa fa-search fa-2x';
    // this.addStyle('fa fa-search fa-2x');
  }

}
