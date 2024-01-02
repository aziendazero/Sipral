import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable()
/** Service meant to make keeping track of certain data easier across components since
 * passing it through the router in Angular 4 seems problematic, deprecate this if another 
 * better solution exists
 */
export class DataService {
  /** Option which tracks whether dead patients should be ignored */
  private ignoreDeadPatientsOptionSource = new BehaviorSubject<boolean>(false);
  currentIgnoreDeadPatientsOption$ = this.ignoreDeadPatientsOptionSource.asObservable()
  constructor() { }
   
  /**
   * Sets a boolean inside data.service that keeps track if dead patients should be ignored or not
   * @param {boolean} x
   */
  setIgnoreDeadPatientsOption(x: boolean){
    this.ignoreDeadPatientsOptionSource.next(x);
  }
}