package com.googlecode.webdriver.remote.server.handler;

import com.googlecode.webdriver.remote.server.rest.ResultType;
import com.googlecode.webdriver.remote.server.DriverSessions;
import com.googlecode.webdriver.remote.Response;

public class GetElementValue extends WebDriverHandler {
    private String elementId;
    private Response response;

    public GetElementValue(DriverSessions sessions) {
        super(sessions);
    }

    public void setId(String elementId) {
        this.elementId = elementId;
    }

    public ResultType handle() throws Exception {
        response = newResponse();
        response.setValue(getKnownElements().get(elementId).getValue());

        return ResultType.SUCCESS;
    }

    public Response getResponse() {
        return response;
    }
}
