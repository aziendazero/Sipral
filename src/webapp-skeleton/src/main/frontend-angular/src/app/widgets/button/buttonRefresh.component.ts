import { Component } from '@angular/core';
import { ButtonComponent } from './button.component';

@Component({
  selector: 'phi-buttonrefresh',
  templateUrl: './button.component.html',
})
export class ButtonRefreshComponent extends ButtonComponent {

  constructor() {
    super();
    this.mnemonicName = 'REFRESH';
    this.btnClass = 'fa fa-refresh fa-2x';
    // this.addStyle('fa fa-refresh fa-2x');
  }

}
