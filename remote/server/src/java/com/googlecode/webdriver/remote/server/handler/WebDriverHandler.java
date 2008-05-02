package com.googlecode.webdriver.remote.server.handler;

import com.googlecode.webdriver.WebDriver;
import com.googlecode.webdriver.remote.Context;
import com.googlecode.webdriver.remote.SessionId;
import com.googlecode.webdriver.remote.Response;
import com.googlecode.webdriver.remote.server.DriverSessions;
import com.googlecode.webdriver.remote.server.Session;
import com.googlecode.webdriver.remote.server.KnownElements;
import com.googlecode.webdriver.remote.server.rest.Handler;

public abstract class WebDriverHandler implements Handler {
    protected final DriverSessions sessions;
    protected SessionId sessionId;
    private Context context;

    public WebDriverHandler(DriverSessions sessions) {
        this.sessions = sessions;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = new SessionId(sessionId);
    }

    public void setContext(String context) {
        this.context = new Context(context);
    }

    public String getSessionId() {
        return sessionId.toString();
    }

    public String getContext() {
        return context.toString();
    }

    protected WebDriver getDriver() {
        Session session = sessions.get(sessionId);
        return  session.getDriver(context);
    }

    protected KnownElements getKnownElements() {
        return sessions.get(sessionId).getKnownElements();
    }

    protected Response newResponse() {
        return new Response(sessionId, context);
    }
}
