package org.openqa.selenium.chrome.internal;

import org.openqa.selenium.chrome.ChromeConnection;

public class NoInstanceConnection implements ChromeConnection {
    public boolean isConnected() {
        return false;
    }

    public String executeCommand(Class<? extends RuntimeException> throwOnFailure, String command) {
        throw new UnsupportedOperationException("Cannot execute command \"" + command + "\" on a disconnected instance");
    }

    public String executeJavascript(Class<? extends RuntimeException> throwOnFailure, String script) {
        throw new UnsupportedOperationException("Cannot execute JavaScript \"" + script + "\" on a disconnected instance");
    }

    public void quit() {
        // no-op
        //TODO: should we log a warning?
    }
}
