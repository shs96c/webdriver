package com.googlecode.webdriver.remote;

import com.googlecode.webdriver.WebElement;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class RemoteWebElement implements WebElement {
    private String id;
    private RemoteWebDriver parent;


    public void setParent(RemoteWebDriver parent) {
        this.parent = parent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void click() {
        throw new UnsupportedOperationException();
    }

    public void submit() {
        throw new UnsupportedOperationException();
    }

    public String getValue() {
        return parent.execute("getElementValue", map("id", id)).getResponseText();
    }

    private Map map(String... keysToValues) {
        Map<String, String> toReturn = new HashMap<String, String>();
        for (int i = 0; i < keysToValues.length; i += 2) {
            toReturn.put(keysToValues[i], keysToValues[i+1]);
        }

        return toReturn;
    }

    public void sendKeys(CharSequence... keysToSend) {
        throw new UnsupportedOperationException();
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public String getAttribute(String name) {
        return parent.execute("getElementAttribute", map("id", id, "name", name)).getResponseText();
    }

    public boolean toggle() {
        throw new UnsupportedOperationException();
    }

    public boolean isSelected() {
        throw new UnsupportedOperationException();
    }

    public void setSelected() {
        throw new UnsupportedOperationException();
    }

    public boolean isEnabled() {
        throw new UnsupportedOperationException();
    }

    public String getText() {
        return parent.execute("getElementText", map("id", id)).getResponseText();
    }

    public List<WebElement> getChildrenOfType(String tagName) {
        throw new UnsupportedOperationException();
    }
}
