package com.thoughtworks.webdriver.firefox;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.webdriver.NoSuchElementException;
import com.thoughtworks.webdriver.WebDriver;
import com.thoughtworks.webdriver.WebElement;

public class FirefoxDriver implements WebDriver {
	private ExtensionListener listener;
	
	public FirefoxDriver() {
		listener = new ExtensionListener("localhost", 7055);
	}
	
	public void close() {
	}

	public void dumpBody() {
	}

	public void evaluateJavascript(String javascript) {
	}

	public void get(String url) {
		listener.writeAndWaitForResponse("get", url);
	}

	public String getTitle() {
		return listener.writeAndWaitForResponse("title", null);
	}

	public boolean getVisible() {
		return true;
	}

	public WebElement selectElement(String selector) {
		if (selector.startsWith("link=")) {
			return selectElementUsingLink(selector.substring("link=".length()));
		} else {
			return selectElementUsingXPath(selector);
		}
	}
	
	private WebElement selectElementUsingLink(String linkText) {
		String elementId = listener.writeAndWaitForResponse("selectElementUsingLink", linkText);
		if (elementId == null) {
			throw new NoSuchElementException("Unable to find " + linkText);
		}
		
		return new FirefoxWebElement(listener, elementId);
	}

	private WebElement selectElementUsingXPath(String xpath) {		
		String elementId = listener.writeAndWaitForResponse("selectElementUsingXPath", xpath);
		if (elementId == null) {
			throw new NoSuchElementException("Unable to find " + xpath);
		}
		
		return new FirefoxWebElement(listener, elementId);
	}

	public List selectElements(String xpath) {
		String returnedIds = listener.writeAndWaitForResponse("selectElementsUsingXPath", xpath);
		String[] ids = returnedIds.split(",");
		List elements = new ArrayList();
		for (int i = 0; i < ids.length; i++) {
			elements.add(new FirefoxWebElement(listener, ids[i]));
		}
		return elements;
	}

	public String selectText(String xpath) {
		return listener.writeAndWaitForResponse("selectText", xpath);
	}

	public void setVisible(boolean visible) {
		// This does nothing
	}
}
