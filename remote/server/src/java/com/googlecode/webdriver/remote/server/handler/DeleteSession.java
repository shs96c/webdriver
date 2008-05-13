// Copyright 2008 Google Inc.  All Rights Reserved.

package com.googlecode.webdriver.remote.server.handler;

import com.googlecode.webdriver.remote.server.DriverSessions;
import com.googlecode.webdriver.remote.server.rest.ResultType;

public class DeleteSession extends WebDriverHandler {
  private final DriverSessions sessions;

  public DeleteSession(DriverSessions sessions) {
    super(sessions);
    this.sessions = sessions;
  }

  public ResultType handle() throws Exception {
    getDriver().quit();
    sessions.deleteSession(getRealSessionId());

    return ResultType.SUCCESS;
  }
}
