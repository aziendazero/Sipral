import {Component, ElementRef, HostBinding, Input} from '@angular/core';
import { Datamodel } from '../../services/datamodel/datamodel';

/**
 * Manages infinite scroll
 * overflow moved from Host to inner div, for IE11
 */

@Component({
  selector: '[phi-datagrid]',
  template: `
  <table>
    <ng-content></ng-content>
  </table>
  `
})
export class DataGridComponent {

  private _dataModel: Datamodel;

  private nativeElement: any;

  @Input('phi-datagrid')
  set dataModel(dm: Datamodel) {
    if (dm) {
      this._dataModel = dm;
      if (this.nativeElement) {
        this.nativeElement.addEventListener('scroll', ((event) => {
          if (event.target.scrollHeight - event.target.scrollTop <= event.target.clientHeight) {
            this._dataModel.getNext();
          }
        }));
      }
    }
  };

  @HostBinding('class.dt') true;

  constructor(private elRef:ElementRef) {

    if (!this.elRef || !this.elRef.nativeElement) {
      console.error('phi-datagrid without nativeElement!');
    } else {
      this.nativeElement = this.elRef.nativeElement;
    }

  }

  // This works, but register listener always, also if no datamodel is present, so it's slower
  // @HostListener('scroll', ['$event'])
  // onScroll(event): void {
  //   if (event.target.scrollHeight - event.target.scrollTop <= event.target.clientHeight) {
  //     if (this.dataModel) {
  //       this.dataModel.getNext();
  //     }
  //   }
  // };

}
