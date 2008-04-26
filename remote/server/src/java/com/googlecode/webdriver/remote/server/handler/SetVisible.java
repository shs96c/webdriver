package com.googlecode.webdriver.remote.server.handler;

import com.googlecode.webdriver.remote.server.rest.ResultType;
import com.googlecode.webdriver.remote.server.DriverSessions;
import com.googlecode.webdriver.remote.server.JsonParametersAware;
import com.googlecode.webdriver.remote.JsonToBeanConverter;
import org.json.simple.JSONArray;

public class SetVisible extends WebDriverHandler implements JsonParametersAware {
    private boolean visible;

    public SetVisible(DriverSessions sessions) {
        super(sessions);
    }

    public void setJsonParameters(JSONArray allParameters) throws Exception {
        visible = new JsonToBeanConverter().convert(Boolean.class, allParameters.get(0));
    }

    public ResultType handle() throws Exception {
        getDriver().setVisible(visible);
        return ResultType.SUCCESS;
    }
}
