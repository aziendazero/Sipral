			

	function showCalendar(fullCalendarConf, operation, parameter, parameter2) {

		//allowed operation:  byoperator, byagenda, byagendaandope
		var urlDetail = operation;
		if (typeof operation == 'undifined' || typeof parameter == 'undefined') {
			console.log("invalid operation for calendar or empty filter parameter")
			return;
		}
		
		urlDetail += '/'+parameter;
		if (operation == 'byagendaandope'){
			if( typeof parameter2 == 'undefined') {
				console.log("missing second operator")
				return;
			}
			else {
				urlDetail+='/'+parameter2;
			}
		} 
		
		//used to avoid doubleclick during event creation
		
		
		var isClicked, isDblClicked;
	
		jQuery('#calendar').fullCalendar({
			header: {
				left: 'prev,next today',
				center: 'title',
				right: 'month,agendaWeek,agendaDay,listWeek'
			},
			
			locale: 'it', //defaultDate: '2017-09-12',
			navLinks: true, // can click day/week names to navigate views
			editable: false,
			selectable: !fullCalendarConf.viewOnly, //${not AppointmentSpAction.temporary['viewOnly']}
			eventLimit: true, // allow "more" link when too many events
			displayEventTime: true,
			displayEventEnd : true,
			timeFormat: 'H:mm',
			eventColor: '#b3d5e6',
			
			slotDuration: '00:'+fullCalendarConf.slotDuration+':00',
			defaultView: fullCalendarConf.defaultView || 'month',  
			
			events: function(start, end, timezone, callback) {
				console.log('start:'+start.format('DD-MM-YYYY') + '  end:'+end.format('DD-MM-YYYY'));
				var rerenderEvents = callback;
				blockUI();
				jQuery.ajax({
						type: "GET", 
						contentType : 'application/json',
						url: 'resource/rest/appointmentsps/'+urlDetail+'/'+start.format('DD-MM-YYYY')+'/'+end.format('DD-MM-YYYY'),
						success: function(response, textStatus, jqXHR) {
							console.log(response);
							rerenderEvents(response);
							unblockUI();							
						},
						error: function(jqXHR, textStatus, errorThrown) {
							console.log('error getting timeframe');
							unblockUI();
						}
				});
			},
			
			eventRender: function(event, element) { 
				var eventDetail = "<br/>" + event.agenda +"<br/>" + event.prot;
				if (event.paziente){
					eventDetail+= "<br/>paz:" + event.paziente;
				}
				element.find('.fc-title').append(eventDetail);  
				element.find('.fc-list-item-title').append(eventDetail);
			},

			eventClick: function(calEvent, jsEvent, view) {
				if (fullCalendarConf.disableAppointmentClick) {
					return; 
				}
				blockUI();
				console.log ('selected calEvent:',calEvent);
				
				var appointmentSpAction = new BaseAction('AppointmentSp');
				appointmentSpAction.inject(calEvent.id).done(function(data) {
					manageTask('Btn_selectedAppointmentSp','SELECT');
				});
			},

			select: function(start, end, allDay) {
				blockUI();
				
				if (!isDblClicked && end.diff(start, 'days') == 0) {
					var appointmentSpAction = new BaseAction('AppointmentSp');
					var appointmentSp = {"data" : start.toJSON(), 'end': end.toJSON() };
					appointmentSpAction.injectNew(JSON.stringify(appointmentSp)).done(function(response){
						manageTaskPopup('Btn_selectedAppointmentSp','ADD');
					});
					
				}
				
				jQuery('#calendar').fullCalendar( 'unselect' ) ;
				unblockUI();
			},
			
			dayClick:function( date, allDay, jsEvent, view ) {
				//blockUI(); 
				if(isClicked){
					isDblClicked = true;
					isClicked = false;
				}
				else{
					isClicked = true;
				}
				setTimeout(function(){
					isClicked = false;
					isDblClicked = false;
				}, 250);
			}
		});
		
		
	}



	