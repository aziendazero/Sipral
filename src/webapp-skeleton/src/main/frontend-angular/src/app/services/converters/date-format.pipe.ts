import { Pipe, PipeTransform } from '@angular/core';
import { pad, getDay } from './date.converter';
import { DatePipe } from '@angular/common';
import { TranslateService } from '@ngx-translate/core';

/**
 * Custom pipe for date formatting.
 * Ignores timezone.
 */

@Pipe({
  name: 'dateFormat'
})
export class DateFormatPipe implements PipeTransform {

  static shortDate = 'shortDate';
  static short = 'short';
  static shortTime = 'shortTime';
  static shortDayDate = 'shortDayDate';
  static hourOrMinute = 'hourOrMinute';
  static dayOfWeek = 'dayOfWeek';

  transform(date: any, args?: any): any {
    let formatted = '';
    if (date && date instanceof Date) {
      if (args === DateFormatPipe.dayOfWeek) {
        return getDay(date, 'it', 'long');
      }
      if (!args || args === DateFormatPipe.shortDayDate) {
        formatted =getDay(date,'it').toUpperCase()+ ' ';
        
      }
      if (args === DateFormatPipe.short || args === DateFormatPipe.shortDate || args === DateFormatPipe.shortDayDate) {
        formatted += pad(date.getDate())
          + '/' + pad(date.getMonth() + 1)
          + '/' + date.getFullYear();
      }
      if (args === DateFormatPipe.short) {
        formatted += ' ';
      }
      if (args === DateFormatPipe.short || args === DateFormatPipe.shortTime) {
        formatted += pad(date.getHours()) + ':' + pad(date.getMinutes());
      }
      if (args === DateFormatPipe.hourOrMinute) {
        if (date.getMinutes() === 0) {
          formatted = pad(date.getHours()) + ':' + pad(date.getMinutes());
        } else {
          formatted = pad(date.getMinutes());
        }
      }
    } else {
      return '';
    }
    return formatted;
  }
}
