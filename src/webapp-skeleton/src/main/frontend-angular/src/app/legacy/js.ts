import * as jQuery from 'jquery/dist/jquery';
import 'jquery-ui/ui/widgets/datepicker';
import 'jquery-ui/ui/widgets/autocomplete';
import 'jquery-ui/ui/widgets/dialog'; // REMOVE TO USE ANGULAR POPUP
import 'jquery-ui/ui/widgets/slider';
import 'jquery-ui-timepicker-addon/dist/jquery-ui-timepicker-addon';
import { logError, logInfo } from '../services/error/global-error-handler';
import pell from 'pell'
import { default as InputPoly } from '../../polyfill/input-date/input';
import { default as Picker } from '../../polyfill/input-date/picker';

export default class Phi {

  // If legacy use jqueryUi for monthcalendar
  legacy = true;

  currentPage = '';
  lang = 'it';
  last_phrase = '';

  currentDialog = null;
  popupWidth;
  popupHeight;
  popupOnCloseFuncion;
  lastOpenTab = {};
  somethingChanged = false;

  cid;
  timerTelevisitInstance;

  ajaxUrl = window.location.pathname;

  blockUI() {
    jQuery('#loader').show();
  }

  unblockUI() {
    jQuery('#loader').hide();
  }

  startAjaxReq() {
    this.blockUI();
  }

  /* Registers an onMouseDown event for each node under phi-page-container.
   * The callback function invoked checks the target where the event fired and:
   *
   * If INPUT:
   * - Button with Style Class containing 'save' - set somethingChanged = false
   * - Button with Style Class containing 'linkUnlink' - set somethingChanged = true
   * else set somethingChanged = true
   *
   * If A (link):
   * with Style Class containing 'save' - set somethingChanged = false
   * with Style Class containing 'linkUnlink' - set somethingChanged = true
   *
   * If INS, SELECT, OPTION or TEXTAREA - set somethingChanged = true
   * If IMG with Style Class containing ui-datepicker-trigger - set somethingChanged = true;
   */
  checkSomethingChanged() {
    try {
      // From phi-page-container we get all mousedown events via bubling
      const pageContainer = document.getElementsByTagName('phi-page-container');
      if (pageContainer && pageContainer.length > 0) {
        pageContainer[0].addEventListener('mousedown', (event) => {
          let target: any = event.target;

          // Exclude divisions, table lines and filters
          if ((target.nodeName !== 'DIV') && (!target.disabled) && (!target.classList.contains('filter'))) {

            switch (target.nodeName) {

              case 'INPUT':
                let id = target.id;
                if (id.indexOf('Button') !== -1) { // contains
                  // Link unlink objects
                  if (target.classList.contains('linkUnlink')) {
                    this.somethingChanged = true;
                  } else if (target.classList.contains('buttonSave')) { // Save button
                    this.somethingChanged = false;
                  }
                } else if (id.indexOf('RadioGroup') !== -1) {  // contains
                  while ((target = target.parentElement) && !(target.nodeName === 'TABLE')) {};
                  if (target && !target.classList.contains('filter')) {
                    this.somethingChanged = true;
                  }
                } else if (!id.startsWith('f')) {
                  this.somethingChanged = true;
                }
                break;

              case 'TD':
                while ((target = target.parentElement) && !(target.nodeName === 'DIV')) {};
                if (target && target.classList.contains('linkUnlink')) {
                  this.somethingChanged = true;
                }
                break;

              case 'I': // icon inside Anchor
                if (target.parentNode instanceof HTMLAnchorElement) {
                  target = target.parentNode;
                  if(target.nodeName === 'A') {
                    // Link unlink objects
                    if (target.classList.contains('linkUnlink')) {
                      this.somethingChanged = true;
                    } else if (target.classList.contains('buttonSave')) { // Save button
                      this.somethingChanged = false;
                    }
                  }
                }
                break;
              case 'A':
                // Link unlink objects
                if (target.classList.contains('linkUnlink')) {
                  this.somethingChanged = true;
                } else if (target.classList.contains('buttonSave')) { // Save button
                  this.somethingChanged = false;
                }
                break;

              case 'INS':
              case 'SELECT':
              case 'OPTION':
              case 'TEXTAREA':
                this.somethingChanged = true;
                break;

              case 'IMG':
                if (target.classList.contains('ui-datepicker-trigger')) {
                  this.somethingChanged = true;
                }
                break;
            }
          }
        });
      }
    } catch (e) {
      console.log('Error checkSomethingChanged' + e.stack);
    }
  }

  stopAjaxReq() {
    try {
      this.unblockUI();

      let newCid = jQuery('#conversationId').val();

      if (this.cid !== newCid) { // if conversation changed
        this.cid = newCid;
        this.flexCommunicator('RETURN_TO_HOME', null, null);
        jQuery('#dashboardInclude').css('height', '100%');
      }

      if (jQuery('.ui-dialog').find('.tabset').length > 0 && jQuery('.ui-dialog').find('.tabset').attr('init') === undefined) {
        jQuery('.ui-dialog').find('.tabset').tabs('.tab-content');
        jQuery('.ui-dialog').find('.tabset').attr('init', true);

      } else if (jQuery('.tabset').attr('init') === undefined) {
        jQuery('.tabset').tabs('.tab-content');
        jQuery('.tabset').attr('init', true);
      }

      // INIT SCROLLER
      this.initScroller();

      // CALCULATES FORM HEIGHT AND POSITION
      this.recalculateVerticalPos();

      // MANAGES REQUIRED WIDGETS
      this.manageRequired();

      // MANAGES DISABLED WIDGETS
      this.manageDisabled();

      // Pell WYSIWYG text editor: https://github.com/jaredreich/pell
      let richTextEditors = document.getElementsByClassName('formattedTextArea');
      for (let z = 0; z < richTextEditors.length; z++) {

        const txtArea: HTMLTextAreaElement = <HTMLTextAreaElement>richTextEditors[z];
        const parent = txtArea.parentElement;

        if (!parent.classList.contains('pell')) {

          txtArea.style.display = 'none';
          parent.classList.add('pell');
          parent.classList.remove('inputArea');

          const onChange = html => {
            txtArea.value = html;
            txtArea.dispatchEvent(new Event('change'));
          };

          const actions: Array<any> = ['bold', 'italic', 'underline', 'olist', 'ulist'];
          const sizes = {
            1: '10px',
            2: '13px',
            3: '16px',
            4: '18px',
            5: '24px',
            6: '32px',
            7: '48px'
          }

          for(let key in sizes) {
            actions.push({
              name: sizes[key],
              icon: sizes[key] + '&nbsp',
              title: sizes[key] + ' font size',
              result: () => pell.exec('fontSize', key)
            });
          }

          const editor = pell.init({
            element: parent,
            onChange: onChange, // not working in IE11
            actions: actions/* ['bold', 'italic', 'underline', 'olist', 'ulist',
            {
              name: '10px',
              icon: '10',
              title: '10 px font size',
              result: () => pell.exec('fontSize', 7)
            }
            ] */
          });

          editor.content.innerHTML = txtArea.value;

          // fix IE 11:
          editor.content.addEventListener('keyup', () => onChange(editor.content.innerHTML));

		  // Paste only text into editor
          editor.addEventListener("paste", (e) => {
            // cancel paste
            e.preventDefault();

            // get text representation of clipboard
            const text = (e.originalEvent || e).clipboardData.getData('text/plain')
												.replace(/(?:\r\n|\r|\n)/g, '<br>')
												.replace(/\s/g, ' ');
            // insert text manually
            document.execCommand("insertHTML", false, text);
          });

          const pellButtons = document.getElementsByClassName('pell-button');
          for (let i = 0; i < pellButtons.length; i++) {
            // fix usage in form:
            pellButtons[i].setAttribute('type', 'button');
            // fix IE 11:
            pellButtons[i].addEventListener('click', () => onChange(editor.content.innerHTML));
          }

          if (txtArea.readOnly) {
            editor.content.contentEditable = false;
            editor.content.classList.add('disabled');
          }

          if (txtArea.style.height) {
            parent.style.height = txtArea.style.height;
            editor.content.style.height = (parent.offsetHeight - 31) + 'px';
          }

          if (txtArea.style.width) {
            parent.style.width = txtArea.style.width;
          }
        }
      }

      jQuery('.radioChecked').prop('checked', true);
      jQuery('.inputDisabled').prop('disabled', true);

      let processReadOnly = jQuery('#processReadOnly').val();

      if (processReadOnly === 'true') {
        jQuery('#i :input').not('.readOnly').attr('disabled', true);
        jQuery('#i :input').not('.readOnly').parent().addClass('disabled');
        jQuery('#i a').filter(function () {
          return jQuery(this).closest('.paging, .tableHeader').length < 1;
        }).not('.readOnly').removeAttr('onclick');
        jQuery('#i a').filter(function () {
          return jQuery(this).closest('.paging, .tableHeader').length < 1;
        }).not('.readOnly').addClass('disabled');
      }


      // DATE AND TIME PICKERS
      if (this.legacy) {
        let dateTimePickers = jQuery('input.datepicker:enabled,input.datetimepicker:enabled,input.timepicker:enabled,' +
          'input.datepicker:disabled,input.datetimepicker:disabled,input.timepicker:disabled');
        this.initDateTimePickers(dateTimePickers);
        this.initDatePickersBehavior(dateTimePickers);
      } else {
        let datePickers = document.getElementsByClassName('datepicker');
        for (let z = 0; z < datePickers.length; z++) {
          const input: HTMLInputElement = <HTMLInputElement>datePickers[z];
          input.setAttribute('type', 'date');

          // Polyfill
          if (!InputPoly.supportsDateInput()) {
            if (input && !input.hasAttribute(`data-has-picker`)) {
              Picker.instance = new Picker();
              let inputPoly = new InputPoly(input, 'it'); // FIXME language
            }
          }

        }

        let dateTimePickers = document.getElementsByClassName('datetimepicker');
        for (let z = 0; z < dateTimePickers.length; z++) {
          const input: HTMLInputElement = <HTMLInputElement>dateTimePickers[z];
          input.setAttribute('type', 'datetime-local');

          // Polyfill
          if (!InputPoly.supportsDateInput()) {
            if (input && !input.hasAttribute(`data-has-picker`)) {
              Picker.instance = new Picker();
              let inputPoly = new InputPoly(input, 'it'); // FIXME language
            }
          }
        }

        let timePickers = document.getElementsByClassName('timepicker');
        for (let z = 0; z < timePickers.length; z++) {
          const input: HTMLInputElement = <HTMLInputElement>timePickers[z];
          input.setAttribute('type', 'time');

          // Polyfill
          if (!InputPoly.supportsDateInput()) {
            if (input && !input.hasAttribute(`data-has-picker`)) {
              Picker.instance = new Picker();
              let inputPoly = new InputPoly(input, 'it'); // FIXME language
            }
          }
        }
      }

      let autocompleteList = jQuery('.autocomplete');
      for (let z = 0; z < autocompleteList.length; z++) {
        let autocomp = jQuery(autocompleteList[z]);
        // add placeholder...
        autocomp.attr('placeholder', 'Cerca...');
        autocomp.attr('isAutocompSelected', 'false');

        let minLength = 0;
        if (autocomp.attr('minLength')) {
          minLength = autocomp.attr('minLength');
        }

        autocomp.autocomplete({
          minLength: minLength,
          source: function (event, ui) {
            if (event.term) {
              let id = this.element[0].id;
              let fNamePre = id.substring(0, id.lastIndexOf('_'));
              let fName = fNamePre.substring(fNamePre.lastIndexOf(':') + 1);
              let date = '#' + fNamePre.replace(/\:/gi, '\\:') + '_D';
              let dateWidget = jQuery(date);
              if (dateWidget.length < 1) {
                let F = fName + '(search,ui)';
                let a4jFtoBeCalled = new Function('search', 'ui', F);
                a4jFtoBeCalled.call(null, event.term, ui);
              } else {
                let F = fName + '(search,date,ui)';
                /*let dateDate = dateWidget.datepicker('getDate');
                 if (!dateDate) { dateDate = new Date();	}*/
                let dateVal = dateWidget.val();
                let a4jFtoBeCalled = new Function('search', 'date', 'ui', F);
                a4jFtoBeCalled.call(null, event.term, dateVal, ui);
              }
            }
          },
          focus: function (event, ui) {

            let currentWidget = jQuery(event.target); // .next();
            let idEnd = currentWidget && currentWidget.length > 0 && currentWidget.is('input') ? currentWidget.attr('id') : '';

            /* IF MAIN FOUND */
            if (currentWidget && currentWidget.length > 0 && idEnd && this.endsWith(idEnd, 'MAIN')) {
              currentWidget.val(ui.item.label);
              currentWidget = currentWidget.next();
              idEnd = currentWidget.attr('id');
            }
            /* LOOKING FOR CPA, ZIP OR CODE INPUT */
            while (currentWidget && currentWidget.length > 0 && !currentWidget.is('input')) {
              currentWidget = currentWidget.next();
              idEnd = currentWidget.attr('id');
            }
            /* IF CPA FOUND */
            if (currentWidget && currentWidget.length > 0 && idEnd && this.endsWith(idEnd, 'CPA')) {
              currentWidget.val(ui.item.province);
              currentWidget = currentWidget.next();
            }
            /* LOOKING FOR ZIP OR CODE INPUT */
            while (currentWidget && currentWidget.length > 0 && !currentWidget.is('input')) {
              currentWidget = currentWidget.next();
              idEnd = currentWidget.attr('id');
            }
            /* IF ZIP FOUND */
            if (currentWidget && currentWidget.length > 0 && idEnd && this.endsWith(idEnd, 'ZIP')) {
              currentWidget.val(ui.item.zip);
              currentWidget = currentWidget.next();
            }
            /* LOOKING FOR CODE INPUT */
            while (currentWidget && currentWidget.length > 0 && !(currentWidget.is('input') && idEnd && this.endsWith(idEnd, 'CODE'))) {
              currentWidget = currentWidget.next();
              idEnd = currentWidget.attr('id');
            }
            /* IF CODE FOUND */
            if (currentWidget) {
              if (ui.item.internalId) { // SUGGESTION CONTAINING Entities
                currentWidget.val(ui.item.entityClass + '-' + ui.item.internalId);
              } else {
                currentWidget.val(ui.item.id);
              }
            }
            return false;
          }.bind(this),
          select: function (event, ui) {
            let jThis = jQuery(event.target);
            let currentWidget = jThis.next();
            let idEnd = currentWidget && currentWidget.length > 0 && currentWidget.is('input') ? currentWidget.attr('id') : '';

            while (currentWidget && currentWidget.length > 0 && !(currentWidget.is('input') && idEnd && this.endsWith(idEnd, 'CODE'))) {
              currentWidget = currentWidget.next();
              idEnd = currentWidget.attr('id');
            }

            if (currentWidget) {
              currentWidget.change();
            }

            jThis.attr('isAutocompSelected', 'true');
            jThis.blur();
            jThis.focus();
            return false; // evita il commit del set che viene fatto in jQuery/autocomplete.js: 302
          }.bind(this),
          change: function (event, ui) {

            let jThis = jQuery(event.target);
            if ( !(jThis.attr('isAutocompSelected') === 'true' || ui.item) ) {
              jThis.val('');

              let currentWidget = jThis.next();
              let idEnd = currentWidget && currentWidget.length > 0 && currentWidget.is('input') ? currentWidget.attr('id') : '';
              /* LOOKING FOR CPA, ZIP OR CODE INPUT */
              while (currentWidget && currentWidget.length > 0 && !currentWidget.is('input')) {
                currentWidget = currentWidget.next();
                idEnd = currentWidget.attr('id');
              }
              /* IF CPA FOUND */
              if (currentWidget && currentWidget.length > 0 && idEnd && this.endsWith(idEnd, 'CPA')) {
                currentWidget.val('');
                currentWidget = currentWidget.next();
              }
              /* LOOKING FOR ZIP OR CODE INPUT */
              while (currentWidget && currentWidget.length > 0 && !currentWidget.is('input')) {
                currentWidget = currentWidget.next();
                idEnd = currentWidget.attr('id');
              }
              /* IF ZIP FOUND */
              if (currentWidget && currentWidget.length > 0 && idEnd && this.endsWith(idEnd, 'ZIP')) {
                currentWidget.val('');
                currentWidget = currentWidget.next();
              }
              /* LOOKING FOR CODE INPUT */
              while (currentWidget && currentWidget.length > 0 && !(currentWidget.is('input') && idEnd && this.endsWith(idEnd, 'CODE'))) {
                currentWidget = currentWidget.next();
                idEnd = currentWidget.attr('id');
              }
              /* IF CODE FOUND */
              if (currentWidget) {
                currentWidget.val('');
                currentWidget.change();
              }
            }
            jThis.attr('isAutocompSelected', 'false');
          }.bind(this)
        });

        let acItem = autocomp.data('uiAutocomplete');
        if (acItem) {
			acItem._renderItem = function (ul, item) {
				let acInput = jQuery(this.element[0]);
				let propertyLabel = acInput.attr("data-label");
				let propertyId = acInput.attr("data-id");
				if (item.id === undefined) {
					item.id = item.internalId;
					if (item.name) {
						if (item.name.fam) {// !== undefined) {
						  item.label = item.name.fam + ' ';
						}
						if (item.name.giv) {// !== undefined) {
						  item.label = (item.label ? item.label : '') + item.name.giv;
						}
						if (item.username) {// !== undefined) {
						  item.label = (item.label ? (item.label + ' ') : '') + '(' + item.username + ')';
						}
					} else if (item.medicineName || item.substanceName) { // FOR DRUGDB MEDICINE/SUBSTANCE SUGGESTIONS
					  item.id = item.externalId;
					  if (item.medicineName) {
						item.label = item.medicineName;
					  } else {
						item.label = item.substanceName;
					  }

					  /*
					  * come usare questo widget con le entity: fai una read con select di un solo campo
					  * se item non è una entity con name allora prendo la prima proprietà che non è internalId o entityClass
					  */
					  } else if (item.medicineDescription) { // FOR KLINIK MEDICINE SUGGESTIONS
					item.id = item.therapyType+'|-|'+item.codArticolo;
					item.label = item.medicineDescription;
				  } else if (propertyLabel) {   // FOR GENERIC ENTITY SEARCH: use jolly parameter to indicate property for label and id
					// EXAMPLE: data-id="productNumCode" data-label="description" (phi klinik f_surgical_intervention.mmgp)
					item.id = item[propertyId];
					item.label = item[propertyLabel];
				  } else {
					for (let property in item) {
					  if (item.hasOwnProperty(property) && property !== 'entityClass' && property !== 'internalId') {
						item.label = item[property];
						break;
					  }
					}
				  }
				}
				let listItem = jQuery('<li></li>').data('item.autocomplete', item);
				listItem.append('<a>' +
				  (acInput.hasClass('showCode') ? '[' + item.code + '] ' : '') +
				  '<span>' + item.label + '</span>' +
				  (acInput.hasClass('autoAddress') ? (item.province !== null || item.zip !== null ? '</br><small>'
					+ (item.province !== null ? item.province + ' ' : '') + (item.zip !== null ? item.zip : '') + '</small>' : '') : '') +
				  '</a>'
				).appendTo(ul);
				return listItem;
          };
        }
      }

      this.textAreaAutoExpand();
    } catch (e) {
      alert(e.stack);
    }
  }

  initDateTimePickers(dateTimePickers) {
    let today = new Date();
    // dateTimePickers.each(function () {
    // let dateTimePicker = jQuery(this);
    for (let z = 0; z < dateTimePickers.length; z++) {
      let dtp = dateTimePickers[z];
      let dateTimePicker = jQuery(dtp);
      if (!dateTimePicker.hasClass('hasDatepicker')) {
        let boundValue = this.parseDate(dateTimePicker);
        if (!boundValue || boundValue > today) {
          boundValue = today;
        }
        let noHigh = new Date(boundValue.getTime());
        noHigh.setFullYear(noHigh.getFullYear() + 50);
        let noLow = new Date(boundValue.getTime());
        noLow.setFullYear(noLow.getFullYear() - 150);
        if (dateTimePicker.hasClass('datepicker')) {
          dtp.customKeyex = /[\d/.-]/;
          dtp.customRegex = /^(?:0?[1-9]?|[12]\d|3[01]?)?$|^(?:0?[1-9]|[12]\d|3[01])[\/.-](?:0?[1-9]?|1[012])?$|^(?:0?[1-9]|[12]\d|3[01])[\/.-](?:0?[1-9]|1[012])[\/.-]\d{0,4}$/;
          dateTimePickers[z].addEventListener('keypress', (event) => {
            let widget = event.currentTarget;
            if (!this.isRightFormat(widget, event, widget.customKeyex, widget.customRegex)) {
              event.preventDefault();
            }
          });
          dateTimePicker.datepicker({
            yearRange: '-150:+50',
            changeMonth: true,
            changeYear: true,
            maxDate: (dateTimePicker.hasClass('todayIsHigherBound') ? today : noHigh),
            minDate: (dateTimePicker.hasClass('todayIsLowerBound') ? boundValue : noLow)
          });
        }
        if (dateTimePicker.hasClass('datetimepicker')) {
          dateTimePicker.datetimepicker({
            yearRange: '-150:+50',
            changeMonth: true,
            changeYear: true,
            maxDate: (dateTimePicker.hasClass('todayIsHigherBound') ? today : noHigh),
            minDate: (dateTimePicker.hasClass('todayIsLowerBound') ? boundValue : noLow)
          });
        }
        if (dateTimePicker.hasClass('timepicker')) {
          dateTimePicker.timepicker({
            hourGrid: 4,
            minuteGrid: 10
          });
        }
      }
    }
  }

  initDatePickersBehavior(dateTimePickers) {
    // dateTimePickers.each(function () {
    //   let dtpThis = jQuery(this);
    for (let z = 0; z < dateTimePickers.length; z++) {
      let dtpThis = jQuery(dateTimePickers[z]);
      if (!dtpThis.hasClass('dtpBehavior')) {
        // GET CURRENT DATE/TIME PICKER DATE AND TIME
        let currentDate = dtpThis.datepicker('getDate');
        // SET DEFAULT DATE
        if (dtpThis.hasClass('todayIsDefault') && !currentDate) {
          dtpThis.datepicker('setDate', new Date());
          this.initOtherDatePickersBounds(dtpThis, dateTimePickers);
        }
        // COMPARISON WITH OTHER DATE/TIME PICKERS
        let thisClass = dtpThis.attr('class');
        if (thisClass.match(/\s*compare(High|Low)_[^\s]*/)) {
          let compareParts = (new RegExp('\\s*(compare(High|Low))_([^\\s]*)\\s*')).exec(thisClass);
          let otherCompare = compareParts[1];
          let otherName = compareParts[3];
          // GET OTHER DATE/TIME PICKER FOR COMPARISON
          // dateTimePickers.each(function () {
          // if (this.id.match(new RegExp(otherName + '(_id)?$'))) {
          // let dtpOther = jQuery(this);
          for (let zz = 0; zz < dateTimePickers.length; zz++) {
            if (dateTimePickers[zz].id.match(new RegExp(otherName + '(_id)?$'))) {
              let dtpOther = jQuery(dateTimePickers[zz]);
              if (dtpOther.length > 0) {
                if (otherCompare.match(/^compareHigh/)) {
                  // SET MINIMUM DATE/TIME
                  dtpThis.datepicker('option', 'minDate', dtpOther.val());
                  // COMPARE DATE AND TIME [WE CANNOT SET FIXED MINIMUM AND MAXIMUM TIME,
                  // BUT WE NEED IT ONLY FOR SAME DAY] SELECTING A NEW DATE/TIME ON THIS PICKER
                  dtpThis.datepicker('option', 'onSelect', function (selectedDate, inst) {
                    this.initOtherDatePickersBounds(inst.input, dateTimePickers, selectedDate);
                    inst.input.change();
                  }.bind(this));
                  // RE-INIT COMPARING DATE/TIME PICKERS WHEN CLOSING THE OTHER PICKER
                  dtpOther.datepicker('option', 'onClose', function (selectedDate, inst) {
                    this.initOtherDatePickersBounds(inst.input, dateTimePickers, selectedDate);
                    inst.input.change();
                  }.bind(this));
                } else if (otherCompare.match(/^compareLow/)) {
                  // SET MAXIMUM DATE/TIME
                  dtpThis.datepicker('option', 'maxDate', dtpOther.val());
                  // COMPARE DATE AND TIME [WE CANNOT SET FIXED MINIMUM AND MAXIMUM TIME,
                  // BUT WE NEED IT ONLY FOR SAME DAY] SELECTING A NEW DATE/TIME ON THIS PICKER
                  dtpThis.datepicker('option', 'onSelect', function (selectedDate, inst) {
                    this.initOtherDatePickersBounds(inst.input, dateTimePickers, selectedDate);
                    inst.input.change();
                  }.bind(this));
                  // RE-INIT COMPARING DATE/TIME PICKERS WHEN CLOSING THE OTHER PICKER
                  dtpOther.datepicker('option', 'onClose', function (selectedDate, inst) {
                    this.initOtherDatePickersBounds(inst.input, dateTimePickers, selectedDate);
                    inst.input.change();
                  }.bind(this));
                }
              } else {
                let dtpThisDate = this.parseDate(dtpThis);
                if (dtpThisDate) {
                  dtpThis.datepicker('setDate', dtpThisDate);
                }
              }
            }
          }
        }
        dtpThis.addClass('dtpBehavior');
      }
    }
  }

  dateComparison(thisWidget, otherWidget, comparison, force) {
    let jThisWidget = jQuery(thisWidget);
    let thisDate = jThisWidget.datepicker('getDate');
    if (force || thisDate) {
      if (otherWidget.length > 0 && otherWidget.is('div')) {
        otherWidget = otherWidget.children('input:first');
      }
      if (otherWidget.length > 0) {
        let otherDate = otherWidget.datepicker('getDate');
        if (otherDate) {
          if (comparison === 'compareHigh') {
            let dummyDate = thisDate;
            thisDate = otherDate;
            otherDate = dummyDate;
          }
          if (force || thisDate < otherDate) {
            jThisWidget.datepicker('setDate', otherWidget.val());
          }
        }
      }
    }
  }

  initOtherDatePickersBounds(thisWidget, dateTimePickers/*, selectedDate*/) {
    let widgetId = jQuery(thisWidget).attr('id').replace(/^([^:]+:)+/, '').replace(/_id$/, '');
    // dateTimePickers.each(function () {
    // let otherWidget = jQuery(this);
    for (let z = 0; z < dateTimePickers.length; z++) {
      let otherWidget = jQuery(dateTimePickers[z]);
      if (otherWidget.attr('class').match(new RegExp('(compareHigh|compareLow)_' + widgetId))) {
        if (otherWidget.is('div')) {
          otherWidget = otherWidget.children('input:first');
        }
        otherWidget.datepicker('option', otherWidget.hasClass('compareHigh_' + widgetId) ? 'minDate' : 'maxDate',
          this.parseDate(thisWidget));
        this.dateComparison(thisWidget, otherWidget, otherWidget.hasClass('compareHigh_' + widgetId) ? 'compareHigh' : 'compareLow', false);
      }
    }
  }

  showDateTimePicker(widget) {
    if (this.legacy) {
      let jWidget = jQuery(widget);
      let prevWidget = jWidget.prev();
      if (prevWidget.length > 0 && prevWidget.is(':enabled')) {
        if (prevWidget.datepicker('widget').is(':visible')) {
          prevWidget.datepicker('hide');
        } else {
          prevWidget.datepicker('show');
        }
      }
    } else {
      widget.previousElementSibling.focus();
      widget.previousElementSibling.click();
    }
  }

  parseDate(widget) {
    let jWidget = jQuery(widget);
    let result = null;
    let widgetValue = jWidget.val();
    if (widgetValue) {
      try {
        let lang = this.getLangCode();
        let dateFormat = jWidget.hasClass('datepicker') || jWidget.hasClass('datetimepicker') ?
          jQuery.datepicker.regional[lang].dateFormat : null;
        let timeFormat = jWidget.hasClass('timepicker') || jWidget.hasClass('datetimepicker') ?
          jQuery.timepicker.regional[lang].timeFormat : null;
        if (dateFormat && timeFormat) {
          result = jQuery.datepicker.parseDateTime(dateFormat, timeFormat, widgetValue);
        } else if (dateFormat) {
          result = jQuery.datepicker.parseDate(dateFormat, widgetValue);
        } else if (timeFormat) {
          let newTime = jQuery.datepicker.parseTime(timeFormat, widgetValue);
          result = new Date();
          result.setHours(newTime['hour']);
          result.setMinutes(newTime['minute']);
          result.setSeconds(0);
          result.setMilliseconds(0);
        }
      } catch (e) {
        result = null;
      }
    }
    return result;
  }

  dateTimePickerFormat(widget, dateObject) {
    let jWidget = jQuery(widget);
    let result = null;
    if (jWidget.length > 0) {
      let lang = this.getLangCode();
      let dateFormat = jWidget.hasClass('datepicker') || jWidget.hasClass('datetimepicker') ?
        jQuery.datepicker.regional[lang].dateFormat : null;
      let timeFormat = jWidget.hasClass('timepicker') || jWidget.hasClass('datetimepicker') ?
        jQuery.timepicker.regional[lang].timeFormat : null;
      if (dateFormat) {
        result = jQuery.datepicker.formatDate(dateFormat, dateObject);
      }
      if (timeFormat) {
        result += (result ? ' ' : '') + jQuery.datepicker.formatTime(timeFormat, dateObject);
      }
    }
    return result;
  }

  getLangCode() {
    return this.lang;
  }

  selectFirst() {
// if nothing is selected check first
    let rGroup = jQuery('#applicationRadio');
    if (rGroup.find(':checked').length === 0) {
      let ciccio = jQuery(rGroup.find('input')[0]);
      ciccio.prop('checked', true);
      // ciccio.change();
    }
  }

  persistentTooltip(widget) {
    let jWidget = jQuery(widget);
    jQuery('#ToolTipDiv').stop(true, true);
    jQuery('#ToolTipDiv').fadeIn('fast');
    let tipY = jWidget.parent().position().top + jWidget.height() + 12;
    let tipX = jWidget.parent().position().left;
    jQuery('#ToolTipDiv').css({'top': tipY, 'left': tipX});

  }

  persistentTooltipRemove() {
    jQuery('#ToolTipDiv').stop(true, true);
    jQuery('#ToolTipDiv').fadeOut('fast');
  }

  /* USED IN F_DOC_UPLOAD */

  submitDocument(form, frame) {
    let callback = function () {
      this.rrLabel();
      jQuery(frame).unbind('load', callback);
    };

    jQuery(frame).bind('load', callback);
    jQuery(form).submit();
  }

  receiveMessage(event) {
    if (event.data === 'removeIframe') {
      window.removeEventListener('message', this.receiveMessage);
      // this.procClosePopup();
    } else if (event.data === 'uploadStarted') {
      this.blockUI();
    } else if (event.data === 'uploadComplete') {
      this.unblockUI();
      window.removeEventListener('message', this.receiveMessage);
      let a4jFtoBeCalled = new Function('rerenderUploadLabel()');
      a4jFtoBeCalled.call(this);
    }
  }
  /* END USED IN F_DOC_UPLOAD */

  constructor(cid) {

    jQuery.ajaxSetup({ cache: false });

    this.ajaxUrl = this.ajaxUrl.substring(0, this.ajaxUrl.indexOf('/', 1) + 1);
    this.ajaxUrl = this.ajaxUrl + 'ajax';

    this.cid = cid;

    /* datepicker and timepicker localization */
    jQuery.datepicker.regional['en'] = {
      dateFormat: 'dd/mm/yy',
      prevText: '&#8592;',
      nextText: '&#8594;'
    };
    jQuery.datepicker.regional['de'] = {
      prevText: '&#8592;',
      nextText: '&#8594;',
      currentText: 'Heute',
      monthNames: ['Januar', 'Februar', 'M&#xe4;rz', 'April', 'Mai', 'Juni',
        'Juli', 'August', 'September', 'Oktober', 'November', 'Dezember'],
      monthNamesShort: ['Jan', 'Feb', 'M&#xe4;r', 'Apr', 'Mai', 'Jun',
        'Jul', 'Aug', 'Sep', 'Okt', 'Nov', 'Dez'],
      dayNames: ['Sonntag', 'Montag', 'Dienstag', 'Mittwoch', 'Donnerstag', 'Freitag', 'Samstag'],
      dayNamesShort: ['Son', 'Mon', 'Die', 'Mit', 'Don', 'Fre', 'Sam'],
      dayNamesMin: ['So', 'Mo', 'Di', 'Mi', 'Do', 'Fr', 'Sa'],
      weekHeader: 'Sm',
      dateFormat: 'dd/mm/yy',
      firstDay: 1,
      isRTL: false,
      showMonthAfterYear: false,
      yearSuffix: ''
    };
    jQuery.datepicker.regional['it'] = {
      closeText: 'Chiudi',
      prevText: '&#8592;',
      nextText: '&#8594;',
      currentText: 'Oggi',
      monthNames: ['Gennaio', 'Febbraio', 'Marzo', 'Aprile', 'Maggio', 'Giugno',
        'Luglio', 'Agosto', 'Settembre', 'Ottobre', 'Novembre', 'Dicembre'],
      monthNamesShort: ['Gen', 'Feb', 'Mar', 'Apr', 'Mag', 'Giu',
        'Lug', 'Ago', 'Set', 'Ott', 'Nov', 'Dic'],
      dayNames: ['Domenica', 'Luned&#236;', 'Marted&#236;', 'Mercoled&#236;', 'Gioved&#236;', 'Venerd&#236;', 'Sabato'],
      dayNamesShort: ['Dom', 'Lun', 'Mar', 'Mer', 'Gio', 'Ven', 'Sab'],
      dayNamesMin: ['Do', 'Lu', 'Ma', 'Me', 'Gi', 'Ve', 'Sa'],
      weekHeader: 'Sm',
      dateFormat: 'dd/mm/yy',
      firstDay: 1,
      isRTL: false,
      showMonthAfterYear: false,
      yearSuffix: ''
    };
    jQuery.timepicker.regional['it'] = {
      timeOnlyTitle: 'Seleziona orario',
      timeText: 'Orario',
      hourText: 'Ora',
      minuteText: 'Minuti',
      secondText: 'Secondi',
      millisecText: 'Millisecondi',
      microsecText: 'Microsecondi',
      timezoneText: 'Fuso orario',
      currentText: 'Corrente',
      closeText: 'Conferma',
      timeFormat: 'HH:mm',
      timeSuffix: '',
      amNames: ['m.', 'AM', 'A'],
      pmNames: ['p.', 'PM', 'P'],
      isRTL: false
    };
    jQuery.timepicker.regional['en'] = {
      timeFormat: 'hh:mmTT',
      ampm: true
    };
    jQuery.timepicker.regional['de'] = {
      timeOnlyTitle: 'Uhrzeit w&#228;hlen',
      timeText: 'Uhrzeit',
      hourText: 'Stunden',
      minuteText: 'Minuten',
      secondText: 'Sekunden',
      currentText: 'Jetzt',
      closeText: 'Best&#228;tigen',
      timeFormat: 'HH:mm',
      ampm: false
    };

    let lang = this.getLangCode();

    jQuery.datepicker.setDefaults(jQuery.datepicker.regional[lang]);
    jQuery.timepicker.setDefaults(jQuery.timepicker.regional[lang]);

    jQuery.fn.tabs = (content) => {
      let tabset = jQuery('.tabset');
      if (jQuery('.ui-dialog').find('.tabset').length > 0) {
        tabset = jQuery('.ui-dialog').find('.tabset');
      }
      for (let tIdx = 0; tIdx < tabset.length; tIdx++) {
        let wrap = jQuery(tabset[tIdx]);
        let tabName = wrap.attr('id');

        let hiddenTabs = wrap.find('.tabs li');
        let currentActive = 0;
        while (!jQuery(hiddenTabs[currentActive]).is(':visible')) {
          currentActive++;
        }
        let currentActiveTab;
        // check if last opened tab is visible
        let lastActive = this.lastOpenTab[tabName];
        if (lastActive !== undefined) {
          let lastActiveTab = jQuery(hiddenTabs[lastActive]);
          if (!lastActiveTab.hasClass('hideTab')) {
            currentActive = lastActive;
            currentActiveTab = lastActiveTab;
          }
        }
        if (currentActiveTab === undefined) {
          for (let z = 0; z < hiddenTabs.length; z++) {
            let currentTab = jQuery(hiddenTabs[z]);
            if (!currentTab.hasClass('hideTab')) {
              this.lastOpenTab[tabName] = z;
              currentActive = z;
              currentActiveTab = currentTab;
              break;
            }
          }
        }

        currentActiveTab.addClass('active');
        wrap.find(content + ':lt(' + currentActive + ')').addClass('hidden');
        wrap.find(content + ':eq(' + currentActive + ')').removeClass('hidden');
        wrap.find(content + ':gt(' + currentActive + ')').addClass('hidden');
        let c = wrap.find(content);
        if (c.length < 1) {
          return;
        }
        let t = jQuery(tabset);
        t.find('.tabs li a').click(function (e) {
          e.preventDefault();
        });
        t.find('.tabs li').click((e) => {
          let li = jQuery(e.currentTarget);
          if (li.hasClass('active')) {
            return;
          }
          this.lastOpenTab[wrap.attr('id')] = li.index();
          let cPosition = jQuery(window).scrollTop();
          c.hide();
          let href_class = '.' + li.find('a').attr('href').split('#')[1];
          wrap.find(href_class).removeClass('hidden').fadeIn();
         // let activeClass = t.find('li.active a').attr('href').split('#')[1];
          t.find('li').removeClass('active');
          li.addClass('active');
          li.parents('.more').addClass('active');

          // manageTables(null);
          this.textAreaAutoExpand();
          jQuery(window).scrollTop(cPosition);
        });
      }
    };

    jQuery.fn.sortElements = (function(){

      let sort = [].sort;

      return function(comparator, getSortable) {

        getSortable = getSortable || function(){ return this; };

        let placements = this.map(function() {

          let sortElement = getSortable.call(this),
            parentNode = sortElement.parentNode,

            // Since the element itself will change position, we have
            // to have some way of storing it's original position in
            // the DOM. The easiest way is to have a 'flag' node:
            nextSibling = parentNode.insertBefore(
              document.createTextNode(''),
              sortElement.nextSibling
            );

          return function() {

            if (parentNode === this) {
              throw new Error(
                'You cannot sort elements if any one is a descendant of another.'
              );
            }

            // Insert before flag:
            parentNode.insertBefore(this, nextSibling);
            // Remove flag:
            parentNode.removeChild(nextSibling);

          };

        });

        return sort.call(this, comparator).each(function(i){
          placements[i].call(getSortable.call(this));
        });

      };

    })();

    document.addEventListener('keydown', (e: KeyboardEvent) => {
      if (e.srcElement.classList.contains('preventNavigation') && !((/^[a-zA-Z0-9]+$/.test(String.fromCharCode(e.keyCode)))
        || e.keyCode === 46  || e.keyCode === 8)) {
        // TODO test in klinik f_summary_amb_enc
        e.preventDefault();
      }
      if (e.srcElement.classList.contains('preventSimpleNav') && !((/^[a-zA-Z0-9]+$/.test(String.fromCharCode(e.keyCode)))
        || e.keyCode === 46  || e.keyCode === 8 || e.keyCode === 9)) {
        e.preventDefault();
      }
      if (e.keyCode === 13) { // ENTER
        const pressOnEnterBtns: Array<Element> = Array.from(document.getElementsByClassName('pressOnEnter'));
        const visiblePressOnEnterBtn: Element = pressOnEnterBtns.find((el: HTMLElement) =>
          el.offsetWidth > 0 && el.offsetHeight > 0 // same as jQuery :visible
        );
        if (visiblePressOnEnterBtn instanceof HTMLElement) {
          visiblePressOnEnterBtn.focus();
        }
      }
      if (e.keyCode === 119) { // F8 - PHI KLINIK
        const pressOnF8Btns: Array<Element> = Array.from(document.getElementsByClassName('keyF8'));
        const visiblePressOnF8Btn: Element = pressOnF8Btns.find((el: HTMLElement) =>
          el.offsetWidth > 0 && el.offsetHeight > 0 // same as jQuery :visible
        );
        if (visiblePressOnF8Btn instanceof HTMLElement) {
          visiblePressOnF8Btn.click();
        }
      }
    });

  }

  textAreaAutoExpand() {
    let textAreas = jQuery('textarea'); /* jQuery("textarea.autoExpand"); */
    let dynTextAreas = [];
    // filter dinamic textAreas
    for (let iTxt = 0; iTxt < textAreas.length; iTxt++) {
      let absParent = jQuery(textAreas[iTxt]).parents()
        .filter(function () {
          return jQuery(this).css('position') === 'absolute';
        }).first(); // check if it's a static form

      if (absParent && absParent.length > 0) {
        // static parents found
      } else {
        dynTextAreas.push(textAreas[iTxt]);
      }
    }

    // initialized not static text areas
    for (let iTxt = 0; iTxt < dynTextAreas.length; iTxt++) {
      this.textAreaExpand(dynTextAreas[iTxt])
    }
    jQuery(dynTextAreas).keyup((e) => this.textAreaExpand(e.target));
  }

  textAreaExpand(textArea) {
    jQuery(textArea).css('height', textArea.scrollHeight + (textArea.offsetHeight - textArea.clientHeight));
  }

  recalculateVerticalPos() {
    let formTitle = jQuery('#formName');
    let hFormTitle = 0;
    if (formTitle.length === 1) {
      hFormTitle = formTitle.height();
      hFormTitle = hFormTitle + parseInt(formTitle.css('margin-bottom'), 10);
      hFormTitle = hFormTitle + parseInt(formTitle.css('margin-top'), 10);
    }

    let middleAlignContainers = jQuery('.middleAlignedContent');
    for (let z = 0; z < middleAlignContainers.length; z++) {
      let container = jQuery(middleAlignContainers[z]);
      let parent = container.parent();
      let hParent = parent.height() - hFormTitle;
      let h = container.height() - 1;
      let pos = (hParent - h) / 2;
      container.css('margin-top', +(pos < 0 ? 0 : pos) + 'px');
    }

    let bottomAlignContainers = jQuery('.bottomAlignedContent');
    for (let z = 0; z < bottomAlignContainers.length; z++) {
      let container = jQuery(bottomAlignContainers[z]);
      let parent = container.parent();
      let hParent = parent.height() - hFormTitle;
      let h = container.height() - 1;
      let pos = (hParent - h);
      container.css('margin-top', pos < 0 ? 0 : pos);
    }
  }


// SLIDER
  manageSlider(targetSlider, orientation, min, max, step, value, range, disable, targetInput, targetInputHigh) {
    let sliders = this.findTargetWidgets(targetSlider);
    let inputs = this.findTargetWidgets(targetInput);
    let inputsHigh = this.findTargetWidgets(targetInputHigh);
    if (sliders !== null && sliders !== undefined) {
      let sLength = sliders.length;
      for (let z = 0; z < sLength; z++) {
        let slider = jQuery(sliders[z]);
        let inputLow = inputs.length === sLength ? jQuery(inputs[z]) : null;
        let inputHigh = inputsHigh.length === sLength ? jQuery(inputsHigh[z]) : null;
        this.initSlider(slider, orientation, min, max, step, value, range, disable, inputLow, inputHigh);
      }
    }
  }

  initSlider(slider, orientation, minimum, maximum, step, value, rangeType, disable, inputLow, inputHigh) {
    if (slider !== null && slider !== undefined && slider.length === 1) {
      slider.slider({
        orientation: orientation,
        range: rangeType,
        min: minimum,
        max: maximum,
        step: step,
        disabled: disable,
        slide: function (event, ui) {
          if (inputHigh === null) {
            if (inputLow !== null) {
              jQuery(inputLow).val(ui.value);
            }
          } else {
            if (rangeType === true) {
              if (inputLow !== null) {
                jQuery(inputLow).val(ui.values[0]);
              }
              if (inputHigh !== null) {
                jQuery(inputHigh).val(ui.values[1]);
              }
            } else {
              let input = (rangeType === 'max' ? inputLow : inputHigh);
              if (input !== null) {
                jQuery(input).val(ui.value);
              }
            }
          }
        }
      });
      this.setSliderDefault(slider, inputLow, inputHigh, rangeType, minimum, maximum, value);
    }
    this.setInputsDefault(slider, inputLow, inputHigh, rangeType, minimum, maximum);
  }

  setSliderValue(input) {
    let inputLow = jQuery(input);
    // if (!/^[-+]$/.test(inputLow.val())) {
    let isInputLow = this.endsWith(inputLow.attr('name'), '_L');
    let inputHigh = null;
    let slider = inputLow.prev();
    if (slider.hasClass('ui-slider')) {
      inputHigh = inputLow.next();
    } else {
      inputHigh = inputLow;
      inputLow = slider;
      slider = slider.prev();
    }
    let rangeType = slider.slider('option', 'range');
    let minimum = slider.slider('option', 'min');
    let maximum = slider.slider('option', 'max');
    if (rangeType === true) {
      let valueLow = parseFloat(inputLow.val());
      let valueHigh = parseFloat(inputHigh.val());
      if (valueLow < minimum) {
        valueLow = minimum;
      }
      if (valueHigh > maximum) {
        valueHigh = maximum;
      }
      if (valueLow > valueHigh) {
        if (isInputLow) {
          valueLow = valueHigh;
        } else {
          valueHigh = valueLow;
        }
      }
      if (!isNaN(valueLow)) {
        inputLow.val(valueLow);
        slider.slider('option', 'values', 0, valueLow);
      }
      if (!isNaN(valueHigh)) {
        inputHigh.val(valueHigh);
        slider.slider('option', 'values', 1, valueHigh);
      }
    } else {
      let currentInput = jQuery(input);
      let currentValue = parseFloat(currentInput.val());
      if (currentValue < minimum) {
        currentValue = minimum;
      }
      if (currentValue > maximum) {
        currentValue = maximum;
      }
      if (!isNaN(currentValue)) {
        currentInput.val(currentValue);
        slider.slider('option', 'value', currentValue);
      }
    }
    // }
  }

  setSliderDefault(slider, inputLow, inputHigh, rangeType, minimum, maximum, value) {
    if (rangeType === true) {
      let currentValueLow = inputLow.val();
      if (currentValueLow === null || currentValueLow === undefined || currentValueLow === '') {
        currentValueLow = value > minimum && value < maximum ? value : minimum;
      }
      let currentValueHigh = inputHigh.val();
      if (currentValueHigh === null || currentValueHigh === undefined || currentValueHigh === '') {
        currentValueHigh = maximum;
        ;
      }
      slider.slider('option', 'values', [currentValueLow, currentValueHigh]);
    } else {
      let input = (rangeType === 'min' ? inputHigh : inputLow);
      let currentValue = input.val();
      if (currentValue === null || currentValue === undefined || currentValue === '') {
        currentValue = value;
      }
      slider.slider('option', 'value', currentValue);
    }
  }

  setInputsDefault(slider, inputLow, inputHigh, rangeType, minimum, maximum) {
    if (rangeType === true) {
      this.setInputDefault(inputLow, slider.slider('values', 0));
      this.setInputDefault(inputHigh, slider.slider('values', 1));
    } else {
      let input = (rangeType === 'max' ? inputLow : inputHigh);
      this.setInputDefault(input, slider.slider('value'));
      let inputOther = (rangeType === 'max' ? inputHigh : inputLow);
      this.setInputDefault(inputOther, rangeType === 'max' ? maximum : minimum);
    }
  }

  setInputDefault(input, value) {
    if (input !== null) {
      let currentValue = input.val();
      if (currentValue === null || currentValue === undefined || currentValue === '') {
        input.val(value);
      }
    }
  }

  // FINDS ALL WIDGETS WITH ID THAT ENDS WIT 'TARGETNAME'
  findTargetWidgets(targetName) {
    return jQuery('[id$="' + targetName + '"]');
  }

  clearField(target, isSuggestion) {
    let targets = this.findTargetWidgets(target);
    if (targets !== null && targets !== undefined) {
      for (let z = 0; z < targets.length; z++) {
        let jThis = jQuery(targets[z]);
        jThis.val('');
        if (isSuggestion) {
          let jNext = jThis.next();
          let toClean = this.endsWith(jNext.attr('id'), 'CODE') ? 1 : 3;
          for (let y = toClean; y > 0; y--) {
            jNext.val('');
            if (this.endsWith(jNext.attr('id'), 'CODE')) {
              jNext.change();
            }
            jNext = jNext.next();
          }
        }
      }
    }
  }

// MANAGES TABLE ROWS COLOR, HEIGHT AND FIXED HEADER
  manageTables(targetName) {
    let tables = (targetName === null || targetName === undefined || targetName === '' ?
      jQuery('.dt:not(".layoutTable")') : this.findTargetWidgets(targetName));
    if (tables) {
      for (let z = 0; z < tables.length; z++) {
        let table = jQuery(tables[z]);

        let firstRow = table.find('tbody tr:visible:first');

        if (firstRow.length > 0 && !firstRow.hasClass('noresults')) {
          // CALCULATE ROWS COLOR
          // recalculateTableRowsColors(table);

          // CALCULATE DATA AREA HEIGHT
          // if (table.closest('.layoutTable').length === 0) {
          this.recalculateTableBodyHeight(table);
          // }

          // CALCULATE COLUMNS WIDTH
          this.recalculateTableColumnsWidth(table);

          // RE-CALCULATE DATA AREA HEIGHT
          // recalculateTableBodyHeight(table);

          // MAKE SOME COLUMNS CLIENT SORTABLE
          this.makeColumnsSortTable(table);

          // PREVENT PROPAGATION FOR BUTTONS
          // preventTableButtonPropagation(table);
        }
      }
    }
  }
// ASSIGNES A SPECIFIC STYLE TO A TABLE ROW DEPENDING ON THE CONTENT OF A COLUMN AT THE
// 'columnIndex' INDEX.
  changeDataGridRowsStyle(table, columnIndex, text, styleName) {

    let tableBody = table.find('.tableBody');

    if (tableBody) {
      let rows = tableBody.find('tr');

      if (rows.length > 0) {

        let row = null;
        let cell = null;
        for (let y = 0; y < rows.length; y++) {

          row = jQuery(rows[y]);
          let cells = row.find('td');
          cell = jQuery(cells[columnIndex]);
          if (cell.text() === text) {
            row.addClass(styleName);
          } else {
            row.removeClass(styleName);
          }
        }
      }
    }

  }
// ASSIGNES A SPECIFIC STYLE TO A GROUPCHECKBOX ROW DEPENDING ON THE POSITION
  changeGroupCheckBoxRowsStyle(groupCheckId, positions, styleName) {

    let rowsBody = groupCheckId.find('tr');

    if (rowsBody) {
      for (let i = 0; i < positions.length; i++) {
        let position = positions[i];
        jQuery(rowsBody[position]).addClass(styleName);
      }
    }
  }

  recalculateTableRowsColors(table) {
    table.find('.tableBody > tr:odd').addClass('odd').removeClass('even');
    table.find('.tableBody > tr:even').addClass('even').removeClass('odd');
  }

  recalculateTableBodyHeight(table) {
    let hasNext = table.next().length > 0;
    let tableHeight = table.height();
    let tableBody = table.find('.tableBody');
    if (tableBody) {
      let bodyPosition = tableBody.position().top;
      let baseHeight = tableHeight - bodyPosition;
      let bodyMaxHeight;
      let conheight;
      if (table && table.hasClass('autoResizeFull')) {
        conheight = jQuery(window).height() - (jQuery('#header').height() + jQuery('#footer').height()) - 35;
        bodyMaxHeight = conheight - 45 - table.position().top;
      } else if (table && table.hasClass('autoResizeContained')) {
        conheight = table.parent().height();
        bodyMaxHeight = conheight - 25 - table.position().top;
      } else {
        bodyMaxHeight = tableHeight;
      }
      bodyMaxHeight = bodyMaxHeight - bodyPosition - parseInt(tableBody.css('border-bottom-width'), 10);
      if (hasNext || bodyMaxHeight < 50) {
        bodyMaxHeight = baseHeight;
      }
      tableBody.css('max-height', bodyMaxHeight);
    }
  }

  recalculateTableColumnsWidth(table) {
    let cols = table.find('tbody tr:visible:first > td');
    let headers = table.find('thead tr th');
    for (let y = 0; y < cols.length; y++) {
      let desiredWidth = jQuery(cols[y]).width();
      jQuery(cols[y]).css({width: desiredWidth + 'px'});
    }
    ;
    table.find('tbody').css({float: 'left'}); // display:'block' });
    table.find('thead').css({float: 'left'}); // display:'block' });
    for (let y = 0; y < cols.length; y++) {
      let desiredWidth = jQuery(cols[y]).width();
      jQuery(headers[y]).css({width: desiredWidth + 'px'});
    }
    ;
  }

  makeColumnsSortTable(table) {
    let self = this;
    jQuery('.sortableColumn')
      .each(function () {

        let th = jQuery(this),
          thIndex = th.index(),
          inverse = false;

        th.click(function () {
          table.find('td').filter(function () {
            return jQuery(this).index() === thIndex;
          }).sortElements(function (a, b) {
            let aValue;
            let bValue;
            let aInput = jQuery(a).find('input');
            let bInput = jQuery(b).find('input');
            if (aInput.length > 0 && bInput.length > 0) {
              aValue = jQuery(aInput[0]).is(':checked');
              bValue = jQuery(bInput[0]).is(':checked');
            } else {
              aValue = jQuery.text([a]);
              bValue = jQuery.text([b]);
              if (self.isNumeric(aValue) && self.isNumeric(bValue)) {
                aValue = parseFloat(aValue);
                if (isNaN(aValue)) {
                  aValue = 0;
                }
                bValue = parseFloat(bValue);
                if (isNaN(bValue)) {
                  bValue = 0;
                }
              }
            }
            if (aValue === bValue) {
              return 0;
            }
            return aValue > bValue ?
              inverse ? -1 : 1
              : inverse ? 1 : -1;
          }, function () {
            return this.parentNode;
          });
          inverse = !inverse;
          self.recalculateTableRowsColors(table);
        });
      });
  }

  stopPropagation(event) {
    if (event.stopPropagation) {
      event.stopPropagation();   // W3C model
    } else {
      event.cancelBubble = true; // IE model
    }
  }

  uncheckRadio(widget) {
    if (widget.checked) {
      jQuery(widget).mouseup(function () {
        // apparently if you do it immediatelly, it will be overriden, hence wait a tiny bit
        setTimeout(function () {
          widget.checked = false;
        }, 5);
        // don't handle mouseup anymore unless bound again
        jQuery(widget).unbind('mouseup');
      });
    }
  }

  isNumeric(s) {
    return /^[-+]?\d*$/.test(jQuery.trim(s.replace(/[,.']/g, '')));
  };

  // LIMIT TEXT INPUTS LENGTH
  limitText(element, limit) {
    if (element.value.length > limit) {
      element.value = element.value.substring(0, limit);
    }
  }

// FOCUSES ON FORM ELEMENT WITH LESSER TABINDEX
// OR FIRST ELEMENT IF TI ISN'T SET
  focusFirstElement() {
    this.resetPagePosition();
    let form = jQuery(document.forms['f']);
    let elements = form.find('input,select,a');
    let y = 0;
    let found = false;
    let tabIndex = Infinity;
    for (let z = 0; z < elements.length; z++) {
      let element = elements[z];
      let ti = element.tabIndex;
      let type = element.type;

      if (!found && 'hidden' !== type) {
        // Found first not hidden.
        found = true;
        y = z;
        tabIndex = ti;
      } else if (found) {
        // search if any with lowest tabindex, greather than 0.
        if (ti > 0 && ti < tabIndex && 'hidden' !== type) {
          y = z;
          tabIndex = ti;
        }
      }

    }

    let scrollerDiv = jQuery('.main-container');
    let pageName = scrollerDiv.attr('title');
    if (pageName && pageName !== this.currentPage && elements.length > 0) {
      this.currentPage = pageName;
      scrollerDiv.scrollTop(0);
      elements[y].focus();
    }
  }

  resetPagePosition() {
    let scrollerDiv = jQuery('.main-container');
    let pageName = scrollerDiv.attr('title');
    if (pageName && pageName !== this.currentPage) {
      this.currentPage = pageName;
      scrollerDiv.scrollTop(0);
    }
  }

  initScroller() {
    let scrollButt = jQuery('.scroll-to-top');
    scrollButt.fadeOut(350);
    let scrollerDiv = jQuery('.main-container');
    if (scrollerDiv && scrollerDiv.length > 0) {
      if (scrollerDiv.scrollTop() >= 50) {
        scrollButt.fadeIn(1200);
      }
      scrollerDiv.off('scroll');
      scrollerDiv.scroll(() => {
        if (jQuery('.main-container').scrollTop() < 50) {
          scrollButt.fadeOut(350);
        } else if (scrollButt.is(':hidden')) {
          scrollButt.fadeIn(1200);
        }
      });
      if (!scrollButt.attr('init')) {
        scrollButt.click(() => {
          this.scrollToPosition(0);
        });
        scrollButt.attr('init', true);
      }
    } else {
      scrollButt.fadeOut(350);
    }
  }

  scrollToPosition(pos) {
    jQuery('.main-container').animate({
      scrollTop: pos
    }, 600);
  }

// SET DEFAULT VALUE
  setDefaultValue(targetName, defaultValue) {
    // FIND ALL WIDGETS WITH ID THAT ENDS WIT 'TARGETNAME'
    let targetWidgets = this.findTargetWidgets(targetName);
    if (targetWidgets !== null && targetWidgets !== undefined) {
      for (let z = 0; z < targetWidgets.length; z++) {
        let targetWidget = jQuery(targetWidgets[z]);
        if (defaultValue === true) { // SINGLE CHECKBOXES
          targetWidget.prop('checked', defaultValue);
        } else { // RADIO GROUPS AND CHECK BOX GROUPS
          let currentValue = targetWidget.val();
          if (targetWidget.is('table')) {
            let defaultValues = defaultValue.split(';');
            let inputs = targetWidget.find('input');
            for (let y = 0; y < inputs.length; y++) {
              let input = jQuery(inputs[y]);
              if (input.prop('checked')) {
                return;
              }
            }
            for (let y = 0; y < inputs.length; y++) {
              for (let x = 0; x < defaultValues.length; x++) {
                let input = jQuery(inputs[y]);
                if (input.val() === defaultValues[x]) {
                  input.prop('checked', true);
                  break;
                }
              }
            }
          } else { // OTHER INPUTS/SELECTS
            if (currentValue === null || currentValue === undefined || currentValue === '') {
              if (defaultValue.indexOf(';') > -1) { // MULTIPLE SELECTS
                let defaultValues = defaultValue.split(';');
                targetWidget.val(defaultValues);
              } else { // SINGLE VALUE WIDGETS
                targetWidget.val(defaultValue);
              }
            }
          }
        }
      }
    }
  }

  getIdPrefix(element) {
    let widgetId = jQuery(element).attr('id');
    let widgetPrefix = widgetId.substring(0, widgetId.lastIndexOf(':') + 1).replace(/:/gi, '\\:');
    return widgetPrefix;
  }


  scrollTogether(master, slave) {
    let jMaster = jQuery(master);
    let jSlave = jQuery(slave);
    jSlave.scrollLeft(jMaster.scrollLeft());
  }

  /* COMPARES TWO DATETIME WIDGETS AND SETS LOWER AND HIGHER DATE/TIME LIMITS*/
  dateCompare(lowerElement, higherElement) {
    let lang = this.getLangCode();
    let datePattern = jQuery.datepicker.regional[lang].dateFormat;
    let timePattern = jQuery.timepicker.regional[lang].timeFormat;

    let lowerWidget = jQuery(lowerElement);
    if (lowerWidget.is('div')) {
      lowerWidget = lowerWidget.children('input');
    }
    let higherWidget = jQuery(higherElement);
    if (higherWidget.is('div')) {
      higherWidget = higherWidget.children('input');
    }

    let lower = lowerWidget.val();
    let higher = higherWidget.val();

    if (lower && lower !== '' && higher && higher !== '') {
      let lowerParts = lower.split(' ');
      let higherParts = higher.split(' ');

      let lowerDate;
      let higherDate;

      let lowerDatePicker = lowerWidget.data('datepicker');
      if (lowerDatePicker) {
        let lowerTimePicker = jQuery.datepicker._get(lowerDatePicker, 'timepicker');
        lowerDate = this.fixDate(lowerWidget, lowerParts, new Date(<number>lowerDatePicker.selectedYear,
          <number>lowerDatePicker.selectedMonth, <number>lowerDatePicker.selectedDay,
          lowerTimePicker === undefined ? 0 : <number>lowerTimePicker.hour,
          lowerTimePicker === undefined ? 0 : <number>lowerTimePicker.minute, 0, 0));
      } else {
        lowerDate = this.fixDate(lowerWidget, lowerParts, jQuery.datepicker.parseDate(datePattern, lower), datePattern, timePattern);
      }
      let higherDatePicker = higherWidget.data('datepicker');
      if (higherDatePicker) {
        let higherTimePicker = jQuery.datepicker._get(higherDatePicker, 'timepicker');
        higherDate = this.fixDate(higherWidget, higherParts, new Date(<number>higherDatePicker.selectedYear,
          <number>higherDatePicker.selectedMonth, <number>higherDatePicker.selectedDay,
          higherTimePicker === undefined ? 0 : <number>higherTimePicker.hour,
          higherTimePicker === undefined ? 0 : <number>higherTimePicker.minute, 0, 0));
      } else {
        higherDate = this.fixDate(higherWidget, higherParts, jQuery.datepicker.parseDate(datePattern, higher), datePattern, timePattern);
      }

      let isLowerComparing = jQuery.type(lowerElement) === 'object';

      if (lowerDate.getTime() > higherDate.getTime()) {
        if (isLowerComparing) {
          lowerWidget.val(higher);
        } else {
          higherWidget.val(lower);
        }
        return false;
      }
    }
    return true;
  }

  /* COMPARES TWO TIME WIDGETS AND SETS LOWER AND HIGHER TIME LIMITS*/
  timeCompare(lowerWidget, higherWidget, /*lower, higher,*/ isLowerComparing) {

    let lowerDatePicker = lowerWidget.data('datepicker');
    let lowerTimePicker = jQuery.datepicker._get(lowerDatePicker, 'timepicker');
    let higherDatePicker = higherWidget.data('datepicker');
    let higherTimePicker = jQuery.datepicker._get(higherDatePicker, 'timepicker');

    if (lowerTimePicker !== null && higherDatePicker !== null && (lowerTimePicker.hour > higherTimePicker.hour
      || (lowerTimePicker.hour === higherTimePicker.hour && lowerTimePicker.minute > higherTimePicker.minute))) {
      if (isLowerComparing) {
        lowerWidget.val(higherWidget.val());
      } else {
        higherWidget.val(lowerWidget.val());
      }
      return false;
    }
    return true;
  }

  /* FIXES DATEPICKER/TIMEPICKER DATE IF IT DOESN'T REFLECT DISPLAYED DATE */
  fixDate(widget, dateParts, currentDate, datePattern = null, timePattern = null) {

    let stringDateOrTime = dateParts[0];
    if (!datePattern) {
      datePattern = widget.datepicker('option', 'dateFormat');
    }
    let newDate;
    try {
      if (dateParts.length === 2) {
        newDate = jQuery.datepicker.parseDate(datePattern, stringDateOrTime);
        newDate = this.fixTime(widget, dateParts[1], newDate, timePattern);
      } else if (dateParts[0].indexOf(':') > -1) {
        newDate = this.fixTime(widget, stringDateOrTime, new Date(), timePattern);
      } else {
        newDate = jQuery.datepicker.parseDate(datePattern, stringDateOrTime);
      }
    } catch (err) {
      widget.val('');
    }

    if (newDate && newDate.getTime() !== currentDate.getTime()) {
      return newDate;
    } else {
      return currentDate;
    }
  }

  /* FIXES DATEPICKER/TIMEPICKER TIME IF IT DOESN'T REFLECT DISPLAYED TIME */
  fixTime(widget, stringTime, currentDate, timePattern) {
    if (!timePattern) {
      timePattern = widget.datepicker('option', 'timeFormat');
    }
    let newDate = currentDate;
    let newTime = jQuery.datepicker.parseTime(timePattern, stringTime);
    newDate.setHours(newTime['hour']);
    newDate.setMinutes(newTime['minute']);
    newDate.setSeconds(0);
    newDate.setMilliseconds(0);
    return newDate;
  }

// MANAGES DISABLED RADIO AND CHECKBOX GROUPES
  manageDisabled() {
    let label = jQuery('input:radio:disabled,input:checkbox:disabled').next();
    if (label) {
      label.css('color', '#ABADB3');
    }
  }

  manageRequired() {
    let requiredWidgets = jQuery('.required');
    requiredWidgets.each(function (/*index*/) {
      let requiredWidget = jQuery(this);
      if (!requiredWidget.attr('init')) {
        requiredWidget.attr('init', true);

        if (requiredWidget.parents('.tableBody').length > 0) {
          if (requiredWidget.hasClass('datepicker') || requiredWidget.hasClass('datetimepicker')) {
            requiredWidget = requiredWidget.next();
          }
          requiredWidget.after('<strong class="reqStar">*</strong>');
        } else {
          if (requiredWidget.hasClass('radioGroupTable') || requiredWidget.hasClass('checkGroupTable')) {
            requiredWidget.before('<strong class="reqStar">*</strong>');
          } else {
            requiredWidget.css('float', 'left');
            requiredWidget.after('<strong class="reqStar">*</strong>');
          }
        }
      }
    });

    let layoutRequiredWidgets = jQuery('.layoutRequired');
    layoutRequiredWidgets.each(function (/*index*/) {
      let layoutRequiredWidget = jQuery(this);
      if (!layoutRequiredWidget.attr('init')) {
        if (layoutRequiredWidget.next().text() !== '*') {
          layoutRequiredWidget.attr('init', true);
          if (layoutRequiredWidget.hasClass('datepicker') || layoutRequiredWidget.hasClass('datetimepicker')) {
            layoutRequiredWidget = layoutRequiredWidget.next();
          }
          if (layoutRequiredWidget.hasClass('radioGroupTable') || layoutRequiredWidget.hasClass('checkGroupTable')) {
            layoutRequiredWidget.before('<strong class="reqStar">*</strong>');
          } else {
            layoutRequiredWidget.css('float', 'left');
            layoutRequiredWidget.after('<strong class="reqStar">*</strong>');
          }
        }
      }
    });

    let removeRequiredWidgets = jQuery('.removeRequired');
    removeRequiredWidgets.each(function (/*index*/) {
      let requiredWidget = jQuery(this);
      if (!requiredWidget.attr('init')) {
        let prevTag = requiredWidget.prev();
        if (prevTag.text() === '*') {
          prevTag.remove();
          if (requiredWidget.parents('.tableBody').length > 0) {
            requiredWidget.css('width', '');
            requiredWidget.css('float', '');
          }
        }
        requiredWidget.attr('init', true);
      }
    });

    let removeLayoutRequiredWidgets = jQuery('.removeLayoutRequired');
    removeLayoutRequiredWidgets.each(function (/*index*/) {
      let layoutRequiredWidget = jQuery(this);
      if (!layoutRequiredWidget.attr('init')) {
        if (layoutRequiredWidget.hasClass('datepicker') || layoutRequiredWidget.hasClass('datetimepicker')) {
          layoutRequiredWidget = layoutRequiredWidget.next();
        }
        let nextTag = layoutRequiredWidget.next();
        if (nextTag.text() === '*') {
          nextTag.remove();
        }
        layoutRequiredWidget.attr('init', true);
      }
    });
  }

  /**Table filter*/
  filterTbl(jq, phrase, column, ifHidden) {
    let new_hidden = false;
    let visibleRows = 0;
    if (this.last_phrase === phrase) {
      return false;
    }

    let phrase_length = phrase.length;
    let words = phrase.toLowerCase().split(' ');

    // these function pointers may change
    let matches = function (elem, visibleRowz) {
      elem.show();
      (visibleRowz % 2 === 0 ? elem.removeClass('odd').addClass('even') : elem.removeClass('even').addClass('odd'));
    };
    let noMatch = function (elem) {
      elem.hide();
      new_hidden = true;
    };
    let getText = function (elem) {
      return elem.text();
    };

    if (column) {
      let index = null;
      jq.find('thead > tr:last > th').each(function (i) {
        if (jQuery.trim(jQuery(this).text()) === column) {
          index = i;
          return false;
        }
      });
      if (index === null) {
        throw('given column: ' + column + ' not found');
      }

      getText = function (elem) {
        return jQuery(elem.find(
          ('td:eq(" + index + ")'))).text();
      };
    }
    let elems;
    // if added one letter to last time,
    // just check newest word and only need to hide
    if ((words.size > 1) && (phrase.substr(0, phrase_length - 1) ===
      this.last_phrase)) {

      if (phrase[-1] === ' ') {
        this.last_phrase = phrase;
        return false;
      }

      words = words[-1]; // just search for the newest word

      // only hide visible rows
      matches = function (/*elem, visibleRows*/) {
        ;
      };
      elems = jq.find('tbody:first > tr:visible');
    } else {
      new_hidden = true;
      elems = jq.find('tbody:first > tr');
    }

    for (let i = 0; i < elems.length; i++) {
      let elem = jQuery(elems[i]);
      if (this.has_words(getText(elem), words, false)) {
        matches(elem, visibleRows);
        visibleRows++;
      } else {
        noMatch(elem);
      }
    };

    // RECALCULATE COLUMNS WIDTH
    this.recalculateTableColumnsWidth(jq.closest('.dt').parent());

    this.last_phrase = phrase;
    if (ifHidden && new_hidden) {
      ifHidden();
    }
    return jq;
  };

  /** Listbox filter*/
  filterListBox(jq, phrase) {
    let new_hidden = false;
    let visibleRows = 0;
    if (this.last_phrase === phrase) {
      return false;
    }

    let phrase_length = phrase.length;
    let words = phrase.toLowerCase().split(' ');

    // these function pointers may change
    let matches = function (elem) {
      elem.show();
    };
    let noMatch = function (elem) {
      elem.hide();
      new_hidden = true;
    };
    let getText = function (elem) {
      return elem.text();
    };

    let elems;
    // if added one letter to last time,
    // just check newest word and only need to hide
    if ((words.size > 1) && (phrase.substr(0, phrase_length - 1) ===
      this.last_phrase)) {

      if (phrase[-1] === ' ') {
        this.last_phrase = phrase;
        return false;
      }

      words = words[-1]; // just search for the newest word

      // only hide visible rows
      matches = function (/*elem, visibleRows*/) {
        ;
      };
      elems = jq.find('select > option:visible');
    } else {
      new_hidden = true;
      elems = jq.find('select > option');
    }

    for (let i = 0; i < elems.length; i++) {
      let elem = jQuery(elems[i]);
      if (this.has_words(getText(elem), words, false)) {
        matches(elem);
        visibleRows++;
      } else {
        noMatch(elem);
      }
    };

    this.last_phrase = phrase;
    return jq;
  };


// not jQuery dependent
// '' [''] -> Boolean
// '' [''] Boolean -> Boolean
  has_words(str, words, caseSensitive) {
    let text = caseSensitive ? str : str.toLowerCase();
    for (let i = 0; i < words.length; i++) {
      if (text.indexOf(words[i]) === -1) {
        return false;
      }
    }
    return true;
  }


  selRow(rowIndex, table) {
    // let jqRowIndex = rowIndex-1;
    // table.find('tbody tr').removeClass('even');
    table.find('tbody tr').removeClass('selRow');
    if (rowIndex !== -1) {
      table.find('tbody tr[id=' + rowIndex + ']').addClass('selRow');
    }
    // table.find('tbody tr:eq('+jqRowIndex+')').addClass('even');
  }

// Used in inject eject buttons inside datatable
  selRadio(rowIndex, radio) {
    let jRadio = jQuery(radio);
    let jBody = jRadio.closest('tbody');
    jBody.find('tr').removeClass('selRow');
    jBody.find('input').removeClass('radioChecked');
    if (rowIndex === -1) {
      radio.checked = false;
    } else {
      jRadio.closest('tr').addClass('selRow');
      jRadio.addClass('radioChecked');
    }
  }

  setTableLayout() {

    let dataTables = jQuery('.dt');
    for (let z = 0; z < dataTables.length; z++) {
      let dataTable = jQuery(dataTables[z]);

      let header = dataTable.find('tr:first th');
      let firstRow = dataTable.find('table tbody tr:first td');

      for (let column = 0; column < firstRow.length; column++) {
        let hW = jQuery(header[column]).width();
        jQuery(firstRow[column]).css('width', hW);
        jQuery(header[column]).css('width', hW);
      }

      let caption = dataTable.find('.caption');


      dataTable.css('position', 'relative');
      caption.css('position', 'fixed');
    }
  }

// hide dashbard at process start
  animateDashboard() {
    jQuery('#dashboardInclude').css('height', '0%');
  }

  buildJSonBaner() {
    let patHeader = jQuery('.patientheader');
    let jSonBaner = null;

    if (patHeader.length > 0) {

      let patientId = jQuery('#patientId').text();
      let patientNameGiv = jQuery('#patientNameGiv').text();
      let patientNameFam = jQuery('#patientNameFam').text();
      let birthTime = jQuery('#hf\\:birthTime').text();
      let patientGenderCode = jQuery('#patientGenderCode').text();
      let patientEncounterId = jQuery('#patientEncounterId').val();
      let assignedSDLId = jQuery('#assignedSDLId').val();
      let temporarySDLId = jQuery('#temporarySDLId').val();
      let therapyId = jQuery('#therapyId').val();
      let appointmentId = jQuery('#appointmentId').val();

      jSonBaner = '{';
      if (patientId !== '') {
        jSonBaner = jSonBaner + '"patientId":' + patientId;
      }
      if (patientNameGiv !== '') {
        jSonBaner = jSonBaner + ',"patientNameGiv":"' + patientNameGiv + '"';
      }
      if (patientNameFam !== '') {
        jSonBaner = jSonBaner + ',"patientNameFam":"' + patientNameFam + '"';
      }
      if (birthTime !== '') {
        jSonBaner = jSonBaner + ',"birthTime":"' + birthTime + '"';
      }
      if (patientGenderCode !== '') {
        jSonBaner = jSonBaner + ',"patientGenderCode":"' + patientGenderCode + '"';
      }
      if (patientEncounterId !== '') {
        jSonBaner = jSonBaner + ',"patientEncounterId":' + patientEncounterId;
        jSonBaner = jSonBaner + ',"renderPrivacy":' + true; // TODO get from server
      }
      if (assignedSDLId !== '') {
        jSonBaner = jSonBaner + ',"assignedSDLId":' + assignedSDLId;
      }
      if (temporarySDLId !== '') {
        jSonBaner = jSonBaner + ',"temporarySDLId":' + temporarySDLId;
      }
      if (therapyId !== '') {
        jSonBaner = jSonBaner + ',"therapyId":' + therapyId;
      }
      if (appointmentId !== '') {
        jSonBaner = jSonBaner + ',"appointmentId":' + appointmentId;
      }
      jSonBaner = jSonBaner + '}';

    } else {
      jSonBaner = '{}';
    }
    return jSonBaner;
  }


// javascript to flex dashboard communicator method
  flexCommunicator(moduleTarget, varName, varValue) {

    let mDashboard = jQuery('MDashboard');

    if (mDashboard.size === 1) {
      let jSonBaner = this.buildJSonBaner();
      mDashboard.context['MDashboard'].jsCommunicator(moduleTarget, this.cid, jSonBaner, varName, varValue);
    }
  }
  /*****************************************
   POPUP FUNCTIONS
   ******************************************/

  /* --- FORM CHANGED ---

   Called by link/button with actions that requires check.
   Set propertie:

   Jolly Parameter = onmousedown='openSomethingChangedPopup(this.id,
   '#{static.dialog_title_warning}',
   '#{static.dialog_message_go_out}',
   '#{static.dialog_button_ok}',
   '#{static.dialog_button_cancel}');' */

  openSomethingChangedPopup(widgetId, title, message, okButton, cancelButton) {
    if (typeof this.somethingChanged !== 'undefined') {
      if (this.somethingChanged) { // && (processReadOnly !== 'true')) {
        // FIXME CHANGE THIS WITH CALLBACK, FIXME WORKS ONLY WITH GLOBAL window.Phi
        let toCall = 'jQuery(\'#' + widgetId.replace(':', '\\\\:') + '\')[0].click(); window.Phi.somethingChanged=false;';
        this.openPopup(toCall, title, message, 'msg-warning', okButton, cancelButton);
      }
    }
  }

  openCancelConfirmPopup(widgetId, title, message, okButton, cancelButton) {
    let toCall = 'document.getElementById("' + widgetId + '").click()';
    this.openPopup(toCall, title, message, 'msg-warning', okButton, cancelButton);
  }

  openPopup(toCall, title, message, popupClass, okButton, cancelButton) {

    let buttons = {};

    let warningDialog = jQuery('<div title="' + title + '" class="' + popupClass + '"><div class="cms">' + message + '</div></div>');
    jQuery('body').after(warningDialog);

    if (cancelButton !== undefined && cancelButton !== null) {
      buttons[cancelButton] = {
        text: cancelButton,
        click: function () {
          jQuery(this).dialog('close');
          warningDialog.remove();
          return false;
        }
      };
    }

    if (okButton !== undefined && okButton !== null) {
      buttons[okButton] = {
        text: okButton,
        click: function () {
          jQuery(this).dialog('close');
          warningDialog.remove();

          if (typeof this.somethingChanged !== 'undefined') {
            this.somethingChanged = false;
          }
          if (toCall) {
            let a4jFtoBeCalled = new Function(toCall);
            a4jFtoBeCalled.call(this);
          }
          return false;
        }
      };
    }
    ;

    warningDialog.dialog({
      resizable: false,
      width: 'auto',
      dialogClass: 'pu-' + popupClass + ' pu-other-customizations' + ((okButton === undefined || okButton === null)
      && (cancelButton === undefined || cancelButton === null) ? '' : ' pu-no-close'),
      modal: true,
      buttons: buttons
    });
  }

  openIframePopup(url) {
    let iframeDialog = jQuery('#iframeDialog');
    let firstOpen = false;

    if (!iframeDialog.length) {
      iframeDialog = jQuery('<div id="iframeDialog" title="PDF" style="display:none"><iframe id="iframe" width="99%" height="99%"/></div>');
      jQuery('body').after(iframeDialog);
      firstOpen = true;
    }
    iframeDialog.find('#iframe').attr('src', '');
    iframeDialog.find('#iframe').attr('src', url);

    iframeDialog.dialog({
      dialogClass: 'minimizableDialog',
      height: jQuery(window).height() - 50,
      width: jQuery(window).width() - 50,
      position: {my: 'center', at: 'center', of: window},
      modal: false,
      close: function (/*event, ui*/) {
        iframeDialog.find('#iframe').attr('src', '');
      } // remove previous pdf
    });


    if (firstOpen) {

      jQuery('.minimizableDialog').children('.ui-dialog-titlebar')
        .append('<button id="miniMaxButton" title="miniMaxButton" aria-disabled="false" role="button" ' +
          'class="ui-button ui-widget ui-state-default ui-corner-all ui-button-icon-only ui-dialog-titlebar-minimize">' +
          '<span id="miniMaxIcon" class="ui-button-icon-primary ui-icon ui-icon-minimizethick"></span>' +
          '<span class="ui-button-text"></span>' +
          '</button>');

      jQuery('#miniMaxButton').click(function () {
        let btn = jQuery('#miniMaxIcon');
        if (btn.hasClass('ui-icon-minimizethick')) {
          btn.removeClass('ui-icon-minimizethick');
          btn.addClass('ui-icon-maximizethick');

          jQuery('#iframeDialog').parents('.ui-dialog').animate({
            height: '32px',
            width: '150px',
            top: jQuery(window).height() - 40,
            left: 0
          }, 200);
        } else {
          btn.removeClass('ui-icon-maximizethick');
          btn.addClass('ui-icon-minimizethick');
          jQuery('#iframeDialog').parents('.ui-dialog').animate({
            top: 25,
            left: 25,
            height: jQuery(window).height() - 50,
            width: jQuery(window).width() - 50
          }, 200);
        }

      });
    }
  }

  openFormPopup(popupId) {
    // ANGULAR POPUP
    // const popup = document.querySelector('#fPop #p');
    //
    // if (popup) {
    //   const popupViewId = popup.getAttribute('title');
    //   if (popupViewId !== 'empty.xhtml') {
    //     location.hash = location.hash + '(popup:jsf)';
    //   } else {
    //     if (location.hash.indexOf('(popup:jsf)') !== -1) {
    //       location.hash = location.hash.replace('(popup:jsf)', '');
    //     }
    //   }
    // }
    // REMOVE TO USE ANGULAR POPUP
    let popupDiv = jQuery(popupId);
    if (popupDiv.children().first().attr('title') !== 'empty.xhtml') {

      let h1Title = popupDiv.find('#formTitle');
      if (h1Title.length) {
        popupDiv.attr('title', h1Title.text());
        h1Title.hide();
      }

      if (!popupDiv.parent().hasClass('ui-dialog')) {
        let dWidth: any = 'auto';
        let dHeight: any = 'auto';

        popupDiv.addClass('phiJsf');

        if (this.popupWidth && this.popupHeight) {

          let wWidth = jQuery(window).width();
          dWidth = wWidth * this.popupWidth;
          let wHeight = jQuery(window).height();
          dHeight = wHeight * this.popupHeight;

        }

        let popupOnClose;
        if (this.popupOnCloseFuncion) {
          popupOnClose = this.popupOnCloseFuncion;
        } else if (window['popupOnCloseFuncion']) {
          popupOnClose = window['popupOnCloseFuncion'];
        }

        this.currentDialog = popupDiv.dialog({
          width: dWidth,
          height: dHeight,
          position: {my: 'center', at: 'center', of: window},
          modal: true,
          close: function (/*event, ui*/) {
            let setPopupFunction = window['setPopup'];
            setPopupFunction('empty'); // FIXME call A4J.submitz
            this.currentDialog.find('.tabset').remove();
            this.currentDialog.dialog('destroy');
            this.currentDialog = null;
            delete this.popupWidth;
            delete this.popupHeight;
            if (popupOnClose) {
              popupOnClose();
            }
            delete this.popupOnCloseFuncion;
          }.bind(this)
        });

      }
    } else { // destroy empty popup
      if (this.currentDialog !== null) {
        this.currentDialog.dialog('destroy');
        this.currentDialog = null;
        delete this.popupWidth;
        delete this.popupHeight;
        delete this.popupOnCloseFuncion;

      }

    }
    // End REMOVE TO USE ANGULAR POPUP
  }

  /**
   * Opens integrations in a new fullscren window, used in combination with chrome plugin Legacy browser to open with IE
   * Replace regexp 4 jolly widgets:
   * var urlToOpen\s*= &quot;(.*)&quot;;   ->   Phi.openWindow(&quot;$1&quot;,null);
   * @param urlToOpen
   * @param closedCallback
   */
  openWindow(urlToOpen, closedCallback, otherCallback = null, widthPortion = 1.0, heightPortion = 1.0, viewBrowserBlocking = true) {
    urlToOpen = urlToOpen.replace('&amp;', '&');
    logInfo('Opening window: ' + urlToOpen, this.cid);

    if (window.navigator.userAgent && window.navigator.userAgent.indexOf('Chrome/32.0.1700.107') !== -1) { // Chrome frame
      this.openPopup(null, 'Attenzione', 'Questo browser non è supportato da tutte le integrazioni.', 'msg-warning', 'Ok', null);
    }

    let windowWidth = screen.width * widthPortion;
    let windowHeight = screen.height * heightPortion;

    let popUp = window.open(urlToOpen, 'myWindow',
      'width=' + windowWidth + '  ,height=' + windowHeight + ' ,top=0, left=0,location=1,scrollbars=1,resizable=0');

    if (otherCallback) {
      otherCallback();
    }

    if (!popUp || popUp.closed || typeof popUp.closed === 'undefined') {
      if (viewBrowserBlocking) {
        this.openPopup(null, 'Attenzione', 'Il browser sta bloccando il popup: verifica le impostazioni', 'msg-warning', 'Ok', null);
        logError('Popup blocked: ' + urlToOpen, this.cid);
      }
    } else {
      let focusTimeout = setTimeout(() => {
        if (popUp) {
          popUp.focus();
          clearTimeout(focusTimeout);
        }
      }, 10);

      let timer = setInterval(() => {
        if (!popUp || popUp.closed || typeof popUp.closed === 'undefined') {
          clearInterval(timer);
          if (closedCallback) {
            closedCallback();
          }
        } else {
          if (otherCallback) {
            otherCallback();
          }
        }
      }, 1000);

    }
    return popUp;
  }

  uploadReportHeader(inputName, finishedCallBack) {
    this.uploadAttachment(inputName, finishedCallBack, false, true);
  }

  uploadAttachment(inputName, finishedCallBack, isTemplate: boolean, isReportHeader: boolean) {

    this.blockUI();

    let uploadUrl = './uploadserv?cid=' + this.cid;
    if (isTemplate) {
      uploadUrl = uploadUrl + '&templateRepo=true'
    }
    if (isReportHeader) {
      uploadUrl = uploadUrl + '&reportHeader=true'
    }
    let formData = new FormData();
    let filesList  = jQuery('input[name=\'' + inputName + '\']')[0].files
    if (filesList) {
      for (let z = 0; z < filesList.length; z++) {
        formData.append( 'filedata_' + z, filesList[z] );
      }
    }

    return jQuery.ajax({
      url: uploadUrl,
      type: 'POST',
      data: formData,
      contentType: false,
      cache: false,
      processData: false,
      success: () => {
        if (finishedCallBack) {
          finishedCallBack();
        }
        this.unblockUI();
      },
      error: (jqXHR, textStatus, errorThrown) => {
        console.log('Error uploading' + jqXHR + ' ' + textStatus + ' ' + errorThrown);
        this.unblockUI();
      }
    });
  }

  upload(formId, entityName, entityId, fieldName, finishedCallBack) {

    this.blockUI();

    let entityUrl = entityName.toLowerCase() + 's';
    let uploadUrl = './resource/rest/' + entityUrl + '/' + entityId + '/upload/' + fieldName + '?cid=' + this.cid;
    let formData = new FormData(jQuery(formId)[0]);

    // if (formData.get('filedata').type === 'application/x-zip-compressed') {
    //   console.log(formData.get('filedata').type);
    // }

    return jQuery.ajax({
      url: uploadUrl,
      type: 'POST',
      data: formData,
      contentType: false,
      cache: false,
      processData: false,
      success: () => {
        if (finishedCallBack) {
          finishedCallBack();
        }
        this.unblockUI();
      },
      error: (jqXHR, textStatus, errorThrown) => {
        console.log('Error uploading' + jqXHR + ' ' + textStatus + ' ' + errorThrown);
        this.unblockUI();
      }
    });
  }

  concatenateText(text, widgetId, prepend, addBreakBefore, addBreakAfter, widgetType) {
    let widget = this.findTargetWidgets(widgetId);
    let type;
    if (widget) {
      if (widgetType) {
        type = widgetType;
      } else {
        type = 'textarea';
      }
      if (!widget.is(type)) {
        widget = widget.find(type + ':first');
      }

      let isFormatted = ( widget.attr('Class').indexOf('formattedTextArea') >= 0 );

      // if idestination text area is formatted and passedText is not formatted, include it in a paragraph.
      if (isFormatted && text.indexOf('<p') < 0) {
        text = '<p style="font-size: 10pt">' + text + '</p>';
      }
      let textOld = widget.val();
      if (addBreakBefore) {
        if (isFormatted) {
          text = '<p style="font-size: 10pt">&nbsp;</p>' + text;
        } else {
          text = '\n' + text;
        }
      }
      if (addBreakAfter) {
        if (isFormatted) {
          text = text + '<p style="font-size: 10pt">&nbsp;</p>';
        } else {
          text += '\n';
        }
      }

      if (prepend) {
        widget.val(text + textOld);
      } else {
        widget.val(textOld + text);
        // trigger change event, to redraw rich editor
        widget.trigger('change');
      }
    }
  }

  openInNewTab(url) {
   url = url + '?cid=' + this.cid;
   let win = window.open(url, '_blank');
   win.focus();
  }

  showError(error) {
    jQuery('#errorMenuErrors dl:first-child').append('<dt><span class="rich-messages-label">' + error + '</span></dt>');
    jQuery('#errorMenuErrors dl:first-child').show();
    jQuery('body').removeClass('togglenav');
  }


  endsWith(str, suffix) {
    return str.indexOf(suffix, str.length - suffix.length) !== -1;
  }

// calling DocumentRest method which render a report(odt template) as pdf.
  renderTemplateAsPdf(templateTitle) {

    jQuery.ajax({
      url: './resource/rest/documents/templateByTitle/' + templateTitle,
      type: 'GET',
      success: function (data/*, textStatus, jqXHR*/) {
        console.log(data);
        this.templateId = data.internalId;
        window.open('./resource/rest/documents/' + this.templateId + '/pdf?cid=' + this.cid, '_blank');
      },
      error: function (jqXHR, textStatus, errorThrown) {
        alert('Error loading template by title: ' + templateTitle + ' ' + textStatus + ' ' + errorThrown + ' ' + jqXHR);
      }
    });
  }

  timerTelevisit= (internalApp: string, firmedRep?: boolean ) =>{
    const getTelevisitUrl = () =>{
      if(!document.getElementById("i:jollyWidgetTimerCheckUrlPath")){
        clearTimeout(this.timerTelevisitInstance);
        delete this.timerTelevisitInstance;
      }else {
        if (jQuery('#loader').is(":visible")) {
        return;
        }
        jQuery.ajax({
          url:  './resource/rest/appointments/televisitUrlPath/' + internalApp + '?_=' + new Date().getTime(),
          type: 'GET',
          success: function(data){
            var btnTele = document.getElementById("i:btnTelevisit");
            if(data){
              console.log(data);
              btnTele.style.display = 'block';
            } else {
              btnTele.style.display = 'none';
            }
          },
          error: function(data) {
            // alert('Qualcosa non ha funzionato, sei sicuro di aver effettuato il login?');
          }
        });
      }
    }
    if(!this.timerTelevisitInstance){
      this.timerTelevisitInstance = setInterval(getTelevisitUrl, 5000);
    }
    if(firmedRep){
      clearTimeout(this.timerTelevisitInstance);
      delete this.timerTelevisitInstance;
      var btnTele = document.getElementById("i:btnTelevisit");
      btnTele.style.display = 'none';
    }
  }

  focusIt(widgetId) {
    if (jQuery('#i\\:' + widgetId + ' input').length > 0) { // input with widget label creates a div parent
      jQuery('#i\\:' + widgetId + ' input').first()[0].focus();
    } else {
      jQuery('#i\\:' + widgetId).focus();
    }
  }

  isRightFormat(widget, e, keyex, regex, callChanged=false) {
    let keynum = this.eventKey(e);
    let kp = '';
    let kbl = true;
    let pbl = true;
    let finalText = '';
    if (regex && e.which) {
      let currentText = widget.value;
      let selectedText = this.getInputSelection(widget);
      if (keynum === 8) { // BACKSPACE
        finalText = currentText.substring(0, selectedText.start - 1) + currentText.substring(selectedText.end, currentText.length);
      } else if (keynum === 127) { // DELETE
        finalText = currentText.substring(0, selectedText.start) + currentText.substring(selectedText.end + 1, currentText.length);
      } else if (
        keynum >= 32 && keynum <= 126
        || keynum >= 128 && keynum <= 255
        || keynum >= 256
      ) { // NUMBERS AND OTHER PRINTABLE CHARACTERS
        /*if (e.type == 'keyup' || e.type == 'keydown' && keynum >= 96 && keynum <= 111) {
          keynum = keynum - 48;
        }*/
        if (e.type == 'keyup' || e.type == 'keydown' && keynum >= 96 && keynum <= 105) {
          keynum = keynum - 48;
        }
        kp = String.fromCharCode(keynum);
        kbl = keyex.test(kp);
        finalText = currentText.substring(0, selectedText.start) + kp + currentText.substring(selectedText.end, currentText.length);
      } else {
        return true;
      }
      pbl = regex.test(finalText);
      if (callChanged && kbl && pbl) {
        widget.value = finalText;
        jQuery(widget).change();
        return false;
      }
    }
    return kbl && pbl;
  }

  eventKey(e) {

    let keynum = e.which || e.keyCode || 0;

    return keynum;
  }

  getInputSelection(el) {
      let start = 0, end = 0, normalizedValue, range,
          textInputRange, len, endRange;

      if (typeof el.selectionStart === 'number' && typeof el.selectionEnd === 'number') {
          start = el.selectionStart;
          end = el.selectionEnd;
      } else {
          range = document.getSelection().getRangeAt(0);

          if (range && range.parentElement() === el) {
              len = el.value.length;
              normalizedValue = el.value.replace(/\r\n/g, '\n');

              // Create a working TextRange that lives only in the input
              textInputRange = el.createTextRange();
              textInputRange.moveToBookmark(range.getBookmark());

              // Check if the start and end of the selection are at the very end
              // of the input, since moveStart/moveEnd doesn't return what we want
              // in those cases
              endRange = el.createTextRange();
              endRange.collapse(false);

              if (textInputRange.compareEndPoints('StartToEnd', endRange) > -1) {
                  start = end = len;
              } else {
                  start = -textInputRange.moveStart('character', -len);
                  start += normalizedValue.slice(0, start).split('\n').length - 1;

                  if (textInputRange.compareEndPoints('EndToEnd', endRange) > -1) {
                      end = len;
                  } else {
                      end = -textInputRange.moveEnd('character', -len);
                      end += normalizedValue.slice(0, end).split('\n').length - 1;
                  }
              }
          }
      }

      return {
          start: start,
          end: end
      };
    }

    openArmoniaWeb(type: 'VIEW_REPORTS'|'INSERT_REQUEST'|'MODIFY_REQUEST', auth, patientId, wardId, VisitNumber, baseUrl) {
      let a4jFtoBeCalled = new Function('goBack()');
      let popUp = this.openWindow('', a4jFtoBeCalled);

      if (popUp) {
        let popUpDoc = popUp.document;
        let form = popUpDoc.createElement('form');
        form.setAttribute('id', 'armoniaWebForm');
        form.setAttribute('method', 'post');
     //   form.setAttribute('action', 'http://172.16.1.70/armoniaweb/passthrough.zul');
        if (baseUrl) {
          form.setAttribute('action', baseUrl);
        } else {
          form.setAttribute('action', 'http://srvspartito/armoniaweb/passthrough.zul');
        }
        form.setAttribute('target', '_self');

        let callParmsJson  = {
          callParms: {
            criteria: [
              {key: 'moduleId', value: 'AIS.DOCVIEW'}
            ]
          }
        };

        let activeContextJson = {
          activeContext: {
          }
        };

        if (type === 'VIEW_REPORTS') {
          if (patientId) {
            activeContextJson.activeContext['patient'] = {externalId: patientId};
          }
          if (wardId) {
              activeContextJson.activeContext['user'] = {activeDepartment: {id: wardId}};
            } else {
              activeContextJson.activeContext['user'] = {activeDepartment: {id: '*'}};
            }
        } else {
          if (patientId) {
            activeContextJson.activeContext['patient'] = {id: patientId};
          }
          if (wardId) {
            activeContextJson.activeContext['order'] = {ward: {id: wardId}};
          }
          if (VisitNumber) {
            activeContextJson.activeContext['admission'] = {id: VisitNumber};
          }
          if (type === 'MODIFY_REQUEST') {
            callParmsJson.callParms.criteria[0].value = 'AIS.EXAMMODF';
          } else {
            callParmsJson.callParms.criteria[0].value = 'AIS.EXAMACC';
          }
        }

        let data = {
          'domain': 'ais',
          'auth': auth,
          'activeContext': JSON.stringify(activeContextJson),
          'activeContextFormat': 'JSON',
          'callParms': JSON.stringify(callParmsJson),
          'callParmsFormat': 'JSON'
        }

        for (let p in data) {
              if (data.hasOwnProperty(p)) {
            let hiddenField = popUpDoc.createElement('input');
            hiddenField.setAttribute('type', 'hidden');
            hiddenField.setAttribute('name', p);
            hiddenField.setAttribute('value', data[p]);
            form.appendChild(hiddenField);
          }
        }

        popUpDoc.body.appendChild(form);
        form.submit();
      }
    }
}
