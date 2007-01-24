function FxDriver(outputStream) {
	this.outputStream = outputStream;
	this.listener = new WebLoadingListener(this);
}

FxDriver.prototype.handleData = function(data) {
	var parts = Command.split(data);

    if (parts[0] in this) {
        this[parts[0]](parts[1]);	
    } else {
    	dump("Unrecognised command\n");
    }
}

FxDriver.prototype.get = function(url) {
	this.getBrowser().loadURI(url);
}

FxDriver.prototype.UrlOpening = function(request) {
	this.write("Opening " + request.URI.spec);
}

FxDriver.prototype.UrlOpened = function(request ) {
	var responseText = request.originalURI ? request.originalURI.spec : request.name;

	try {
		var channel = request.QueryInterface(Components.interfaces.nsIHttpChannel);	
		responseText += " " + channel.responseStatus + " " + channel.responseStatusText;
	} catch (e) {
		responseText += " undefined undefined";
	}
	
	try {
		request.QueryInterface(Components.interfaces.nsIXMLHttpRequest)
		dump("Is XmlHttpRequest\n");
	} catch (e) {
		// Do nothing
	}
	this.write("Opened " + responseText);
}

FxDriver.prototype.title = function() {
	this.write(this.getBrowser().contentTitle);
}

FxDriver.prototype.getBrowser = function()
{
    var wm = Components.classes["@mozilla.org/appshell/window-mediator;1"].getService(Components.interfaces.nsIWindowMediator);
    var win = wm.getMostRecentWindow("navigator:browser");
    return win.getBrowser();
};

FxDriver.prototype.write = function(text) {
    var output = text + "\n";
//	dump(output);
    this.outputStream.write(output, output.length);
    this.outputStream.flush();
};