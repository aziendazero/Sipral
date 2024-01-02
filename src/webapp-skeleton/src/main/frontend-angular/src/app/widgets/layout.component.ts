import { Component, Input } from '@angular/core';

@Component({
  selector: '[phi-layout]',
  template: `
    <legend *ngIf="label !== undefined">{{label}}</legend>
    <ng-content></ng-content>
  `
})
export class LayoutComponent {
  @Input() id: string;
  @Input() label: string;
}
