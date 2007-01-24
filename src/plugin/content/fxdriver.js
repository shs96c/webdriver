function FxDriver(outputStream) {
	this.outputStream = outputStream;
	this.listener = new WebLoadingListener(this);
};

FxDriver.prototype.handleData = function(data) {
	var parts = Command.split(data);

    if (parts[0] in this) {
        this[parts[0]](parts[1]);	
    } else {
    	dump("Unrecognised command\n");
    }
};

FxDriver.prototype.get = function(url) {
	this.getBrowser().loadURI(url);
};

FxDriver.prototype.UrlOpening = function(request) {
	this.write("Opening " + request.URI.spec);
};

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
	this.write("get " + responseText);
};

FxDriver.prototype.title = function() {
	this.write("title " + this.getBrowser().contentTitle);
};

FxDriver.prototype.selectText = function(xpath) {
	var result = this.getDocument().evaluate(xpath, this.getDocument().documentElement, null, Components.interfaces.nsIDOMXPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;
    if (result != null) {
        // Handle Title elements slightly differently. On the plus side, IE does this too :)
        //             result.QueryInterface(Components.interfaces.nsIDOMHTMLTitleElement);            
        if (result.tagName == "TITLE") {
            this.write("selectText " + this.getBrowser().contentTitle);
        } else {
            this.write("selectText " + this.getText(result));
        }
    } else {
    	this.write("selectText ");
    }
};

FxDriver.prototype.getText = function(element) {
    var nodes = element.childNodes;
    var str = ""
    for (var i = 0; i < nodes.length; i++) {
        if (nodes[i].nodeName == "#text") {
            str += nodes[i].nodeValue;
        }
    }
    return str;
};

FxDriver.prototype.getDocument = function() {
	return getBrowser().contentDocument;
};

FxDriver.prototype.getBrowser = function() {
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