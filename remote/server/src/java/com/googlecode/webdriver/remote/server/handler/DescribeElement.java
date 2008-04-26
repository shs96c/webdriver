package com.googlecode.webdriver.remote.server.handler;

import com.googlecode.webdriver.remote.server.rest.ResultType;
import com.googlecode.webdriver.remote.server.DriverSessions;
import com.googlecode.webdriver.WebElement;

public class DescribeElement extends WebDriverHandler {
    private String elementId;
    private WebElement element;

    public DescribeElement(DriverSessions sessions) {
        super(sessions);
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public ResultType handle() throws Exception {
        element = getKnownElements().get(elementId);

        return ResultType.SUCCESS;
    }

    public WebElement getElement() {
        return element;
    }
}
