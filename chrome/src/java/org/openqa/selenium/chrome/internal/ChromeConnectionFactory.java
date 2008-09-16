package org.openqa.selenium.chrome.internal;

import java.io.IOException;

import org.openqa.selenium.chrome.ChromeConnection;

public class ChromeConnectionFactory {
    public static ChromeConnection connectTo(String host, int port) {
        boolean isDev = Boolean.getBoolean("webdriver.chrome.useExisting");
        if (isDev) {
            try {
                return new RunningInstanceConnection(host, port);
            } catch (IOException e) {
                // Fine. No running instance
            }
        }

        try {
          return new NewInstanceConnection(host, port);
        } catch (Exception e) {
          // Tell the world what went wrong
          e.printStackTrace();
        }

        return new NoInstanceConnection();
    }
}
