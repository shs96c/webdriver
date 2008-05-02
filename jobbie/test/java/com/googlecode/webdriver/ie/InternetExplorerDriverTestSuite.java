package com.googlecode.webdriver.ie;

import junit.framework.Test;
import com.googlecode.webdriver.TestSuiteBuilder;

public class InternetExplorerDriverTestSuite {
	public static Test suite() throws Exception {
		return new TestSuiteBuilder()
					.addSourceDir("common")
					.addSourceDir("jobbie")
					.usingDriver(InternetExplorerDriver.class)
					.exclude("ie")
					.keepDriverInstance()
					.create();
	}
}
