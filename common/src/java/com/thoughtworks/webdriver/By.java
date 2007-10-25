package com.thoughtworks.webdriver;

public class By {
    private final How how;
    private final String value;

    private By(How how, String value) {
        // We don't want users accidentally creating one of these things
        this.value = value;
        this.how = how;
    }

    public static By id(String id) {
        return new By(How.ID, id);
    }

    public static By xpath(String xpath) {
        return new By(How.XPATH, xpath);
    }

    public static By linkText(String linkText) {
        return new By(How.LINK, linkText);
    }

    public How getHow() {
        return how;
    }

    public String getValue() {
        return value;
    }

    public static enum How {
        ID,
        LINK,
        XPATH,
    }
}
