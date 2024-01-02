import { Injectable } from '@angular/core';
import { select } from '@angular-redux/store';

@Injectable()
export class ParameterManager {

  @select(['config', 'param']) param$;
  param: Map<string, any>;

  constructor() {
    this.param$.subscribe(res => this.param = res);
  }

  getParameter(paramId: string, paramKey: string) {

    if (this.param && this.param[paramId]) {
      return this.param[paramId][paramKey];
    }
  }

}
