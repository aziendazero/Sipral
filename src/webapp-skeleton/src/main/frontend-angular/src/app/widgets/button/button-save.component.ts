import { Component } from '@angular/core';
import { ButtonComponent } from './button.component';

@Component({
  selector: 'phi-buttonsave',
  templateUrl: './button.component.html',
})
export class ButtonSaveComponent extends ButtonComponent {

  constructor() {
    super();
    this.mnemonicName = 'SAVE';
    this.btnClass = 'fa fa-floppy-o fa-2x';
  }

}
