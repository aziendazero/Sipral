var selectedEntities = [];
var updatableEntities = false;


function removeItemAll(arr, value) {
	var i = 0;
	while (i < arr.length) {
		if (arr[i] === value) {
			arr.splice(i, 1);
		} else {
			++i;
		}
	}
	return arr;
}

function isAnyEntitySelected(){
	var ret = false;
	if(selectedEntities.length > 0){
		ret = true;
	}
	return ret;
}

function manageEntityList(event, checked, id){

	if(checked){
		selectedEntities.push(id);
	}else{
		selectedEntities = removeItemAll(selectedEntities, id)
	}
	var pippo = 'pippo';
	stopPropagation(event);
}

function manageEntityListStateCookie(event, element, id){

	var replacedId = element.id.replace(/:/g,'-');
	var checked = element.checked;
	if(checked){
		selectedEntities.push(id);
		document.cookie = replacedId + '=true';
	}else{
		selectedEntities = removeItemAll(selectedEntities, id);
		document.cookie = replacedId + '=; expires=Thu, 01 Jan 1970 00:00:00 UTC;';
	}
	stopPropagation(event);
}

function sendEntityList(entityName, list){
	//jQuery('#loader').show();
	updatableEntities = true;
	//entityName = 'ImpiantiDocument';
	var entityUrl = entityName.toLowerCase() + '/';
	var postUrl = BaseAction.buildUrl('impiantidocuments/' + entityUrl + 'list')
	return jQuery.ajax({
		type: 'POST', 
		contentType : 'application/json',
		data: JSON.stringify(list),
		url: postUrl,
		success: function(response, textStatus, jqXHR) {
			//self.entity = response.entity;
		},
		error: function(jqXHR, textStatus, errorThrown) {
			alert('Error copying list ' + ' ' + textStatus + ' ' + errorThrown);
		}
	});
}

function updateList(serverList){
	//console.log(event);
	if(serverList==undefined || serverList.length==0){
		selectedEntities = [];
	}
	
	if(updatableEntities===true){
		selectedEntities = serverList;
		updatableEntities = false;
	}

//	var pageCookies = document.cookie;
//	var target = jQuery('td input[type=checkbox]');
//	target.each(function(){
//		var replacedId = jQuery(this).attr('id').replace(/:/g,'-');
//		pageCookies = pageCookies.replace(/\s/g,'-');
//		var isSpecific = pageCookies.indexOf(replacedId);
//
//		if(isSpecific != -1){
//			jQuery(this).prop('checked',true);
//		}
//	});
}

function cleanCookies(){
    var cookies = document.cookie.split(";");
	
    for (var i = 0; i < cookies.length; i++) {
        var cookie = cookies[i].trim();
        var eqPos = cookie.indexOf("=");
        var name = eqPos > -1 ? cookie.substr(0, eqPos) : cookie;
		if(name.startsWith('i-')){
		       document.cookie = name + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT";
		}
    }

}
