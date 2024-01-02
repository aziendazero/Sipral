import { TranslateService } from '@ngx-translate/core';
import { CodeValue } from '../entities/data-types/code-value';
/**
 * Created by Daniele Tarticchio on 16/06/2017.
 */

//ISO8601
const dateFormat =
/^(-?(?:[1-9][0-9]*)?[0-9]{4})-(1[0-2]|0[1-9])-(3[0-1]|0[1-9]|[1-2][0-9])T(2[0-3]|[0-1][0-9]):([0-5][0-9]):([0-5][0-9])(\.[0-9]+)?(Z|[+-](?:2[0-3]|[0-1][0-9]):[0-5][0-9](:[0-5][0-9])?)?$/;

function isISO8601(str: any): boolean {
  if (!str && typeof str != 'string') {
    return false;
  }
  return dateFormat.test(str);
}

function stringISO8601ToDate(str: string): Date {
  let finalDate: Date;

  let dateStr: string = str.substring(0, str.indexOf("T"));
  let timeStr: string = str.substring(str.indexOf("T") + 1, str.length);
  let dateArr = dateStr.split("-");
  let year: number = Number(dateArr.shift());
  let month: number = Number(dateArr.shift());
  let date: number = Number(dateArr.shift());

  if (timeStr.indexOf("Z") != -1) {// Ignore the time zone passed from server
    timeStr = timeStr.replace("Z", "");
  } else if (timeStr.indexOf("+") != -1) {
    timeStr = timeStr.substring(0, timeStr.indexOf("+"));
  } else {// offset is -
    timeStr = timeStr.substring(0, timeStr.indexOf("-"));
  }

  let timeArr = timeStr.split(":");
  let hour: number = Number(timeArr.shift());
  let minutes: number = Number(timeArr.shift());
  let secondsArr = (timeArr.length > 0) ? String(timeArr.shift()).split(".") : null;
  let seconds: number = (secondsArr != null && secondsArr.length > 0) ? Number(secondsArr.shift()) : 0;
  let milliseconds: number = (secondsArr != null && secondsArr.length > 0) ? 1000 * parseFloat("0." + secondsArr.shift()) : 0;

  finalDate = new Date(year, month - 1, date, hour, minutes, seconds, milliseconds);

  if (finalDate.toString() == "Invalid Date") {
    throw new Error("This date does not conform.");
  }
  return finalDate;
}

function dateToISO8601String(date: Date) {
  let tzo = -date.getTimezoneOffset(),
      dif = tzo >= 0 ? '+' : '-';

  return date.getFullYear()
    + '-' + pad(date.getMonth() + 1)
    + '-' + pad(date.getDate())
    + 'T' + pad(date.getHours())
    + ':' + pad(date.getMinutes())
    + ':' + pad(date.getSeconds())
    + dif + pad(tzo / 60)
    + ':' + pad(tzo % 60);
}

export function dateToInputDate(date: Date) {

  return date.getFullYear()
    + '-' + pad(date.getMonth() + 1)
    + '-' + pad(date.getDate());
}

export function dateToInputDateTime(date: Date) {

  return date.getFullYear()
    + '-' + pad(date.getMonth() + 1)
    + '-' + pad(date.getDate())
    + 'T' + pad(date.getHours())
    + ':' + pad(date.getMinutes())
    + ':' + pad(date.getSeconds());
}

export function pad(num) {
  let norm = Math.abs(Math.floor(num));
  return (norm < 10 ? '0' : '') + norm;
}
export function getDay(date: Date, locallang, lenght = 'short') {
  return date.toLocaleString(locallang , {weekday: lenght})
}
export function anyToDate(str: any): any {
  if (isISO8601(str)) {
    return stringISO8601ToDate(str);
  }
  return str;
}

export function dateToString(date: any): any {
  if (date instanceof Date) {
    return dateToISO8601String(date);
  }
  return date;
}

export function dateWithoutTime(date: Date): Date {
  var d = new Date(date);
  d.setHours(0, 0, 0, 0);
  return d;
}

export function dateToTime(date: Date): string {
  return date.toTimeString().substr(0,5);
}
