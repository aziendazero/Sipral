		var old_stop = stopAjaxReq;
		var old_start;
		var old_show_hide;
	    window.onload = function(){
			
			//MAIUSCOLARIZZAZIONE ARPAV
			if(!old_start)
				old_start=startProcess;
			startProcess = function(currentProcess) {
				//sovrascrivo stopAjaxReq qui perchè solo in questo contesto ho currentProcess
				stopAjaxReq = function() {
					old_stop();
					if('true'===user_arpav && 'true'===enable_maiusc &&
						!(currentProcess.startsWith('MOD_Admin') || 
						 currentProcess.startsWith('MOD_HBS') || 
						 currentProcess.startsWith('MOD_Document') || 
						 currentProcess === 'MOD_Anagrafiche/CORE/PROCESSES/UtentiDelSistema')) {
						forceUpperCase();
					} else {
						disableUpperCase();
					}
				};
				old_start(currentProcess);
			};
			
			//HIDE TOOLTIP
			if('true'===user_arpav && 'true'!==is_developer){
				if(!old_show_hide)
					old_show_hide=showHideProcessList;
				showHideProcessList = function() {
					if(document.getElementById('i'))
						document.getElementById('i').setAttribute('title', 'form');
					if(document.getElementById('p'))
						document.getElementById('p').setAttribute('title', 'popup');
					old_show_hide();
					if(document.getElementById('i'))
						document.getElementById('i').removeAttribute('title');
					if(document.getElementById('p'))
						document.getElementById('p').removeAttribute('title');
				}
			}
		};