package com.googlecode.webdriver.htmlunit;

import com.googlecode.webdriver.TestSuiteBuilder;

import junit.framework.Test;

public class HtmlUnitDriverTestSuite {
	public static Test suite() throws Exception {
		return new TestSuiteBuilder()
					.addSourceDir("common")
					.addSourceDir("htmlunit")
					.usingDriver(HtmlUnitDriver.class)
					.exclude("htmlunit")
					.create();
	}
}
