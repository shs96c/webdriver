// Copyright 2008 Google Inc.  All Rights Reserved.

package com.googlecode.webdriver.remote.server.handler;

import com.googlecode.webdriver.RenderedWebElement;
import com.googlecode.webdriver.remote.server.DriverSessions;
import com.googlecode.webdriver.remote.server.JsonParametersAware;
import com.googlecode.webdriver.remote.server.rest.ResultType;

import org.json.simple.JSONArray;

public class DragElement extends WebDriverHandler implements JsonParametersAware {
  private String id;
  private long x;
  private long y;

  public DragElement(DriverSessions sessions) {
    super(sessions);
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setJsonParameters(JSONArray allParameters) throws Exception {
    x = (Long) allParameters.get(1);
    y = (Long) allParameters.get(2);
  }

  public ResultType handle() throws Exception {
    RenderedWebElement element = (RenderedWebElement) getKnownElements().get(id);
    element.dragAndDropBy((int) x, (int) y);
    return ResultType.SUCCESS;
  }


}
