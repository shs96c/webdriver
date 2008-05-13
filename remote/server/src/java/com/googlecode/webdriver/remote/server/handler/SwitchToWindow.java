// Copyright 2008 Google Inc.  All Rights Reserved.

package com.googlecode.webdriver.remote.server.handler;

import com.googlecode.webdriver.remote.server.rest.ResultType;
import com.googlecode.webdriver.remote.server.DriverSessions;

public class SwitchToWindow extends WebDriverHandler {
  private String name;

  public SwitchToWindow(DriverSessions sessions) {
    super(sessions);
  }

  public void setName(String name) {
    this.name = name;
  }

  public ResultType handle() throws Exception {
    getDriver().switchTo().window(name);

    return ResultType.SUCCESS;
  }
}
