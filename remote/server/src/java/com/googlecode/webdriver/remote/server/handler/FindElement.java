package com.googlecode.webdriver.remote.server.handler;

import com.googlecode.webdriver.remote.server.JsonParametersAware;
import com.googlecode.webdriver.remote.server.DriverSessions;
import com.googlecode.webdriver.remote.server.rest.ResultType;
import com.googlecode.webdriver.remote.JsonToBeanConverter;
import com.googlecode.webdriver.By;
import com.googlecode.webdriver.WebElement;
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

        by = null;
        if ("id".equals(method))
            by = By.id(selector);
        else if ("link text".equals(method))
            by = By.linkText(selector);
        else if ("name".equals(method))
            by = By.name(selector);
        else if ("xpath".equals(method))
            by = By.xpath(selector);
        else
            throw new RuntimeException("Cannot find matching element locator to: " + method);
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
