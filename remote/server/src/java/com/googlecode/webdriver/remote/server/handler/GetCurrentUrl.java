package com.googlecode.webdriver.remote.server.handler;

import com.googlecode.webdriver.remote.server.DriverSessions;
import com.googlecode.webdriver.remote.server.rest.ResultType;

public class GetCurrentUrl extends WebDriverHandler {
    private String url;

    public GetCurrentUrl(DriverSessions sessions) {
        super(sessions);
    }

    public ResultType handle() throws Exception {
        url = getDriver().getCurrentUrl();
        return ResultType.SUCCESS;
    }

    public String getUrl() {
        return url;
    }
}
