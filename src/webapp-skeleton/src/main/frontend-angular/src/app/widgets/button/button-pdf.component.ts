import { Component } from '@angular/core';
import { ButtonComponent } from './button.component';

@Component({
  selector: 'phi-buttonpdf',
  templateUrl: './button.component.html',
})
export class ButtonPdfComponent extends ButtonComponent {

  constructor() {
    super();
    this.mnemonicName = 'PDF';
    this.btnClass = 'fa fa-file-pdf-o fa-2x';
  }

}
