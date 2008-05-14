// Copyright 2008 Google Inc.  All Rights Reserved.

package com.googlecode.webdriver.remote.server.handler;

import com.googlecode.webdriver.remote.server.rest.ResultType;
import com.googlecode.webdriver.remote.server.DriverSessions;
import com.googlecode.webdriver.remote.server.JsonParametersAware;
import com.googlecode.webdriver.Speed;

import org.json.simple.JSONArray;

public class SetMouseSpeed extends WebDriverHandler implements JsonParametersAware {
  private Speed speed;

  public SetMouseSpeed(DriverSessions sessions) {
    super(sessions);
  }

  public void setJsonParameters(JSONArray allParameters) throws Exception {
    speed = Speed.valueOf((String) allParameters.get(0));
  }

  public ResultType handle() throws Exception {
    getDriver().manage().setMouseSpeed(speed);

    return ResultType.SUCCESS;
  }
}
