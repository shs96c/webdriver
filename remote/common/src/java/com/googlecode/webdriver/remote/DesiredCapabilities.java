package com.googlecode.webdriver.remote;

import com.googlecode.webdriver.internal.OperatingSystem;

public class DesiredCapabilities implements Capabilities {
    private String browserName;
    private String version;
    private OperatingSystem operatingSystem;
    private boolean javascriptEnabled;

    public DesiredCapabilities(String browser, String version, OperatingSystem operatingSystem) {
        this.browserName = browser;
        this.version = version;
        this.operatingSystem = operatingSystem;
    }

    public DesiredCapabilities() {
        // no-arg constructor
    }

    public String getBrowserName() {
        return browserName;
    }

    public void setBrowserName(String browserName) {
        this.browserName = browserName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public OperatingSystem getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(OperatingSystem operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public boolean isJavascriptEnabled() {
        return javascriptEnabled;
    }

    public void setJavascriptEnabled(boolean javascriptEnabled) {
        this.javascriptEnabled = javascriptEnabled;
    }

    static DesiredCapabilities firefox() {
        return new DesiredCapabilities("firefox", "", OperatingSystem.ANY);
    }

    static DesiredCapabilities internetExplorer() {
        return new DesiredCapabilities("internet explorer", "", OperatingSystem.WINDOWS);
    }

    static DesiredCapabilities htmlUnit() {
        return new DesiredCapabilities("htmlunit", "", OperatingSystem.ANY);
    }

    static DesiredCapabilities safari() {
        return new DesiredCapabilities("safari", "", OperatingSystem.MAC);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DesiredCapabilities)) return false;

        DesiredCapabilities that = (DesiredCapabilities) o;

        if (javascriptEnabled != that.javascriptEnabled) return false;
        if (browserName != null ? !browserName.equals(that.browserName) : that.browserName != null) return false;
        if (operatingSystem != that.operatingSystem) return false;
        if (version != null ? !version.equals(that.version) : that.version != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (browserName != null ? browserName.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (operatingSystem != null ? operatingSystem.hashCode() : 0);
        result = 31 * result + (javascriptEnabled ? 1 : 0);
        return result;
    }
}
