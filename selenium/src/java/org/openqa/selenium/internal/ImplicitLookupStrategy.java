package org.openqa.selenium.internal;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ImplicitLookupStrategy implements LookupStrategy {
    public WebElement find(WebDriver driver, String use) {
        if (use.startsWith("//")) {
            return new XPathLookupStrategy().find(driver, use);
        } else {
            return new IdentifierLookupStrategy().find(driver, use);
        }
    }
}
