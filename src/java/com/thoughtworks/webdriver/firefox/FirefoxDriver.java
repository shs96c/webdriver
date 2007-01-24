package com.thoughtworks.webdriver.firefox;

import java.util.List;

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
		return null;
	}

	public List selectElements(String xpath) {
		return null;
	}

	public String selectText(String xpath) {
		return listener.writeAndWaitForResponse("selectText", xpath);
	}

	public void setVisible(boolean visible) {
		// This does nothing
	}
}
