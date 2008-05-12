// Copyright 2008 Google Inc.  All Rights Reserved.

package com.googlecode.webdriver.remote.server.handler;

import com.googlecode.webdriver.internal.ReturnedCookie;
import com.googlecode.webdriver.remote.server.DriverSessions;
import com.googlecode.webdriver.remote.server.rest.ResultType;

public class AddCookie extends CookieHandler {
  public AddCookie(DriverSessions sessions) {
    super(sessions);
  }

  public ResultType handle() throws Exception {
    ReturnedCookie cookie = createCookie();

    getDriver().manage().addCookie(cookie);

    return ResultType.SUCCESS;
  }
}
