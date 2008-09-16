package org.openqa.selenium.chrome;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.SearchContext;

import java.util.List;

public class ChromeWebElement implements WebElement, SearchContext {
    private final int index;
    private final ChromeConnection connection;
    private final ChromeDriver parent;
    private final String locator;

    public ChromeWebElement(ChromeDriver parent, ChromeConnection connection, String elementIndex) {
        this.parent = parent;
        index = Integer.parseInt(elementIndex);
        locator = ChromeDriver.ELEMENTS + "[" + index + "]";

        this.connection = connection;
    }

    public void click() {
        connection.executeJavascript(RuntimeException.class,
                "if (" + locator + "[\"click\"])" +
                    locator + ".click(); " +
                "var event = document.createEvent(\"MouseEvents\"); " +
                "event.initMouseEvent(\"click\", true, true, null, 1, 0, 0, 0, 0, false, false, false, false, 0, null);" +
                locator + ".dispatchEvent(event)"
        );
        parent.waitForLoadToComplete();
    }

    public void submit() {
        throw new UnsupportedOperationException("submit");
    }

    public String getValue() {
        return connection.executeJavascript(RuntimeException.class,
            "if (" + locator + "[\"value\"] !== undefined)" +
            "  " + locator + ".value;" +
            "else if (" + locator + ".hasAttribute(\"value\"))" +
            "  " + locator + ".getAttribute(\"value\");");
    }

    public void sendKeys(CharSequence... value) {
        StringBuilder builder = new StringBuilder();
        for (CharSequence seq : value)
            builder.append(seq);

        //TODO: this needs to be properly implemented (and escaped); the current implementation is for testing purposes only
        connection.executeJavascript(RuntimeException.class, locator + ".focus(); " +
                locator + ".value = \"" + builder.toString() + "\"");

        connection.executeJavascript(RuntimeException.class, locator + ".blur()");
    }

    public void clear() {
    	connection.executeJavascript(RuntimeException.class,
                "if (" + locator + "['value']) { " + locator + ".value = ''; }" +
                "else { " + locator + ".setAttribute('value', ''); }"
        );
    }

    public String getAttribute(String name) {
        throw new UnsupportedOperationException("getAttribute");
    }

    public boolean toggle() {
    	throw new UnsupportedOperationException("toggle");
    }

    public boolean isSelected() {
        throw new UnsupportedOperationException("isSelected");
    }

    public void setSelected() {
    	throw new UnsupportedOperationException("setSelected");
    }

    public boolean isEnabled() {
        throw new UnsupportedOperationException("isEnabled");
    }

    public String getText() {
        return connection.executeJavascript(RuntimeException.class, locator + ".innerText");
    }

    public List<WebElement> getChildrenOfType(String tagName) {
        throw new UnsupportedOperationException("getChildrenOfType");
    }

    public WebElement findElement(By by) {
        throw new UnsupportedOperationException("To be implemented");
    }

    public List<WebElement> findElements(By by) {
        throw new UnsupportedOperationException("To be implemented");
    }
}
