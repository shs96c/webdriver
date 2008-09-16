package org.openqa.selenium.chrome;

public interface ChromeConnection {
    boolean isConnected();
    String executeCommand(Class<? extends RuntimeException> throwOnFailure, String command);
    String executeJavascript(Class<? extends RuntimeException> throwOnFailure, String script);
    void quit();
}
