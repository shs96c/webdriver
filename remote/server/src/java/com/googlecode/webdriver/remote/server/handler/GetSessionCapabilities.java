package com.googlecode.webdriver.remote.server.handler;

import com.googlecode.webdriver.internal.OperatingSystem;
import com.googlecode.webdriver.remote.Capabilities;
import com.googlecode.webdriver.remote.server.DriverSessions;
import com.googlecode.webdriver.remote.server.Session;
import com.googlecode.webdriver.remote.server.rest.ResultType;

public class GetSessionCapabilities extends WebDriverHandler {
    public GetSessionCapabilities(DriverSessions sessions) {
        super(sessions);
    }

    public ResultType handle() {
		Session session = sessions.get(sessionId);
		return ResultType.SUCCESS;
	}
	
	public Capabilities getCapabilities() {
        // Hard code it for HtmlUnit for now
        return new ReadOnlyCapabilities("htmlunit", "", OperatingSystem.ANY, false);
	}

    public class ReadOnlyCapabilities implements Capabilities {
		private final String browser;
		private final String version;
		private final OperatingSystem os;
		private final boolean supportsJavascript;

		public ReadOnlyCapabilities(String browser, String version, OperatingSystem os, boolean supportsJavascript) {
			this.browser = browser;
			this.version = version;
			this.os = os;
			this.supportsJavascript = supportsJavascript;
		}

		public String getBrowserName() {
			return browser;
		}

		public OperatingSystem getOperatingSystem() {
			return os;
		}

		public String getVersion() {
			return version;
		}

		public boolean isJavascriptEnabled() {
			return supportsJavascript;
		}
	}
}
