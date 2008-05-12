// Copyright 2008 Google Inc.  All Rights Reserved.

package com.googlecode.webdriver.remote.server.handler;

import com.googlecode.webdriver.internal.ReturnedCookie;
import com.googlecode.webdriver.remote.server.DriverSessions;
import com.googlecode.webdriver.remote.server.rest.ResultType;

public class DeleteCookie extends CookieHandler {
  public DeleteCookie(DriverSessions sessions) {
    super(sessions);
  }

  public ResultType handle() throws Exception {
    getDriver().manage().deleteAllCookies();

    return ResultType.SUCCESS;
  }
}
