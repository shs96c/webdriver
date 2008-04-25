package com.googlecode.webdriver.remote.server;

import com.googlecode.webdriver.WebDriver;
import com.googlecode.webdriver.remote.Context;

public class Session {
	private final WebDriver driver;

	public Session(WebDriver driver) {
		this.driver = driver;
	}

    public WebDriver getDriver(Context context) {
        return driver;
    }
}
