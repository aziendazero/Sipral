/**
 * Alfresco Object manages Rest and CIMS methods
 * login, browse, search, history and save
 * Api doc: http://im-spisallx:8080/alfresco/service/index/lifecycle/
 * Web Scripts Home: http://im-spisallx:8080/alfresco/service/index
 * Node browser: http://im-spisallx:8080/alfresco/s/admin/admin-nodebrowser
 */

function Alfresco(url, urlHttps, t, blockUIcallBack, unblockUIcallBack) {
    "use strict";
    
    var self = this;
    
    var alfrescoUrl = url;
    
    if (location.protocol == 'https:') {
        alfrescoUrl = urlHttps;
    }

	var ticket;
	var ticketHeader;
	
	if (t) {
		ticket = t;
		if (window.btoa != undefined) {
			ticketHeader = 'Basic ' + btoa('' + ":" + ticket);
		}//else use polyfill
	}
	
	this.block = blockUIcallBack;
	this.unblock = unblockUIcallBack;
	
	/**
	 * public login method
	 */
	this.login = function(username, password) {
		if (this.block) {
			this.block();
		}
		return jQuery.ajax({
			type: "POST", 
			contentType : 'application/json',
			url: alfrescoUrl + '/alfresco/service/api/login',
			data: '{"username":"' + username + '","password":"' + password + '"}',
			success: function(response, textStatus, jqXHR) {
				ticket = response.data.ticket;
				ticketHeader = 'Basic ' + btoa('' + ":" + ticket);
	       		if (self.unblock) {
	       			self.unblock();
	       		}
			},
			error: function(jqXHR, textStatus, errorThrown) {
				showError('Errore login Alfresco: accertarsi sia in esecuzione e di aver accettato il certificato <a href="'+ alfrescoUrl + '/share/page" target="_blank">qui<\a>');
	       		if (self.unblock) {
	       			self.unblock();
	       		}
			}
		});
	}
	
	/**
	 * public method that returns current authentication ticket
	 */
	this.getTicket = function() {
		return ticket;
	}
	
	/**
	 * public method that browse an alfresco folder by path
	 */
	this.browse = function(path, onlyMajorVersions) {
		if (this.block) {
			this.block();
		}
		return jQuery.ajax({
			url: alfrescoUrl + "/alfresco/api/-default-/public/cmis/versions/1.1/browser/root" + path + "?cmisselector=children",
			type: "GET",
			beforeSend: function (xhr){
				xhr.setRequestHeader('Authorization', ticketHeader);
			},
			success: function (data, textStatus, jqXHR) {
				populateTable(path, data, onlyMajorVersions);
	       		if (self.unblock) {
	       			self.unblock();
	       		}
			},
			error: function (jqXHR, textStatus, errorThrown) {
				alert('Alfresco error browsing ' + path + ' ' + textStatus + ' ' + errorThrown);
	       		if (self.unblock) {
	       			self.unblock();
	       		}
			}
		});
	}
	
	/**
	 * private method that populates a table with results of search, history and search
	 */
	var populateTable = function(path, rawData, onlyMajorVersions, parentNodeRef) {
	
		//breadcrumbs
		var breadcrumb = jQuery('.breadcrumb');
		breadcrumb.empty();
		
		var path2split = path;
		if (path.charAt(0) == '/') {
			path2split = path.substring(1, path.length);
		}
		
		var pathParts = path2split.split('/');
	
		var breadStr = '<li><a href="#" onclick="alfresco.browse(\'/\', ' + onlyMajorVersions + ')" >/</a></li>';
	
		var absPath = '';
		for (var pathI = 0; pathI<pathParts.length; pathI++) {
			absPath += '/' + pathParts[pathI];
			breadStr += '<li><a href="#" onclick="alfresco.browse(\'' + absPath + '\', ' + onlyMajorVersions + ')" >' + pathParts[pathI] + '</a></li>';
		}
		breadcrumb.append(breadStr);
	
		//table
		var tBody = jQuery('#dDgAlfrescoResults tbody');
		tBody.empty();
		
		var data;
		if (rawData.objects != undefined) { //Browse
			data = rawData.objects;
			
			data.sort(function(a, b) {
				var aName = a.object.properties['cmis:name'].value;
				var bName = b.object.properties['cmis:name'].value;
				if (aName < bName)
					return -1;
				if (aName > bName)
					return 1;
				return 0;
			});
			
		} else if (rawData.results != undefined) { //Search
			data = rawData.results;
		} else { //Versions
			data = rawData;
		}
	
	
		for (var res = 0; res<data.length; res++) {
	
			var doc;
			if (data[res].object != undefined) {//Browse
				doc = data[res].object;
			} else { //Search
				doc = data[res];
			}
			
			var title;
			var author = '';
			//var createdBy;
			var type;
			var nodeRef;
			var versionLabel = '';
			var description = '';
			var date = '';
			
			var revert = '';
			
			if (doc.properties != undefined) {//Browse,Search
		
				title = 	doc.properties['cmis:name'].value;
				if (doc.properties['cm:author'] != null && doc.properties['cm:author'].value != null) {
					author =	doc.properties['cm:author'].value;
				}
				//createdBy = doc.properties['cmis:createdBy'].value;
				type = 		doc.properties['cmis:baseTypeId'].value;
				nodeRef = 	doc.properties['alfcmis:nodeRef'].value;
				if (doc.properties['cm:description'] != null && doc.properties['cm:description'].value != null) {
					description=doc.properties['cm:description'].value;
				}
				if (doc.properties['cmis:creationDate'].value != null) {
					date = 	new Date(doc.properties['cmis:creationDate'].value);
				}
	
				if (doc.properties['cmis:versionLabel'] != undefined) {
					versionLabel = doc.properties['cmis:versionLabel'].value;
				}
				
			} else { //History
				title = 		doc.name;
				author =		'';
				//createdBy = 	doc.creator.userName;
				type = 			'cmis:document';
				nodeRef = 		doc.nodeRef;
				versionLabel = 	doc.label;
				description = 	doc.description;
				if (doc.createdDateISO != null) {
					date = new Date(Date.parse(doc.createdDateISO));
				}
				
				if (onlyMajorVersions && versionLabel.indexOf('.0', versionLabel.length - 2) == -1) {
					//if onlyMajorVersions == true skip all versions that doesen't end with .0
					continue;
				}
				
				if (res !== 0) {
					revert = '<a href="#" onclick="stopPropagation(event);alfresco.revertAndRefreshHistory(\'' + parentNodeRef + '\', \'' + versionLabel + '\', false, \'Revert to version ' + versionLabel +'\', \'' + path + '\',' + onlyMajorVersions + ')" class="fa fa-undo fa-3x link"></a>';
				}
			}
			var month = date.getMonth() + 1;
			date = date.getDate() + '/' + month + '/' + date.getFullYear() + ' ' + date.getHours() + ':' + (date.getMinutes()<10?'0':'') + date.getMinutes();
			
			var preview = '';
			var pdfLink = '';
			var download = '';
			var action;
			
			if (type == 'cmis:folder') {
				preview = '<i class="fa fa-folder fa-2x"></i>';
				if (path == '/') {
					action = 'alfresco.browse(\'/' + title + '\', ' + onlyMajorVersions + ');';
				} else {
					action = 'alfresco.browse(\'' + path + '/' + title + '\', ' + onlyMajorVersions + ');';
				}
			} else {
				var nodeRefPath = nodeRef.replace("://", "/");
				
				//pdf link
				pdfLink = '<a href="#" onclick="stopPropagation(event);alfresco.openDocument(\'' + nodeRefPath + '\')" title="Pdf" class="fa fa-file-pdf-o fa-3x" />';
				
				download = '<a href="' + alfresco.getDocumentUrl(nodeRef) + '" target="_blank" title="Download" class="fa fa-download fa-3x tableButton"/>';
				
				if (doc.properties != undefined) {//Browse,Search
					preview = '<img src="' + alfrescoUrl +'/alfresco/service/api/node/' + nodeRefPath + '/content/thumbnails/doclib?c=queue&amp;ph=true&amp;alf_ticket=' + ticket + '" alt="Prewiew of ' + title + '">';
					action = 'alfresco.showDocumentAndHistory(\'' + path + '\',\'' + nodeRef + '\', ' + onlyMajorVersions + ');';
				} else { //History
					action = 'alfresco.showDocument(\'' + path + '\',\'' + nodeRef + '\', ' + onlyMajorVersions + ');';
				}
			}
			var desc = '';
			if (author != '') {
				desc = author + '<br/>';
			}
			if (description != '') {
				desc += description + '<br/>';
			}
			if (date != '') {
				desc += date + '<br/>';
			}
			desc = desc.substring(0, desc.length-5);
			var row = '<tr onclick="' + action + '"><td>' + preview + '</td><td>' + title + '</span></td><td>' + versionLabel + '</td><td>' + desc + '</td><td>' + pdfLink + '</td><td>' + download + '</td><td>' + revert + '</td></tr>';
			tBody.append(row);
		}
	}
	
	/**
	 * private history
	 * returns all versions of a node
	 */
	var history = function(path, nodeRef, onlyMajorVersions) {
		if (self.block) {
			self.block();
		}
		jQuery.ajax({
			url: alfrescoUrl + "/alfresco/service/api/version?nodeRef=" + nodeRef,
			type: "GET",
			beforeSend: function (xhr){
				xhr.setRequestHeader('Authorization', ticketHeader);
			},
			success: function (data, textStatus, jqXHR) {
				//alert('TOP');
				populateTable(path, data, onlyMajorVersions, nodeRef);
	       		if (self.unblock) {
	       			self.unblock();
	       		}
			},
			error: function (jqXHR, textStatus, errorThrown) {
				alert('Alfresco error browsing history ' + path + ' ' + nodeRef + ' ' + textStatus + ' ' + errorThrown);
	       		if (self.unblock) {
	       			self.unblock();
	       		}
			}
		});
	}
	
	/**
	 * public method used to show a document with webodf
	 * http://172.22.25.37:7080/alfresco/service/api/node/content/workspace/SpacesStore/fd0651d5-7be0-4023-bcb7-31929f8fd399?alf_ticket=" + ticket
	 */
	this.showDocument = function (path, nodeRef, onlyMajorVersions) {

	    var odfelement = document.getElementById("i:LayoutAlfrescoPreviewContainer");
		//var iframe = document.getElementById('odfViewer');
		//iframe.src = iframe.src;
		//var odfelement = iframe.contentDocument.getElementById('viewerContainer');
		var odfcanvas = new odf.OdfCanvas(odfelement);

		var nodeRefPath = nodeRef.replace("://", "/");
		
	    odfcanvas.load(alfrescoUrl + "/alfresco/service/api/node/content/" + nodeRefPath + "?alf_ticket=" + ticket);
	}
	
	this.showDocumentAndHistory = function (path, nodeRef, onlyMajorVersions) {
		history(path, nodeRef, onlyMajorVersions);
		this.showDocument(path, nodeRef, onlyMajorVersions);
	}

	/**
	 * public query method
	 */
	this.query = function(sql, searchAllVersions) {
		if (this.block) {
			this.block();
		}
		searchAllVersions = 'false';

		var data = 'cmisaction=query&statement=' + sql + '&searchAllVersions=' + searchAllVersions + '&includeAllowableActions=true&includeRelationships=both&skipCount=0';
		
		return jQuery.ajax({
			url: alfrescoUrl + "/alfresco/api/-default-/public/cmis/versions/1.1/browser",
			type: "POST",
			data: data,
			beforeSend: function (xhr){
				xhr.setRequestHeader('Authorization', ticketHeader);
			},
			success: function (data, textStatus, jqXHR) {
	       		if (self.unblock) {
	       			self.unblock();
	       		}
			}
		/*	,
			error: function (jqXHR, textStatus, errorThrown) {
				alert('Alfresco error searching ' + sql + ' ' + searchAllVersions + ' ' + textStatus + ' ' + errorThrown);
	       		if (self.unblock) {
	       			self.unblock();
	       		}
			}*/
		});
	
	}
	
	/**
	 * public method used to search into alfresco repository
	 * with cmis query
	 * SELECT * FROM cmis:document WHERE CONTAINS('" + term + "')
	 */
	this.search = function(term, searchAllVersions) {
		var sql = "SELECT * FROM cmis:document WHERE CONTAINS('" + term + "')";
		return this.query(sql, searchAllVersions)
			.then(function(data) {
				populateTable(term, data);
			}).fail(function(err) {
				var msg = "Error search term: " + term + " searchAllVersions " + searchAllVersions + " err " + err.statusText + " " + err.status;
				manageError(msg);
			});

	}
	
	/**
	 * public method to get the document url from a noderef
	 * nodeRef alfresco id for example: 'workspace://SpacesStore/fd0651d5-7be0-4023-bcb7-31929f8fd399'
	 */
    this.getDocumentUrl = function(nodeRef) {
    	var nodeRefPath = nodeRef.replace("://", "/");
    	return alfrescoUrl + '/alfresco/service/api/node/content/' + nodeRefPath + '?alf_ticket=' + ticket;
    }
    
	/**
	 * public method to get the thumbnail url from a noderef
	 */
    this.getThumbnailUrl = function(nodeRef) {
    	var nodeRefPath = nodeRef.replace("://", "/");
    	return alfrescoUrl + '/alfresco/service/api/node/' + nodeRefPath + '/content/thumbnails/doclib?c=queue&ph=true&alf_ticket=' + ticket;
    }
    
	/**
	 * public method to get the pdf url from a noderef
	 */
    this.getPdfUrl = function(nodeRef) {
    	var nodeRefPath = nodeRef.replace("://", "/");
    	return alfrescoUrl + '/alfresco/service/api/node/' + nodeRefPath + '/content/thumbnails/pdf?alf_ticket=' + ticket;
    }
    
    this.get = async (url) => {
    	const response = await fetch(url, { method: 'GET' });
    	const file = await response.blob();
    	return file;
    }
    
	/**
	 * public method getFolderNoderef
	 * find nodeRef of folder with path
	 */
	this.getFolderNoderef = async function(path) {
		const response = await fetch(alfrescoUrl + "/alfresco/api/-default-/public/cmis/versions/1.1/browser/root" + path + "?cmisselector=object", {
            method: 'GET',
            headers: new Headers({ "Authorization": ticketHeader }),
        });
        if (response.ok) {
			const json = await response.json();
        	return json.properties["alfcmis:nodeRef"].value;
        } else {
        	return null;
        }
	}
	
	/**
	 * public method createFolder
	 * create folder with path
	 * curl -X POST -uadmin:admin "http://localhost:8080/alfresco/s/api/node/folder/workspace/SpacesStore/d259c12a-a90b-465a-82dc-df71ad6f3b90" -H "Content-Type: application/json" -d'{"name":"foo"}'
	 * response: {"nodeRef": "workspace://SpacesStore/faaf73a0-ff4a-4f75-a5a3-21c9e00d4f26"}
	 */
	this.createFolder = function(parentFolderNodeRef, folderName) {
		var parentFolderNodeRefPath = parentFolderNodeRef.replace("://", "/");
		return jQuery.ajax({
			url: alfrescoUrl + '/alfresco/service/api/node/folder/' + parentFolderNodeRefPath,
			type: "POST",
			data: '{"name":"' + folderName + '"}',
			contentType : 'application/json',
			beforeSend: function (xhr){
				xhr.setRequestHeader('Authorization', ticketHeader);
			},
			error: function (jqXHR, textStatus, errorThrown) {
				alert('Alfresco error creating folder with name ' + folderName + ' into ' + parentFolderNodeRef + ' ' + textStatus + ' ' + errorThrown);
			}
		}).then(function(response, textStatus, jqXHR) {
			return response.nodeRef;
		});
	}
	
	/**
	 * public method getOrCreateFolder
	 * find nodeRef of folder with path, if doesen't exsist create it.
	 * FIXME: can be optimized, when nodeRef != null --> getFolderNoderef is useless
	 */
	this.getOrCreateFolder = function(fullPath, currentPath, nodeRef) {

		return self.getFolderNoderef(currentPath).then(function(folderNodeRef) {
			
       		if (folderNodeRef != null) { //exsist
       			
       			if (fullPath == currentPath) {
       				return folderNodeRef;
       			} else {
       				var relativePath = fullPath.substring(currentPath.length + 1, fullPath.length);
					var nextSlash = relativePath.indexOf('/',1);
					if (nextSlash != -1) {
							relativePath = relativePath.substring(0, relativePath.indexOf('/',1));
					}
					return self.createFolder(folderNodeRef, relativePath).then(function(newNodeRef) {
						return self.getOrCreateFolder(fullPath, currentPath + '/' + relativePath, newNodeRef);
					}).fail(function(err) {
						var msg = "Error createFolder path: " + relativePath + " folderNodeRef " + folderNodeRef + " err " + err.statusText + " " + err.status;
						manageError(msg);
					});
       			}

       		} else { //not exsists
       			
				var lastSlash = currentPath.lastIndexOf('/');
				var newPath = currentPath.substring(0, lastSlash);
				
				return self.getOrCreateFolder(fullPath, newPath, nodeRef);
       		}
		}).catch(function(err) {
			var msg = "Error getOrCreateFolder path: " + fullPath + " currentPath " + currentPath + " nodeRef " + nodeRef + " err " + err.statusText + " " + err.status;
			manageError(msg);
		});
	}

	
	/**
	 * public method used to create document into alfresco repository
	 * data array of bytes
	 * path file path
	 * name file name
	 * promise then : see: http://stackoverflow.com/questions/15068549/change-data-result-while-passing-promise-up-chain
	 */
	this.create = function(data, folderNodeRef, fileName) {
		return createOrUpdate(data, null, folderNodeRef, fileName, false);
	}
	
    
	/**
	 * public method used to update document into alfresco repository
	 * data array of bytes
	 * nodeRef alfresco id for example: 'workspace://SpacesStore/fd0651d5-7be0-4023-bcb7-31929f8fd399'
	 * majorversion
	 */
    this.update = function(data, nodeRef, majorversion) {
    	return createOrUpdate(data, nodeRef, null, null, majorversion);
    }
    
	/**
	 * private method used to create or update into alfresco repository
	 * if nodeRef != null -> update
	 * if folderNodeRef != -> create
	 * See: http://docs.alfresco.com/5.0/references/RESTful-UploadUploadPost.html
	 * WebScript src: http://im-spisal:7080/alfresco/service/script/org/alfresco/repository/upload/upload.post
	 * WebScript desc: http://im-spisal:7080/alfresco/service/description/org/alfresco/repository/upload/upload.post
	 */
    var createOrUpdate = function(data, nodeRef, folderNodeRef, fileName, majorversion) {	
    	try {
    		var formData = new FormData();
    		var mimeType = "application/vnd.oasis.opendocument.text";
    		formData.append("filedata", new Blob([data], {type: mimeType}));
    		
    		if (majorversion) {
    			formData.append("majorversion", true);
    		}
    		
    		if (nodeRef != null) { //Update
    			formData.append("updateNodeRef", nodeRef);
    			return doUpload(formData, mimeType);
    		} else if (folderNodeRef != null) { //Create
    			formData.append("destination", folderNodeRef);
    			formData.append("filename", fileName);
    			return doUpload(formData, mimeType);
    		} else {
    			return;
    		}
    		
		} catch (err) {
			alert('Alfresco error creating or updating ' + data + ' ' + nodeRef + ' ' + folderNodeRef + ' ' + err);
		}
    }
    
	/**
	 * public upload a file via form
	 * if nodeRef != null -> update
	 * 	upload an update to an existing file with nodeRef
	 * if folderNodeRef != -> create
	 * 	upload a new file via form under folder: folderPath
	 */
    this.upload = function(formId, folderPath, nodeRef, finishedCallBack) {
		//FIXME: to be removed
		LOG.fatal("[INFO] Uploading to path: " + folderPath + " nodeRef " + nodeRef);
    	
		var uploadForm = jQuery(formId);
		var mimeType = uploadForm.find('#p\\:returnedMimeType').val();
    	var formData = new FormData(uploadForm[0]);
    	
/*    	var fileData = uploadForm.find('#filedata')[0].files[0];
    	var fileName = uploadForm.find('#filename').val();
    	var description = uploadForm.find('#description').val();
    	
        formData.append('filedata', fileData, fileData.name);
        formData.append('fileName', fileName);
        formData.append('description', description);
*/
    	
    	if (nodeRef == null || nodeRef == "") { //Create
	    	
	    	//if (folderPath.endsWith('/')) {ES6: NO IE11!!
    		if (endsWith(folderPath, '/')) {
	    		folderPath = folderPath.substring(0, folderPath.length-1);
	    	}
	    	
	    	this.getOrCreateFolder(folderPath, folderPath, null).then(function(folderNodeRef) {
	    		formData.append('destination', folderNodeRef);
	    		formData.append('folder', folderPath);
       			return doUpload(formData, mimeType).then(function(folderNodeRef) {
       				if (finishedCallBack) {
       					finishedCallBack();
       				}
       			});
	    	});
    	} else { //Update
           	formData.append('updateNodeRef', nodeRef);
        		
           	return doUpload(formData, mimeType).then(function(folderNodeRef) {
    			if (finishedCallBack) {
    				finishedCallBack();
    			}
   			});
    	}
    }
    
    
	/**
	 * private method used to upload a file via form into alfresco repository
	 */
    var doUpload = function(formData, mimeType) {
    	
       	try {
       		if (self.block) {
			self.block();
       		}
       		
       		var uploadUrl = alfrescoUrl + "/alfresco/service/api/upload";
       		
			//formData.get('filedata').type get not supported by IE...
			if (mimeType == 'application/x-zip-compressed') {
				//Post zip to phi server, will be uncompressed and then each file posted to alfresco
				uploadUrl = "./resource/rest/reports/upload?cid=" + cid;
			}
	    		
       		
    		return jQuery.ajax({
    			url: uploadUrl,
    			type: "POST",
    			data: formData,
    			contentType: false,
    			cache: false,
    			processData: false,
    			dataType:'json',
    			beforeSend: function (xhr){
    				xhr.setRequestHeader('Authorization', ticketHeader);
    			},
    			success: function (response, textStatus, jqXHR) {
    	       		if (self.unblock) {
    	       			self.unblock();
    	       		}
    			},
    			error: function (jqXHR, textStatus, errorThrown) {
					var msg = 'Errore upload Alfresco: ' + jqXHR.statusText + ' ' + jqXHR.responseText; 
    	       		manageError(msg);
                }
    		}).then(function(response, textStatus, jqXHR) {
    			if (response != null && response.status != null) {
					if (response.status.name == 'OK') {
						//FIXME REMOVE
						jQuery('#p\\:returnedNodeRef').val(response.nodeRef);
						jQuery('#p\\:returnedName').val(response.fileName);
						jQuery('#p\\:returnedDesc').val(jQuery('#description').val());
						
						return response.nodeRef;
					}
    		}});
    		
		} catch (err) {
			alert('Alfresco error uploading ' + formId + ' ' + err);
		}
	}
    
    /**
	 * public revert a document
	 * POST /alfresco/service/api/revert
	 * Descriptor: http://im-spisallx:8080/alfresco/service/script/org/alfresco/repository/version/revert.post
     */
    this.revert = function(nodeRef, version, majorVersion, description) {
		if (this.block) {
			this.block();
		}
		
		var data = {
				nodeRef: nodeRef,
				version: version,
				majorVersion: majorVersion,
				description: description
		};
		
		return jQuery.ajax({
			type: "POST", 
			contentType : 'application/json',
			url: alfrescoUrl + '/alfresco/service/api/revert',
			data: JSON.stringify(data),
			beforeSend: function (xhr){
				xhr.setRequestHeader('Authorization', ticketHeader);
			},
			success: function(response, textStatus, jqXHR) {
				//console.log(response);
				if (self.unblock) {
	       			self.unblock();
	       		}
			},
			error: function(jqXHR, textStatus, errorThrown) {
				showError('Errore revert Alfresco: nodeRef: ' + nodeRef + ' version: ' + version);
				if (self.unblock) {
	       			self.unblock();
	       		}
			}
		});
    }
    
    this.revertAndRefreshHistory = function(nodeRef, version, majorVersion, description, path, onlyMajorVersions) {
		return this.revert(nodeRef, version, majorVersion, description)
			.then(function(data) {
				self.showDocumentAndHistory(path, nodeRef, onlyMajorVersions);
			});
    }
    
	/**
	 * FIXME: unused
	 * public method used to add a tag
	 * POST /alfresco/service/api/node/{store_type}/{store_id}/{id}/tags
	 */
    this.addTag = function(nodeRef, tag) {
       	try {
       		if (this.block) {
       			this.block();
       		}
    		return jQuery.ajax({
    			url: alfrescoUrl + "/alfresco/service/api/node/" + nodeRef + "/tags" ,
    			type: "POST",
    			data: tag,
    			contentType: false,
    			cache: false,
    			processData: false,
    			dataType:'json',
    			beforeSend: function (xhr){
    				xhr.setRequestHeader('Authorization', ticketHeader);
    			},
    			success: function (response, textStatus, jqXHR) {
    	       		if (self.unblock) {
    	       			self.unblock();
    	       		}
    			},
    			error: function (jqXHR, textStatus, errorThrown) {
    	       		if (self.unblock) {
    	       			self.unblock();
    	       		}
                }
    		});
    		
		} catch (err) {
			alert('Alfresco error adding tag ' + formId + ' ' + err);
		}
    }

	/**
	 * public remove
	 * delete a node by nodeRef
	 */
    this.remove = function(nodeRef, finishedCallBack) {
    	var nodeRefPath = nodeRef.replace("://", "/");
   		if (this.block) {
   			this.block();
   		}
		return jQuery.ajax({
			url: alfrescoUrl + '/alfresco/service/api/archive/' + nodeRefPath,
			type: "DELETE",
			beforeSend: function (xhr){
				xhr.setRequestHeader('Authorization', ticketHeader);
			},
			error: function (jqXHR, textStatus, errorThrown) {
				alert('Alfresco error deleting ' + nodeRef + ' ' + textStatus + ' ' + errorThrown);
			}
		}).then(function(response, textStatus, jqXHR) {
       		if (self.unblock) {
       			self.unblock();
       		}
			if (finishedCallBack) {
				finishedCallBack();
			}
		});
    }

    /**
     * public openReport
     * call phi rest method that will return report
     */
    this.openReport = function(nodeRef) {
    	window.open("./resource/rest/reports/" + nodeRef + "/PDF" + "?cid=" + cid,'_blank');
    }
    
    this.openDocument = function(nodeRef) {
    	if(nodeRef!=null && nodeRef!=""){
			window.open("./resource/rest/reports/" + nodeRef + "?cid=" + cid,'_blank');
		}else{
			
			window.open("./resource/rest/reports/" + nodeRefz.value.replace(":/","") + "?cid=" + cid,'_blank');
		}
		
    }
    
    /**
     * public editReport
     * open odfEditor
     */
    this.editReport = function(documentId) {
    	window.open("./common/odfEditor.html?documentId=" + documentId  + "&u=" + alfrescoUrl + "&t=" + this.getTicket() + "&cid=" + cid,'_blank');
    }

    
    /**
     * method to save/read document for webOdf editor
     */
    
	var alfrescoDocumentAction = new BaseAction('AlfrescoDocument');
    
    this.read = function (alfrescoDocumentId){

    	var loadedFilename = 'Empty.odt';

        if (alfrescoDocumentId) {

			alfrescoDocumentAction.equal['internalId'] = alfrescoDocumentId;
		
			// Using read instead of inject to avoid accidental conversation corruption with template instead of document
			return alfrescoDocumentAction.read().done(function(data) {
	    		if(data && data.length > 0) {
					alfrescoDocumentAction.entity = data[0];
		    		if(data[0].nodeRef) {
						loadedFilename = this.getDocumentUrl(data[0].nodeRef);
		   	    	}
	   	    	}
	    	}.bind(this)).then(function(){
				return loadedFilename;
			});
        } 
    }.bind(this);
    
    this.save = function(documentId, data, editor) {
    	
    	var chkBxMajorVersion = parent.document.getElementById("i:ChkBxMajorVersion")
        var majorVersion = false;
        if (chkBxMajorVersion) {
        	majorVersion = chkBxMajorVersion.checked;
        }

        if (alfrescoDocumentAction.entity.nodeRef != null) { //update
        	
        	return this.update(data, alfrescoDocumentAction.entity.nodeRef, majorVersion);
        } 
        else { //create

        	var folderInAlfresco = parent.folderInAlfresco;
        	var nameInAlfresco = parent.nameInAlfresco;
        	var extension = '.odt';
        	if (!(nameInAlfresco.indexOf(extension, nameInAlfresco.length - extension.length) !== -1)) { //ends with
        		nameInAlfresco = nameInAlfresco + extension;
        	}
	
        	return this.getOrCreateFolder(folderInAlfresco, folderInAlfresco, null).then(function(folderNodeRef) {
    			
    			return this.create(data, folderNodeRef, nameInAlfresco).then(function(nodeRef) {
	    					
	    			alfrescoDocumentAction.entity.nodeRef = nodeRef;
	    			return alfrescoDocumentAction.update().then(function() {
	    				editor.setDocumentModified(false);
						return alfrescoDocumentAction.entity.nodeRef;
	    			})
	    			.fail(function() {
	    				alert("Error creating AlfrescoDocument ");
	    			});
        		}.bind(this));
    		}.bind(this));
        	
        }
    	
    }.bind(this);
	
	var manageError = function(msg) {
		LOG.fatal(msg);
		if (self.unblock) {
			self.unblock();
		}
		//showError(new Date() + "\n" + msg);
		//alert(new Date() + "\n" + msg);
	}

}