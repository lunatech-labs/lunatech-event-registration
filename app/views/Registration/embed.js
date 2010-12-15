jQuery.fn.extend({
/**
* Returns get parameters.
*
* If the desired param does not exist, null will be returned
*
* To get the document params:
* @example value = jQuery(document).getUrlParam("paramName");
* 
* To get the params of a html-attribut (uses src attribute)
* @example value = jQuery('#imgLink').getUrlParam("paramName");
*/ 
 getUrlParam: function(strParamName){
	  strParamName = escape(unescape(strParamName));
	  
	  var returnVal = new Array();
	  var qString = null;
	  
	  if (jQuery(this).attr("nodeName")=="#document") {
	  	//document-handler
		
		if (window.location.search.search(strParamName) > -1 ){
			
			qString = window.location.search.substr(1,window.location.search.length).split("&");
		}
			
	  } else if (jQuery(this).attr("src")!="undefined") {
	  	
	  	var strHref = jQuery(this).attr("src")
	  	if ( strHref.indexOf("?") > -1 ){
	    	var strQueryString = strHref.substr(strHref.indexOf("?")+1);
	  		qString = strQueryString.split("&");
	  	}
	  } else if (jQuery(this).attr("href")!="undefined") {
	  	
	  	var strHref = jQuery(this).attr("href")
	  	if ( strHref.indexOf("?") > -1 ){
	    	var strQueryString = strHref.substr(strHref.indexOf("?")+1);
	  		qString = strQueryString.split("&");
	  	}
	  } else {
	  	return null;
	  }
	  	
	  
	  if (qString==null) return null;
	  
	  
	  for (var i=0;i<qString.length; i++){
			if (escape(unescape(qString[i].split("=")[0])) == strParamName){
				returnVal.push(qString[i].split("=")[1]);
			}
			
	  }
	  
	  
	  if (returnVal.length==0) return null;
	  else if (returnVal.length==1) return returnVal[0];
	  else return returnVal;
	}
	});
	
var registerURL = #{jsAction @@Registration.register(':community', ':eventId', ':embed', ':returnURL') /};
var confirmURL = #{jsAction @@Registration.confirm(':community', ':eventId', ':emailAddress', ':confirmationCode', ':embed') /};
var eventName = "${event.id}";
var communityName = "${community}";
jQuery(document).ready(function() {
	if (jQuery(document).getUrlParam("confirmationCode") != null) {
		jQuery("#"+ eventName).attr('src', confirmURL({community: communityName,
														eventId: eventName,
														embed: true,
														confirmationCode: jQuery(document).getUrlParam("confirmationCode"),
														emailAddress: jQuery(document).getUrlParam("emailAddress")}));
	} else { 
		jQuery("#"+ eventName).attr('src', registerURL({community: communityName,
			eventId: eventName,
			embed: true,
			returnURL: escape(location.href)}));
	}
});
