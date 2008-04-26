package com.googlecode.webdriver.remote.server.handler;

import com.googlecode.webdriver.remote.server.rest.ResultType;
import com.googlecode.webdriver.remote.server.DriverSessions;

public class GetElementValue extends WebDriverHandler {
    private String elementId;
    private String value;

    public GetElementValue(DriverSessions sessions) {
        super(sessions);
    }

    public void setId(String elementId) {
        this.elementId = elementId;
    }

    public ResultType handle() throws Exception {
        value = getKnownElements().get(elementId).getValue();

        return ResultType.SUCCESS;
    }

    public String getValue() {
        return value;
    }
}
