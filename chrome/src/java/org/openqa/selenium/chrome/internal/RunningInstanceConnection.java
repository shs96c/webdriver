package org.openqa.selenium.chrome.internal;

import java.io.IOException;
import java.net.ConnectException;

public class RunningInstanceConnection extends AbstractChromeConnection {
    public RunningInstanceConnection(String host, int port) throws IOException {
        this(host, port, 500);
    }

    public RunningInstanceConnection(String host, int port, long timeOut) throws IOException {
        setAddress(host, port);
        if (!connectToBrowser(timeOut))
            throw new ConnectException("Cannot connect to browser");
    }

    public void quit() {
        throw new UnsupportedOperationException("quit");
    }
}
