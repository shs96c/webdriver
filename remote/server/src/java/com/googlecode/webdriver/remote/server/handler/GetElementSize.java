// Copyright 2008 Google Inc.  All Rights Reserved.

package com.googlecode.webdriver.remote.server.handler;

import com.googlecode.webdriver.remote.Response;
import com.googlecode.webdriver.remote.server.DriverSessions;
import com.googlecode.webdriver.remote.server.rest.ResultType;
import com.googlecode.webdriver.WebElement;
import com.googlecode.webdriver.RenderedWebElement;

public class GetElementSize  extends WebDriverHandler {
  private Response response;
  private String id;

  public GetElementSize(DriverSessions sessions) {
    super(sessions);
  }

  public void setId(String id) {
    this.id = id;
  }

  public ResultType handle() throws Exception {
    response = newResponse();

    WebElement element = getKnownElements().get(id);
    response.setValue(((RenderedWebElement) element).getSize());

    return ResultType.SUCCESS;
  }

  public Response getResponse() {
    return response;
  }
}
