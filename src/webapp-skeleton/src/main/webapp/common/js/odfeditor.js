/**
 * Phi - WebODF (http://www.webodf.org/) - integration
 * Load and edit an odf file
 * Manage creation and update
 */

function OdfEditor(username, readCallBack, saveCallBack, url, documentId) {
    "use strict";

    var editor = null;
    
    var loadedFilename;
    
    this.username = username;
	
	this.isDirty = function () {
		return editor.isDocumentModified();
	}

    /**
     * editor created callback
     */
	this.onEditorCreated = function (err, e) {
    	
		if (err) {
			alert(err);
            return;
        }

        editor = e;
        editor.setUserData({
            fullName: this.username,
            color:    "black"
        });

        if(typeof readCallBack != 'undefined') {

			var readResult = readCallBack(documentId);
			
			if (typeof readResult == "string") {
				loadedFilename = readResult;
				editor.openDocumentFromUrl(loadedFilename, startEditing);
			}
			else {
				readResult.then(function(loadedFilename) {
					editor.openDocumentFromUrl(loadedFilename, startEditing);
				});
				
			}
		}
        
        window.addEventListener("keydown", function (e) {
           //if (editor.isDocumentModified())
            	parent.somethingChanged = true;
        });

    }.bind(this);
    
    /**
     * Start editing callback
     */
    function startEditing() {
    	//set something chnged popup!
    	parent.somethingChanged = true;
    }

    /**
     * Save callback
     * Saves or updates odf into alfresco
     * Updates noderef in Phi
     */
    this.save = function () {

		var dfd = new jQuery.Deferred();

        function saveByteArrayInAlfresco(err, data) {
            if (err) {
                alert("Error saving odt " + err);
                return;
            }

			saveCallBack(documentId, data, editor).then(function(nr) {
				dfd.resolve(nr)
			});
			parent.somethingChanged = false;
        }

        editor.getDocumentAsByteArray(saveByteArrayInAlfresco);
		editor.setDocumentModified(false);
		return dfd.promise();
    }
    
    /**
     * Destroy editor to reuse it without iframe.
     */
    this.destroy = function () {
    	var callback = function() { };
    	
    	editor.closeDocument(callback);
    	editor.destroy(callback);
    }
    
    if (documentId == null) {
    	documentId = getUrlParameter('documentId');
    }
	
	var editorOptions = {
		//loadCallback: load,
		//saveCallback: this.save,
		//modus: Wodo.MODUS_REVIEW,//Wodo.MODUS_FULLEDITING
        allFeaturesEnabled: true
    };

    if (editor != null) {
        this.destroy();
    }

    Wodo.createTextEditor('editorContainer', editorOptions, this.onEditorCreated);
    
}

    var getUrlParameter = function getUrlParameter(sParam) {
        var sPageURL = decodeURIComponent(window.location.search.substring(1)),
            sURLVariables = sPageURL.split('&'),
            sParameterName,
            i;

        for (i = 0; i < sURLVariables.length; i++) {
            sParameterName = sURLVariables[i].split('=');

            if (sParameterName[0] === sParam) {
                return sParameterName[1] === undefined ? true : sParameterName[1];
            }
        }
    };