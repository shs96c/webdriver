package org.openqa.selenium.chrome;

import org.openqa.selenium.TestSuiteBuilder;

import junit.framework.Test;
import junit.framework.TestCase;

public class ChromeDriverTestSuite extends TestCase {
	public static Test suite() throws Exception {
		return new TestSuiteBuilder()
					.addSourceDir("chrome")
					.addSourceDir("common")
					.usingDriver(ChromeDriver.class)
					.exclude("chrome")
					.create();
	}
}
