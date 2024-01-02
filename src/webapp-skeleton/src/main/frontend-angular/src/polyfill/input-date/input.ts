import Picker from './picker';
import locales from './locales';

/**
 * Extended version of "nodep-date-input-polyfill": "^5.2.0", see: https://github.com/brianblakely/nodep-date-input-polyfill
 * Added datetime-local and time
 */

export default class Input {

  private static dateFormat = /^\d{4}-\d{2}-\d{2}$/;
  private static dateTimeFormat = /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}$/;
  private static timeFormat = /^\d{2}:\d{2}:\d{2}$/;


  element; //: HTMLInputElement;
  type: string;
  format;
  locale;
  localeText;

  constructor(input, lang = null) {
    this.element = input;

    this.type = input.getAttribute('type');
    if (this.type === 'date') {
      this.format = Input.dateFormat;
    } else if (this.type === 'datetime-local') {
      this.format = Input.dateTimeFormat;
    } else if (this.type === 'time') {
      this.format = Input.timeFormat;
    }

    this.element.setAttribute(`data-has-picker`, ``);

    this.locale = lang || `en`;

    this.localeText = this.getLocaleText();

    if (this.type !== 'number') {
      Object.defineProperties(
        this.element,
        {
          'value': {
            get: function () {
              return this.element.polyfillValue;
            }.bind(this), // ()=> this.element.polyfillValue,
            set: function (val) { // val=> {
              if (!this.format.test(val)) {
                this.element.polyfillValue = ``;
                this.element.setAttribute(`value`, ``);
                return false;
              }

              this.element.polyfillValue = val;

              if (this.type === 'date' || this.type === 'datetime-local') {
                const YMD = val.split(`-`);
                let HM = '';
                if (this.type === 'datetime-local') {
                  const DH = YMD[2].split(`T`);
                  YMD[2] = DH[0];
                  HM = ' ' + DH[1].substring(0, 5);
                }

                this.element.setAttribute(
                  `value`,
                  this.localeText.format
                    .replace(`Y`, YMD[0])
                    .replace(`M`, YMD[1])
                    .replace(`D`, YMD[2]) + HM
                );

              } else if (this.type === 'time') {
                this.element.setAttribute(
                  `value`,
                  val.substring(0, 5)
                );
              }
            }.bind(this)
          },
          'valueAsDate': {
            get: function () { //()=> {
              if (!this.element.polyfillValue) {
                return null;
              }
              if (this.type === 'date' || this.type === 'datetime-local') {
                return new Date(this.element.polyfillValue);
              } else if (this.type === 'time') {
                let newDate = new Date(0);
                const HMS = this.element.polyfillValue.split(`:`);
                newDate.setHours(HMS[0]);
                newDate.setMinutes(HMS[1]);
                return newDate;
              }
            }.bind(this),
            set: function (val) { // val=> {
              if (this.type === 'time') {
                this.element.value =  val.toTimeString().substr(0, 8);
              } else if (this.type === 'date') {
                this.element.value =  val.toISOString().substr(0, 10);
              } else if (this.type === 'datetime-local') {
                this.element.value = val.toISOString().substr(0, 10) + 'T' + val.toTimeString().substr(0, 8);
              }
            }.bind(this)
          },
          'valueAsNumber': {
            get: function () { // ()=> {
              if (!this.element.value) {
                return NaN;
              }

              return this.element.valueAsDate.getTime();
            }.bind(this),
            set: function (val) { // val=> {
              this.element.valueAsDate = new Date(val);
            }.bind(this)
          }
        }
      );
    }

    // Initialize value for display.
    this.element.value = this.element.getAttribute(`value`);

    // Open the picker when the input get focus,
    // also on various click events to capture it in all corner cases.
    const showPicker = ()=> {
      Picker.instance.attachTo(this);
    };
    this.element.addEventListener(`focus`, showPicker);
    this.element.addEventListener(`mousedown`, showPicker);
    this.element.addEventListener(`mouseup`, showPicker);

    // Update the picker if the date changed manually in the input.
    this.element.addEventListener(`keydown`, e => {
      const date = new Date();

      switch(e.keyCode) {
        case 27:
          Picker.instance.hide();
          break;
        case 38:
          if(this.element.valueAsDate) {
            date.setDate(this.element.valueAsDate.getDate() + 1);
            this.element.valueAsDate = date;
            Picker.instance.pingInput();
          }
          break;
        case 40:
          if(this.element.valueAsDate) {
            date.setDate(this.element.valueAsDate.getDate() - 1);
            this.element.valueAsDate = date;
            Picker.instance.pingInput();
          }
          break;
        default:
          if (this.type !== 'number') {
            e.preventDefault();
          }
          return;
      }

      Picker.instance.sync();
    });
  }

  getLocaleText() {
    const locale = this.locale.toLowerCase();

    // First, look for an exact match to the provided locale.

    for(const localeSet in locales) {
      const localeList = localeSet.split(`_`).map(el=>el.toLowerCase());

      if(!!~localeList.indexOf(locale)) {
        return locales[localeSet];
      }
    }

    // If not found, look for a match to only the language.

    for(const localeSet in locales) {
      const localeList = localeSet.split(`_`).map(el=>el.toLowerCase());

      if(!!~localeList.indexOf(locale.substr(0,2))) {
        return locales[localeSet];
      }
    }

    // If still not found, reassign locale to English and rematch.

    this.locale = `en`;

    return this.getLocaleText();
  }

  // Return false if the browser does not support input[type="date"].
  static supportsDateInput() {
    const input = (<HTMLInputElement>document.createElement(`input`));
    input.setAttribute(`type`, `date`);

    const notADateValue = `not-a-date`;
    input.setAttribute(`value`, notADateValue);

    return !(input.value === notADateValue);
  }

  static supportsDateTimeInput() {
    const input = (<HTMLInputElement>document.createElement(`input`));
    input.setAttribute(`type`, `datetime-local`);

    const notADateValue = `not-a-date`;
    input.setAttribute(`value`, notADateValue);

    return !(input.value === notADateValue);
  }

}
