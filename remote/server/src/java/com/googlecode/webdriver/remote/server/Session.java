package com.googlecode.webdriver.remote.server;

import com.googlecode.webdriver.WebDriver;
import com.googlecode.webdriver.remote.Context;

public class Session {
    private final WebDriver driver;
    private KnownElements knownElements = new KnownElements();

    public Session(WebDriver driver) {
		this.driver = driver;
	}

    public WebDriver getDriver(Context context) {
        return driver;
    }

    public KnownElements getKnownElements() {
        return knownElements;
    }
}
