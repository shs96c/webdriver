// Copyright 2008 Google Inc.  All Rights Reserved.

package com.googlecode.webdriver.remote.server.handler;

import com.googlecode.webdriver.RenderedWebElement;
import com.googlecode.webdriver.remote.Response;
import com.googlecode.webdriver.remote.server.DriverSessions;
import com.googlecode.webdriver.remote.server.rest.ResultType;

public class GetElementDisplayed extends WebDriverHandler {
  private String id;
  private Response response;

  public GetElementDisplayed(DriverSessions sessions) {
    super(sessions);
  }

  public void setId(String id) {
    this.id = id;
  }

  public ResultType handle() throws Exception {
    response = newResponse();
    RenderedWebElement element = (RenderedWebElement) getKnownElements().get(id);
    response.setValue(element.isDisplayed());

    return ResultType.SUCCESS;
  }

  public Response getResponse() {
    return response;
  }
}
