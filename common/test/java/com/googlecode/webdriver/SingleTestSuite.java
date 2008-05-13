package com.googlecode.webdriver;

import junit.framework.Test;

public class SingleTestSuite {
    private final static String FIREFOX = "com.googlecode.webdriver.firefox.FirefoxDriver";
    private final static String HTML_UNIT = "com.googlecode.webdriver.htmlunit.HtmlUnitDriver";
    private final static String IE = "com.googlecode.webdriver.ie.InternetExplorerDriver";
    private final static String REMOTE = "com.googlecode.webdriver.remote.RemoteWebDriverTestSuite$RemoteWebDriverForTest";
    private final static String SAFARI = "com.googlecode.webdriver.safari.SafariDriver";

    public static Test suite() throws Exception {
        String driver = REMOTE;

        TestSuiteBuilder builder = new TestSuiteBuilder()
              .addSourceDir("common")
//              .addSourceDir("firefox")
              .usingDriver(driver)
              .keepDriverInstance()
              .includeJavascriptTests()
              .onlyRun("PageLoadingTest")
//              .method("testShouldBeAbleToFlipToAFrameIdentifiedByItsId")
//                        .leaveRunningAfterTest();
            ;  // Yeah, this look strange :)

        if (REMOTE.equals(driver)) {
            builder.addSuiteDecorator("com.googlecode.webdriver.remote.RemoteWebDriverTestSuite$RemoteDriverServerStarter");
        }

        return builder.create();
    }
}
