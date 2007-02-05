FxDriver.prototype.type = function(element, text) {
	element.setAttribute("value", "");
	for (var i = 0; i < text.length; i++) {
		var character = text.charAt(i);
	
		this.keyDownOrUp(element, true, character);
		this.keyPress(element, character);
		this.keyDownOrUp(element, false, character);
	}
}

FxDriver.prototype.keyPress = function(element, text) {
	var event  = this.getDocument().createEvent ('KeyEvents'); 
	event.initKeyEvent('keypress', true, true, this.getBrowser().contentWindow, 0, 0, 0, 0, 0, text.charCodeAt(0) ); 
	element.dispatchEvent(event);
}

FxDriver.prototype.keyDownOrUp = function(element, down, text) {
	var keyCode = text;   // We should do something clever with non-text characters
	
	var event  = this.getDocument().createEvent ('KeyEvents'); 
	event.initKeyEvent(down ? 'keydown' : 'keyup', true, true, this.getBrowser().contentWindow, 0, 0, 0, 0, keyCode, 0 ); 
	element.dispatchEvent(event);
}