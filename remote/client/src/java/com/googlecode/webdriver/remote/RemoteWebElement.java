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
        parent.execute("clickElement", map("id", id));
    }

    public void submit() {
        parent.execute("submitElement", map("id", id));
    }

    public String getValue() {
        return (String) parent.execute("getElementValue", map("id", id)).getValue();
    }

    public void sendKeys(CharSequence... keysToSend) {
        parent.execute("sendKeys", map("id", id, "value", keysToSend));
    }

    public void clear() {
        parent.execute("clearElement", map("id", id));
    }

    public String getAttribute(String name) {
        Object value = parent.execute("getElementAttribute", map("id", id, "name", name)).getValue();
        if (value == null)
            return null;
        return String.valueOf(value);
    }

    public boolean toggle() {
        return (Boolean) parent.execute(UnsupportedOperationException.class, "toggleElement", map("id", id)).getValue();
    }

    public boolean isSelected() {
      return (Boolean) parent.execute("isElementSelected", map("id", id)).getValue();
    }

    public void setSelected() {
        parent.execute(UnsupportedOperationException.class, "setElementSelected", map("id", id));
    }

    public boolean isEnabled() {
        return (Boolean) parent.execute("isElementEnabled", map("id", id)).getValue();
    }

    public String getText() {
        Response response = parent.execute("getElementText", map("id", id));
        return (String) response.getValue();
    }

    public List<WebElement> getChildrenOfType(String tagName) {
        throw new UnsupportedOperationException();
    }

	protected Map<Object, Object> map(Object... keysToValues) {
        Map<Object, Object> toReturn = new HashMap<Object, Object>();
        for (int i = 0; i < keysToValues.length; i += 2) {
            toReturn.put(keysToValues[i], keysToValues[i+1]);
        }

        return toReturn;
    }
}
