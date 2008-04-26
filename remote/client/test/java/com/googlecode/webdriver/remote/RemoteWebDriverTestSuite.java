package com.googlecode.webdriver.remote;

import com.googlecode.webdriver.TestSuiteBuilder;
import com.googlecode.webdriver.EnvironmentStarter;
import com.googlecode.webdriver.remote.server.DriverServlet;
import com.googlecode.webdriver.environment.webserver.AppServer;
import com.googlecode.webdriver.environment.webserver.Jetty6AppServer;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.extensions.TestSetup;

import java.io.File;

public class RemoteWebDriverTestSuite {
    public static Test suite() {
        Test rawSuite =
            new TestSuiteBuilder()
                    .addSourceDir("common")
//                    .addSourceDir("remote/client")
                    .usingDriver(RemoteWebDriverForTest.class)
                    .exclude("remote")
                    .create();


        TestSuite toReturn = new TestSuite();
        toReturn.addTest(new RemoteDriverServerStarter(rawSuite));
        return toReturn;
    }

    public static class RemoteWebDriverForTest extends RemoteWebDriver {
        public RemoteWebDriverForTest() throws Exception {
            super(DesiredCapabilities.htmlUnit());
        }
    }

    public static class RemoteDriverServerStarter extends TestSetup {
        private AppServer appServer;

        public RemoteDriverServerStarter(Test test) {
            super(test);
        }

        @Override
        protected void setUp() throws Exception {
            appServer = new Jetty6AppServer() {
                protected File findRootOfWebApp() {
                    File common = super.findRootOfWebApp();
                    return new File(common, "../../../remote/server/src/web");
                }
            };
            appServer.listenOn(7055);
            appServer.addServlet("remote webdriver", "/hub/*", DriverServlet.class);
            appServer.start();


            super.setUp();
        }

        @Override
        protected void tearDown() throws Exception {
            appServer.stop();

            super.tearDown();
        }
    }

}