package com.googlecode.webdriver.remote.server.handler;

import com.googlecode.webdriver.remote.Response;
import com.googlecode.webdriver.remote.server.DriverSessions;
import com.googlecode.webdriver.remote.server.rest.ResultType;

public class GetPageSource  extends WebDriverHandler {
    private Response response;

    public GetPageSource(DriverSessions sessions) {
        super(sessions);
    }

    public ResultType handle() throws Exception {
        response = newResponse();
        response.setValue(getDriver().getPageSource());
        return ResultType.SUCCESS;
    }

    public Response getResponse() {
        return response;
    }
}
