package com.googlecode.webdriver.remote.server.handler;

import com.googlecode.webdriver.remote.server.JsonParametersAware;
import com.googlecode.webdriver.remote.server.DriverSessions;
import com.googlecode.webdriver.remote.server.rest.ResultType;
import com.googlecode.webdriver.remote.JsonToBeanConverter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.ArrayList;

public class SendKeys extends WebDriverHandler implements JsonParametersAware {
    private String elementId;
    private List<CharSequence> keys = new ArrayList<CharSequence>();

    public SendKeys(DriverSessions sessions) {
        super(sessions);
    }

    public void setJsonParameters(JSONArray allParameters) throws Exception {
        JSONObject namedParameters = (JSONObject) allParameters.get(0);

        JsonToBeanConverter converter = new JsonToBeanConverter();
        elementId = converter.convert(String.class, namedParameters.get("id"));

        JSONArray rawKeys = (JSONArray) namedParameters.get("value");
        for (int i = 0; i < rawKeys.size(); i++) {
            keys.add(converter.convert(String.class, rawKeys.get(i)));
        }
    }

    public ResultType handle() throws Exception {
        String[] keysToSend = keys.toArray(new String[0]);
        getKnownElements().get(elementId).sendKeys(keysToSend);

        return ResultType.SUCCESS;
    }
}
