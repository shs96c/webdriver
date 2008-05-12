// Copyright 2008 Google Inc.  All Rights Reserved.

package com.googlecode.webdriver.remote.server.handler;

import com.googlecode.webdriver.remote.server.JsonParametersAware;
import com.googlecode.webdriver.remote.server.DriverSessions;
import com.googlecode.webdriver.remote.server.rest.ResultType;
import com.googlecode.webdriver.internal.ReturnedCookie;

import org.json.simple.JSONArray;

import java.util.Map;

public abstract class CookieHandler extends WebDriverHandler implements JsonParametersAware {
  private Map<String, Object> rawCookie;

  public CookieHandler(DriverSessions sessions) {
    super(sessions);
  }

  public void setJsonParameters(JSONArray allParameters) throws Exception {
    if (allParameters == null)
      return;
    
    rawCookie = (Map<String, Object>) allParameters.get(0);
  }

  protected ReturnedCookie createCookie() {
    if (rawCookie == null)
      return null;

    String name = (String) rawCookie.get("name");
    String value = (String) rawCookie.get("value");
    String path = (String) rawCookie.get("path");
    String domain = (String) rawCookie.get("domain");
    Boolean secure = (Boolean) rawCookie.get("secure");

    return new ReturnedCookie(name, value, domain, path, null, secure);
  }

}
