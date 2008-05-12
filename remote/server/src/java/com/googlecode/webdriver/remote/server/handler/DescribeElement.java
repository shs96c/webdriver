package com.googlecode.webdriver.remote.server.handler;

import com.googlecode.webdriver.remote.Response;
import com.googlecode.webdriver.remote.server.DriverSessions;
import com.googlecode.webdriver.remote.server.rest.ResultType;

public class DescribeElement extends WebDriverHandler {
    private String elementId;
    private Response response;

    public DescribeElement(DriverSessions sessions) {
        super(sessions);
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public ResultType handle() throws Exception {
        response = newResponse();
        response.setValue(getKnownElements().get(elementId));

        return ResultType.SUCCESS;
    }

    public Response getResponse() {
        return response;
    }
}
