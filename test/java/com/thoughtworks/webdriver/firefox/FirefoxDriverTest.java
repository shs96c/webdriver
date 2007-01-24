package com.thoughtworks.webdriver.firefox;

import com.thoughtworks.webdriver.JavascriptEnabledDriverTest;
import com.thoughtworks.webdriver.WebDriver;

public class FirefoxDriverTest extends JavascriptEnabledDriverTest {
	@Override
	protected boolean isUsingSameDriverInstance() {
		return true;
	}
	
	protected WebDriver getDriver() {
		return new FirefoxDriver();
	}
}
