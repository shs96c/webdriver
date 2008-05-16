package com.googlecode.webdriver.firefox;

import com.googlecode.webdriver.TestSuiteBuilder;

import junit.framework.Test;
import junit.framework.TestCase;

public class FirefoxDriverTestSuite extends TestCase {
	public static Test suite() {
		return new TestSuiteBuilder()
					.addSourceDir("firefox")
					.addSourceDir("common")
					.usingDriver(FirefoxDriver.class)
					.exclude("firefox")
					.keepDriverInstance()
					.includeJavascriptTests()
					.create();
	}
}
