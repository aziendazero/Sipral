import { Component, OnInit, EventEmitter, Input, Output, HostBinding, ViewChild } from '@angular/core';
import { NgForm, AbstractControl } from '@angular/forms';
import { BaseEntity } from '../../services/entities/base-entity/base-entity';

@Component({
  selector: 'phi-button',
  templateUrl: './button.component.html',
  styleUrls: ['./button.component.scss']
})
export class ButtonComponent implements OnInit {
  @Input() mnemonicName: string;
  @Input() inject: BaseEntity;
  @Input() tooltip: string;
  @Input() styleClass: string;
  @Input() renderedEl: boolean;
  @Input() disabled: boolean;
  @Input() form: NgForm; // FIXME: add to template:  [form]="form"
  @Output() binding: EventEmitter<any> = new EventEmitter<any>();

  @HostBinding('class') clazz = 'phi-widget';

  @ViewChild('button') button;
  @Input() autoFocus = false;

  btnClass = '';

  ngOnInit() {
    if (typeof this.renderedEl === 'undefined') {
      this.renderedEl = true;
    }
  }

  ngAfterViewInit() {
    if (this.autoFocus) {
      if (!this.button) {
        console.log('WARNING: #button is missing in the widget. Autofocus will not work');
        return;
      }
      this.button.nativeElement.focus();
    }
  }

  handleClick(event: Event) {
    if (!this.form || !this.form.invalid) {
      event.stopPropagation();
      this.binding.emit(event);
    }
    if (this.form && this.form.invalid) {
      Object.keys(this.form.controls).map(key => {
        let ctrl: AbstractControl = this.form.controls[key];
        if (!ctrl.valid && !ctrl.disabled) {
          ctrl.markAsTouched();
          // console.log(key + ' ' + JSON.stringify(ctrl.errors));
        }
      });
    }
  }

  click() {
    if (!this.button) {
      // console.log('WARNING: #button is missing in the widget. Autofocus will not work');
      return;
    }
    this.button.nativeElement.click();
  }
}
