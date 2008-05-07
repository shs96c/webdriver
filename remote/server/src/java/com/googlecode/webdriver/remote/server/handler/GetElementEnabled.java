// Copyright 2008 Google Inc.  All Rights Reserved.

package com.googlecode.webdriver.remote.server.handler;

import com.googlecode.webdriver.remote.Response;
import com.googlecode.webdriver.remote.server.DriverSessions;
import com.googlecode.webdriver.remote.server.rest.ResultType;

/**
 * @author simonstewart@google.com (Simon Stewart)
 */
public class GetElementEnabled extends WebDriverHandler {
    private String elementId;
    private Response response;

    public GetElementEnabled(DriverSessions sessions) {
        super(sessions);
    }

    public void setId(String elementId) {
        this.elementId = elementId;
    }

    public ResultType handle() throws Exception {
        response = newResponse();
        response.setValue(getKnownElements().get(elementId).isEnabled());

        return ResultType.SUCCESS;
    }

    public Response getResponse() {
        return response;
    }
}
