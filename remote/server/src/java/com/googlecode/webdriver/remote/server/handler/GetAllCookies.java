// Copyright 2008 Google Inc.  All Rights Reserved.

package com.googlecode.webdriver.remote.server.handler;

import com.googlecode.webdriver.remote.server.rest.ResultType;
import com.googlecode.webdriver.remote.server.DriverSessions;
import com.googlecode.webdriver.remote.Response;
import com.googlecode.webdriver.Cookie;

import java.util.Set;

public class GetAllCookies extends WebDriverHandler {

  private Response response;

  public GetAllCookies(DriverSessions sessions) {
    super(sessions);
  }

  public ResultType handle() throws Exception {
    response = newResponse();
    Set<Cookie> cookies = getDriver().manage().getCookies();
    response.setValue(cookies);
    return ResultType.SUCCESS;
  }

  public Response getResponse() {
    return response;
  }
}
