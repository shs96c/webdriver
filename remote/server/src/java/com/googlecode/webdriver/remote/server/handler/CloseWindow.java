// Copyright 2008 Google Inc.  All Rights Reserved.

package com.googlecode.webdriver.remote.server.handler;

import com.googlecode.webdriver.WebDriver;
import com.googlecode.webdriver.remote.server.DriverSessions;
import com.googlecode.webdriver.remote.server.rest.ResultType;

public class CloseWindow extends WebDriverHandler {
  public CloseWindow(DriverSessions sessions) {
    super(sessions);
  }

  public ResultType handle() throws Exception {
    WebDriver driver = getDriver();
    driver.close();

    return ResultType.SUCCESS;
  }
}
