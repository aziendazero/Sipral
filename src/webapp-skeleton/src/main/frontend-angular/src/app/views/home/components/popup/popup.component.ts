import { Component, Output, EventEmitter, Input } from '@angular/core';

@Component({
  selector: 'phi-popup',
  templateUrl: './popup.component.html',
  styleUrls: ['./popup.component.scss']
})
export class PopupComponent {

  @Input() title: String = '';
  @Input() fullScreen = false;
  @Input() modal = true;

  @Output() onClose : EventEmitter<any> = new EventEmitter();

  close() {
    this.onClose.emit(null);
  }

}
