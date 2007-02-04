function FxDriver(outputStream) {
	this.outputStream = outputStream;
	this.listener = new WebLoadingListener(this);
};

FxDriver.prototype.handleData = function(data) {
	var parts = Command.split(data);

    if (parts[0] in this) {
    	try {
        	this[parts[0]](parts[1]);	
    	} catch (e) {
    		dump(e + "\n");
    		this.write(parts[0] + " " + e);
    	}
    } else {
    	dump("Unrecognised command\n");
    }
};

FxDriver.prototype.get = function(url) {
	this.getBrowser().loadURI(url);
};

FxDriver.prototype.UrlOpening = function(request) {
//	this.write("Opening " + request.URI.spec);
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

FxDriver.prototype.selectElementUsingXPath = function(xpath) {
	var result = this.getDocument().evaluate(xpath, this.getDocument().documentElement, null, Components.interfaces.nsIDOMXPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;
	if (result) {
		var index = this.addToKnownElements(result);
		this.write("selectElementUsingXPath " + index);
	} else {
		this.write("selectElementUsingXPath ");
	}
}

FxDriver.prototype.selectElementsUsingXPath = function(xpath) {
	var result = this.getDocument().evaluate(xpath, this.getDocument().documentElement, null, Components.interfaces.nsIDOMXPathResult.ORDERED_NODE_ITERATOR_TYPE, null);
	var response = "";
	var element = result.iterateNext();
	while (element) {
		var index = this.addToKnownElements(element);
		response += index + ",";
		element = result.iterateNext();
	}
	response = response.substring(0, response.length - 1);  // Strip the trailing comma
	this.write("selectElementsUsingXPath " + response);
}

FxDriver.prototype.selectElementUsingLink = function(linkText) {
	var allLinks = this.getDocument().getElementsByTagName("A");
	var index;
	for (var i = 0; i < allLinks.length && !index; i++) {
		var text = this.getText(allLinks[i]);
		if (linkText == text) {
			index = this.addToKnownElements(allLinks[i]);
		}
	}
	if (index !== undefined) 
		this.write("selectElementUsingLink " + index);
	else
		this.write("selectElementUsingLink ");
}

FxDriver.prototype.selectText = function(xpath) {
	var result = this.getDocument().evaluate(xpath, this.getDocument().documentElement, null, Components.interfaces.nsIDOMXPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;
    if (result) {
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

FxDriver.prototype.getChildrenOfType = function(args) {
	var spaceIndex = args.indexOf(" ");
	var element = this.getElementAt(args.substring(0, spaceIndex));
	spaceIndex = args.indexOf(" ", spaceIndex);
	var tagName = args.substring(spaceIndex + 1);
	
	var children = element.getElementsByTagName(tagName);
	var response = "";
	for (var i = 0; i < children.length; i++) {
		response += this.addToKnownElements(children[i]) + ",";
	}
	response = response.substring(0, response.length - 1);  // Strip the trailing comma
	this.write("getChildrenOfType " + response);
}

FxDriver.prototype.getElementText = function(elementId) {
	var element = this.getElementAt(elementId);
	this.write("getElementText " + this.getText(element));
}

FxDriver.prototype.getElementValue = function(elementId) {
	var element = this.getElementAt(elementId);
	this.write("getElementValue " + element.getAttribute("value"));
}

FxDriver.prototype.setElementValue = function(args) {
	var spaceIndex = args.indexOf(" ");
	var element = this.getElementAt(args.substring(0, spaceIndex));
	spaceIndex = args.indexOf(" ", spaceIndex);
	var newValue = args.substring(spaceIndex + 1);
	
	element.setAttribute("value", newValue);
	this.write("setElementValue ");
}

FxDriver.prototype.click = function(position) {
	var index = position.indexOf(" ");
	index = position.substring(0, index);
    var element = this.getElementAt(index);
    if (!element) {
    	this.write("click ");
		return;
    }
    
    // Attach a listener so that we can wait until any page load this causes to complete
    var driver = this;
    var clickListener = {
    	QueryInterface : function(aIID) {
			if (aIID.equals(Components.interfaces.nsIWebProgressListener) || aIID.equals(Components.interfaces.nsISupportsWeakReference) || aIID.equals(Components.interfaces.nsISupports))
				return this;
			throw Components.results.NS_NOINTERFACE;
		},
    	onLocationChange : function() {},
    	onProgressChange : function() {},
    	onSecurityChange : function() {},
    	onStatusChange : function() {},
		onStateChange :  function(webProgress, request, stateFlags, message) {
			if (stateFlags & Components.interfaces.nsIWebProgressListener.STATE_STOP) {
				driver.clickFinished(this);
			}
		}
    };
    this.getBrowser().addProgressListener(clickListener)
    
    // Now do the click
    var driver = this;
	    try {
    		var button = element.QueryInterface(Components.interfaces.nsIDOMNSHTMLButtonElement);
	    	button.focus();
    		button.click();
	    } catch (e) {
    		// It's not a button. That's cool. We'll just send the appropriate mouse event
        	var event = driver.getDocument().createEvent("MouseEvents");
	        event.initMouseEvent('click', true, true, null, 1, 0, 0, 0, 0, false, false, false, false, 0, null);
    	    element.dispatchEvent(event);
	    }

	var checkForLoad = function() {
		// Returning should be handled by the click listener, unless we're not actually loading something. Do a check and return if we are 
		// There's a race condition here, in that the click event and load may have finished before we get here. For now, let's pretend that
		// doesn't happen. The other race condition is that we make this check before the load has begun. With all the javascript out there,
		// this might actually be a bit of a problem.
		//    if (!this.getBrowser().webProgress.isLoadingDocument) {
		var docLoaderService = Components.classes["@mozilla.org/docloaderservice;1"].getService(Components.interfaces.nsIWebProgress);
		if (!docLoaderService.isLoadingDocument) {
			driver.getBrowser().removeProgressListener(clickListener);
			driver.write("click ");
		}
	}
	    		
    this.getBrowser().contentWindow.setTimeout(checkForLoad, 25);
};

FxDriver.prototype.clickFinished = function(listener) {
	this.getBrowser().removeProgressListener(listener);
	this.write("click ");
}

FxDriver.prototype.addToKnownElements = function(element) {
	if (!this.getDocument().fxdriver_elements) {
		this.getDocument().fxdriver_elements = new Array();
	}
	var start = this.getDocument().fxdriver_elements.length;
	this.getDocument().fxdriver_elements.push(element);
	return start;
}

FxDriver.prototype.getElementAt = function(index) {
	index = index - 0; // Convert to a number if we're dealing with a string....
	if (this.getDocument().fxdriver_elements)
		return this.getDocument().fxdriver_elements[index];
	return undefined;
}

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
//	dump("++++ " + output);
    this.outputStream.write(output, output.length);
    this.outputStream.flush();
};