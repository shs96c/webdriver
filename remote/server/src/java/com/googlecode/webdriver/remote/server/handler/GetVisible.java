package com.googlecode.webdriver.remote.server.handler;

import com.googlecode.webdriver.remote.server.rest.ResultType;
import com.googlecode.webdriver.remote.server.DriverSessions;

public class GetVisible extends WebDriverHandler {
    private boolean visible;

    public GetVisible(DriverSessions sessions) {
        super(sessions);
    }

    public ResultType handle() throws Exception {
        visible = getDriver().getVisible();
        return ResultType.SUCCESS;
    }

    public boolean isVisible() {
        return visible;
    }
}
