package org.openqa.selenium.chrome;

import org.openqa.selenium.chrome.internal.ChromeBinary;

import java.io.IOException;

public class ChromeLauncher {
    private int port;

    public ChromeLauncher(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws IOException {
        int port = 12345;
        if (args.length == 1)
            port = Integer.parseInt(args[0]);

        ChromeLauncher launcher = new ChromeLauncher(port);
        launcher.start();
    }

    public ChromeBinary start() throws IOException {
        ChromeBinary binary = new ChromeBinary();
        binary.start("--remote-shell-port=" + port, "about:blank");
        return binary;
    }
}
