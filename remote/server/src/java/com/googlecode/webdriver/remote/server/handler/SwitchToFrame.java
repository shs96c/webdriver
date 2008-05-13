// Copyright 2008 Google Inc.  All Rights Reserved.

package com.googlecode.webdriver.remote.server.handler;

import com.googlecode.webdriver.remote.server.rest.ResultType;
import com.googlecode.webdriver.remote.server.DriverSessions;

public class SwitchToFrame extends WebDriverHandler {
  private String id;

  public SwitchToFrame(DriverSessions sessions) {
    super(sessions);
  }

  public void setId(String id) {
    this.id = id;
  }

  public ResultType handle() throws Exception {
    if (id == null)
      getDriver().switchTo().defaultContent();
    else
      getDriver().switchTo().frame(id);

    return ResultType.SUCCESS;
  }
}
