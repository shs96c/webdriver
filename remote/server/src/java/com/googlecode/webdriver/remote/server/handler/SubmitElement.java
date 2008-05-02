package com.googlecode.webdriver.remote.server.handler;

import com.googlecode.webdriver.remote.server.rest.ResultType;
import com.googlecode.webdriver.remote.server.DriverSessions;

public class SubmitElement extends WebDriverHandler {
    private String elementId;

    public SubmitElement(DriverSessions sessions) {
        super(sessions);
    }

    public void setId(String elementId) {
        this.elementId = elementId;
    }


    public ResultType handle() throws Exception {
        getKnownElements().get(elementId).submit();
        
        return ResultType.SUCCESS;
    }
}
