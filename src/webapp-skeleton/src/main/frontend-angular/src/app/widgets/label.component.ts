import { Component, Input, HostBinding } from '@angular/core';

@Component({
  selector: 'phi-label',
  template: `
    <label *ngIf="widgetLabel" for={{id}}>{{widgetLabel}}</label>
    <span class={{styleClass}}>{{binding}}</span>
  `
})
export class LabelComponent {
  @Input() id: string;
  @Input() binding: string;
  @Input() styleClass: string;
  @Input() render: string;
  @Input() renderedEl: string;
  @Input() widgetLabel: string;

  @HostBinding('class') clazz = 'lbl';
}
