import { Component } from '@angular/core';
import { ButtonComponent } from './button/button.component';

@Component({
  selector: 'phi-link',
  template: `
  <a (click)="handleClick($event)" class={{styleClass}} *ngIf="renderedEl" title={{tooltip}} [class.disabled]="disabled">
    <ng-content></ng-content>
  </a>
  `
})
export class LinkComponent extends ButtonComponent {

}
