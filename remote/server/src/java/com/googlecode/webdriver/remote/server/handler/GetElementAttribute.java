package com.googlecode.webdriver.remote.server.handler;

import com.googlecode.webdriver.remote.server.DriverSessions;
import com.googlecode.webdriver.remote.server.rest.ResultType;
import com.googlecode.webdriver.remote.Response;

public class GetElementAttribute extends WebDriverHandler {
    private String elementId;
    private String name;
    private Response response;

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
        response = newResponse();
        response.setValue(getKnownElements().get(elementId).getAttribute(name));

        return ResultType.SUCCESS;
    }

    public Response getResponse() {
        return response;
    }

}
