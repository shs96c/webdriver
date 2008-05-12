// Copyright 2008 Google Inc.  All Rights Reserved.

package com.googlecode.webdriver.remote.server.handler;

import com.googlecode.webdriver.remote.server.rest.ResultType;
import com.googlecode.webdriver.remote.server.DriverSessions;

public class DeleteNamedCookie extends WebDriverHandler {

  private String name;

  public DeleteNamedCookie(DriverSessions sessions) {
    super(sessions);
  }

  public void setName(String name) {
    this.name = name;
  }

  public ResultType handle() throws Exception {
    getDriver().manage().deleteCookieNamed(name);

    return ResultType.SUCCESS;
  }
}
