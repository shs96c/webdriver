// Copyright 2008 Google Inc.  All Rights Reserved.

package com.googlecode.webdriver.remote.server.handler;

import com.googlecode.webdriver.remote.server.rest.ResultType;
import com.googlecode.webdriver.remote.server.DriverSessions;

public class GoForward extends WebDriverHandler {
  public GoForward(DriverSessions sessions) {
    super(sessions);
  }

  public ResultType handle() throws Exception {
    getDriver().navigate().forward();

    return ResultType.SUCCESS;
  }
}
