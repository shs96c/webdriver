package com.googlecode.webdriver.firefox.internal;

import java.io.IOException;

import com.googlecode.webdriver.firefox.ExtensionConnection;
import com.googlecode.webdriver.firefox.FirefoxProfile;

public class ExtensionConnectionFactory {
    public static ExtensionConnection connectTo(FirefoxProfile profile, String host, int port) {
        boolean isDev = Boolean.getBoolean("webdriver.firefox.useExisting");
        if (isDev) {
            try {
                return new RunningInstanceConnection(host, port);
            } catch (IOException e) {
                // Fine. No running instance
            }
        }

        try {
          return new NewProfileExtensionConnection(profile, host, port);
        } catch (Exception e) {
            // Then we can't connect
        }

        return new DisconnectedExtension();
    }
}
