import { Component, Input, forwardRef, HostBinding, AfterViewInit } from '@angular/core';
import { NG_VALIDATORS, NG_VALUE_ACCESSOR } from '@angular/forms';
import { TextBoxComponent } from './textBox.component';
import {TranslateService} from '@ngx-translate/core';

export interface CalendarDate {
  date: Date;
  selected?: boolean;
  today?: boolean;
}

export interface CalendarMonth {
  firstDay: Date;
  weeks: CalendarDate[][];
}

@Component({
  selector: 'phi-inlinecalendar',
  template: `
    <div class="calendar-navs">
      <div class="month-nav">
        <button (click)="prevMonth()" class="btn btn-primary fa fa-arrow-left fa-2x "></button>
        <button (click)="nextMonth()" class="btn btn-primary fa fa-arrow-right fa-2x "></button>
      </div>
     </div>
    
    <div *ngFor="let month of months" class="month-grid">
      <span class="month-name">{{ month.firstDay?.toLocaleString(this.translateService.store.currentLang, { month: 'long', year: 'numeric' }) }}</span>
      <div class="day-names">
        <div *ngFor="let name of dayNames" class="day-name">
          {{ name }}
        </div>
      </div>
      <div class="weeks">
        <div *ngFor="let week of month.weeks" class="week">
          <ng-container *ngFor="let day of week">
            <div class="week-date disabled" *ngIf="!isSelectedMonth(month.firstDay, day.date)">
              <span class="date-text">{{ day.date.getDate() }}</span>
            </div>
            <div class="week-date enabled"
                 *ngIf="isSelectedMonth(month.firstDay, day.date)"
                 (click)="selectDate(day)"
                 [ngClass]="{ today: day.today, selected: day.selected }">
              <span class="date-text">{{ day.date.getDate() }}</span>
            </div>
          </ng-container>
        </div>
      </div>
    </div>
  `,

  styleUrls: ['./inline-calendar.component.scss'],
  providers: [{ provide: NG_VALUE_ACCESSOR, useExisting: forwardRef(() => InlineCalendarComponent), multi: true },
              { provide: NG_VALIDATORS, useExisting: forwardRef(() => InlineCalendarComponent), multi: true}]
})
export class InlineCalendarComponent extends TextBoxComponent implements AfterViewInit {

  @HostBinding('class') clazz = 'phi-widget inline-calendar';

  @Input()
  numberOfMonths = 1;

  date: Date = null;
  currentDate: CalendarDate = null;
  currentMonth: Date = null;

  today: Date;

  dayNames;
  months: CalendarMonth[] = [];

  constructor(private translateService: TranslateService) {
    super();
    this.today = new Date();
    this.today.setHours(0,0,0,0);
  }

  get value() {
    return this.date;
  }

  set value(val) {
    this.date = val;

    this.onChangeCallback(this.date);
    this.onTouchedCallback();

    this.change.emit( {value: this.date} );
  }

  writeValue(val) {
    if (val && (this.date === null || val.getTime() !== this.date.getTime())) {
      this.date = val;
      this.currentMonth = new Date(this.date.getFullYear(), this.date.getMonth(), 1);
      this.generateCalendar();
    }
  }

  generateCalendar(): void {
    this.months = Array.from({length: this.numberOfMonths}, (_, i) => {
      let firstDay = new Date(this.currentMonth);
      firstDay.setMonth(firstDay.getMonth() + i);

      const dates = this.fillDates(firstDay);
      const weeks: CalendarDate[][] = [];
      while (dates.length > 0) {
        weeks.push(dates.splice(0, 7));
      }
      return {
        firstDay: new Date(firstDay),
        weeks
      };
    });
  }

  fillDates(date: Date): CalendarDate[] {
    let firstDayOfGrid = new Date(date);
    let day = firstDayOfGrid.getDay() -1;
    day = day < 0 ? 6 : day;
    firstDayOfGrid.setDate(firstDayOfGrid.getDate() - day);

    this.dayNames = Array.from({length: 7}, (_, i) => {
      const date: Date = new Date(firstDayOfGrid.getTime());
      date.setDate(date.getDate() + i);
      return date.toLocaleString(this.translateService.store.currentLang, { weekday: 'narrow' });
    });

    return Array.from({length: 42}, (_, i) => {
      const date: Date = new Date(firstDayOfGrid.getTime());
      date.setDate(date.getDate() + i);

      const calendarDate: CalendarDate = {
        today: this.isToday(date),
        selected: this.isSelected(date),
        date: date,
      };
      if (calendarDate.selected) {
        this.currentDate = calendarDate;
      }
      return calendarDate;
    })
  }

  isToday(date: Date): boolean {
    return this.today.getTime() === date.getTime();
  }

  isSelected(date: Date): boolean {
    return this.date.getTime() === date.getTime();
  }

  isSelectedMonth(firstDayOfMonth: Date, date: Date): boolean {
    return firstDayOfMonth.getMonth() === date.getMonth()
  }

  prevMonth(): void {
    this.currentMonth.setMonth(this.currentMonth.getMonth() - 1);
    this.generateCalendar();
  }

  nextMonth(): void {
    this.currentMonth.setMonth(this.currentMonth.getMonth() + 1);
    this.generateCalendar();
  }

  selectDate(date: CalendarDate): void {
    this.currentDate.selected = false;
    date.selected = true;
    this.currentDate = date;
    this.value = date.date;
    // this.generateCalendar();
  }

}
