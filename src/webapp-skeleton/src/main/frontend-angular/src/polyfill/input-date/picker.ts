import Input from './input';

/**
 * Extended version of "nodep-date-input-polyfill": "^5.2.0", see: https://github.com/brianblakely/nodep-date-input-polyfill
 * Added datetime-local and time
 */

class Picker {

  static instance;
  date: Date;
  input: Input;
  isOpen: boolean;
  container;
  locale;

  year: HTMLSelectElement;
  month: HTMLSelectElement;
  today;

  daysMatrix;
  daysHead;
  days;

  hour: HTMLSelectElement;
  minute: HTMLSelectElement;

  number: number;
  numberUp: HTMLButtonElement;
  numberDown: HTMLButtonElement;

  constructor() {
    // This is a singleton.
    if(Picker.instance) {
      return Picker.instance;
    }

    this.date = new Date();
    this.input = null;
    this.isOpen = false;

    // The picker element. Unique tag name attempts to protect against
    // generic selectors.
    this.container = document.createElement(`date-input-polyfill`);

    // Add controls.
    // Year picker.
    this.year = <HTMLSelectElement>document.createElement(`select`);
    Picker.createRangeSelect(
      this.year,
      this.date.getFullYear() - 80,
      this.date.getFullYear() + 20
    );
    this.year.className = `yearSelect`;
    this.year.addEventListener(`change`, ()=> {
      this.date.setFullYear(parseInt(this.year.value));
      this.refreshDaysMatrix();
    });
    this.container.appendChild(this.year);

    // Month picker.
    this.month = <HTMLSelectElement>document.createElement(`select`);
    this.month.className = `monthSelect`;
    this.month.addEventListener(`change`, ()=> {
      this.date.setMonth(parseInt(this.month.value));
      this.refreshDaysMatrix();
    });
    this.container.appendChild(this.month);

    // Today button.
    this.today = document.createElement(`button`);
    this.today.textContent = `Today`;
    this.today.addEventListener(`click`, ()=> {
      this.date = new Date();

      this.setInput();
    });
    this.container.appendChild(this.today);

    // Setup unchanging DOM for days matrix.
    this.daysMatrix = document.createElement(`table`);
    this.daysHead = document.createElement(`thead`);
    this.days = document.createElement(`tbody`);

    // THIS IS THE BIG PART.
    // When the user clicks a day, set that day as the date.
    // Uses event delegation.
    this.days.addEventListener(`click`, e=> {
      const tgt = e.target;

      if(!tgt.hasAttribute(`data-day`)) {
        return false;
      }

      const curSel = this.days.querySelector(`[data-selected]`);
      if(curSel) {
        curSel.removeAttribute(`data-selected`);
      }
      tgt.setAttribute(`data-selected`, ``);

      this.date.setDate(parseInt(tgt.textContent));
      this.setInput();
    });

    this.daysMatrix.appendChild(this.daysHead);
    this.daysMatrix.appendChild(this.days);
    this.container.appendChild(this.daysMatrix);

    // Hour picker.
    this.hour = <HTMLSelectElement>document.createElement(`select`);
    Picker.createRangeSelect(
      this.hour,
      0,
      23
    );
    this.hour.className = `hourSelect`;
    this.hour.addEventListener(`change`, ()=> {
      this.date.setHours(parseInt(this.hour.value));
      this.setInput();
    });
    this.container.appendChild(this.hour);

    // Minute picker.
    this.minute = <HTMLSelectElement>document.createElement(`select`);
    Picker.createRangeSelect(
      this.minute,
      0,
      59
    );
    this.minute.className = `minuteSelect`;
    this.minute.addEventListener(`change`, ()=> {
      this.date.setMinutes(parseInt(this.minute.value));
      this.setInput();
    });
    this.container.appendChild(this.minute);

    // Number up
    this.numberUp = <HTMLButtonElement>document.createElement(`button`);
    this.numberUp.innerHTML = '&uarr;';
    this.numberUp.className = `numberUp`;
    this.numberUp.addEventListener(`click`, ()=> {
      if (!this.input.element.value || isNaN(this.input.element.value)) {
        this.input.element.value = 1;
      } else {
        this.input.element.value = +this.input.element.value + 1;
      }
      const max = parseInt(this.input.element.max);
      if (!isNaN(max) && +this.input.element.value > max) {
        this.input.element.value = max;
      }
      this.pingInput();
    });
    this.container.appendChild(this.numberUp);

    // Number down
    this.numberDown = <HTMLButtonElement>document.createElement(`button`);
    this.numberDown.innerHTML = '&darr;';
    this.numberDown.className = `numberDown`;
    this.numberDown.addEventListener(`click`, ()=> {
      if (!this.input.element.value || isNaN(this.input.element.value)) {
        this.input.element.value = 1;
      } else {
        this.input.element.value = +this.input.element.value - 1;
      }
      const min = parseInt(this.input.element.min);
      if (!isNaN(min) && +this.input.element.value < min) {
        this.input.element.value = min;
      }
      this.pingInput();
    });
    this.container.appendChild(this.numberDown);

    this.hide();
    document.body.appendChild(this.container);

    // Close the picker when clicking outside of a date input or picker.
    document.addEventListener(`click`, e=> {
      let el: any = e.target;
      let isPicker = el === this.container;

      while(!isPicker && (el = el.parentNode)) {
        isPicker = el === this.container;
      }
      const target = (<any>e.target).getAttribute(`type`);
      (target !== `date` && target !== `datetime-local` && target !== `time` && target !== `number`) && !isPicker
        && this.hide();
    });
  }

  // Hide.
  hide() {
    this.container.setAttribute(`data-open`, this.isOpen = false);
  }

  // Show.
  show() {
    this.container.setAttribute(`data-open`, this.isOpen = true);
  }

  // Position picker below element. Align to element's left edge.
  goto(element) {
    const rekt = element.getBoundingClientRect();

    let top  = rekt.top  + (document.documentElement.scrollTop  || document.body.scrollTop);
    let left = rekt.left + (document.documentElement.scrollLeft || document.body.scrollLeft);

    if (this.input.type === 'number') {

      top = top + (rekt.height - this.container.clientHeight) / 2;
      left = left + rekt.width - this.container.clientWidth - 3;

    } else {

      if (window.innerHeight - top - rekt.height < this.container.clientHeight) {
        top = top - this.container.clientHeight;
      } else {
        top = top + rekt.height;
      }

      if (window.innerWidth - left < this.container.clientWidth) {
        left = left + rekt.width - this.container.clientWidth;
      }

    }

    this.container.style.top = `${top}px`;
    this.container.style.left = `${left}px`;

    this.show();
  }

  // Initiate I/O with given date input.
  attachTo(input) {
    if(
      input === this.input
      && this.isOpen
    ) {
      return false;
    }

    this.input = input;
    this.sync();
    this.goto(this.input.element);
  }

  // Match picker date with input.
  sync() {
    if(this.input.element.valueAsDate) {
      this.date = this.input.element.valueAsDate; // Picker.absoluteDate(this.input.element.valueAsDate);
    } else {
      this.date = new Date();
      this.date.setMinutes(0, 0, 0);
    }

    const showDate: any = this.input.type.startsWith('date');

    if (showDate) {
      this.year.value = this.date.getFullYear().toString();
      this.month.value = this.date.getMonth().toString();
      this.refreshDaysMatrix();
    }

    this.year.setAttribute(`show`, showDate);
    this.month.setAttribute(`show`, showDate);
    this.today.setAttribute(`show`, showDate);
    this.daysMatrix.setAttribute(`show`, showDate);

    const showTime: any = this.input.type.indexOf('time') !== -1;

    if (showTime) {
      this.hour.value = this.date.getHours().toString();
      this.minute.value = this.date.getMinutes().toString();
    }
    this.hour.setAttribute(`show`, showTime);
    this.minute.setAttribute(`show`, showTime);

    const showNumber: any = this.input.type === 'number';

    this.numberUp.setAttribute(`show`, showNumber);
    this.numberDown.setAttribute(`show`, showNumber);

  }

  // Match input date with picker date.
  setInput() {
    let newValue: string;
    if (this.input.type === 'date' || this.input.type === 'datetime-local') {
      newValue =
        `${
          this.date.getFullYear()
          }-${
          `0${this.date.getMonth() + 1}`.slice(-2)
          }-${
          `0${this.date.getDate()}`.slice(-2)
          }`;
      if (this.input.type === 'datetime-local') {
        newValue += 'T' + ('0' + this.date.getHours()).slice(-2) + ':' + ('0' + this.date.getMinutes()).slice(-2) + ':00';
      }
    } else if (this.input.type === 'time') {
      newValue = ('0' + this.date.getHours()).slice(-2) + ':' + ('0' + this.date.getMinutes()).slice(-2) + ':00';
    }
    this.input.element.value = newValue;

    this.input.element.focus();
    setTimeout(()=> { // IE wouldn't hide, so in a timeout you go.
      this.hide();
    }, 100);

    this.pingInput();
  }

  refreshLocale() {
    if(this.locale === this.input.locale) {
      return;
    }

    this.locale = this.input.locale;

    const daysHeadHTML = [`<tr>`];
    for(let i = 0, len = this.input.localeText.days.length; i < len; ++i) {
      daysHeadHTML.push(`<th scope="col">${this.input.localeText.days[i]}</th>`);
    }
    this.daysHead.innerHTML = daysHeadHTML.join(``);

    Picker.createRangeSelect(
      this.month,
      0,
      11,
      this.input.localeText.months,
      this.date.getMonth()
    );

    this.today.textContent = this.input.localeText.today;
  }

  refreshDaysMatrix() {
    this.refreshLocale();

    // Determine days for this month and year,
    // as well as on which weekdays they lie.
    const year = this.date.getFullYear(); // Get the year (2016).
    const month = this.date.getMonth(); // Get the month number (0-11).
    const startDay = new Date(year, month, 1).getDay(); // First weekday of month (0-6).
    const maxDays = new Date(
      this.date.getFullYear(),
      month + 1,
      0
    ).getDate(); // Get days in month (1-31).

    // The input's current date.
    const selDate: Date = Picker.absoluteDate(this.input.element.valueAsDate);

    // Are we in the input's currently-selected month and year?
    const selMatrix =
      selDate
      && year === selDate.getFullYear()
      && month === selDate.getMonth();

    // Populate days matrix.
    const matrixHTML = [];
    for(let i = 0; i < maxDays + startDay; ++i) {
      // Add a row every 7 days.
      if(i % 7 === 0) {
        matrixHTML.push(`
          ${i !== 0 ? `</tr>` : ``}
          <tr>
        `);
      }

      // Add new column.
      // If no days from this month in this column, it will be empty.
      if(i + 1 <= startDay) {
        matrixHTML.push(`<td></td>`);
        continue;
      }

      // Populate day number.
      const dayNum = i + 1 - startDay;
      const selected = selMatrix && selDate.getDate() === dayNum;

      matrixHTML.push(
        `<td data-day ${selected ? `data-selected` : ``}>
          ${dayNum}
        </td>`
      );
    }

    this.days.innerHTML = matrixHTML.join(``);
  }

  pingInput() {
    // Dispatch DOM events to the input.
    let inputEvent;
    let changeEvent;

    // Modern event creation.
    try {
      inputEvent = new Event(`input`);
      changeEvent = new Event(`change`);
    }
    // Old-fashioned way.
    catch(e) {
      inputEvent = document.createEvent(`KeyboardEvent`);
      inputEvent.initEvent(`input`, true, false);
      changeEvent = document.createEvent(`KeyboardEvent`);
      changeEvent.initEvent(`change`, true, false);
    }

    this.input.element.dispatchEvent(inputEvent);
    this.input.element.dispatchEvent(changeEvent);
  }

  static createRangeSelect(theSelect, min, max, namesArray = null, selectedValue = null) {
    theSelect.innerHTML = ``;

    for(let i = min; i <= max; ++i) {
      const aOption: HTMLOptionElement = (<HTMLOptionElement>document.createElement(`option`));
      theSelect.appendChild(aOption);

      const theText = namesArray ? namesArray[i - min] : i;

      aOption.text = theText;
      aOption.value = i;

      if(i === selectedValue) {
        aOption.selected = true;
      }
    }

    return theSelect;
  }

  static absoluteDate(date) {
    return date && new Date(date.getTime() + date.getTimezoneOffset()*60*1000);
  }
}

Picker.instance = null;

export default Picker;
