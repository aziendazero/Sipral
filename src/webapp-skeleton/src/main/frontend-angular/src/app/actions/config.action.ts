/**
 * Created by Daniele on 26/06/2017.
 */
import { Injectable } from '@angular/core';
import { NgRedux } from '@angular-redux/store';
import { IAppState } from '../store/index';
import { Employee } from '../services/entities/role/employee';
import { ServiceDeliveryLocation } from '../services/entities/role/service-delivery-location';

@Injectable()
export class ConfigActions {
  static CONFIG_PUT = 'CONFIG_PUT';
  static CONFIG_REMOVE = 'CONFIG_REMOVE';
  static CONFIG_TOGGLE_MENU = 'CONFIG_TOGGLE_MENU';
  static CONFIG_TOGGLE_BANNER = 'CONFIG_TOGGLE_BANNER';
  static CONFIG_CLEAN = 'CONFIG_CLEAN';


  constructor(private ngRedux: NgRedux<IAppState>) { }

  /**
   * Put variables into Config
   * @returns {Function}
   */
  put(key: string, value: any) {
    this.ngRedux.dispatch({
      type: ConfigActions.CONFIG_PUT,
      payload: { key, value }
    });
  }

  remove(key: string) {
    this.ngRedux.dispatch({
      type: ConfigActions.CONFIG_REMOVE,
      payload: { key }
    });
  }

  putParam(param: Map<string, any>) {
    this.ngRedux.dispatch({
      type: ConfigActions.CONFIG_PUT,
      payload: { key: 'param', value: param }
    });
  }

  putSid(sid: string) {
    this.ngRedux.dispatch({
      type: ConfigActions.CONFIG_PUT,
      payload: { key: 'sid', value: sid }
    });
  }

  putEmployee(employee: Employee) {
    this.ngRedux.dispatch({
      type: ConfigActions.CONFIG_PUT,
      payload: { key: 'employee', value: employee }
    });
  }

  putPasswordExpired(passwordExpired: boolean) {
    this.ngRedux.dispatch({
      type: ConfigActions.CONFIG_PUT,
      payload: { key: 'passwordExpired', value: passwordExpired }
    });
  }

  putOptions(options: boolean) {
    this.ngRedux.dispatch({
      type: ConfigActions.CONFIG_PUT,
      payload: { key: 'options', value: options }
    });
  }

  putEmployeeRoleCode(employeeRoleCode: string) {
    this.ngRedux.dispatch({
      type: ConfigActions.CONFIG_PUT,
      payload: { key: 'employeeRoleCode', value: employeeRoleCode }
    });
  }

  putFlashVars(flashVars: string) {
    this.ngRedux.dispatch({
      type: ConfigActions.CONFIG_PUT,
      payload: { key: 'flashVars', value: flashVars }
    });
  }

  toggleMenu() {
    this.ngRedux.dispatch({
      type: ConfigActions.CONFIG_TOGGLE_MENU,
    });
  }

  toggleBanner() {
    this.ngRedux.dispatch({
      type: ConfigActions.CONFIG_TOGGLE_BANNER,
      payload: {}
    });
  }

  clean() {
    this.ngRedux.dispatch({
      type: ConfigActions.CONFIG_CLEAN,
      payload: null
    });
  }
}
