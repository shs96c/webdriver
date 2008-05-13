// Copyright 2008 Google Inc.  All Rights Reserved.

package com.googlecode.webdriver.remote.server.handler;

import com.googlecode.webdriver.remote.server.JsonParametersAware;
import com.googlecode.webdriver.remote.server.DriverSessions;
import com.googlecode.webdriver.remote.server.rest.ResultType;
import com.googlecode.webdriver.remote.Response;
import com.googlecode.webdriver.remote.JsonToBeanConverter;
import com.googlecode.webdriver.By;
import com.googlecode.webdriver.WebElement;
import com.googlecode.webdriver.NoSuchElementException;

import org.json.simple.JSONArray;

import java.util.List;
import java.util.Set;
import java.util.LinkedHashSet;

public class FindElements extends WebDriverHandler implements JsonParametersAware {
  private By by;
  private Response response;

  public FindElements(DriverSessions sessions) {
    super(sessions);
  }

  public void setJsonParameters(JSONArray allParameters) throws Exception {
    JsonToBeanConverter converter = new JsonToBeanConverter();
    String method = converter.convert(String.class, allParameters.get(0));
    String selector = converter.convert(String.class, allParameters.get(1));

    by = new BySelector().pickFrom(method, selector);
  }

  public ResultType handle() throws Exception {
    response = newResponse();

    Set<String> urls = new LinkedHashSet<String>();
    List<WebElement> elements = getDriver().findElements(by);
    for (WebElement element : elements) {
      String elementId = getKnownElements().add(element);

      // URL will be relative to the current one.
      urls.add(String.format("element/%s", elementId));
    }

    response.setValue(urls);
    return ResultType.SUCCESS;
  }

  public Response getResponse() {
    return response;
  }
}
