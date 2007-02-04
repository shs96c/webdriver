package com.thoughtworks.webdriver.firefox;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.webdriver.WebElement;

public class FirefoxWebElement implements WebElement {
	private final String elementId;
	private final ExtensionListener listener;

	public FirefoxWebElement(ExtensionListener listener, String elementId) {
		this.listener = listener;
		this.elementId = elementId;
	}

	public void click() {
		listener.writeAndWaitForResponse("click", elementId);
	}

	public String getAttribute(String name) {
		return null;
	}

	public List getChildrenOfType(String tagName) {
		String childIds = listener.writeAndWaitForResponse("getChildrenOfType", elementId + " " + tagName);
		String[] ids = childIds.split(",");
		List children = new ArrayList();
		for (int i = 0; i < ids.length; i++) 
			children.add(new FirefoxWebElement(listener, ids[i]));
		return children;
	}

	public String getText() {
		return listener.writeAndWaitForResponse("getElementText", elementId);
	}

	public String getValue() {
		return listener.writeAndWaitForResponse("getElementValue", elementId);
	}

	public void setValue(String value) {
		listener.writeAndWaitForResponse("setElementValue", elementId + " " + value);
	}
	
	public boolean isEnabled() {
		return false;
	}

	public boolean isSelected() {
		return false;
	}

	public void setSelected() {
	}

	public void submit() {
	}

	public boolean toggle() {
		return false;
	}	
}
