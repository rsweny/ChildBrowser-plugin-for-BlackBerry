(function () {

	var API_URL = "blackberry/polarmobile/childbrowser";
	var URL_PARAM_NAME = "url";
	var JS_EVENT_HANDLER_ID = "jsEventHandlerId";
	
	var URL_PARAM_NAME2 = "url";
	var SOURCE_FILE = "source";
	var PARAMS = "params";
	var JS_EVENT_HANDLER_ID2 = "jsEventHandlerId";
	
	function ChildBrowser() {
	};

	function loadURL(url,jsEventHandler){
		var jsEventHandlerId = blackberry.events.registerEventHandler("jsEventHandler", jsEventHandler);

		var remoteCall = new blackberry.transport.RemoteFunctionCall(API_URL + "/loadURL");
		remoteCall.addParam(URL_PARAM_NAME, url); 
		remoteCall.addParam(JS_EVENT_HANDLER_ID, jsEventHandlerId); 
		return remoteCall.makeSyncCall();
	}
	
	function uploadFile(sourceFile,url,jsEventHandler2,params){
		var jsEventHandlerId2 = blackberry.events.registerEventHandler("jsEventHandler2", jsEventHandler2);
		var remoteCall = new blackberry.transport.RemoteFunctionCall(API_URL + "/uploadFile");
		remoteCall.addParam(SOURCE_FILE, sourceFile);
		remoteCall.addParam(URL_PARAM_NAME2, url);
		remoteCall.addParam(JS_EVENT_HANDLER_ID2, jsEventHandlerId2); 
		remoteCall.addParam(PARAMS, params);
		return remoteCall.makeSyncCall();
	}

	function clearCookies(){
		var remoteCall = new blackberry.transport.RemoteFunctionCall(API_URL + "/clearCookies");
		return remoteCall.makeSyncCall();
	}

	function getLocation(){
		var remoteCall = new blackberry.transport.RemoteFunctionCall(API_URL + "/getLocation");
		return remoteCall.makeSyncCall();
	}

	function getVisible(){
		var remoteCall = new blackberry.transport.RemoteFunctionCall(API_URL + "/getVisible");
		return remoteCall.makeSyncCall();
	}

	function back(){
		var remoteCall = new blackberry.transport.RemoteFunctionCall(API_URL + "/back");
		return remoteCall.makeSyncCall();
	}

	function forward(){
		var remoteCall = new blackberry.transport.RemoteFunctionCall(API_URL + "/forward");
		return remoteCall.makeSyncCall();
	}

	function refresh(){
		var remoteCall = new blackberry.transport.RemoteFunctionCall(API_URL + "/refresh");
		return remoteCall.makeSyncCall();
	}

	function close(){
		var remoteCall = new blackberry.transport.RemoteFunctionCall(API_URL + "/close");
		return remoteCall.makeSyncCall();
	}
	
	ChildBrowser.prototype.loadURL = function(url,jsEventHandler) {
		return loadURL(url,jsEventHandler);
	};
	
	ChildBrowser.prototype.uploadFile = function(source,url,jsEventHandler,params) {
		return uploadFile(source,url,jsEventHandler,params);
	};

	ChildBrowser.prototype.clearCookies = function() {
		return clearCookies();
	};

	ChildBrowser.prototype.getLocation = function() {
		return getLocation();
	};

	ChildBrowser.prototype.getVisible = function() {
		return getVisible();
	};

	ChildBrowser.prototype.back = function() {
		return back();
	};

	ChildBrowser.prototype.forward = function() {
		return forward();
	};

	ChildBrowser.prototype.refresh = function() {
		return refresh();
	};

	ChildBrowser.prototype.close = function() {
		return close();
	};


	blackberry.Loader.javascriptLoaded("blackberry.polarmobile.childbrowser", ChildBrowser);
})();
