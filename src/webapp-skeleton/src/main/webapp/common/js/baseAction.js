/**
 * Base Action. Javascript implementation.
 * Manages Read, Create, Update, Delete and inject
 */

function BaseAction(entityName) {
    "use strict";
    
    var self = this;

    var JSONCONTENTTYPE = 'application/json';

    this.entity = null;
    this.list = null;
    
	//var entityName;
	//var entityClass;
	var entityUrl = entityName.toLowerCase() + 's/';
	
	//Read restrictions
	
	this.select = [];
	this.equal = {};
	this.greaterEqual = {};
	this.greater = {};
	this.lessEqual = {};
	this.like = {};
	this.less = {};
	this.notEqual = {};
	this.isNull = {};
	this.orderBy = {};

	/**
	 * public inject method
	 * Get an entity by id, and inject same entity in conversation
	 */
	this.inject = function(id, loads) {
	    var loadsRestrictions = '';
        if (loads) {
          for (let i = 0; i < loads.length; i++) {
            loadsRestrictions += ';load=' + loads[i];
          }
        }
		return jQuery.ajax({
			type: 'GET', 
			contentType : JSONCONTENTTYPE,
			url: BaseAction.buildUrl(entityUrl + id + loadsRestrictions),
			success: function(response, textStatus, jqXHR) {
				self.entity = response.entity;
			},
			error: function(jqXHR, textStatus, errorThrown) {
				alert('Error injecting ' + id + ' ' + textStatus + ' ' + errorThrown);
			}
		}).then(function(response, textStatus, jqXHR) {
			return response.entity;
		});
	}
	
	
	/**
	 * public inject new Entity in server side conversation
	 * data passed are optional 
	 * (if null or empty new clean entity is injected)
	 *  -- not strictly REST method --
	 */
	this.injectNew = function(data) {
		return jQuery.ajax({
			type: 'POST',
			contentType : JSONCONTENTTYPE,
			url: BaseAction.buildUrl(entityUrl + 'injectnew'),
			data: data,
			success: function(response, textStatus, jqXHR) {
				
			},
			error: function(jqXHR, textStatus, errorThrown) {
				alert('Error injectingNew ' + data + ' ' + textStatus + ' ' + errorThrown);
			}
		}).then(function(response, textStatus, jqXHR) {
			return response.entity;
		});
	}
	
	/**
	 * public eject method
	 * Eject entity
	 */
	this.eject = function() {
		return jQuery.ajax({
			type: 'GET', 
			contentType : JSONCONTENTTYPE,
			url: BaseAction.buildUrl(entityUrl + 0),
			success: function(response, textStatus, jqXHR) {
				self.entity = null;
			},
			error: function(jqXHR, textStatus, errorThrown) {
				alert('Error ejecting ' + textStatus + ' ' + errorThrown);
			}
		});
	}
	
	/**
	 * public read method
	 * Execute criteria query and return a list of results.
	 */
	this.read = function() {
		var matrixParam = ""; //example: ";name.fam;name.giv;birthTime;name.fam=ad;name.giv=ad"
		for (var z=0;z<this.select.length;z++) {
			matrixParam += this.select[z] + ";";
		}

		for (var key in this.equal) {
			if (this.equal.hasOwnProperty(key)) {
				var value = this.equal[key];
				if (value != null && value != "") {
					matrixParam += key + "=" + value + ";"; //URLEncoder.encode!!!!!!
				}
			}
		}
		for (var key in this.greater) {
		  if (this.greater.hasOwnProperty(key)) {
			var value = this.greater[key];
			if (value !== null && value !== '') {
			  matrixParam += key + '=>>' + value + ";"; //URLEncoder.encode!!!!!!
			}
		  }
		}
		for (var key in this.greaterEqual) {
		  if (this.greaterEqual.hasOwnProperty(key)) {
			var value = this.greaterEqual[key];
			if (value !== null && value !== '') {
			  matrixParam += key + '=>' + value + ";"; //URLEncoder.encode!!!!!!
			}
		  }
		}
		for (var key in this.less) {
		  if (this.less.hasOwnProperty(key)) {
			var value = this.less[key];
			if (value !== null && value !== '') {
			  matrixParam += key + '=<<' + value + ";"; //URLEncoder.encode!!!!!!
			}
		  }
		}
		for (var key in this.lessEqual) {
		  if (this.lessEqual.hasOwnProperty(key)) {
			var value = this.lessEqual[key];
			if (value !== null && value !== '') {
			  matrixParam += key + '=<' + value + ";"; //URLEncoder.encode!!!!!!
			}
		  }
		}
		
		for (var key in this.like) {
			if (this.like.hasOwnProperty(key)) {
				var value = this.like[key];
				if (value != null && value != "") {
					matrixParam += key + "=~" + value + ";";
				}
			}
		}
		
		for (var key in this.orderBy) {
			if (this.orderBy.hasOwnProperty(key)) {
				var value = this.orderBy[key];
				if (value != null && value != "") {
					matrixParam += key + "=" + value + ";";
				}
			}
		}
		
		return jQuery.ajax({
			type: 'GET', 
			contentType : JSONCONTENTTYPE,
			url: BaseAction.buildUrl(entityUrl + matrixParam + "/1"),
			success: function(response, textStatus, jqXHR) {
				self.list = response.entities;
			},
			error: function(jqXHR, textStatus, errorThrown) {
				alert('Error reading ' + matrixParam + ' ' + textStatus + ' ' + errorThrown);
			}
		}).then(function(response, textStatus, jqXHR) {
			return response.entities;
		});
	}
	
	/**
	 * public create method
	 * Create a new entity
	 */
	this.create = function() {
		return jQuery.ajax({
			type: 'POST', 
			contentType : JSONCONTENTTYPE,
			url: BaseAction.buildUrl(entityUrl),
			data: self.entity,
			success: function(response, textStatus, jqXHR) {
				self.entity.internalId = response;
			},
			error: function(jqXHR, textStatus, errorThrown) {
				alert('Error creating ' + self.entity + ' ' + textStatus + ' ' + errorThrown);
			}
		});
	}
	
	/**
	 * public update method
	 * Update an entity
	 */
	this.update = function() {
		
		var body = JSON.stringify(this.entity, dateReplacer);
		
		return jQuery.ajax({
			type: 'PUT', 
			contentType : JSONCONTENTTYPE,
			dataType: 'json',
			url: BaseAction.buildUrl(entityUrl),
			data: body,
			success: function(response, textStatus, jqXHR) {
				self.entity = response;
			},
			error: function(jqXHR, textStatus, errorThrown) {
				alert('Error updating ' + self.entity + ' ' + textStatus + ' ' + errorThrown);
			}
		});
	}
	
	/**
	 * public erase method
	 * Delete an entity
	 */
	this.erase = function() {
		return jQuery.ajax({
			type: 'DELETE', 
			contentType : JSONCONTENTTYPE,
			url: BaseAction.buildUrl(entityUrl),
			data: self.entity,
			success: function(response, textStatus, jqXHR) {
				self.entity = null;
			},
			error: function(jqXHR, textStatus, errorThrown) {
				alert('Error deleting ' + self.entity + ' ' + textStatus + ' ' + errorThrown);
			}
		});
	}
	
	/**
	 * Clean read restriction, to execute a new read
	 */	
	this.cleanRestrictions = function() {
		select = [];
		equal = {};
		greaterEqual = {};
		greater = {};
		lessEqual = {};
		like = {};
		less = {};
		notEqual = {};
		isNull = {};
		orderBy = {};
	}
	
	
}

/**
* static restBaseUrl
*/
BaseAction.restBaseUrl = document.URL.substring(0, document.URL.lastIndexOf('/')+1) + 'resource/rest/';

/**
* static buildUrl method
* adds cid and timestamp
*/
BaseAction.buildUrl = function(url) {
   
   if (url.indexOf("?") >= 0) {
	   url += "&";
   } else {
	   url += "?";
   }
   
   url = BaseAction.restBaseUrl + url + "cid=" + cid;			
   return url;
}