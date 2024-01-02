jQuery.noConflict();
jQuery.ajaxSetup({ cache: false });
jQuery.support.cors = true; 
var prevPage = '';
var messages = [];
var isValidForm = true;
var lastOpenTab = {};
var currentPage = '';
var username;
var goBackAjax = null;
var goBackPage = '';


if(window.Prototype) {
	delete Object.prototype.toJSON;
	delete Array.prototype.toJSON;
	delete Hash.prototype.toJSON;
	delete String.prototype.toJSON;
}

if (!Function.prototype.bind) {
	  Function.prototype.bind = function(oThis) {
	    if (typeof this !== 'function') {
	      // closest thing possible to the ECMAScript 5
	      // internal IsCallable function
	      throw new TypeError('Function.prototype.bind - what is trying to be bound is not callable');
	    }

	    var aArgs   = Array.prototype.slice.call(arguments, 1),
	        fToBind = this,
	        fNOP    = function() {},
	        fBound  = function() {
	          return fToBind.apply(this instanceof fNOP
	                 ? this
	                 : oThis,
	                 aArgs.concat(Array.prototype.slice.call(arguments)));
	        };

	    if (this.prototype) {
	      // Function.prototype doesn't have a prototype property
	      fNOP.prototype = this.prototype; 
	    }
	    fBound.prototype = new fNOP();

	    return fBound;
	  };
	}

/* Value used to check "Exit without saving" via popup. 
 * See function: openSomethingChangedPopup(widgetId, title, message, okButton, cancelButton)
 * 
 * Remove or comment to exclude check */ 
var somethingChanged = false;
				
window.onbeforeunload = function() {
	if (somethingChanged){
		var popit = 'ATTENZIONE! \nStai lasciando questa pagina senza salvare le modifiche.';
		return 	popit;
	}
}

var cid;

var ajaxUrl = jQuery(location).attr('pathname');
ajaxUrl = ajaxUrl.substring(0, ajaxUrl.indexOf('/',1)+1);
ajaxUrl = ajaxUrl + "ajax";

function blockUI() {
	jQuery('#loader').css("display", "table");
}

function unblockUI() {
	//console.log("UI unblocked");
	jQuery('#loader').hide();
}

function startAjaxReq(){
	blockUI();
}

String.prototype.startsWith = function(searchString, position){
	position = position || 0;
    return this.substr(position, searchString.length) === searchString;
}

String.prototype.contains = function(it) { 
	return this.indexOf(it) != -1; 
}

/* Function called on document ready:
 * see jQuery().ready(function() {
 * 
 * It registers an onMouseDown event for each node under #content.
 * The callback function invoked checks the target where the event fired and:
 * 
 * If INPUT:
 * - Button with Style Class containing "save" - set somethingChanged = false
 * - Button with Style Class containing "linkUnlink" - set somethingChanged = true
 * else set somethingChanged = true
 * 
 * If A (link):
 * with Style Class containing "save" - set somethingChanged = false
 * with Style Class containing "linkUnlink" - set somethingChanged = true
 * 
 * If INS, SELECT, OPTION or TEXTAREA - set somethingChanged = true
 * If IMG with Style Class containing ui-datepicker-trigger - set somethingChanged = true;
 */
function checkSomethingChanged() {
	//From #content we get all mousedown events via bubling
	jQuery('#content').on('mousedown', function(event) {
		try {
		var target=event.target;
		var disabled=target.disabled;
		var nodeName=target.nodeName;
		var className=target.className;
		
		//Exclude divisions, table lines and filters
		if ((nodeName != 'DIV') && (!disabled) && (!className.contains('filter'))) {
			
		    switch(nodeName) {
		    
		    case 'INPUT':
		    	var id=target.id;
				if (id.contains('Button')){
					//Link unlink objects
					if (className.contains('linkUnlink'))
						somethingChanged = true;
					
					//Save button
					else if (className.contains('save'))
						somethingChanged = false;
				} else if (id.contains('RadioGroup')){
					if (!target.closest('table').className.contains('filter'))
						somethingChanged = true;
					
				} else if (!id.startsWith('f'))
					somethingChanged = true;
				break
		    
		    case 'TD':
		    	if (target.closest('div[id*=\'DataGrid\']') && target.closest('div[id*=\'DataGrid\']').className.contains('linkUnlink'))
		    		somethingChanged = true;
		    	break
			
		    case 'A':
				//Link unlink objects
				if (className.contains('linkUnlink'))
					somethingChanged = true;
				//Save button
				else if (className.contains('save'))
					somethingChanged = false;
				break
				
			case 'INS':
			case 'SELECT':
			case 'OPTION':
			case 'TEXTAREA':
				somethingChanged = true;
				break
			
			case 'IMG':
				if (className.contains('ui-datepicker-trigger'))
					somethingChanged = true;
				break
		    }
		}
		} catch(e) {
			console.log('Error checkSomethingChanged' + e.stack);
		}
	})
}

function stopAjaxReq(){
	
	try {
		unblockUI();
		
		var newCid = jQuery('#conversationId').val();
	
		if (cid != newCid) { //if conversation changed
			//console.log("Changed conversation from " + cid + " to " + newCid);
			cid = newCid;
			Tree.cid = cid;
			flexCommunicator('RETURN_TO_HOME', null, null );
			jQuery('#dashboardInclude').css( "height", "100%");
		} 
	
		if (cid != undefined) {
			location.hash = 'cid-'+cid;
		} else {
			location.hash = "";
		}
	
		showHideProcessList();
	
		setToggleHeight();
	
		/* tabs */
		if (jQuery('.tabset').attr('init')==undefined){ 
			jQuery('.tabset').tabs('.tab-content');
			jQuery('.tabset').attr('init',true);   
		}

		// INIT SCROLLER
		initScroller();

		// CALCULATES FORM HEIGHT AND POSITION
		recalculateVerticalPos();
	
		// MANAGES REQUIRED WIDGETS
		manageRequired();

		// MANAGES DISABLED WIDGETS
		manageDisabled();
		
		// DISABLE CONTAINER INPUTS
		var inputsToDisable = jQuery('.disableContent :input, .disableTab :input');
		inputsToDisable.each(function(){
			jQuery(this).prop( "disabled", true );
		});
		  
		//MANAGES tinyMCE editors
		tinyMCE.remove();
		var richTextEditors = jQuery('.formattedTextArea');
		for (z=0; z < richTextEditors.length ; z++) {
			var jEditor = jQuery(richTextEditors[z]);
			redrawFormattedTextArea("#"+jEditor.prop('id').replace(/:/gi,'\\:'), jEditor.width(),jEditor.height(), jEditor.prop('readonly'));
		}

		jQuery('.radioChecked').prop('checked',true);
		jQuery('.inputDisabled').prop('disabled',true);
		
		var processReadOnly = jQuery('#processReadOnly').val();
		
		if (processReadOnly == "true") {
			jQuery('#i :input').not('.readOnly').attr("disabled", true);
			jQuery('#i :input').not('.readOnly').parent().addClass("disabled");
			jQuery('#i a').filter(function () {return jQuery(this).closest('.paging, .tableHeader').length < 1;}).not('.readOnly').removeAttr("onclick");
			jQuery('#i a').filter(function () {return jQuery(this).closest('.paging, .tableHeader').length < 1;}).not('.readOnly').addClass("disabled");
		}

	
		// DATE AND TIME PICKERS
		var dateTimePickers = jQuery('input.datepicker:enabled,input.datetimepicker:enabled,input.timepicker:enabled,input.datepicker:disabled,input.datetimepicker:disabled,input.timepicker:disabled');
		initDateTimePickers(dateTimePickers);
		initDatePickersBehavior(dateTimePickers);

		var autocompleteList = jQuery('.autocomplete');
		for (z=0; z<autocompleteList.length; z++) {
			var autocomp = jQuery(autocompleteList[z]);
			//add placeholder...
			autocomp.attr("placeholder", "Cerca...");
			autocomp.autocomplete({
				source: function(event, ui){  
					var id = this.element.context.id;
					var fNamePre = id.substring(0, id.lastIndexOf('_'));
					var fName = fNamePre.substring(fNamePre.lastIndexOf(':')+1);
					var date = '#' + fNamePre.replace(/\:/gi,'\\:') + '_D';
					var dateWidget = jQuery(date);
					if (dateWidget.length < 1) {
						var F = fName + '(search,ui)';
						var a4jFtoBeCalled = new Function('search', 'ui' , F);
						a4jFtoBeCalled.call(null, event.term, ui);
					} else {
						var F = fName + '(search,date,ui)';
						/*var dateDate = dateWidget.datepicker('getDate');
						if (!dateDate) { dateDate = new Date();	}*/
						var dateVal = dateWidget.val();
						var a4jFtoBeCalled = new Function('search', 'date', 'ui', F);
						 a4jFtoBeCalled.call(null, event.term, dateVal, ui);
					}
				},
				focus: function( event, ui ) {
					this.value = ui.item.label;
	
					var currentWidget = jQuery(this).next();
					var idEnd = currentWidget && currentWidget.length > 0 && currentWidget.is('input') ? currentWidget.attr('id') : '';
					/* LOOKING FOR CPA, ZIP OR CODE INPUT */
					while (currentWidget && currentWidget.length > 0 && !currentWidget.is('input')) {
						currentWidget = currentWidget.next();
						idEnd =  currentWidget.attr('id');
					}
					/* IF CPA FOUND */
					if (currentWidget && currentWidget.length > 0 && idEnd && endsWith(idEnd, 'CPA')) {
						currentWidget.val(ui.item.province);
					    currentWidget = currentWidget.next();
					}
					/* LOOKING FOR ZIP OR CODE INPUT */
					while (currentWidget && currentWidget.length > 0 && !currentWidget.is('input')) {
						currentWidget = currentWidget.next();
						idEnd =  currentWidget.attr('id');
					}
					/* IF ZIP FOUND */
					if (currentWidget && currentWidget.length > 0 && idEnd && endsWith(idEnd, 'ZIP')) {
						currentWidget.val(ui.item.zip);
					    currentWidget = currentWidget.next();
					}
					/* LOOKING FOR CODE INPUT */
					while (currentWidget && currentWidget.length > 0 && !(currentWidget.is('input') && idEnd && endsWith(idEnd, 'CODE'))) {
						currentWidget = currentWidget.next();
						idEnd =  currentWidget.attr('id');
					}
					/* IF CODE FOUND */
					if (currentWidget) {
						if (ui.item.internalId) { //SUGGESTION CONTAINING Entities
							currentWidget.val(ui.item.entityClass + "-" + ui.item.internalId);
						} else {
							currentWidget.val(ui.item.id);
						}
					}
					return false;
				},
				select: function( event, ui ) {
					var jThis = jQuery(this);
				
					var currentWidget = jThis.next();
					var idEnd = currentWidget && currentWidget.length > 0 && currentWidget.is('input') ? currentWidget.attr('id') : '';

					while (currentWidget && currentWidget.length > 0 && !(currentWidget.is('input') && idEnd && endsWith(idEnd, 'CODE'))) {
						currentWidget = currentWidget.next();
						idEnd =  currentWidget.attr('id');
					}
					
					if (currentWidget) {
						currentWidget.change();
					}
				
					return false;
				},
				change: function( event, ui ) {
					if ( !ui.item ) {
						var jThis = jQuery(this);
						jThis.val('');
					
						var currentWidget = jThis.next();
						var idEnd = currentWidget && currentWidget.length > 0 && currentWidget.is('input') ? currentWidget.attr('id') : '';
						/* LOOKING FOR CPA, ZIP OR CODE INPUT */
						while (currentWidget && currentWidget.length > 0 && !currentWidget.is('input')) {
							currentWidget = currentWidget.next();
							idEnd =  currentWidget.attr('id');
						}
						/* IF CPA FOUND */
						if (currentWidget && currentWidget.length > 0 && idEnd && endsWith(idEnd, 'CPA')) {
							currentWidget.val('');
							currentWidget = currentWidget.next();
						}
						/* LOOKING FOR ZIP OR CODE INPUT */
						while (currentWidget && currentWidget.length > 0 && !currentWidget.is('input')) {
							currentWidget = currentWidget.next();
							idEnd =  currentWidget.attr('id');
						}
						/* IF ZIP FOUND */
						if (currentWidget && currentWidget.length > 0 && idEnd && endsWith(idEnd, 'ZIP')) {
							currentWidget.val('');
							currentWidget = currentWidget.next();
						}
						/* LOOKING FOR CODE INPUT */
						while (currentWidget && currentWidget.length > 0 && !(currentWidget.is('input') && idEnd && endsWith(idEnd, 'CODE'))) {
							currentWidget = currentWidget.next();
							idEnd =  currentWidget.attr('id');
						}
						/* IF CODE FOUND */
						if (currentWidget) {
							currentWidget.val('');
							currentWidget.change();
						}
					}
				}
			});

			var acItem = autocomp.data('uiAutocomplete');
			if (acItem) {
				acItem._renderItem = function( ul, item ) {
					var acInput = jQuery(this.bindings[0]);
					if (item.id == undefined) {
						item.id = item.internalId;
						item.label = "";
						if(item.name){
							if (item.name.fam != undefined) {
								item.label = item.name.fam + " ";
							} 
							if (item.name.giv != undefined) {
								item.label += item.name.giv;
							}
						/*
						 * come usare questo widget con le entity: fai una read con select di un solo campo
						 * se item non è una entity con name allora prendo la prima proprietà che non è internalId o entityClass
						 */
						}else{
							for (var property in item) {
								if (item.hasOwnProperty(property) && property != 'entityClass' && property != 'internalId') {
									item.label = item[property];
									break;
								}
							}
						}
					}
					var listItem = jQuery( "<li></li>" ).data( "item.autocomplete", item );
					listItem.append("<a>" +
						(acInput.hasClass('showCode') ? "["+item.code+"] " : "") +
						"<span>" + item.label + "</span>" +
						(acInput.hasClass('autoAddress') ? (item.province != null || item.zip != null ? "</br><small>" + (item.province != null ? item.province + " " :  "") + (item.zip != null ? item.zip : "") + "</small>" : "") : "") +
						"</a>"
					).appendTo( ul );
					return listItem;
				};
			}
		}
		
		//make visibile menu if error rich-messages are present..
		var richMessagesVisibile = jQuery('.rich-messages').css('display') != 'none';
		var processListHidden = jQuery('body').is('.togglenav');
		if (richMessagesVisibile && processListHidden ) {
			jQuery('body').removeClass('togglenav');
		} 
		
		
	/*	============= NOT USED TO REVAMP ============
		jQuery('.autocompleteBtn').click(function() {
			var inputName = '#i\\:TSB_' + this.id.substring(this.id.indexOf('_')+1) + '_T';
			var input = jQuery(inputName);
			// close if already visible
			if ( input.autocomplete( "widget" ).is( ":visible" ) ) {
				input.autocomplete( "close" );
				return false;
			}
			// work around a bug (likely same cause as #5265)
			$( this ).blur();
			// pass empty string as value to search for, displaying all results
			input.autocomplete( "search", [""] );
			input.focus();
		});*/
	 
		 /* collapse tree 	  
		 var list = jQuery('.processlist h2');	  
		 if (jQuery('.processlist').attr('init')==undefined){ 
			  list.click(function() { 
			  if(jQuery(this).parent().hasClass('collapsed')) { 
				   jQuery(this).parent().removeClass('collapsed'); 
			  } else { 
				   jQuery(this).parent().addClass('collapsed'); 
			  } 
			  jQuery(this).next().slideToggle('slow',function() {setToggleHeight();}); 
			  }); 
			  jQuery('.processlist').attr('init',true);		   
		 }
		 */
	 
		/* close messages 
		jQuery('.message .close').click(function() {
			jQuery(this).parent().hide('slow');
		});
		*/
	
	} catch(e) {
		alert(e.stack);
	}
	
	var noRichMessages = jQuery('.rich-messages').css('display') == 'none';
	if(goBackAjax && noRichMessages && goBackPage==currentPage){
		goBackAjax.call();
		goBackAjax = null;			//salva e invoca il back
	}else if(goBackAjax && noRichMessages && goBackPage!=currentPage){
		if(goBackAjax.toString().contains("#hf\\\\:l")){
			goBackAjax.call();
			goBackAjax = null;			//salva e LOGOUT
		}else if(goBackAjax.toString().contains("#hf\\\\:hh")){
			goBackAjax.call();
			goBackAjax = null;			//salva e GO HOME
		}else{
			goBackAjax = null;			//salva e non invocare il back
		}		
	}else if(goBackAjax && goBackPage==currentPage){
		goBackAjax = null;
		somethingChanged = true;	//rich-messages visible: no save, no back
	}
}

function initDateTimePickers(dateTimePickers) {
	var today = new Date();
	dateTimePickers.each(function(){
		var dateTimePicker = jQuery(this);
		if (!dateTimePicker.hasClass('hasDatepicker')) {
			var boundValue = parseDate(dateTimePicker);
			if (!boundValue || boundValue > today) {
				boundValue = today;
			}
			var noHigh = new Date(boundValue.getTime());
			noHigh.setFullYear(noHigh.getFullYear()+50);
			var noLow = new Date(boundValue.getTime());
			noLow.setFullYear(noLow.getFullYear()-150);
			if (dateTimePicker.hasClass('datepicker')) {
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
	});
}

function initDatePickersBehavior(dateTimePickers) {
	dateTimePickers.each(function(){
		var dtpThis = jQuery(this);
		if (!dtpThis.hasClass('dtpBehavior')) {
			// GET CURRENT DATE/TIME PICKER DATE AND TIME
			var currentDate = dtpThis.datepicker('getDate');
			// SET DEFAULT DATE
			if (dtpThis.hasClass('todayIsDefault') && !currentDate) {
				dtpThis.datepicker('setDate', new Date());
				initOtherDatePickersBounds(this, dateTimePickers);
			}
			// COMPARISON WITH OTHER DATE/TIME PICKERS
			var thisClass = dtpThis.attr('class');
			if (thisClass.match(/\s*compare(High|Low)_[^\s]*/)) {
				var compareParts = (new RegExp('\\s*(compare(High|Low))_([^\\s]*)\\s*')).exec(thisClass);
				var otherCompare = compareParts[1];
				var otherName = compareParts[3];
				// GET OTHER DATE/TIME PICKER FOR COMPARISON
				dateTimePickers.each(function() {
					if (this.id.match(new RegExp(otherName+'(_id)?$'))) {
						var dtpOther = jQuery(this);
						if (dtpOther.length > 0) {
							if (otherCompare.match(/^compareHigh/)) {
								// SET MINIMUM DATE/TIME
								dtpThis.datepicker( 'option', 'minDate', dtpOther.val());
								// COMPARE DATE AND TIME [WE CANNOT SET FIXED MINIMUM AND MAXIMUM TIME, BUT WE NEED IT ONLY FOR SAME DAY] SELECTING A NEW DATE/TIME ON THIS PICKER
								dtpThis.datepicker( 'option', 'onSelect', function(selectedDate){
									initOtherDatePickersBounds(this, dateTimePickers, selectedDate);
									jQuery(this).change();
								});
								// RE-INIT COMPARING DATE/TIME PICKERS WHEN CLOSING THE OTHER PICKER
								dtpOther.datepicker( 'option', 'onClose', function(selectedDate){
									initOtherDatePickersBounds(this, dateTimePickers, selectedDate);
									jQuery(this).change();
								});
							} else if (otherCompare.match(/^compareLow/)) {
								// SET MAXIMUM DATE/TIME
								dtpThis.datepicker( 'option', 'maxDate', dtpOther.val());
								// COMPARE DATE AND TIME [WE CANNOT SET FIXED MINIMUM AND MAXIMUM TIME, BUT WE NEED IT ONLY FOR SAME DAY] SELECTING A NEW DATE/TIME ON THIS PICKER
								dtpThis.datepicker( 'option', 'onSelect', function(selectedDate){
									initOtherDatePickersBounds(this, dateTimePickers, selectedDate);
									jQuery(this).change();
								});
								// RE-INIT COMPARING DATE/TIME PICKERS WHEN CLOSING THE OTHER PICKER
								dtpOther.datepicker( 'option', 'onClose', function(selectedDate){
									initOtherDatePickersBounds(this, dateTimePickers, selectedDate);
									jQuery(this).change();
								});
							}
						} else {
							var dtpThisDate = parseDate(dtpThis);
							if (dtpThisDate){
								dtpThis.datepicker('setDate',dtpThisDate);
							}
						}
					}
				});
			}
			dtpThis.addClass('dtpBehavior');
		}
	});
}


function dateComparison(thisWidget, otherWidget, comparison, force) {
	var jThisWidget = jQuery(thisWidget);
	var thisDate = jThisWidget.datepicker('getDate');
	if (force || thisDate) {
		if (otherWidget.length > 0 && otherWidget.is('div')) {
			otherWidget = otherWidget.children('input:first');
		}
		if (otherWidget.length > 0) {
			var otherDate = otherWidget.datepicker('getDate');
			if (otherDate) {
				if (comparison == 'compareHigh') {
					var dummyDate = thisDate;
					thisDate = otherDate;
					otherDate = dummyDate;
				}
				if (force || thisDate < otherDate) {
					jThisWidget.datepicker('setDate',otherWidget.val());
				}
			}
		}
	}
}

function initOtherDatePickersBounds(thisWidget, dateTimePickers, selectedDate) {
	var widgetId = jQuery(thisWidget).attr('id').replace(/^([^:]+:)+/,'').replace(/_id$/,'');
	dateTimePickers.each(function(){
		var otherWidget = jQuery(this);
		if (otherWidget.attr('class').match(new RegExp('(compareHigh|compareLow)_'+widgetId))) {
			if (otherWidget.is('div')) {
				otherWidget = otherWidget.children('input:first');
			}
			otherWidget.datepicker( 'option', otherWidget.hasClass('compareHigh_'+widgetId) ? 'minDate' : 'maxDate', parseDate(thisWidget));
			dateComparison(thisWidget, otherWidget, otherWidget.hasClass('compareHigh_'+widgetId) ? 'compareHigh' : 'compareLow');
		}
	});
}

/*function initThisDatePickerBehaviour(thisWidget, dateTimePickers) {
	var jThisWidget = jQuery(thisWidget);
	var widgetId = jThisWidget.attr('id').replace(/^([^:]+:)+/,'').replace(/_id$/,'');
	dateTimePickers.each(function(){
		var otherWidget = jQuery(this);
		if (otherwidget.attr('class').match(new RegExp('(compareHigh|compareLow)_'+widgetId))) {
			jThisWidget.datepicker( 'option', 'onClose', function(selectedDate){
				initOtherDatePickersBounds(this, dateTimePickers, selectedDate);
			});
		}
	});
}*/

function showDateTimePicker(widget) {
	var jWidget = jQuery(widget);
	var prevWidget = jWidget.prev();
	if (prevWidget.length > 0 && prevWidget.is(':enabled')) {
		if (prevWidget.datepicker('widget').is(':visible')) {
			prevWidget.datepicker('hide');
		} else {
			prevWidget.datepicker('show');
		}
	}
}

function parseDate(widget) {
	var jWidget = jQuery(widget);
	var result = null;
	var widgetValue = jWidget.val();
	if (widgetValue) {
		try{
			var lang = getLangCode();
			var dateFormat = jWidget.hasClass('datepicker') || jWidget.hasClass('datetimepicker') ? jQuery.datepicker.regional[lang].dateFormat : null;
			var timeFormat = jWidget.hasClass('timepicker') || jWidget.hasClass('datetimepicker') ? jQuery.timepicker.regional[lang].timeFormat : null;
			if (dateFormat && timeFormat) {
				result = jQuery.datepicker.parseDateTime(dateFormat, timeFormat, widgetValue);
			} else if (dateFormat) {
				result = jQuery.datepicker.parseDate(dateFormat, widgetValue);
			} else if (timeFormat) {
				var newTime = jQuery.datepicker.parseTime(timeFormat, widgetValue);
				result = new Date();
				result.setHours(newTime['hour']);
				result.setMinutes(newTime['minute']);
				result.setSeconds(0);
				result.setMilliseconds(0);
			}
		}catch(e){
			result=null;
		}
	}
	return result;
}

function dateTimePickerFormat(widget, dateObject) {
	var jWidget = jQuery(widget);
	var result = null;
	if (jWidget.length > 0) {
		var lang = getLangCode();
		var dateFormat = jWidget.hasClass('datepicker') || jWidget.hasClass('datetimepicker') ? jQuery.datepicker.regional[lang].dateFormat : null;
		var timeFormat = jWidget.hasClass('timepicker') || jWidget.hasClass('datetimepicker') ? jQuery.timepicker.regional[lang].timeFormat : null;
		if (dateFormat) {
			result = jQuery.datepicker.formatDate(dateFormat, dateObject);
		}if (timeFormat) {
			result += (result ? ' ' : '') + jQuery.datepicker.formatTime(timeFormat, dateObject);
		}
	}
	return result;
}

/**
 * Date.parse with progressive enhancement for ISO 8601 <https://github.com/csnover/js-iso8601>
 * © 2011 Colin Snover <http://zetafleet.com>
 * Released under MIT license.
 */
//(function (Date, undefined) {
    var origParse = Date.parse, numericKeys = [ 1, 4, 5, 6, 7, 10, 11 ];
    Date.parse = function (date) {
        var timestamp, struct, minutesOffset = 0;

        // ES5 §15.9.4.2 states that the string should attempt to be parsed as a Date Time String Format string
        // before falling back to any implementation-specific date parsing, so that’s what we do, even if native
        // implementations could be faster
        //              1 YYYY                2 MM       3 DD           4 HH    5 mm       6 ss        7 msec        8 Z 9 ±    10 tzHH    11 tzmm
        if ((struct = /^(\d{4}|[+\-]\d{6})(?:-(\d{2})(?:-(\d{2}))?)?(?:T(\d{2}):(\d{2})(?::(\d{2})(?:\.(\d{3}))?)?(?:(Z)|([+\-])(\d{2})(?::(\d{2}))?)?)?$/.exec(date))) {
            // avoid NaN timestamps caused by “undefined” values being passed to Date.UTC
            for (var i = 0, k; (k = numericKeys[i]); ++i) {
                struct[k] = +struct[k] || 0;
            }

            // allow undefined days and months
            struct[2] = (+struct[2] || 1) - 1;
            struct[3] = +struct[3] || 1;

            if (struct[8] !== 'Z' && struct[9] !== undefined) {
                minutesOffset = struct[10] * 60 + struct[11];

                if (struct[9] === '+') {
                    minutesOffset = 0 - minutesOffset;
                }
            }

            timestamp = Date.UTC(struct[1], struct[2], struct[3], struct[4], struct[5] + minutesOffset, struct[6], struct[7]);
        }
        else {
            timestamp = origParse ? origParse(date) : NaN;
        }

        return timestamp;
    };
//}(Date));

 //Date.prototype.toISO8601String  = function() {
var dateToISO8601String = function (date) {
	if(date) {
	    var tzo = -date.getTimezoneOffset(),
	        dif = tzo >= 0 ? '+' : '-',
	        pad = function(num) {
	            var norm = Math.abs(Math.floor(num));
	            return (norm < 10 ? '0' : '') + norm;
	        };
	    return date.getFullYear() 
	        + '-' + pad(date.getMonth()+1)
	        + '-' + pad(date.getDate())
	        + 'T' + pad(date.getHours())
	        + ':' + pad(date.getMinutes()) 
	        + ':' + pad(date.getSeconds()) 
	        + dif + pad(tzo / 60) 
	        + ':' + pad(tzo % 60);
    }
}

/*Date.prototype.toJSON = function(){
	//return this.toISOString();
	return this.toISO8601String();
}*/


var dateTimeReviver = function (key, value) {
	var a;
	if (typeof value === 'string') {
		a = /^(\d{4}|[+\-]\d{6})(?:-(\d{2})(?:-(\d{2}))?)?(?:T(\d{2}):(\d{2})(?::(\d{2})(?:\.(\d{3}))?)?(?:(Z)|([+\-])(\d{2})(?::(\d{2}))?)?)?$/.exec(value);
		if (a) {
			return new Date(Date.parse(value));
		}
	}
	return value;
}

var dateReplacer = function(key, value){
	if (this[key] instanceof Date){
        	return dateToISO8601String(this[key]);
	}else{
		return value;    
	}
}

function getLangCode() {
	var lang = jQuery('html').attr('xml:lang');
	if (!lang) {
		lang = jQuery('html').attr('lang');
	}
	return lang;
}

function selectFirst(){
//if nothing is selected check first
	var rGroup = jQuery('#applicationRadio');
	if (rGroup.find(':checked').length == 0) {
		var ciccio =jQuery(rGroup.find('input')[0]);
		ciccio.prop('checked',true);
		//ciccio.change();
	}
}		

function persistentTooltip(widget) {
	var jWidget = jQuery(widget);
	jQuery("#ToolTipDiv").stop(true, true);
	jQuery("#ToolTipDiv").fadeIn("fast");
	var tipY = jWidget.parent().position().top + jWidget.height() + 12;
	var tipX = jWidget.parent().position().left;
	jQuery("#ToolTipDiv").css({'top': tipY, 'left': tipX});

}

function persistentTooltipRemove() {
	jQuery("#ToolTipDiv").stop(true, true);
	jQuery("#ToolTipDiv").fadeOut("fast");
}

function submitDocument(form, frame) {
	var callback = function () {
		rrLabel();
		jQuery(frame).unbind('load', callback);
	};

	jQuery(frame).bind('load', callback);
	jQuery(form).submit();
}

/* onload */
jQuery().ready(function() {
	if (typeof somethingChanged !== 'undefined') {
		checkSomethingChanged();
	}
	
	if ( window.location.pathname.indexOf('home.seam') >= 0) {
		//onload of home.seam 
	
		jQuery('body').removeClass('nojs');
		
		cid = jQuery('#conversationId').val();
		Tree.cid = cid;
		
		username = jQuery('#username').val();
		
		setToggleHeight();
	
		jQuery(window).resize(function() {
			setToggleHeight();
			recalculateVerticalPos();
			//manageTables(null);
		});
		
			
		/* collapse tree */
		var list = jQuery('.processlist h2');	  
		
		list.click(function() { 
			if(jQuery(this).parent().hasClass('collapsed')) { 
				jQuery(this).parent().removeClass('collapsed'); 
			} else { 
				jQuery(this).parent().addClass('collapsed'); 
			} 
			jQuery(this).next().slideToggle('slow',function() {setToggleHeight();}); 
		}); 
			
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
			monthNames: ['Januar','Februar','M&#xe4;rz','April','Mai','Juni',
				'Juli','August','September','Oktober','November','Dezember'],
			monthNamesShort: ['Jan','Feb','M&#xe4;r','Apr','Mai','Jun',
				'Jul','Aug','Sep','Okt','Nov','Dez'],
			dayNames: ['Sonntag','Montag','Dienstag','Mittwoch','Donnerstag','Freitag','Samstag'],
			dayNamesShort: ['Son','Mon','Die','Mit','Don','Fre','Sam'],
			dayNamesMin: ['So','Mo','Di','Mi','Do','Fr','Sa'],
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
			monthNames: ['Gennaio','Febbraio','Marzo','Aprile','Maggio','Giugno',
				'Luglio','Agosto','Settembre','Ottobre','Novembre','Dicembre'],
			monthNamesShort: ['Gen','Feb','Mar','Apr','Mag','Giu',
				'Lug','Ago','Set','Ott','Nov','Dic'],
			dayNames: ['Domenica','Luned&#236;','Marted&#236;','Mercoled&#236;','Gioved&#236;','Venerd&#236;','Sabato'],
			dayNamesShort: ['Dom','Lun','Mar','Mer','Gio','Ven','Sab'],
			dayNamesMin: ['Do','Lu','Ma','Me','Gi','Ve','Sa'],
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
			currentText: 'Corrente',
			closeText: 'Conferma',
			timeFormat: 'HH:mm',
			ampm: false };
		jQuery.timepicker.regional['en'] = {
			timeFormat: 'hh:mmTT',
			ampm: true };
		jQuery.timepicker.regional['de'] = {
			timeOnlyTitle: 'Uhrzeit w&#228;hlen',
			timeText: 'Uhrzeit',
			hourText: 'Stunden',
			minuteText: 'Minuten',
			secondText: 'Sekunden',
			currentText: 'Jetzt',
			closeText: 'Best&#228;tigen',
			timeFormat: 'HH:mm',
			ampm: false };
		
		var lang = getLangCode();
		
		jQuery.datepicker.setDefaults(jQuery.datepicker.regional[lang]);
		jQuery.timepicker.setDefaults(jQuery.timepicker.regional[lang]);

		//call a4jFunction to getRuleData:
		// 	getRuleList();
		//	includeDashboard();

		jQuery(document).keydown(function(e) {
			preventNavigation(e);
		});

		stopAjaxReq();
		
	} else  {

		centerDiv();

		jQuery(window).resize(function() {
			centerDiv();
		});

		//		if ( #{userBean.isAuthenticated() and not userBean.isPasswordExpired()}   ) {
		//			alert('#{messages.Login_ActiveSessionAlert}');
		//			location.replace('../../home.seam');
		//		}

		//Set focus
		var usernameWidget = document.getElementById("username");
		var okBtn = document.getElementById("ok");
		var oldPwd = document.getElementById("oldPwd");
		if (usernameWidget) {
			usernameWidget.focus();
		} else if (okBtn) {
			okBtn.focus();
		} else {
			oldPwd.focus();
		}

		var selectedRole = jQuery('input[name=r]:checked').val();

		if(selectedRole != undefined) {
			loadSdlocs(selectedRole);
		}

	}
});

/* toggle sidebar */
function toggleNav() {
	if(jQuery('body').hasClass('togglenav')) {
		jQuery('body').removeClass('togglenav');
	} else {
		jQuery('body').addClass('togglenav');
	}
}

function pressHome(event) {
	if (event.keyCode == 13) {
		//commented to prevent home press
		//goHome();
	}
}

//function to prevent navigation in application pages pressing
//ENTER (KeyCode = 13) or BACKSPACE (KeyCode = 8)
function preventNavigation(e) {
	var doPrevent;
	if (e.keyCode == 116) { //F5 
		doPrevent = true;  
	}
	
	else if (e.keyCode == 8 || e.keyCode == 13) {  //8: BACKSPACE, 13: Enter
		var d = e.srcElement || e.target;
		//if INPUT ot TEXTAREA or DASHBOARD do not prevent
		if (d.tagName.toUpperCase() == 'INPUT') {
			if (e.keyCode == 8) {
				doPrevent = d.readOnly || d.disabled;
			}
			else { 
				var buttonToBePressed = document.getElementsByClassName("pressOnEnter")[0];
				if (buttonToBePressed != undefined) {
					buttonToBePressed.focus();
					buttonToBePressed.click();
				}
				else {
					//no button to be pressed are found, use enter with default behaviour.
					//here enter is totaly blocked.
					doPrevent = true; //d.readOnly || d.disabled;
				}
			}
		} else if(d.tagName.toUpperCase() == 'TEXTAREA'){
			doPrevent = d.readOnly || d.disabled;
		} else if( d.tagName.toUpperCase() == 'EMBED'){
			doPrevent = false;
		} 
		else {
			if (e.keyCode == 8) {
				doPrevent = true;
			}
			else if (e.keyCode == 13) {
				//disable Enter if no widget are selected!
				doPrevent = true;
			}
		}
	} else if (e.keyCode == 115) { //F4
		var d = e.srcElement || e.target;
		
		var buttonsWithSameClass = document.getElementsByClassName("hotKeyF4");
		
		var buttonWithinPopup;
		var buttonOutsidePopup;
		
		for(b=0; b < buttonsWithSameClass.length; b++){
			var lookup = traverseAncestorsLookingforClass(buttonsWithSameClass[b],"ui-dialog");
			
			if(lookup!=undefined && lookup!=null){
				buttonWithinPopup=buttonsWithSameClass[b];
			}else{
				buttonOutsidePopup=buttonsWithSameClass[b];
			}
		}
		
		var buttonToBePressed;
		
		if(buttonWithinPopup==undefined || buttonWithinPopup==null){
			buttonToBePressed=buttonOutsidePopup;
		}else{
			buttonToBePressed=buttonWithinPopup;
		}
		
		if (buttonToBePressed != undefined) {
			buttonToBePressed.focus();
			buttonToBePressed.click();
		} else {
			//no button to be pressed are found, use enter with default behaviour.
			//here enter is totaly blocked.
			doPrevent = true; //d.readOnly || d.disabled;
		}
	
			
		
	} else {
		doPrevent = false;
	}
	
	if (doPrevent) {
		e.preventDefault();
	}
	
}

// esempio traverseAncestorsLookingforClass(xyz,"ui-dialog") per vedere se è dentro un popup
function traverseAncestorsLookingforClass(el, cls) {
    while ((el = el.parentElement) && !el.classList.contains(cls));
    return el;
}

function centerDiv() {
	//Center loginDiv
	var loginDiv = jQuery("#loginDiv");
	if (document.documentElement.clientWidth > 850) {
		var outerDiv = jQuery("#outerDiv");
		var leftMargin = -(outerDiv.width() / 2);
		var rightMargin = -(outerDiv.height() / 2);
		loginDiv.css("marginLeft", leftMargin + "px");
		loginDiv.css("marginTop", rightMargin + "px");
	} else {
		loginDiv.css({'marginLeft' : '', 'marginTop' : ''});
	}
}



function recalculateVerticalPos() {
	var formTitle = jQuery('#formName');
	var hFormTitle = 0;
	if (formTitle.length == 1) {
		hFormTitle = formTitle.height();
		hFormTitle = hFormTitle + parseInt(formTitle.css('margin-bottom'),10);
		hFormTitle = hFormTitle + parseInt(formTitle.css('margin-top'),10);
	}

	var middleAlignContainers = jQuery('.middleAlignedContent');
	for (z=0; z<middleAlignContainers.length; z++) {
		var container = jQuery(middleAlignContainers[z])
				var parent = container.parent();
		var hParent = parent.height() - hFormTitle;
		var h = container.height() - 1;
		var pos = (hParent - h) / 2;
		container.css('margin-top', + (pos < 0 ? 0 : pos) + "px");
	}

	var bottomAlignContainers = jQuery('.bottomAlignedContent');
	for (z=0; z<bottomAlignContainers.length; z++) {
		var container = jQuery(bottomAlignContainers[z])
				var parent = container.parent();
		var hParent = parent.height() - hFormTitle;
		var h = container.height() - 1;
		var pos = (hParent - h);
		container.css('margin-top', pos < 0 ? 0 : pos);
	}
}


//SLIDER
function manageSlider(targetSlider, orientation, min, max, step, value, range, disable, targetInput, targetInputHigh) {
	var sliders = findTargetWidgets(targetSlider);
	var inputs = findTargetWidgets(targetInput);
	var inputsHigh = findTargetWidgets(targetInputHigh);
	if (sliders != null && sliders != undefined) {
		var sLength = sliders.length;
		for (z=0; z<sLength; z++) {
			var slider = jQuery(sliders[z]);
			var inputLow = inputs.length == sLength ? jQuery(inputs[z]) : null;
			var inputHigh = inputsHigh.length == sLength ? jQuery(inputsHigh[z]) : null;
			initSlider(slider, orientation, min, max, step, value, range, disable, inputLow, inputHigh);
		}
	}
}

function initSlider(slider, orientation, minimum, maximum, step, value, rangeType, disable, inputLow, inputHigh) {
	if (slider != null && slider != undefined && slider.length==1) {
		slider.slider({
			orientation: orientation,
			range: rangeType,
			min: minimum,
			max: maximum,
			step: step,
			disabled: disable,
			slide: function( event, ui ) {
			if (inputHigh == null) {
				if (inputLow != null) {
					jQuery( inputLow ).val( ui.value );
				}
			} else {
				if (rangeType == true) {
					if (inputLow != null) {
						jQuery( inputLow ).val( ui.values[0] );
					}
					if (inputHigh != null) {
						jQuery( inputHigh ).val( ui.values[1] );
					}
				} else {
					var input = (rangeType == "max" ? inputLow : inputHigh);
					if (input != null) {
						jQuery( input ).val( ui.value );
					}
				}
			}
		}
		});
		setSliderDefault(slider, inputLow, inputHigh, rangeType, minimum, maximum, value);
	}
	setInputsDefault(slider, inputLow, inputHigh, rangeType, minimum, maximum);
}

function setSliderValue(input) {
	var inputLow = jQuery(input);
	//if (!/^[-+]$/.test(inputLow.val())) {
	var isInputLow = endsWith(inputLow.attr('name'), '_L');
	var inputHigh = null;
	var slider = inputLow.prev();
	if (slider.hasClass('ui-slider')) {
		inputHigh = inputLow.next();
	} else {
		inputHigh = inputLow;
		inputLow = slider;
		slider = slider.prev();
	}
	var rangeType = slider.slider('option', 'range');
	var minimum = slider.slider('option', 'min');
	var maximum = slider.slider('option', 'max');
	if (rangeType == true) {
		var valueLow = parseFloat(inputLow.val());
		var valueHigh = parseFloat(inputHigh.val());
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
			slider.slider('option','values',0,valueLow);
		}
		if (!isNaN(valueHigh)) {
			inputHigh.val(valueHigh);
			slider.slider('option','values',1,valueHigh);
		}
	} else {
		var currentInput = jQuery(input);
		var currentValue = parseFloat(currentInput.val());
		if (currentValue < minimum) {
			currentValue = minimum;
		}
		if (currentValue > maximum) {
			currentValue = maximum;
		}
		if (!isNaN(currentValue)) {
			currentInput.val(currentValue);
			slider.slider('option','value',currentValue);
		}
	}
	//}
}

function setSliderDefault(slider, inputLow, inputHigh, rangeType, minimum, maximum, value) {
	if (rangeType == true) {
		var currentValueLow = inputLow.val();
		if (currentValueLow == null || currentValueLow == undefined || currentValueLow == '') {
			currentValueLow = value > minimum && value < maximum ? value : minimum;
		}
		var currentValueHigh = inputHigh.val();
		if (currentValueHigh == null || currentValueHigh == undefined || currentValueHigh == '') {
			currentValueHigh = maximum;;
		}
		slider.slider( "option", "values", [ currentValueLow , currentValueHigh ] );
	} else {
		var input = (rangeType == "min" ? inputHigh : inputLow);
		var currentValue = input.val();
		if (currentValue == null || currentValue == undefined || currentValue == '') {
			currentValue = value;
		}
		slider.slider( "option", "value", currentValue );
	}
}

function setInputsDefault(slider, inputLow, inputHigh, rangeType, minimum, maximum) {
	if (rangeType == true) {
		setInputDefault(inputLow, slider.slider( "values" , 0 ));
		setInputDefault(inputHigh, slider.slider( "values" , 1 ));
	} else {
		var input = (rangeType == "max" ? inputLow : inputHigh);
		setInputDefault(input, slider.slider( "value" ));
		var inputOther = (rangeType == "max" ? inputHigh : inputLow);
		setInputDefault(inputOther, rangeType == "max" ? maximum : minimum);
	}
}

function setInputDefault(input, value) {
	if (input != null) {
		var currentValue = input.val();
		if (currentValue == null || currentValue == undefined || currentValue == '') {
			input.val( value );
		}
	}
}

// FINDS ALL WIDGETS WITH ID THAT ENDS WIT 'TARGETNAME'
function findTargetWidgets(targetName) {
	/*var results = [];
	var targetWidgets = jQuery('[id$="'+targetName+'"]');
	if (targetWidgets != null && targetWidgets != undefined) {
		for (z=0; z<targetWidgets.length; z++) {
			var targetWidget = jQuery(targetWidgets[z]);
			var id = targetWidget.attr('id');
			if (id.indexOf(':') > -1) {
				id = id.substring(id.lastIndexOf(':')+1);
			}
			if (id == targetName) {
				results.push(targetWidget);
			}
		}
	}*/
	return jQuery('[id$="'+targetName+'"]');
}

function clearField(target, isSuggestion) {
	var targets = findTargetWidgets(target);
	if (targets != null && targets != undefined) {
		for (z=0; z<targets.length; z++) {
			var jThis = jQuery(targets[z]);
			jThis.val('');
			if (isSuggestion) {
				var jNext = jThis.next();
				var toClean = endsWith(jNext.attr('id'), 'CODE') ? 1 : 3;
				for(y=toClean; y>0; y--) {
					jNext.val('');
					if (endsWith(jNext.attr('id'), 'CODE')) {
						jNext.change();
					}
					jNext = jNext.next();
				}
			}
		}
	}
}

// MANAGES TABLE ROWS COLOR, HEIGHT AND FIXED HEADER
function manageTables(targetName) {
	var tables = (targetName == null || targetName == undefined || targetName == '' ? jQuery(".dt:not('.layoutTable')") : findTargetWidgets(targetName));
	if (tables) {
		for (z=0; z<tables.length; z++) {
			var table = jQuery(tables[z]);
			
			var firstRow = table.find('tbody tr:visible:first');

			if (firstRow.length > 0 && !firstRow.hasClass('noresults')) {
			// CALCULATE ROWS COLOR
			//recalculateTableRowsColors(table);

			// CALCULATE DATA AREA HEIGHT
//			if (table.closest('.layoutTable').length == 0) {
				recalculateTableBodyHeight(table);
//			}

			// CALCULATE COLUMNS WIDTH
			recalculateTableColumnsWidth(table);

			// RE-CALCULATE DATA AREA HEIGHT
			//recalculateTableBodyHeight(table);

			// MAKE SOME COLUMNS CLIENT SORTABLE
			makeColumnsSortTable(table);

			// PREVENT PROPAGATION FOR BUTTONS
			//preventTableButtonPropagation(table);
			}
		}
	}
}
//ASSIGNES A SPECIFIC STYLE TO A TABLE ROW DEPENDING ON THE CONTENT OF A COLUMN AT THE 
//'columnIndex' INDEX.
function changeDataGridRowsStyle(table,columnIndex,text,styleName) {

	var tableBody = table.find('.tableBody');
	
	if (tableBody) {
		var rows = tableBody.find('tr');
		
		if (rows.length > 0) {
		
			var row = null;
			var cell = null;
			for (y=0; y<rows.length; y++) {
			
				row = jQuery(rows[y]);
				var cells = row.find('td');
				cell = jQuery(cells[columnIndex]);
				if(cell.text() == text) {
					row.addClass(styleName);
				} 
				else {
					row.removeClass(styleName);
				}
			}
		}
	}

}
//ASSIGNES A SPECIFIC STYLE TO A GROUPCHECKBOX ROW DEPENDING ON THE POSITION
function changeGroupCheckBoxRowsStyle(groupCheckId,positions,styleName) {

	var rowsBody = groupCheckId.find('tr');

	if (rowsBody) {
		for (var i=0; i<positions.length; i++) {
			position = positions[i];
			jQuery(rowsBody[position]).addClass(styleName);
		}		
	}
}

function recalculateTableRowsColors(table) {
	 table.find('.tableBody > tr:odd').addClass('odd').removeClass('even');
	 table.find('.tableBody > tr:even').addClass('even').removeClass('odd');
}

function recalculateTableBodyHeight(table) {
	var hasNext = table.next().length > 0;
	var tableHeight = table.height();
	var tableBody = table.find('.tableBody');
	if (tableBody) {
		var bodyPosition = tableBody.position().top;
		var baseHeight = tableHeight - bodyPosition;
		var bodyMaxHeight;
		if (table && table.hasClass('autoResizeFull')) {
			var conheight = jQuery(window).height() - (jQuery('#header').height() + jQuery('#footer').height()) - 35;
			bodyMaxHeight = conheight - 45 - table.position().top;
		} else if (table && table.hasClass('autoResizeContained')) {
			var conheight = table.parent().height();
			bodyMaxHeight = conheight - 25 - table.position().top;
		} else {
			bodyMaxHeight = tableHeight;
		}
		bodyMaxHeight = bodyMaxHeight - bodyPosition - parseInt(tableBody.css('border-bottom-width'),10);
		if (hasNext || bodyMaxHeight < 50) {
			bodyMaxHeight = baseHeight;
		}
		tableBody.css('max-height', bodyMaxHeight);
	}
}

function recalculateTableColumnsWidth(table) {
	var cols = table.find('tbody tr:visible:first > td');
	var headers = table.find('thead tr th');
	for (y=0; y<cols.length; y++) {
		var desiredWidth = jQuery(cols[y]).width();
		jQuery(cols[y]).css({ width: desiredWidth + 'px' });
	};
	table.find('tbody').css({ float:'left' });//display:'block' });
	table.find('thead').css({ float:'left' });//display:'block' });
	for (y=0; y<cols.length; y++) {
		var desiredWidth = jQuery(cols[y]).width();
		jQuery(headers[y]).css({ width: desiredWidth + 'px' });
	};
}

function makeColumnsSortTable(table) {
	jQuery('.sortableColumn')
	.each(function(){

		var th = jQuery(this),
				thIndex = th.index(),
				inverse = false;

		th.click(function(){
			table.find('td').filter(function(){
				return jQuery(this).index() === thIndex;
			}).sortElements(function(a, b){
				var aValue;
				var bValue;
				var aInput = jQuery(a).find('input');
				var bInput = jQuery(b).find('input');
				if (aInput.length > 0 && bInput.length > 0) {
					aValue = jQuery(aInput[0]).is(':checked');
					bValue = jQuery(bInput[0]).is(':checked');
				} else {
					var aValue = jQuery.text([a]);
					var bValue = jQuery.text([b])
							if (isNumeric(aValue) && isNumeric(bValue)) {
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
				if( aValue == bValue )
					return 0;
				return aValue > bValue ?
						inverse ? -1 : 1
								: inverse ? 1 : -1;
			}, function(){
				return this.parentNode; 
			});
			inverse = !inverse;
			recalculateTableRowsColors(table);
		});
	});
}

function stopPropagation(event) {
	if (event.stopPropagation) {
		event.stopPropagation();   // W3C model
	} else {
		event.cancelBubble = true; // IE model
	}
}

function uncheckRadio(widget) {
	if (widget.checked) {
        jQuery(widget).mouseup(function() {  
            // apparently if you do it immediatelly, it will be overriden, hence wait a tiny bit
            setTimeout(function() { 
                widget.checked = false; 
            }, 5); 
            // don't handle mouseup anymore unless bound again
            jQuery(widget).unbind('mouseup');
        });
	}
}

function isNumeric(s) {
	return /^[-+]?\d*$/.test(jQuery.trim(s.replace(/[,.']/g,'')));
};

function validateForm(object) {
	return isValidForm;
}

// LIMIT TEXT INPUTS LENGTH
function limitText(element, limit) {
	if (element.value.length > limit) {
		element.value = element.value.substring(0, limit);
	}
}

//FOCUSES ON FORM ELEMENT WITH LESSER TABINDEX
//OR FIRST ELEMENT IF TI ISN'T SET
function focusFirstElement(){
	resetPagePosition();
	var form = jQuery(document.forms['f']);
	var elements = form.find('input,select,a');
	var y = 0;
	var found = false;
	var tabIndex = Infinity;
		for (z=0;z<elements.length;z++) {
			var element = elements[z];
			var ti = element.tabIndex;
			var type = element.type;
			
				if (!found && 'hidden' != type) {
					//Found first not hidden.
					found=true;
					y=z;
					tabIndex = ti;
				}
				else if (found) {
					//search if any with lowest tabindex, greather than 0.
					if (ti > 0 && ti < tabIndex && 'hidden' != type) {
						 y = z;
						 tabIndex = ti;
					}
				}
		
		}
		
	var scrollerDiv = jQuery("#i");
	var pageName = scrollerDiv.attr('title');
	if (pageName && pageName != currentPage && elements.length > 0) {
		currentPage = pageName;
		scrollerDiv.scrollTop(0);
		elements[y].focus();
	}
}

function resetPagePosition() {
	var scrollerDiv = jQuery("#i");
	var pageName = scrollerDiv.attr('title');
	if (pageName && pageName != currentPage) {
		currentPage = pageName;
		scrollerDiv.scrollTop(0);
	}
}

function initScroller() {
	var scrollButt = jQuery(".scroll-to-top");
	scrollButt.fadeOut(350);
	var scrollerDiv = jQuery("#i");
	if (scrollerDiv && scrollerDiv.length > 0) {
		if (scrollerDiv.scrollTop() != "0") {
			scrollButt.fadeIn(1200);
		}
		scrollerDiv.scroll(function(){
			if (jQuery(this).scrollTop() == "0") {
				scrollButt.fadeOut(350);
			} else if (scrollButt.is(':hidden')) {
				scrollButt.fadeIn(1200);
			}
		});
		if (!scrollButt.attr('init')) {
			scrollButt.click(function(){
				currentPosition = jQuery(this).scrollTop();
				scrollToPosition(0);
			});
			scrollButt.attr('init', true);
		}
	} else {
		scrollButt.fadeOut(350);
	}
}

function scrollToPosition(pos) {
	jQuery("#i").animate({
		scrollTop: pos
	}, 600);
}

// SET DEFAULT VALUE
function setDefaultValue(targetName, defaultValue) {
	// FIND ALL WIDGETS WITH ID THAT ENDS WIT 'TARGETNAME'
	var targetWidgets = findTargetWidgets(targetName);
	if (targetWidgets != null && targetWidgets != undefined) {
		for (z=0; z<targetWidgets.length; z++) {
			var targetWidget = jQuery(targetWidgets[z]);
			if (defaultValue == true) { // SINGLE CHECKBOXES
				targetWidget.prop('checked', defaultValue);
			} else { // RADIO GROUPS AND CHECK BOX GROUPS
				var currentValue = targetWidget.val();
				if (targetWidget.is('table')) {
					var defaultValues = defaultValue.split(';');
					var inputs = targetWidget.find('input');
					for (y=0; y<inputs.length; y++){
						var input = jQuery(inputs[y]);
						if (input.prop('checked')) {
							return;
						}
					}
					for (y=0; y<inputs.length; y++){
						for (x=0; x<defaultValues.length; x++) {
							var input = jQuery(inputs[y]);
							if (input.val() == defaultValues[x]) {
								input.prop('checked',true);
								break;
							}
						}
					}
				} else { // OTHER INPUTS/SELECTS
					if (currentValue == null || currentValue == undefined || currentValue == '') {
						if (defaultValue.indexOf(";")>-1) { // MULTIPLE SELECTS
							var defaultValues = defaultValue.split(';');
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

function getIdPrefix(element) {
	var widgetId = jQuery(element).attr('id');
	var widgetPrefix = widgetId.substring(0, widgetId.lastIndexOf(':')+1).replace(/:/gi,'\\:');
	return widgetPrefix;
}


function scrollTogether(master, slave) {
	var jMaster = jQuery(master);
	var jSlave = jQuery(slave);
	jSlave.scrollLeft(jMaster.scrollLeft());
}

/* COMPARES TWO DATETIME WIDGETS AND SETS LOWER AND HIGHER DATE/TIME LIMITS*/
function dateCompare(lowerElement, higherElement) {
	var lang = getLangCode();
	var datePattern = jQuery.datepicker.regional[lang].dateFormat;
	var timePattern = jQuery.timepicker.regional[lang].timeFormat;

	var lowerWidget = jQuery(lowerElement);
	if (lowerWidget.is('div')) {
		lowerWidget = lowerWidget.children('input');
	}
	var higherWidget = jQuery(higherElement);
	if (higherWidget.is('div')) {
		higherWidget = higherWidget.children('input');
	}

	var lower = lowerWidget.val();
	var higher = higherWidget.val();

	if (lower && lower != '' && higher && higher != '') {
		var lowerParts = lower.split(' ');
		var higherParts = higher.split(' ');

		var lowerDate;
		var higherDate;
		
		var lowerDatePicker = lowerWidget.data('datepicker');
		if (lowerDatePicker) {
			var lowerTimePicker = jQuery.datepicker._get(lowerDatePicker, 'timepicker');
			lowerDate = fixDate(lowerWidget, lowerParts, new Date(lowerDatePicker.selectedYear, lowerDatePicker.selectedMonth, lowerDatePicker.selectedDay, lowerTimePicker == undefined ? 0 : lowerTimePicker.hour, lowerTimePicker == undefined ? 0 : lowerTimePicker.minute, 0, 0));
		} else {
			lowerDate = fixDate(lowerWidget, lowerParts, jQuery.datepicker.parseDate(datePattern,  lower), datePattern, timePattern);
		}
		var higherDatePicker = higherWidget.data('datepicker');
		if (higherDatePicker) {
			var higherTimePicker = jQuery.datepicker._get(higherDatePicker, 'timepicker');
			higherDate = fixDate(higherWidget, higherParts, new Date(higherDatePicker.selectedYear, higherDatePicker.selectedMonth, higherDatePicker.selectedDay, higherTimePicker == undefined ? 0 : higherTimePicker.hour, higherTimePicker == undefined ? 0 : higherTimePicker.minute, 0, 0));
		} else {
			higherDate = fixDate(higherWidget, higherParts, jQuery.datepicker.parseDate(datePattern,  higher), datePattern, timePattern);
		}
		var initTime = lowerTimePicker != undefined && higherTimePicker != undefined && (lowerParts[0] == '' || higherParts[0] == '' || lowerDate.getDate() != higherDate.getDate() || lowerDate.getMonth() != higherDate.getMonth() || lowerDate.getYear() != higherDate.getYear());
		var isLowerComparing = jQuery.type(lowerElement) === "object";

		/*if (isLowerComparing) {
			if (higherParts[0] == '') {
				lowerDatePicker.settings.maxDate = null;
				lowerDatePicker.settings.maxDateTime = null;
			} else {
				lowerDatePicker.settings.maxDate = higherDate;
				lowerDatePicker.settings.maxDateTime = higherDate;
			}
			if (initTime) {
				lowerDatePicker.settings.hourMax = 23;
				lowerDatePicker.settings.minuteMax = 59;
			}
		} else {
			if (lowerParts[0] == '') {
				higherDatePicker.settings.minDate = null;
				higherDatePicker.settings.minDateTime = null;
			} else {
				higherDatePicker.settings.minDate = lowerDate;
				higherDatePicker.settings.minDateTime = lowerDate;
			}
			if (initTime) {
				higherDatePicker.settings.hourMin = 0;
				higherDatePicker.settings.minuteMin = 0;
			}
		}*/

		
		if(lowerDate.getTime() > higherDate.getTime()) {
			if (isLowerComparing) {
				lowerWidget.val(higher);
			} else {
				higherWidget.val(lower);
			}
			return false;
		}
		/*
		if( lowerParts[0] != '' && higherParts[0] != '') {
			if (lowerParts[0].indexOf(':') > -1 && higherParts[0].indexOf(':') > -1) {
				return timeCompare(lowerWidget, higherWidget, lowerParts[0], higherParts[0], isLowerComparing);
			} else if (lowerParts[0].indexOf(':') > -1 && higherParts[0].indexOf(':') == -1 || lowerParts[0].indexOf(':') == -1 && higherParts[0].indexOf(':') > -1) {
				return false;
			} else {

				if (lowerDate.getDate() == higherDate.getDate() && lowerDate.getMonth() == higherDate.getMonth() && lowerDate.getYear() == higherDate.getYear()) {
					return timeCompare(lowerWidget, higherWidget, lowerParts[1], higherParts[1], isLowerComparing);
				} else if(lowerDate.getTime() > higherDate.getTime()) {
					if (isLowerComparing) {
						lowerWidget.val(higher);
					} else {
						higherWidget.val(lower);
					}
					return false;
				}
			}
		}*/
	}
	return true;
}

/* COMPARES TWO TIME WIDGETS AND SETS LOWER AND HIGHER TIME LIMITS*/
function timeCompare(lowerWidget, higherWidget, lower, higher, isLowerComparing) {

	var lowerDatePicker = lowerWidget.data('datepicker');
	var lowerTimePicker = jQuery.datepicker._get(lowerDatePicker, 'timepicker');
	var higherDatePicker = higherWidget.data('datepicker');
	var higherTimePicker = jQuery.datepicker._get(higherDatePicker, 'timepicker');

	/*if (isLowerComparing) {
		if (higher != null) {
		   lowerDatePicker.settings.hourMax = higherTimePicker.hour;
			if (lowerTimePicker.hour == higherTimePicker.hour) {
				lowerDatePicker.settings.minuteMax = higherTimePicker.minute;
			} else {
				lowerDatePicker.settings.minuteMax = 59;
			}
		}
	} else {
		if (lower != null) {
			higherDatePicker.settings.hourMin = lowerTimePicker.hour;
			if (lowerTimePicker.hour == higherTimePicker.hour) {
				higherDatePicker.settings.minuteMin = lowerTimePicker.minute;
			} else {
				higherDatePicker.settings.minuteMin = 0;
			}
		}
	}*/

	if(lowerTimePicker != null && higherDatePicker != null && (lowerTimePicker.hour > higherTimePicker.hour || (lowerTimePicker.hour == higherTimePicker.hour && lowerTimePicker.minute > higherTimePicker.minute))) {
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
function fixDate(widget, dateParts, currentDate, datePattern, timePattern) {

	var stringDateOrTime = dateParts[0];
	if (!datePattern) {
		datePattern = widget.datepicker('option', 'dateFormat');
	}
	var newDate;
	try {
		if (dateParts.length == 2) {
			newDate = jQuery.datepicker.parseDate(datePattern, stringDateOrTime);
			newDate = fixTime(widget, dateParts[1], newDate, timePattern);
		} else if (dateParts[0].indexOf(':') > -1) {
			newDate = fixTime(widget, stringDateOrTime, new Date(), timePattern);
		} else {
			newDate = jQuery.datepicker.parseDate(datePattern, stringDateOrTime);
		}
	} catch(err) {
		widget.val('');
	}

	if (newDate && newDate.getTime() != currentDate.getTime()) {
		return newDate;
	} else {
		return currentDate;
	}
}

/* FIXES DATEPICKER/TIMEPICKER TIME IF IT DOESN'T REFLECT DISPLAYED TIME */
function fixTime(widget, stringTime, currentDate, timePattern) {
	if (!timePattern) {
		timePattern = widget.datepicker('option', 'timeFormat');
	}
	var newDate = currentDate;
	var newTime = jQuery.datepicker.parseTime(timePattern, stringTime);
	newDate.setHours(newTime['hour']);
	newDate.setMinutes(newTime['minute']);
	newDate.setSeconds(0);
	newDate.setMilliseconds(0);
	return newDate;
}

/* set toggle height */
function setToggleHeight() {
	var conheight = jQuery(window).height() - (jQuery('#header').height() + jQuery('#footer').outerHeight());
	var navheight = jQuery('#main-nav').height();
	// var zoom = jQuery('html').css('zoom');
	// if(!zoom || zoom==0){
	// 	zoom = 1;
	// }
	// conheight = conheight/zoom;
	/* set main navigation height */
	jQuery('#sidebar-left').css('height',conheight);
	//if(jQuery('.flashcontent').length > 0) jQuery('.flashcontent').css('height',conheight);
	/*zz*/
	jQuery('#content').css('height',conheight);
	/* move #body below #header */
	jQuery('#body').css('paddingTop',jQuery('#header').height());
	//jQuery('#i').css('height',conheight-15);
}

/* onload */


/* TABBED PANELS HANDLING */
jQuery.fn.tabs = function(content) {
	return this.each(function() {
		var wrap = jQuery(this);
		var tabName = wrap.attr('id');
		
		var hiddenTabs = wrap.find('.tabs li');
		var currentActive = 0;
		while (!jQuery(hiddenTabs[currentActive]).is(':visible')) {
			currentActive++;
		}
		var currentActiveTab;
		// check if last opened tab is visible
		var lastActive = lastOpenTab[tabName];
		if (lastActive != undefined) {
			var lastActiveTab = jQuery(hiddenTabs[lastActive]);
				if (!lastActiveTab.hasClass('hideTab')) {
					currentActive = lastActive;
					currentActiveTab = lastActiveTab;
				}
		}
		if (currentActiveTab == undefined) {
			for (z=0; z<hiddenTabs.length; z++) {
				var currentTab = jQuery(hiddenTabs[z]);
				if (!currentTab.hasClass('hideTab')) {
					lastOpenTab[tabName] = z;
					currentActive = z;
					currentActiveTab = currentTab;
					break;
				}
			}
		}


		currentActiveTab.addClass('active');
		wrap.find(content + ':lt('+currentActive+')').addClass('hidden');
		wrap.find(content + ':eq('+currentActive+')').removeClass('hidden');
		wrap.find(content + ':gt('+currentActive+')').addClass('hidden');
		var c = wrap.find(content);
		if (c.length < 1) {
			return;
		}
		var t = jQuery(this);
		t.find('.tabs li a').click(function(e) {
			e.preventDefault();
		});
		t.find('.tabs li').click(function() {
			var li = jQuery(this);
			if (li.hasClass('active')) {
				return;
			}
			lastOpenTab[wrap.attr('id')] = li.index();
			var cPosition = jQuery(window).scrollTop();
			c.hide();
			var href_class = '.' + li.find('a').attr('href').split('#')[1];
			wrap.find(href_class).removeClass('hidden').fadeIn();
			var activeClass = t.find('li.active a').attr('href').split('#')[1];
			t.find('li').removeClass('active');
			li.addClass('active');
			li.parents('.more').addClass('active');

			// adding anchors to location.href
			href_class = href_class.substring(1);
			var href = window.location.href;
			var anchors = [];
			if (href.indexOf("#") > -1) {
				anchors = href.substring(href.indexOf("#") + 1).split(',');
				href = href.substring(0, href.indexOf("#"));
			}
			href += '#';
			for (var i = 0, j = anchors.length; i < j; i++) {
				var anchor = anchors[i];
				if (anchor != activeClass && anchor != href_class) {
					if (href.indexOf("#") < href.length - 1) {
						href += ',';
					}
					href += anchor;
				}
			}
			if (href.indexOf("#") < href.length - 1) {
				href += ',';
			}
			window.location.href = href + href_class;
			//manageTables(null);
			jQuery(window).scrollTop(cPosition);
		});
	});
};

// MANAGES DISABLED RADIO AND CHECKBOX GROUPES
function manageDisabled() {
	var label = jQuery('input:radio:disabled,input:checkbox:disabled').next();
	if (label) {
		label.css('color','#ABADB3');
	}
}

function manageRequired() {
	var requiredWidgets = jQuery('.required');
	requiredWidgets.each( function(index){
		var requiredWidget = jQuery(this);
		if(!requiredWidget.attr('init')){
			requiredWidget.attr('init',true);
			if (requiredWidget.prev().text() != '(*)') {
				if (requiredWidget.parents('.tableBody').length > 0) {
					var parent = requiredWidget.parent();
					var parentWidth = parent.width();
					requiredWidget.width(requiredWidget.width()-20);
					parent.width(parentWidth);
					requiredWidget.css('float','left');
					if (requiredWidget.hasClass('datepicker') || requiredWidget.hasClass('datetimepicker')) {
						requiredWidget = requiredWidget.next();
					}
					requiredWidget.after('<span style="float:right;">(*)</span>');
					//recalculateTableColumnsWidth(requiredWidget.closest('.dt').parent());
				} else {
					requiredWidget.before('<span style="position:absolute;left: -20px;">(*)</span>');
				}
			}
		}
	});

	var layoutRequiredWidgets = jQuery('.layoutRequired');
	layoutRequiredWidgets.each( function(index){
		var layoutRequiredWidget = jQuery(this);
		if(!layoutRequiredWidget.attr('init')){
			if (layoutRequiredWidget.next().text() != '(*)') {
				layoutRequiredWidget.attr('init',true);
				if (layoutRequiredWidget.hasClass('datepicker') || layoutRequiredWidget.hasClass('datetimepicker')) {
					layoutRequiredWidget = layoutRequiredWidget.next();
				}
				layoutRequiredWidget.after('<strong class="reqStar">(*)</strong>');
			}
		}
	});

	var removeRequiredWidgets = jQuery('.removeRequired');
	removeRequiredWidgets.each( function(index){
		var requiredWidget = jQuery(this);
		if (!requiredWidget.attr('init')){
			var prevTag = requiredWidget.prev();
			if (prevTag.text() == '(*)') {
				prevTag.remove();
				if (requiredWidget.parents('.tableBody').length > 0) {
					requiredWidget.css('width','');
					requiredWidget.css('float','');
				}
			}
			requiredWidget.attr('init',true);
		}
	});

	var removeLayoutRequiredWidgets = jQuery('.removeLayoutRequired');
	removeLayoutRequiredWidgets.each( function(index){
		var layoutRequiredWidget = jQuery(this);
		if (!layoutRequiredWidget.attr('init')){
			if (layoutRequiredWidget.hasClass('datepicker') || layoutRequiredWidget.hasClass('datetimepicker')) {
				layoutRequiredWidget = layoutRequiredWidget.next();
			}
			var nextTag = layoutRequiredWidget.next();
			if (nextTag.text() == '(*)') {
				nextTag.remove();
			}
			layoutRequiredWidget.attr('init',true);
		}
	});
}

/**Table filter*/
function filterTbl(jq, phrase, column, ifHidden){
	var new_hidden = false;
	var visibleRows = 0;
	if( this.last_phrase === phrase ) return false;

	var phrase_length = phrase.length;
	var words = phrase.toLowerCase().split(" ");

	// these function pointers may change
	var matches = function(elem, visibleRows) { elem.show();(visibleRows%2 == 0 ? elem.removeClass('odd').addClass('even') : elem.removeClass('even').addClass('odd')); }
	var noMatch = function(elem) { elem.hide(); new_hidden = true }
	var getText = function(elem) { return elem.text() }

	if( column ) {
		var index = null;
		jq.find("thead > tr:last > th").each( function(i){
			if( jQuery.trim(jQuery(this).text()) == column ){
				index = i; return false;
			}
		});
		if( index == null ) throw("given column: " + column + " not found")

		getText = function(elem){ return jQuery(elem.find(
				("td:eq(" + index + ")")  )).text()
		}
	}

	// if added one letter to last time,
	// just check newest word and only need to hide
	if( (words.size > 1) && (phrase.substr(0, phrase_length - 1) ===
			this.last_phrase) ) {

		if( phrase[-1] === " " )
		{ this.last_phrase = phrase; return false; }

		var words = words[-1]; // just search for the newest word

		// only hide visible rows
		matches = function(elem,visibleRows) {;}
		var elems = jq.find("tbody:first > tr:visible")
	}
	else {
		new_hidden = true;
		var elems = jq.find("tbody:first > tr")
	}

	elems.each(function(){
		var elem = jQuery(this);
		if (has_words( getText(elem), words, false )) {
			matches(elem,visibleRows);
			visibleRows++;
		} else {
			noMatch(elem);
		}
	});

	// RECALCULATE COLUMNS WIDTH
	recalculateTableColumnsWidth(jq.closest('.dt').parent());

	last_phrase = phrase;
	if( ifHidden && new_hidden ) ifHidden();
	return jq;
};

// caching for speedup
var last_phrase = ""

// not jQuery dependent
// "" [""] -> Boolean
// "" [""] Boolean -> Boolean
function has_words( str, words, caseSensitive ){
	var text = caseSensitive ? str : str.toLowerCase();
	for (var i=0; i < words.length; i++) {
		if (text.indexOf(words[i]) === -1) return false;
	}
	return true;
}


function selRow(rowIndex, table) {
	//var jqRowIndex = rowIndex-1;
	//table.find('tbody tr').removeClass('even');
	table.find('tbody tr').removeClass('selRow');
	if(rowIndex != -1) //{
		table.find('tbody tr[id='+rowIndex+']').addClass('selRow');
	//table.find('tbody tr:eq('+jqRowIndex+')').addClass('even');
}

//Used in inject eject buttons inside datatable
function selRadio(rowIndex, radio) { 
	var jRadio = jQuery(radio);
	var jBody = jRadio.closest('tbody')
			jBody.find('tr').removeClass('selRow');
	jBody.find('input').removeClass('radioChecked');
	if(rowIndex == -1) {
		radio.checked = false;
	} else {
		jRadio.closest('tr').addClass('selRow');
		jRadio.addClass('radioChecked');
	}
}

//Menu show hide
function showHideProcessList() {
	if((jQuery('#i').attr('title') == 'home') || (jQuery('#i').attr('title') == undefined)){
		showProcessList();
		jQuery('#header ol li:gt(0)').remove();
		
		//ripristina menu quando si torna in home
		jQuery('body').removeClass('togglenav');
	} else {
		hideProcessList();
		//nascondi completamente menu quando parte un processo
		jQuery('body').addClass('togglenav');
	}
}

function hideProcessList() {
	jQuery('#processesPnl').css('display','none');
}

function showProcessList() {
	jQuery('#processesPnl').css('display','');
}

/* temporary fix: it will be removed in future releases of jQuery*/
(function(){
	// remove layerX and layerY
	var all = jQuery.event.props,
			len = all.length,
			res = [];
	while (len--) {
		var el = all[len];
		if (el != 'layerX' && el != 'layerY') res.push(el);
	}
	jQuery.event.props = res;
}());



//Header fissi TO BE FIXED
var reinitHeight = { previousNewHeight: new Array() }

function setTableLayout() {

	var dataTables = jQuery('.dt');
	for (z=0; z<dataTables.length; z++) {
		var dataTable = jQuery(dataTables[z]);

		var header   = dataTable.find('tr:first th');
		var firstRow = dataTable.find('table tbody tr:first td');

		for (column=0; column < firstRow.length; column++) {
			var hW = jQuery(header[column]).width();
			jQuery(firstRow[column]).css('width', hW);
			jQuery(header[column]).css('width', hW);
		}

		var caption = dataTable.find('.caption');


		dataTable.css('position', 'relative');
		caption.css('position', 'fixed');
	}

}

//hide dashbard at process start
function animateDashboard() {
  jQuery('#dashboardInclude').css( "height", "0%");
}

function buildJSonBaner() {
	var patHeader = jQuery('.patientheader');
	var jSonBaner = null;

	if (patHeader.length > 0) {

		var patientId = jQuery('#patientId').text();
		var patientNameGiv = jQuery('#patientNameGiv').text();
		var patientNameFam = jQuery('#patientNameFam').text();
		var birthTime = jQuery('#hf\\:birthTime').text();
		var patientGenderCode = jQuery('#patientGenderCode').text();
		var patientEncounterId = jQuery('#patientEncounterId').val();
		var assignedSDLId = jQuery('#assignedSDLId').val();
		var temporarySDLId = jQuery('#temporarySDLId').val();
		var therapyId = jQuery('#therapyId').val();
		var appointmentId = jQuery('#appointmentId').val();

		jSonBaner = '{';
		if (patientId != "") {
			jSonBaner = jSonBaner + '"patientId":' + patientId;
		}
		if (patientNameGiv != "") {
			jSonBaner = jSonBaner + ',"patientNameGiv":"' + patientNameGiv + '"';
		}
		if (patientNameFam != "") {
			jSonBaner = jSonBaner + ',"patientNameFam":"' + patientNameFam + '"';
		}
		if (birthTime != "") {
			jSonBaner = jSonBaner + ',"birthTime":"' + birthTime + '"';
		}
		if (patientGenderCode != "") {
			jSonBaner = jSonBaner + ',"patientGenderCode":"' + patientGenderCode + '"';
		}
		if (patientEncounterId != "") {
			jSonBaner = jSonBaner + ',"patientEncounterId":' + patientEncounterId;
		}
		if (assignedSDLId != "") {
			jSonBaner = jSonBaner + ',"assignedSDLId":' + assignedSDLId;
		}
		if (temporarySDLId != "") {
			jSonBaner = jSonBaner + ',"temporarySDLId":' + temporarySDLId;
		}
		if (therapyId != "") {
			jSonBaner = jSonBaner + ',"therapyId":' + therapyId;
		}
		if (appointmentId != "") {
			jSonBaner = jSonBaner + ',"appointmentId":' + appointmentId;
		}
		jSonBaner = jSonBaner + '}';

	} else {
		jSonBaner = '{}';
	}
	return jSonBaner;
}


//javascript to flex dashboard communicator method
function flexCommunicator(moduleTarget, varName, varValue) {

	var mDashboard = jQuery("MDashboard");

	if (mDashboard.size == 1) {
		var jSonBaner = buildJSonBaner();
		mDashboard.context['MDashboard'].jsCommunicator(moduleTarget, cid, jSonBaner, varName, varValue);
	}
}


/***************************************** 
 POPUP FUNCTIONS
******************************************/

/* --- FORM CHANGED ---

Called by link/button with actions that requires check.
Set propertie:

	Jolly Parameter = onmousedown="openSomethingChangedPopup(this.id, 
		'#{static.dialog_title_warning}', 
		'#{static.dialog_message_go_out}', 
		'#{static.dialog_button_ok}', 
		'#{static.dialog_button_cancel}');" */

//saveButton: added for Spisal, non needed to be passed in general. Used default hard-coded button message. 

function openSomethingChangedPopup(widgetId, title, message, okButton, cancelButton, saveButton){
	if (typeof somethingChanged !== 'undefined') {
		if (somethingChanged){
			var toCall = 'jQuery(\'#' + widgetId.replace(':', '\\\\:') + '\')[0].click()';
			openPopup(toCall, title, message, 'msg-warning', okButton, cancelButton, saveButton);
		}
	}
}
/* --- --- END --- --- */

function openPopup(toCall, title, message, popupClass, okButton, cancelButton, saveButton){

	var buttons = {};
	
	var saveClass='.savePopupButton';
	var savePopupTextButton = (typeof saveButton === 'undefined') ? 'Salva e procedi' : saveButton;
		
	var formSaveButton = jQuery(saveClass);
	
	//set savePopupTextButton=null to disable button
	if(savePopupTextButton!=null && formSaveButton.length) {
		buttons[savePopupTextButton] = {
			text:savePopupTextButton,
			click:function() {
				jQuery( this ).dialog( "close" );
				warningDialog.remove();
				
				if (typeof somethingChanged !== 'undefined')
					somethingChanged=false;
				
				var scrollerDiv = jQuery("#i");
				goBackPage = scrollerDiv.attr('title');				
				goBackAjax = new Function(toCall);
				formSaveButton.click();
				return true;
			}
		}
	}
	
	
	if (cancelButton != undefined && cancelButton != null) {
		buttons[cancelButton] = {
				text:cancelButton,
				click: function() {
			jQuery( this ).dialog( "close" );
			warningDialog.remove();
			return false;
		}
		};
	}
	
	if (okButton != undefined && okButton != null) {
		buttons[okButton] = {
					text:okButton,
					click: function() {
				jQuery( this ).dialog( "close" );
				warningDialog.remove();
				
				if (typeof somethingChanged !== 'undefined')
					somethingChanged=false;
				
				var a4jFtoBeCalled = new Function(toCall);
				a4jFtoBeCalled.call();
				//goHome();
				
				return true;
			}
		};
	};
	
	

	var warningDialog = jQuery('<div title="'+title+'" class="'+popupClass+'"><div class="cms">'+message+'</div></div>');
	jQuery('body').after(warningDialog);
	warningDialog.dialog({
		resizable: false,
		width: 'auto', 
		position: {my: "center", at: "center", of: jQuery('html')},
		dialogClass: "pu-"+popupClass+" pu-other-customizations"+((okButton == undefined || okButton == null) && (cancelButton == undefined || cancelButton == null) ? "" : " pu-no-close"),
		modal: true,
		buttons: buttons
	});
}

function openIframePopup(url){
	var iframeDialog = jQuery('#iframeDialog');
	var firstOpen = false;

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
  		position: {my: "center", at: "center", of: jQuery('html')},
    	modal: false,
		close: function(event, ui){iframeDialog.find('#iframe').attr('src', '');} //remove previous pdf
    });


	if (firstOpen) {

 		jQuery('.minimizableDialog').children('.ui-dialog-titlebar').append('<button id="miniMaxButton" title="miniMaxButton" aria-disabled="false" role="button" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-icon-only ui-dialog-titlebar-minimize"><span id="miniMaxIcon" class="ui-button-icon-primary ui-icon ui-icon-minimizethick"></span><span class="ui-button-text"></span></button>');

	    jQuery('#miniMaxButton').click(function() {
			var btn = jQuery('#miniMaxIcon');
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

	var currentDialog = null;

	function openFormPopup(popupId){
		var popupDiv = jQuery(popupId);
		if (popupDiv.children().first().attr('title') != "empty.xhtml") {
			
				var h1Title = popupDiv.find('#formTitle');
				if (h1Title.length) {
					popupDiv.attr('title',h1Title.text());
					h1Title.hide();
				}
				
			if (!popupDiv.parent().hasClass('ui-dialog')) {
				var dWidth = 'auto';
				var dHeight = 'auto';
				
				if(typeof this.popupWidth != 'undefined' && typeof this.popupHeight != 'undefined'){

					var wWidth = jQuery(window).width();
					dWidth = wWidth * this.popupWidth;
					var wHeight = jQuery(window).height();
					dHeight = wHeight * this.popupHeight;
				
				}
			
				currentDialog = popupDiv.dialog({
					width:dWidth,
					height:dHeight,
					position: {my: "center", at: "center", of: jQuery('html')},
					modal: true,
					close: function(event, ui){
						setPopup("empty");
						currentDialog.dialog('destroy');
						currentDialog == null;
						delete this.popupWidth;
						delete this.popupHeight;
					},
					open: function(event, ui){
						var tabset = jQuery('#fPop .tabset');
						if (tabset && tabset.attr('init')==undefined && tabset.length>0){ 
							for(var i=0;i<tabset.length;i++){
								lastOpenTab[jQuery(tabset[i]).attr('id')]=0;
							}
							
							jQuery('#fPop .tabset').tabs('.tab-content');
							jQuery('#fPop .tabset').attr('init',true);   
						}
					}
				});
			}
		} else { //destroy empty popup
			if (currentDialog != null) {
				currentDialog.dialog('destroy');
				currentDialog == null;
				delete this.popupWidth;
				delete this.popupHeight;
			}

		}
	}
	
	function upload(formId, entityName, entityId, fieldName, finishedCallBack, path) {
		
		blockUI();
		
		var entityUrl = entityName.toLowerCase() + 's';
		
		var uploadUrl = "./resource/rest/" + entityUrl + "/" + entityId + "/upload/" + fieldName + "?cid=" + cid;
		if (path !== undefined) {
			uploadUrl = uploadUrl + '&path=' + path;
		}
		var formData = new FormData(jQuery(formId)[0]);
//		if (formData.get('filedata').type == 'application/x-zip-compressed') {
//			console.log(formData.get('filedata').type);
//		}
		return jQuery.ajax({
			url: uploadUrl,
			type: "POST",
			data: formData,
			contentType: false,
			cache: false,
			processData: false,
			success: function (response, textStatus, jqXHR) {
				if (finishedCallBack) {
					finishedCallBack();
				}
				unblockUI();
			},
			error: function (jqXHR, textStatus, errorThrown) {
				console.log("Error uploading" + jqXHR + " " + textStatus + " " + errorThrown);
				unblockUI();
            }
		});
	}
	
/***************************************** 
 HBS tree manager
******************************************/	

function Tree(customTypes, customMenu) {

	var cid;
	var ajaxUrl;
	this.types = {
		HBSROOT: { valid_children: ["REGIONE"], icon : "fa fa-circle treeIconSize" },
		REGIONE: { valid_children: ["ULSS", "ARPAV"], icon : "fa fa-building treeIconSize" },
	    ULSS: {valid_children: ["UOC", "ARPAV"], icon: "fa fa-hospital-o treeIconSize"},
		UOC: { valid_children: ["UOS"], icon : "fa fa-medkit treeIconSize" },
		UOS: { valid_children: ["none"], icon : "fa fa-user-md treeIconSize" },
		ARPAV: { valid_children: ["none"], icon: "fa fa-h-square treeIconSize" }
	};
	
	if (customTypes != null){
		types = customTypes;
	}

	//Load HBS tree tree (form: f_HBS_management)
	this.initHBSTree = function(hbsTree) {
		this.initHBSTree(hbsTree, 'hbsManager');
	}

	this.initHBSTree = function(hbsTree, treeType) {

		var operation = 'HbsGetTree';
		if (treeType == 'employeeManager') {
			operation = 'HbsGetTree4CurrentEmployeeRole';
		} else if (treeType == 'codeValueRole') {
			operation = 'HbsGetTree4CodeValueRole';
		}

		this.buildTree(hbsTree, treeType, this.ajaxUrl, operation, null, null);
	}

	/**
	 * Builds a html tree. Used at login to select sdloc, managing employee to enable sdlocs, managing sdlocs and managing process security
	 * @param hbsTree div that will contain the tree
	 * @param treeType can be: login, employeeManager, hbsManager or codeValueRole
	 * @param ajaxUrl
	 * @param operation
	 * @param passedcid
	 * @param data
	 */

	this.buildTree = function(hbsTree, treeType, ajaxUrl, operation, passedcid, data) {

		var plugins = [ 'types', 'massload' ];
		var treecid = null;
		if (passedcid != null)
			treecid = passedcid;
		else if (this.cid != null)
			treecid = this.cid;

		var ajaxData = null;

		var self = this;

		if (treeType == 'employeeManager' || treeType =='login' || treeType =='codeValueRole' ) {
			plugins.push("checkbox");
			plugins.push("changed");
		}	

		var contextmenu= null;
		if (treeType == 'hbsManager') {
			contextmenu=  { items: this.customMenu }
			plugins.push("contextmenu");
		}

		var relativePath="";
		if (treeType == 'login') {
			relativePath='../../'
		}

		hbsTree.jstree('destroy');

		hbsTree.jstree({
		    'core' : {
				'data' : { //load data onclick, opening node
					type: 'post',
					url: self.ajaxUrl,
					data : function(node) {
						if (treeType == 'login') {
							return { operation: 'HbsGetTree4CurrentUser', cid: treecid, selectedRole: data };
						} else {
							if (node.id === '#') {
								return { operation: operation, cid: treecid, levelOfDepth: 3 };
							} else {
								return { operation: operation, cid: treecid, parentId : node.id, levelOfDepth: 1 };
							}
						}
					}
				},
				check_callback : true
			},
			'types': this.types,
			plugins: plugins,
			contextmenu : contextmenu,
			checkbox : { three_state : false }
		});
		
		//when tree is loaded, change selection according to unselectableSdlTypeCouple
		//unselectableSdlTypeCouple describe couple of unselectable sdl. 
		//if one sdl type is selected, the other sdl type is deselected.
		//unselectableSdlTypeCouple if availble is defined into options.html
		//example: unselectableSdlTypeCouple = ['UCO', 'ARPAV']
		hbsTree.on("loaded.jstree", function(e,data){
			if (treeType == 'login' && configuredUnselectableSdlTypeCouple()){
				selectDeselect("", data.instance);  //defualt: first found as ARPAV or UOC.
			}
		});

		function configuredUnselectableSdlTypeCouple() {
			return (typeof unselectableSdlTypeCouple != 'undefined');
		}
			
		function selectDeselect (alreadySelectedType, treeInstance) {
			var nodes = treeInstance._model.data;
			console.log (hbsTree);
			if (!configuredUnselectableSdlTypeCouple()) {
				return;
			}
			
			for (var nodId in nodes) {
				var nod = nodes[nodId];
				if (nod && nod.original) {
					if (nod.original.type ==  unselectableSdlTypeCouple[0] || nod.original.type ==  unselectableSdlTypeCouple[1] ) {
						var parentId = nod.parent;
						var nodeType = nod.original.type;
						if (alreadySelectedType == "") {
							alreadySelectedType = nodeType;
						}
						
						if (alreadySelectedType == nodeType) {
							treeInstance.select_node(nod.id);
						}
						else {
							treeInstance.deselect_node(nod.id);
						}
					}
				}
			}
		}
		
	
		
		// INJECT
		hbsTree.bind("select_node.jstree", function(event, data) {
			var nodeType = data.node.type;
			if (treeType == 'login' && configuredUnselectableSdlTypeCouple()) {
				if (nodeType == unselectableSdlTypeCouple[0] ||  nodeType == unselectableSdlTypeCouple[1] ){
					selectDeselect(nodeType, data.instance);
				}
			}
			
			var id = data.node.id;
			var injectConfig = 'btnTree;injectConfig'; //used in process MANAGEALL of the HBS (SdL)
			if (id != undefined && treeType == 'hbsManager') {
				injectAndrerenderDetail(id,injectConfig);
			}

		});
		// CREATE
		hbsTree.bind("create_node.jstree", function(event, data) {
			var object_name = data.node.text;
			var parent_id = data.parent;

			jQuery.ajax({
				type: "post", 
				url: self.ajaxUrl,
				data: { operation: "HbsCreate", id: parent_id, name : object_name, node_type : data.node.type },
				success: function(response, textStatus, jqXHR) {
					data.node.id = response.id;
					data.node.type = response.type;				
				},
				error: function(jqXHR, textStatus, errorThrown) {
					alert(jqXHR.responseText);
				}
			});
		});
		// RENAME
		hbsTree.bind("rename_node.jstree", function(event, data) {
			var id = data.node.id;
			var object_name = data.text;

			jQuery.ajax({
				type: "post", 
				url: self.ajaxUrl,
				data: { operation: "HbsRename", id: id, name : object_name },
				success: function(response, textStatus, jqXHR) {
					data.instance.refresh();			
				},
				error: function(jqXHR, textStatus, errorThrown) {
					data.instance.refresh();
					alert(jqXHR.responseText);
				}
			});
		});
		// DELETE
		hbsTree.bind("delete_node.jstree", function(event, data) {
			var id = data.node.id;

			jQuery.ajax({
				type: "post", 
				url: self.ajaxUrl,
				data: { operation: "HbsDelete", id: id },
				error: function(jqXHR, textStatus, errorThrown) {
					alert(jqXHR.responseText);
				}
			});
		});
		
		
		
		//CHANGE
		hbsTree.on("changed.jstree", function (e, data) {
			if (treeType == 'employeeManager' || treeType == 'login' || treeType == 'codeValueRole') {
				
			if (data.action != 'select_node' && data.action != 'deselect_node') {
				return;
			}
			
			if (treeType != 'login' && typeof somethingChanged !== 'undefined') {
				somethingChanged = true;
			}

			if (data.action == 'select_node') {

				var parents = [];
				var selectedAndParentsAndChilds = [data.node.id];
				if (treeType != 'codeValueRole') {
					//find parents
					for (var z=0; z<data.node.parents.length; z++) {
						var parent = data.node.parents[z];
						if (parent != "#" && data.selected.indexOf(parent)==-1) {
							parents.push(parent);
							selectedAndParentsAndChilds.push(parent);
							data.instance.select_node(parent, true);
						}
					}
					//find childs
					selectedAndParentsAndChilds.push.apply(selectedAndParentsAndChilds, data.node.children_d);
					data.instance.select_node(data.node.children_d, true);
				}
				//operation
				var selectOperation;
				if(treeType == 'login') { 
					selectOperation = 'HbsSelect4CurrentUser';
				} else if(treeType == 'employeeManager') {
					selectOperation = 'HbsSelect';
				} else if(treeType == 'codeValueRole') {
					selectOperation = 'HbsSelect4CodeValueRole';
				}
				//select post
				jQuery.ajax({
					type: 'post', 
					url: self.ajaxUrl,
					data: { operation: selectOperation, cid: treecid, id: selectedAndParentsAndChilds, selected:data.node.id, parents: parents, 'class': 'com.phi.entities.role.ServiceDeliveryLocation' },
					error: function(jqXHR, textStatus, errorThrown) {
						alert(jqXHR.responseText);
					}, 
					success: function(response, textStatus, jqXHR) {
						if(treeType == 'login') {
							if(data.selected.length > 0) {
								jQuery("#ok").removeAttr('disabled');
							}
						} else if (treeType == 'employeeManager') {
							reRendeOKButton();
						}
					}
				});

			} else if (data.action == 'deselect_node') {
				var selectedAndChilds = [data.node.id];
				if (treeType != 'codeValueRole') {
					//find childs
					selectedAndChilds.push.apply(selectedAndChilds, data.node.children_d);
					data.instance.deselect_node(data.node.children_d, true);
				}
				//operation
				var unselectOperation;
				if(treeType == 'login') { 
					unselectOperation = 'HbsUnselect4CurrentUser';
				} else if(treeType == 'employeeManager') {
					unselectOperation = 'HbsUnselect';
				} else if(treeType == 'codeValueRole') {
					unselectOperation = 'HbsUnselect4CodeValueRole';
				}
				//unselect post
				jQuery.ajax({
					type: 'post', 
					url: self.ajaxUrl,
					data: { operation: unselectOperation, cid: treecid, id: selectedAndChilds, unselected:data.node.id, 'class': 'com.phi.entities.role.ServiceDeliveryLocation' },
					error: function(jqXHR, textStatus, errorThrown) {
						alert(jqXHR.responseText);
					}, 
					success: function(response, textStatus, jqXHR) {
						if(treeType == 'login') {
							if(data.selected.length == 0) {
								jQuery("#ok").attr('disabled', 'disabled');
							}
						} else if (treeType == 'employeeManager') {
							reRendeOKButton();
						}
					}
				});
			}
		}
		});
	}

	this.customMenu = function(node) {
		var menu = this.menu(node);
		
		var createREGION = {
			"label": "Aggiungi Regione",
			"action": function(obj) { createNode('REGIONE', obj.reference); }
		};
		var createULSS = {
			"label": "Aggiungi ULSS",
			"action": function(obj) { createNode('ULSS', obj.reference); }
		};
		var createUOC = {
			"label": "Aggiungi UOC",
			"action": function(obj) { createNode('UOC', obj.reference); }
		};
		var createUOS = {
			"label": "Aggiungi UOS",
			"action": function(obj) { createNode('UOS', obj.reference); }
		};
		var createARPAV = {
			"label": "Aggiungi ARPAV",
			"action": function(obj) { createNode('ARPAV', obj.reference); }
		};

		if (node.type == 'HBSROOT') {
			menu.createREGION = createREGION;
		} else if (node.type == 'REGIONE') {
			menu.createULSS = createULSS;
			menu.createARPAV = createARPAV;
		} else if (node.type == 'ULSS') {
			menu.createUOC = createUOC;
		} else if (node.type == 'UOC') {
			menu.createUOS = createUOS;
		}
		
		return menu;
	}.bind(this);
	
	this.menu = function(node) {
		var menu = {};

		if(hasChildren(node)){
			menu.deleteItem = {
				"label": "Cancella",
				"action": function(data) {
					var inst = jQuery.jstree.reference(data.reference);
					var obj = inst.get_node(data.reference);
					inst.delete_node(obj);
				}
			};
		}
		menu.rename = {
			"label": "Rinomina",
			"action": function(data) {
				var inst = jQuery.jstree.reference(data.reference);
				var obj = inst.get_node(data.reference);
				inst.edit(obj);
			}
		};
		if( areAllChildrenDisabled(node) && !isDisabled(node) ){
			menu.disable = {
				"label": "Disabilita",
				"action": function(data) {
					var inst = jQuery.jstree.reference(data.reference);
					var obj = inst.get_node(data.reference);
					
					jQuery.ajax({
						type: "post", 
						url: this.ajaxUrl,
						data: { operation: "HbsDisable", id: obj.id },
						success: function(response, textStatus, jqXHR) {
							obj.state.disabled = true;
							inst.refresh();
						},
						error: function(jqXHR, textStatus, errorThrown) {
							alert(jqXHR.responseText);
						}
					});
				}
			};
		}
		if(nodeCanBeEnabled(node)){
			menu.reEnable = {
				"label": "Abilita",
				"action": function(data) {
					var inst = jQuery.jstree.reference(data.reference);
					var obj = inst.get_node(data.reference);
					
					jQuery.ajax({
						type: "post", 
						url: this.ajaxUrl,
						data: { operation: "HbsReEnable", id: obj.id },
						success: function(response, textStatus, jqXHR) {
							obj.state.disabled = false;
							inst.refresh();
						},
						error: function(jqXHR, textStatus, errorThrown) {
							alert(jqXHR.responseText);
						}
					});
				}
			};
		}
		return menu;
	}

	var createNode = function(type, reference) {
		var inst = jQuery.jstree.reference(reference);
		var obj = inst.get_node(reference);
		inst.create_node(obj, {type:type}, "last", function (new_node) {
			setTimeout(function () { inst.edit(new_node); },0);
		});
	}

	var isDisabled = function(node) {
		return node.state.disabled;
	}


	var nodeCanBeEnabled = function(node) {
//		var liElement = jQuery(node.attr('class'));
//		var liParent = jQuery(node.parent().parent().attr('class'));
//
//		if(((liElement.selector).indexOf('jstree-disabled') >= 0) && ((liParent.selector).indexOf('jstree-disabled')) == -1){
//			return true;
//		}
		return node.state.disabled; //FIXME check parent is enabled!
	}

	var hasChildren = function(node) {
		//var liChilds = jQuery(node.find('li'));	
		if(jQuery(node).hasClass('jstree-leaf') ) {	
			return true;		
		} else {
			return false;
		}
	}



	var areAllChildrenDisabled = function(node) {
		//var liElement = jQuery(node.attr('class'));
//		var liChilds = jQuery(node.find('li'));
		//.attr('class')
		//if(jQuery(node).hasClass('jstree-leaf')){
		if(node.children.length == 0){
			//if((liElement.selector).indexOf('jstree-closed') == 0){
			if(node.state.opened == true){
				return false;
			} else {
				return true
			}
		//} else if( ( (liElement.selector).indexOf('jstree-closed') == 0) && (liChilds.length == 0) ){
		} else if( node.state.opened == true && node.children == 0 ){
			return false;
		} else {
			//FIXME check all children disabled!!!
			//for(z=0; z < liChilds.length ; z++){
//			for(z=0; z < node.children.length ; z++){
//				//if (jQuery(liChilds[z]).attr('class').indexOf('jstree-disabled') == -1){
//				if (node.children[z].state.disabled == -1){
//					return false;
//				} 
//
//			}

			return true;
		}

		return false;
	}




	//-----  end HBS tree manager  -------------------------------------------------------


	//FIXME: la funzione accetta 11 parametri ma c'è la documentazione solo per 5

	//Dictionary tree widget:
	//create a tree containing dictionary values of codeSystem, under domain.
	//treeElement: div that will contain tree
	//displayNameAndTranslation: if true renders: displayName - translation; false: translation
	//dataComponent: What will be written in the tree node as label. Allowed value:  "displayName - translation", "translation", "[code] displayName"
	//a4jF4inject: name of a4j function called onclick of a leaf
	//injectIntoConversationName: onClick injects object into injectIntoConversationName
	this.initTreeDictionary = function(treeElement, codeSystem, domain, codeValueClass, dataComponent, a4jF4inject, injectIntoConversationName, injectOnlyLeaf, treeType, selected, currentNode, validityDate) {

		this.buildTreeDictionary(treeElement, codeSystem, domain,codeValueClass, dataComponent, a4jF4inject, injectIntoConversationName, null, injectOnlyLeaf, treeType, selected, currentNode, validityDate);
	}


	this.buildTreeDictionary = function(treeElement, codeSystem, domain,codeValueClass, dataComponent, a4jF4inject, injectIntoConversationName, data, injectOnlyLeaf, treeType, selected, currentNode, validityDate) {

		if (codeSystem == null || codeSystem == "" || codeValueClass == null || codeValueClass == "") {
			return;
		}

    	this.codeSystem = codeSystem;
		var plugins = ["types", "search", 'massload'];
		var contextmenu= null;
		if (treeType == 'editableCvTree') {
			contextmenu=  { items: this.dictionaryMenu }
			plugins.push("contextmenu");
		}
		
		var self = this;
		if(selected == undefined || selected == ""){
			selected = [];
		}else{
			selected = selected.split(", ");
		}
		
		if (validityDate) {
			validityDate = dateToISO8601String(validityDate)
		}

		treeElement.jstree({
			plugins: plugins,
			contextmenu : contextmenu,
			'core' : {
				'data' : { //load data onclick, opening node
					type: 'post',
					url: self.ajaxUrl,
					data : function(node) {
						if (node.id == '#') {
							console.log('get tree' + self.cid);
							return { operation:"DictionaryGetTree", cid:self.cid, codeSystem:codeSystem, domain:domain, dataComponent:dataComponent, levelOfDepth: 1,codeValueClass:codeValueClass, validityDate: validityDate };
						} else {
							return { operation:"DictionaryGetTree", cid:self.cid, parentId : node.id, dataComponent:dataComponent, levelOfDepth: 1,codeValueClass:codeValueClass, validityDate: validityDate };
						}
					}
				},
				check_callback : true
			},
			search : {
				show_only_matches : true,
				case_insensitive : true,
				ajax : { //search, server loads parents of search results
					type: 'post',
					url: this.ajaxUrl,
					data : {
						operation: "DictionarySearch",
						cid: self.cid,
						codeValueClass:codeValueClass
					},
					beforeSend: function(jqXHR, settings) {
						blockUI();   
					},
					error: function(jqXHR, textStatus, errorThrown) {
						unblockUI();
					}
				}
			},
			'types': {
				ROOT: 		{ icon : "fa fa-folder-o treeIconSize" },
				TOPLEVEL: 	{ icon : "fa fa-folder-o treeIconSize" },
				DOMAIN: 	{ icon : "fa fa-folder-o treeIconSize" },
				CODE: 		{ icon : "fa fa-leaf treeIconSize" },
				PARAMETER: 	{ icon : "fa fa-cog treeIconSize" }
			}
		});

		treeElement.bind('search.jstree', function (e, data) {
			unblockUI();
		});
		
		treeElement.bind('loaded.jstree', function (e, data) {
			data.instance.set_state({core : {open : selected, 'selected' : [currentNode] }});
		});

		// INJECT
		treeElement.bind("select_node.jstree", function(event, data) {
			var id = data.node.id;
			var a4jFtoBeCalled = new Function('id', 'codeValueClass', 'injectInto' , a4jF4inject + '(id,codeValueClass,injectInto)');
			a4jFtoBeCalled.call(null, id, codeValueClass, injectIntoConversationName);
		});
		if (injectOnlyLeaf) {
			//INJECT ONLY LEAF
			treeElement.bind("before.jstree", function (e, data) {
				if(data.func === "select_node" && !data.inst.is_leaf(data.args[0])) {
					e.stopImmediatePropagation();
					return false;
				}
			 }); 
		}

		
		treeElement.bind("delete_node.jstree", function(event, data) {
			var id = data.node.id;
		
			jQuery.ajax({
				type: "post", 
				url: self.ajaxUrl,
				data: { operation: "CvDelete", id: id, cid: self.cid },
				error: function(jqXHR, textStatus, errorThrown) {
					//jQuery.jstree.rollback(data.rlbk);
					alert(jqXHR.responseText);
				}
			});
		});
		
		
	}
	 

	this.dictionaryMenu = function(node) {

		var node_type = node.original.type;
		
		var menu = {};

		var self = this;
		
		function createCV(new_node) {
			var object_name = new_node.text;
			var parent_id = new_node.parent;
		
			jQuery.ajax({
				type: "post", 
				url: self.ajaxUrl,
				data: { operation: "CvCreate", id: parent_id, name : object_name, node_type : new_node.original.type, generateId: false, cid: self.cid },
				success: function(response, textStatus, jqXHR) {
					new_node.id = response.id;
					new_node.type = response.type;				
				},
				error: function(jqXHR, textStatus, errorThrown) {
					//jQuery.jstree.rollback(data.rlbk);
					alert(jqXHR.responseText);
				}
			});
		};

		var createCode = {
			"label": "Aggiungi codice",
			"action": function(data) {
				var inst = jQuery.jstree.reference(data.reference);
				var obj = inst.get_node(data.reference);
				inst.create_node(obj, { type : 'CODE' }, "last", function (new_node) {
					inst.edit(new_node, 'nuovo',  createCV); 
				});
			}
		};
		var createDomain = {
			"label": "Aggiungi dominio",
			"action": function(data) {
				var inst = jQuery.jstree.reference(data.reference);
				var obj = inst.get_node(data.reference);
				inst.create_node(obj, { type : 'DOMAIN' }, "last", function (new_node) {
					setTimeout(function () { inst.edit(new_node); },0);
				});
			}
		};
		var createTopLevelDomain = {
			"label": "Aggiungi dominio top level",
			"action": function(data) {
				var inst = jQuery.jstree.reference(data.reference);
				var obj = inst.get_node(data.reference);
				inst.create_node(obj, { type : 'TOPLEVEL' }, "last", function (new_node) {
					setTimeout(function () { inst.edit(new_node); },0);
				});
			}
		};
		var createParameter = {
			"label": "Aggiungi parametro",
			"action": function(data) {
				var inst = jQuery.jstree.reference(data.reference);
				var obj = inst.get_node(data.reference);
				inst.create_node(obj, { type : 'PARAMETER' }, "last", function (new_node) {
					inst.edit(new_node, 'nuovo',  createCV); 
				});
			}
		};
		var deleteItem = {
			"label": "Elimina",
			"action": function(data) {
				var inst = jQuery.jstree.reference(data.reference);
				var obj = inst.get_node(data.reference);
				inst.delete_node(obj);
			}
		};
		
		//control menu items
		if (node_type == 'ROOT') {
			menu.createCode = createCode;
			menu.createTopLevelDomain = createTopLevelDomain;
		} 
		else if (node_type == 'TOPLEVEL') {
			if (this.codeSystem === 'ApplicationParameters') {
		    	menu.createParameter = createParameter;
			} else {
		        menu.createCode = createCode;
		    }
			menu.createDomain = createDomain;
			menu.deleteItem = deleteItem;
		} 
		else if (node_type == 'DOMAIN') {
			if (this.codeSystem === 'ApplicationParameters') {
				menu.createParameter = createParameter;
			} else {
				menu.createCode = createCode;
			}
			menu.createDomain = createDomain;
			menu.deleteItem = deleteItem;
		} 
		else if (node_type == 'CODE') {
			menu.deleteItem = deleteItem;
		}
		else if (node_type == 'PARAMETER') {
			menu.createCode = createCode;
			menu.deleteItem = deleteItem;
		}

		return menu;
	}.bind(this);


	this.buildCheckTreeDictionary = function(chkTree, codeSystem, domain, codeValueClass, dataComponent, toReloadFunction, injectIntoConversationName, data, checkOnlyLeaves, treeType, selected, currentNodes, listName) {

		var plugins = [ 'types', 'massload', 'checkbox', 'changed' ];
		var processReadOnly = jQuery('#processReadOnly').val();

		var self = this;
		if(selected == undefined || selected == ""){
			selected = [];
		}else{
			selected = selected.substring(1,selected.length-1);
			selected = selected.split(", ");
		}
		if(currentNodes == undefined || currentNodes == ""){
			currentNodes = [];
		}else{
			currentNodes = currentNodes.substring(1,currentNodes.length-1);
			currentNodes = currentNodes.split(", ");
		}
		
		if(checkOnlyLeaves){
			chkTree.addClass('checkOnlyLeaves');
		}else{
			chkTree.removeClass('checkOnlyLeaves');
		}

		chkTree.jstree({
			plugins: plugins,
			checkbox : { three_state : false },
		    'core' : {
				'data' : { //load data onclick, opening node
					type: 'post',
					url: self.ajaxUrl,
					data : function(node) {
						if (node.id == '#') {
							console.log('get tree' + self.cid);
							return { operation:"DictionaryGetTree", cid:self.cid, codeSystem:codeSystem, domain:domain, dataComponent:dataComponent, levelOfDepth: 1, codeValueClass:codeValueClass, disabled:processReadOnly};
						} else {
							return { operation:"DictionaryGetTree", cid:self.cid, parentId : node.id, dataComponent:dataComponent, levelOfDepth: 1, codeValueClass:codeValueClass, disabled:processReadOnly};
						}
					}
				},
				check_callback : true
			},
			'types': {
				ROOT: 		{ icon : "fa fa-folder-o treeIconSize" },
				TOPLEVEL: 	{ icon : "fa fa-folder-o treeIconSize" },
				DOMAIN: 	{ icon : "fa fa-folder-o treeIconSize" },
				CODE: 		{ icon : "fa fa-leaf treeIconSize" }
			}
		});

		chkTree.bind('loaded.jstree', function (e, data) {
			data.instance.set_state({core : {open : selected, 'selected' : currentNodes }});
		});

		//CHANGE
		chkTree.on("changed.jstree", function (e, data) {
			
			if (data.action != 'select_node' && data.action != 'deselect_node') {
				return;
			}
			
			if (typeof somethingChanged !== 'undefined') {
				somethingChanged = true;
			}

			if (data.action == 'select_node') {

				var parents = [];
				var selectedAndParentsAndChilds = [data.node.id];
				//find parents
				for (var z=0; z<data.node.parents.length; z++) {
					var parent = data.node.parents[z];
					if (parent != "#" && data.selected.indexOf(parent)==-1) {
						parents.push(parent);
						selectedAndParentsAndChilds.push(parent);
						data.instance.select_node(parent, true);
					}
				}

				//operation
				var selectOperation = 'CvSelect';
				
				//select post
				jQuery.ajax({
					type: 'post', 
					url: self.ajaxUrl,
					data: { operation: selectOperation, cid:self.cid, id: selectedAndParentsAndChilds, selected:data.node.id, parents: parents, codeValueClass:codeValueClass, listName:listName }
				});
				
				if(toReloadFunction){
					var a4jFtoBeCalled = new Function(toReloadFunction+'()');
					a4jFtoBeCalled.call();
				}

			} else if (data.action == 'deselect_node') {
				var selectedAndChilds = [data.node.id];
				//find childs
				selectedAndChilds.push.apply(selectedAndChilds, data.node.children_d);
				data.instance.deselect_node(data.node.children_d, true);

				//operation
				var unselectOperation = 'CvUnselect';

				//unselect post
				jQuery.ajax({
					type: 'post', 
					url: self.ajaxUrl,
					data: { operation: unselectOperation, cid:self.cid, id: selectedAndChilds, unselected:data.node.id, codeValueClass:codeValueClass, listName:listName }
				});
				
				if(toReloadFunction){
					var a4jFtoBeCalled = new Function(toReloadFunction+'()');
					a4jFtoBeCalled.call();
				}
			}
		});
	}

	this.initTreeCV = function(dicTreeCV,selectedIDCV,confirmed,conversationId,type,conversationName) {

		dicTreeCV.bind("before.jstree", function (e, data) { 

			if(data.func === "select_node" && !data.inst.is_leaf(data.args[0])) 
			{ 

				data.inst.toggle_node(data.args[0]).closest; 
				e.stopImmediatePropagation(); 
				return false; 
			} 

		}); 

		dicTreeCV.jstree({
			plugins: ["themes", "html_data", "core","ui", "crrm", "types", "search"],
			themes : {
			theme : "default",
			dots : true,
			icons : false
		},
		/*core:{
	        	  initially_open : [selectedIDCV]

	        },*/


		search : {
			case_insensitive : true,
			show_only_matches: true         
		}/*,
	      ui :{
	          select_limit : 1,
	           initially_select : [selectedIDCV]

	           }*/

		});
		// INJECT
		dicTreeCV.bind("select_node.jstree", function(event, data) { 
			var id = data.node.id;
			var conversationName = data.rslt.obj.attr('conversationName');
			injectAndrerenderDetail(id,conversationName);
		});

		/*if(!selectedIDCV){
		dicTreeCV.bind('loaded.jstree', function(e, data){
			//Open nodes on load (until 1'th level)
			var depth = 1;
			data.inst.get_container().find('li').each(function(i) {
				if(data.inst.get_path(jQuery(this)).length<=depth){
					data.inst.open_node(jQuery(this));
				}
			});
		});
	}*/

	}
}

var Tree = new Tree();
Tree.cid = cid;
Tree.ajaxUrl = ajaxUrl;

function redrawFormattedTextArea(id, w, h, isReadOnly){
	//console.log("started redrawFormattedTextArea "+id);
	//var editor = tinyMCE.get("#i:TextArea_uno_id");//tinyMCE.get("#i\\:"+id); //tinyMCE.get("i:"+id)

	tinyMCE.init({
		selector: id, //   "textarea."+id,   //note: formattedTextarea need to have a style class equal to its id.
		width: w,
		height: h-26-2,  //26: height of one toolbar. 2px: borders of toolbar.
		plugins: [  //cleanup not used plugins!
				"visualblocks paste"
		],

		toolbar1: "bold italic underline | visualblocks",
		paste_as_text: true,
		menubar: false,
		statusbar : false,
		readonly: isReadOnly,
		nonbreaking_force_tab: 'true',
		toolbar_items_size: 'small',
		content_css : "SKIN/css/tinyMceDefault.css",
		
		setup: function(ed){
			ed.on('blur', function(e) {
					//console.log('blur event', e);
					if( !ed.settings.readonly) {
						ed.save();
						var content = ed.getContent({format : 'html'});
						ed.setContent(content);
						var textAreaId = ed.id; //.replace('_id','');
						var textAreaHidden = findTargetWidgets(textAreaId);
						textAreaHidden.val(content);
						textAreaHidden.trigger("change");
					}
				});
		},
		
		init_instance_callback : function(editor) {
			if(isReadOnly) {
				editor.getBody().setAttribute('contenteditable',false);
				editor.getBody().style.backgroundColor = "#EBEBE4";
			}
		}
	});

	/*
	var iframeId = '#i\\:'+id+'_ifr';
	var bodyRichDocument = jQuery(iframeId).contents().find('body')[0];
	bodyRichDocument.contentEditable=false;
	bodyRichDocument.style.backgroundColor='#cccccc';
	*/

	//console.log("end redrawFormattedTextArea");
}


function saveTinyMceContent(inst) {
	inst.save();
	var content = inst.getContent();
	var textAreaId = inst.editorId; //.replace('_id','');
	var textAreaHidden = findTargetWidgets(textAreaId);
	textAreaHidden.val(content);
	textAreaHidden.trigger("change");
}

function concatenateText(text, widgetId, prepend, addBreakBefore, addBreakAfter, widgetType) {
 
	var widget = findTargetWidgets(widgetId);
 	var type;
 	if (widget) {
  		if (widgetType) {
   			type = widgetType;
  		} else {
   			type = 'textarea';
  		}
  		if (!widget.is(type)) {
  			widget = widget.find(type+':first');
  		}
 
  		var isFormatted = ( widget.attr("Class").indexOf("formattedTextArea")>=0 );
  
		//if idestination text area is formatted and passedText is not formatted, include it in a paragraph.
		if (isFormatted && text.indexOf('<p') <0 ) {
			text = "<p style=\"font-size: 10pt\">"+text+"</p>"
		}
		var textOld = widget.val();
		if (addBreakBefore) {
			if (isFormatted ){
				text="<p style=\"font-size: 10pt\">&nbsp;</p>"+text;
			}else {
				text="\n"+text;
			}	
		}
		if (addBreakAfter) {
			if (isFormatted){
				text=text+"<p style=\"font-size: 10pt\">&nbsp;</p>";
			} else {
				text+="\n";
			}
		}
	   
		if (prepend)
			widget.val(text+textOld);
		else {
				 widget.val(textOld+text);
				//trigger change event, to redraw rich editor
				widget.trigger("change");
		}
 	}
}

/*function openInNewTab(url) {
	url = url + '?cid=' + cid;
	var win = window.open(url, '_blank');
	win.focus();
}*/


function showError(error) {
	jQuery('#errorMenuErrors dl:first-child').append('<dt><span class="rich-messages-label">' + error + '</span></dt>');
	jQuery('#errorMenuErrors dl:first-child').show();
	jQuery('body').removeClass('togglenav');
}


function endsWith(str, suffix) {
    return str.indexOf(suffix, str.length - suffix.length) !== -1;
}

//calling DocumentRest method which render a report(odt template) as pdf.
function renderTemplateAsPdf (templateTitle) {
	
	jQuery.ajax({
			url: "./resource/rest/documents/templateByTitle/"+templateTitle,
			type: "GET",
			success: function (data, textStatus, jqXHR) {
				console.log(data);
				templateId= data.internalId;
				window.open("./resource/rest/documents/" + templateId + "/pdf?cid=" + cid,'_blank');
	       	},
			error: function (jqXHR, textStatus, errorThrown) {
				alert('Error loading template by title: ' + templateTitle + ' ' + textStatus + ' ' + errorThrown);
	       	}
		});
}

function focusIt(widgetId) {
	jQuery('#i\\:'+widgetId).focus();
}


// ******************* geolocalization ****************** //
var deviceCoordinates;
var startCoordinate  = {lat: 45.4350168, lng: 12.329868};
var map, marker, infoWindow;

function detectIE() {
    var ua = window.navigator.userAgent;

    var msie = ua.indexOf('MSIE ');
    if (msie > 0) {
        // IE 10 or older => return version number
        return parseInt(ua.substring(msie + 5, ua.indexOf('.', msie)), 10);
    }

    var trident = ua.indexOf('Trident/');
    if (trident > 0) {
        // IE 11 => return version number
        var rv = ua.indexOf('rv:');
        return parseInt(ua.substring(rv + 3, ua.indexOf('.', rv)), 10);
    }

    var edge = ua.indexOf('Edge/');
    if (edge > 0) {
       // Edge (IE 12+) => return version number
       return parseInt(ua.substring(edge + 5, ua.indexOf('.', edge)), 10);
    }

    // other browser
    return false;
}



var locationOptions = {};
if(detectIE() == 11) {
            locationOptions = {
                enableHighAccuracy: false,
                  maximumAge: 50000
            }
    }

function locationError(error ){
	 var message = ""; 

    switch (error.code) {
      case error.PERMISSION_DENIED:
          message = "Questo sito non è autorizzato ad utilizzare le funzioni di Google Maps";
          break;
       case error.POSITION_UNAVAILABLE:
          message = "Il browser non riesce a determinare la poszione attuale.";
          break;
       case error.TIMEOUT:
          message = "Il browser non ha fornito la posizione nel tempo utile.";            
          break;
    }

    if (message == "")
    {
        var strErrorCode = error.code.toString();
        message = "Il browser non riesce a determinare la poszione attuale a causa di un errore non specificato (Codice: " + strErrorCode + ").";
    }
    alert(message);
}

function getCurrentLocation(callback) {
   if(navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(function(position) {
		 deviceCoordinates = position.coords;
		 callback(position.coords);
       },locationError, locationOptions);
    }
    else {
       window.alert("Questo browser non supporta la geolocalizzazione.");    
    }
}



function doUserPickMap(escapedStr,escapedBnr,escapedCty,latitude,longitude){

	

	map = new google.maps.Map(document.getElementById('map'), {
		zoom: 19
		//,center: startCoordinate
		//,mapTypeId: 'hybrid'
	});

	var drag;
	if(longitude=="" || latitude==""){
		drag=true;		
	}else{
		drag=false;
	};
	
	marker = new google.maps.Marker({
		position: startCoordinate,
		draggable: drag,
		map: map
	});


	google.maps.event.addListener(marker, 'dragend', function(evt){
		document.getElementById('current').innerHTML = '<p>Indicatore posizionato alle coordinate:  <b>Lat: ' + evt.latLng.lat().toFixed(4) + ', Lon: ' + evt.latLng.lng().toFixed(4) + ' </b></p>';
		setTemporary(evt.latLng.lat().toFixed(7),evt.latLng.lng().toFixed(7));
	});

	google.maps.event.addListener(marker, 'dragstart', function(evt){
		document.getElementById('current').innerHTML = '<p>Indicatore in movimento...</p>';
	});	
								
								
								


	escapedStr = escapedStr.replace(/"/g, '\'');
	escapedBnr = escapedBnr.replace(/"/g, '\'');
	escapedCty = escapedCty.replace(/"/g, '\'');


	var addr = escapedStr + "+" + escapedBnr + "+" + escapedCty;

	var geocoder = new google.maps.Geocoder();
	var addressLocationFromEntity;


	geocoder.geocode( {'address' : addr}, function(results, status) {
		
		if (status == google.maps.GeocoderStatus.OK) {
			addressLocationFromEntity=results[0].geometry.location;
			
			map.setCenter(addressLocationFromEntity);
			document.getElementById('current').innerHTML = '<p>Indicatore posizionato alle coordinate:  <b>Lat: ' +addressLocationFromEntity.lat().toFixed(4) + ', Lon: ' + addressLocationFromEntity.lng().toFixed(4) + ' </b></p>';
			marker.setPosition(addressLocationFromEntity);
			setTemporary(addressLocationFromEntity.lat().toFixed(7),addressLocationFromEntity.lng().toFixed(7));
			
		}else{
			
			addressLocationFromEntity=null;

				if (navigator.geolocation) {
					navigator.geolocation.getCurrentPosition(function(position) {
					 
					var pos = {
						lat: position.coords.latitude,
						lng: position.coords.longitude
					};

					map.setCenter(pos);
					marker.setPosition(pos);
					document.getElementById('current').innerHTML = '<p>Indicatore posizionato alle coordinate:  <b>Lat: ' + pos.lat.toFixed(4) + ', Lon: ' + pos.lng.toFixed(4) + ' </b></p>';
					setTemporary(pos.lat.toFixed(7),pos.lng.toFixed(7));
				}, function() {
				
					fallback();

				});
			}else{
				
				 fallback();
			}
		}
	});		

}


function initMap() {
	
	map = new google.maps.Map(document.getElementById('map'), {
		zoom: 19
		//,center: startCoordinate
		//,mapTypeId: 'hybrid'
	});

	
	marker = new google.maps.Marker({
		position: startCoordinate,
		draggable: true,
		map: map
	});


	google.maps.event.addListener(marker, 'dragend', function(evt){
		document.getElementById('current').innerHTML = '<p>Indicatore posizionato alle coordinate:  <b>Lat: ' + evt.latLng.lat().toFixed(4) + ', Lon: ' + evt.latLng.lng().toFixed(4) + ' </b></p>';
		setTemporary(evt.latLng.lat().toFixed(7),evt.latLng.lng().toFixed(7));
	});

	google.maps.event.addListener(marker, 'dragstart', function(evt){
		document.getElementById('current').innerHTML = '<p>Indicatore in movimento...</p>';
	});



}

function fallback(){

	map.setCenter(startCoordinate);
	marker.setPosition(startCoordinate);
	document.getElementById('current').innerHTML = '<p>Indicatore posizionato alle coordinate: <b>Lat: ' + startCoordinate.lat.toFixed(4) + ', Lon: ' + startCoordinate.lng.toFixed(4) + '</b></p>';
	setTemporary(startCoordinate.lat.toFixed(7),startCoordinate.lng.toFixed(7));
	
}

function googleStaticMapErrorFunction(url) {

    var request = new XMLHttpRequest();
    request.open('GET', url, true);
    request.onreadystatechange = function() {
        if (request.readyState === 4) {
            //if (request.status === 403) {  

            var t = document.createTextNode(request.response);

            var targetElement = document.getElementById("googleMapStatic");

            while (targetElement.firstChild) {
                targetElement.removeChild(targetElement.firstChild);
            }

            targetElement.appendChild(t);

            //   }
        }
    };
    request.send();

}

function forceUpperCase() {
	jQuery('input[type=text]:visible, textarea:visible').each(function() {
		jQuery(this).val(jQuery(this).val().toUpperCase());
	}).on('input',function() {
		var start = this.selectionStart;
        var end = this.selectionEnd;
		jQuery(this).val(jQuery(this).val().toUpperCase());
		this.setSelectionRange(start, end);
	});
	// I00080634: maiuscolo anche quanto non previsto
	// nel codice originale
	jQuery('[class="labelContent base-label"],td span[class="base-label"]').each(function() {
		jQuery(this).text(jQuery(this).text().toUpperCase());
	});
}

function disableUpperCase() {
	jQuery('input[type=text]:visible, textarea:visible').off('input');
}