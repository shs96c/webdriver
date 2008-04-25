package com.googlecode.webdriver.remote.server.handler;

import com.googlecode.webdriver.remote.Capabilities;
import com.googlecode.webdriver.remote.DesiredCapabilities;
import com.googlecode.webdriver.remote.JsonToBeanConverter;
import com.googlecode.webdriver.remote.SessionId;
import com.googlecode.webdriver.remote.server.DriverSessions;
import com.googlecode.webdriver.remote.server.JsonParametersAware;
import com.googlecode.webdriver.remote.server.rest.Handler;
import com.googlecode.webdriver.remote.server.rest.ResultType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class NewSession implements Handler, JsonParametersAware {
    private DriverSessions allSession;
    private Capabilities desiredCapabilities;
    private SessionId sessionId;

    public NewSession(DriverSessions allSession) {
        this.allSession = allSession;
    }

    public void setJsonParameters(JSONArray allParameters) throws Exception {
        desiredCapabilities = new JsonToBeanConverter().convertBean(DesiredCapabilities.class, (JSONObject) allParameters.get(0));
    }
    
    public ResultType handle() throws Exception {
        sessionId = allSession.newSession(desiredCapabilities);
        return ResultType.SUCCESS;
	}
	
	public String getSessionId() {
		return sessionId.toString();
	}
	
	public String getContext() {
		return "context";
	}
}
