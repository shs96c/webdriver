package com.googlecode.webdriver.remote;

import com.googlecode.webdriver.internal.OperatingSystem;

public interface Capabilities {
    String getBrowserName();
    OperatingSystem getOperatingSystem();
    String getVersion();
    boolean isJavascriptEnabled();
}
