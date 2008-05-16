package com.googlecode.webdriver.selenium.internal;

public class ExactTextMatchingStrategy implements TextMatchingStrategy {
    public boolean isAMatch(String compareThis, String with) {
        return with.contains(compareThis);
    }
}
