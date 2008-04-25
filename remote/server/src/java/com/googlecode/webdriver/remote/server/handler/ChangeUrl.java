package com.googlecode.webdriver.remote.server.handler;

import com.googlecode.webdriver.remote.server.DriverSessions;
import com.googlecode.webdriver.remote.server.JsonParametersAware;
import com.googlecode.webdriver.remote.server.rest.ResultType;
import org.json.simple.JSONArray;

public class ChangeUrl extends WebDriverHandler implements JsonParametersAware {
    private String url;

    public ChangeUrl(DriverSessions sessions) {
        super(sessions);
    }

    public void setJsonParameters(JSONArray allParameters) throws Exception {
        url = (String) allParameters.get(0);
    }

    public ResultType handle() throws Exception {
        getDriver().get(url);

        return ResultType.SUCCESS;
    }
}
