// Copyright 2008 Google Inc.  All Rights Reserved.

package com.googlecode.webdriver.remote.server.handler;

import com.googlecode.webdriver.remote.server.rest.ResultType;
import com.googlecode.webdriver.remote.server.DriverSessions;
import com.googlecode.webdriver.remote.Response;

public class GetMouseSpeed extends WebDriverHandler {
  private Response response;

  public GetMouseSpeed(DriverSessions sessions) {
    super(sessions);
  }

  public ResultType handle() throws Exception {
    response = newResponse();
    response.setValue(getDriver().manage().getMouseSpeed());

    return ResultType.SUCCESS;
  }

  public Response getResponse() {
    return response;
  }
}
