import { CalendarItem } from './calendar-item';

export class CalendarRow {
    items?: Array<CalendarItem>;
    offset?: number;
    enabled = 0;
    color: string;
}