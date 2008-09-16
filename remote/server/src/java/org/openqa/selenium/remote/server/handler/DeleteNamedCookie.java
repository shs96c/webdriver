// Copyright 2008 Google Inc.  All Rights Reserved.

package org.openqa.selenium.remote.server.handler;

import org.openqa.selenium.remote.server.DriverSessions;
import org.openqa.selenium.remote.server.rest.ResultType;

public class DeleteNamedCookie extends WebDriverHandler {

  private String name;

  public DeleteNamedCookie(DriverSessions sessions) {
    super(sessions);
  }

  public void setName(String name) {
    this.name = name;
  }

  public ResultType call() throws Exception {
    getDriver().manage().deleteCookieNamed(name);

    return ResultType.SUCCESS;
  }
}
