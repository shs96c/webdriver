package com.googlecode.webdriver.remote.server;

import org.json.simple.JSONArray;

public interface JsonParametersAware {
    void setJsonParameters(JSONArray allParameters) throws Exception;
}
