package com.googlecode.webdriver.remote.server;

import com.googlecode.webdriver.WebDriver;
import com.googlecode.webdriver.remote.Context;
import com.googlecode.webdriver.remote.Capabilities;
import com.googlecode.webdriver.remote.DesiredCapabilities;
import com.googlecode.webdriver.remote.server.handler.GetSessionCapabilities;

public class Session {
  private final WebDriver driver;
  private KnownElements knownElements = new KnownElements();
  private Capabilities capabilities;

  public Session(WebDriver driver, Capabilities capabilities, boolean rendered) {
    this.driver = driver;
    DesiredCapabilities desiredCapabilities =
        new DesiredCapabilities(capabilities.getBrowserName(), capabilities.getVersion(),
                                capabilities.getOperatingSystem());
    desiredCapabilities.setJavascriptEnabled(rendered);
    this.capabilities = desiredCapabilities;
  }

  public WebDriver getDriver(Context context) {
    return driver;
  }

  public KnownElements getKnownElements() {
    return knownElements;
  }

  public Capabilities getCapabilities() {
    return capabilities;
  }
}
