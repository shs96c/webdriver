package com.googlecode.webdriver.remote.server.handler;

import com.googlecode.webdriver.remote.server.DriverSessions;
import com.googlecode.webdriver.remote.server.rest.ResultType;
import com.googlecode.webdriver.remote.Response;

public class GetTitle extends WebDriverHandler {
    private Response response;

    public GetTitle(DriverSessions sessions) {
        super(sessions);
    }

    public ResultType handle() throws Exception {
        response = newResponse();
        response.setValue(getDriver().getTitle());
        return ResultType.SUCCESS;
    }

    public Response getResponse() {
        return response;
    }

}
