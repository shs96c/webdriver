package com.googlecode.webdriver.remote.server.handler;

import com.googlecode.webdriver.remote.server.DriverSessions;
import com.googlecode.webdriver.remote.server.rest.ResultType;

public class ClearElement extends WebDriverHandler {
    private String elementId;

    public ClearElement(DriverSessions sessions) {
        super(sessions);
    }

    public void setId(String elementId) {
        this.elementId = elementId;
    }


    public ResultType handle() throws Exception {
        getKnownElements().get(elementId).clear();

        return ResultType.SUCCESS;
    }
}