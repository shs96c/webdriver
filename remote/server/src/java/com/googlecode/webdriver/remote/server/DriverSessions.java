package com.googlecode.webdriver.remote.server;

import com.googlecode.webdriver.WebDriver;
import com.googlecode.webdriver.remote.Capabilities;
import com.googlecode.webdriver.remote.SessionId;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DriverSessions {
	private static Map<SessionId, Session> sessionIdToDriver = new ConcurrentHashMap<SessionId, Session>();
	
	public SessionId newSession(Capabilities capabilities) throws Exception {
		SessionId sessionId = new SessionId(String.valueOf(System.currentTimeMillis()));

        WebDriver driver = createNewDriverMatching(capabilities);

        sessionIdToDriver.put(sessionId, new Session(driver));
		return sessionId;
	}

    private WebDriver createNewDriverMatching(Capabilities capabilities) throws Exception {
        String browser = capabilities.getBrowserName();
        if ("htmlunit".equals(browser)) {
            return (WebDriver) Class.forName("com.googlecode.webdriver.htmlunit.HtmlUnitDriver").newInstance();
        } else if ("firefox".equals(browser)) {
            return (WebDriver) Class.forName("com.googlecode.webdriver.firefox.FirefoxDriver").newInstance();
        }

        return null;
    }

    public Session get(SessionId sessionId) {
		return sessionIdToDriver.get(sessionId);
	}
}
