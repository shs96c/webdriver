package com.googlecode.webdriver.remote.server.handler;

import com.googlecode.webdriver.remote.server.DriverSessions;
import com.googlecode.webdriver.remote.server.rest.ResultType;

public class GetTitle extends WebDriverHandler {
    private String title;

    public GetTitle(DriverSessions sessions) {
        super(sessions);
    }

    public ResultType handle() throws Exception {
        title = getDriver().getTitle();
        return ResultType.SUCCESS;
    }

    public String getTitle() {
        return title;
    }

}
