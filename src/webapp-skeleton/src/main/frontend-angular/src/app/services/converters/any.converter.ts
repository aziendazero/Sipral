/**
 * Created by Daniele Tarticchio on 22/06/2017.
 */

import { CodeValue } from '../entities/data-types/code-value';
import { anyToDate, dateToString } from './date.converter';

export function anyToString(date: any): any {
  if (date instanceof Date) {
    return dateToString(date);
  } else if (date && date['entityName'] && date['entityName'].indexOf("com.phi.entities.dataTypes.CodeValue") != -1) { // TODO: find an instanceof custom Classes
    return (date as CodeValue).id;
  }
  return date;
}

export function reviver(key, value) {

  // const objKeys = Object.keys(this);
  //
  // if (objKeys.length === 2 && objKeys.indexOf('entityName') !== -1 && objKeys.indexOf('internalId') !== -1) {
  //   // proxy
  //   console.log('Proxy: ', this);
  //   this['isProxy'] = true;
  // }
  //
  // if (objKeys.length === 2 && objKeys.indexOf('entityName') !== -1 && objKeys.indexOf('id') !== -1) {
  //   // proxy
  //   console.log('CodeValueProxy: ', this);
  //   this['isCodeValueProxy'] = true;
  // }

  return anyToDate(value);
}

export function color2hex(color: string): string {
  const number = parseInt(color, 10);
  let hex = number.toString(16);
  if (hex.length > 6) {
    hex = hex.substring(hex.length - 6, hex.length);
  }
  return '#' + '0'.repeat(6 - hex.length) + hex;
}
