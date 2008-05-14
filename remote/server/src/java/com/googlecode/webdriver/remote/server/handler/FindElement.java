package com.googlecode.webdriver.remote.server.handler;

import com.googlecode.webdriver.remote.server.JsonParametersAware;
import com.googlecode.webdriver.remote.server.DriverSessions;
import com.googlecode.webdriver.remote.server.rest.ResultType;
import com.googlecode.webdriver.remote.JsonToBeanConverter;
import com.googlecode.webdriver.remote.Response;
import com.googlecode.webdriver.By;
import com.googlecode.webdriver.WebElement;
import com.googlecode.webdriver.NoSuchElementException;

import org.json.simple.JSONArray;

public class FindElement extends WebDriverHandler implements JsonParametersAware {
  private By by;
  private String elementId;

  public FindElement(DriverSessions sessions) {
        super(sessions);
    }

    public void setJsonParameters(JSONArray allParameters) throws Exception {
        JsonToBeanConverter converter = new JsonToBeanConverter();
        String method = converter.convert(String.class, allParameters.get(0));
        String selector = converter.convert(String.class, allParameters.get(1));

        by = new BySelector().pickFrom(method, selector);
    }

    public ResultType handle() throws Exception {
        WebElement element = getDriver().findElement(by);
        elementId = getKnownElements().add(element);

        return ResultType.SUCCESS;
    }

    public String getElement() {
        return elementId;
    }
}
