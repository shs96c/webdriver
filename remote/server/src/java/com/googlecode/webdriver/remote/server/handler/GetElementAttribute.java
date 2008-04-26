package com.googlecode.webdriver.remote.server.handler;

import com.googlecode.webdriver.remote.server.DriverSessions;
import com.googlecode.webdriver.remote.server.rest.ResultType;

public class GetElementAttribute extends WebDriverHandler {
    private String elementId;
    private String name;
    private String value;

    public GetElementAttribute(DriverSessions sessions) {
        super(sessions);
    }

    public void setId(String elementId) {
        this.elementId = elementId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ResultType handle() throws Exception {
        value = getKnownElements().get(elementId).getAttribute(name);

        return ResultType.SUCCESS;
    }

    public String getValue() {
        return value;
    }

}
