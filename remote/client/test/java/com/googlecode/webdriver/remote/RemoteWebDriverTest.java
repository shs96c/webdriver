package com.googlecode.webdriver.remote;

import com.googlecode.webdriver.environment.webserver.AppServer;
import com.googlecode.webdriver.environment.webserver.Jetty6AppServer;
import com.googlecode.webdriver.remote.server.DriverServlet;
import junit.framework.TestCase;

import java.io.File;

public class RemoteWebDriverTest extends TestCase {
	public void testShouldBeAbleToCreateANewSession() throws Exception {
        AppServer servletServer = new Jetty6AppServer() {
            protected File findRootOfWebApp() {
                File common = super.findRootOfWebApp();
                return new File(common, "../../../remote/server/src/web");
            }
        };
        servletServer.listenOn(7055);
        servletServer.addServlet("remote webdriver", "/hub/*", DriverServlet.class);
        servletServer.start();

        Jetty6AppServer mainServer = new Jetty6AppServer();
        mainServer.listenOn(3000);
        mainServer.start();

        RemoteWebDriver driver = new RemoteWebDriver(DesiredCapabilities.htmlUnit());
        driver.get("http://localhost:3000/xhtmlTest.html");
        System.out.println("title = " + driver.getTitle());
        System.out.println("url = " + driver.getCurrentUrl());
    }
}
