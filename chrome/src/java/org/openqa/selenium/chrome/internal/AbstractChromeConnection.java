package org.openqa.selenium.chrome.internal;

import org.openqa.selenium.chrome.ChromeConnection;

import java.net.*;
import java.io.OutputStreamWriter;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Enumeration;

//TODO: most of this code was based on FirefoxDriver's AbstractExtensionConnection, so this needs some refactoring to avoid duplication 
public abstract class AbstractChromeConnection implements ChromeConnection {
    private Socket socket;
    protected SocketAddress address;
    private OutputStreamWriter out;
    private BufferedInputStream in;
    private boolean isInJavascriptMode = false;

    static final String TELNET_STARTUP = "\uFFFD\uFFFD\u0001\uFFFD\uFFFD\u001F\uFFFD\uFFFD!\uFFFD\uFFFD\u0001\uFFFD\uFFFD\u0003";
    static final String CHROME_PROMPT = "Chrome> ";
    static final String V8_PROMPT = "v8(running)> ";
    static final String LOST_CONNECTION_TO_TAB = ">>lost connection to tab";
    static final String NOT_CONNECTED_TO_TAB = ">>not connected to a tab";

    protected void setAddress(String host, int port) {
        InetAddress addr;

        if ("localhost".equals(host)) {
            addr = obtainLoopbackAddress();
        } else {
            try {
                addr = InetAddress.getByName(host);
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        }

        address = new InetSocketAddress(addr, port);
    }

    private InetAddress obtainLoopbackAddress() {
        InetAddress localIp4 = null;
        InetAddress localIp6 = null;

        try {
            Enumeration<NetworkInterface> allInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allInterfaces.hasMoreElements()) {
                NetworkInterface iface = allInterfaces.nextElement();
                Enumeration<InetAddress> allAddresses = iface.getInetAddresses();
                while (allAddresses.hasMoreElements()) {
                    InetAddress addr = allAddresses.nextElement();
                    if (addr.isLoopbackAddress()) {
                        if (addr instanceof Inet4Address && localIp4 == null)
                            localIp4 = addr;
                        else if (addr instanceof Inet6Address && localIp6 == null)
                            localIp6 = addr;
                    }
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

        // Firefox binds to the IP4 address by preference
        if (localIp4 != null)
            return localIp4;

        if (localIp6 != null)
            return localIp6;

        // Nothing found. Grab the first address we can find
        NetworkInterface firstInterface = null;
        try {
            firstInterface = NetworkInterface.getNetworkInterfaces().nextElement();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        InetAddress firstAddress = null;
        if (firstInterface != null) {
            firstAddress = firstInterface.getInetAddresses().nextElement();
        }

        if (firstAddress != null)
            return firstAddress;

        throw new RuntimeException("Unable to find loopback address for localhost");
    }

    protected boolean connectToBrowser(long timeToWaitInMilliSeconds) throws IOException {
        long waitUntil = System.currentTimeMillis() + timeToWaitInMilliSeconds;
        while (!isConnected() && waitUntil > System.currentTimeMillis()) {
            try {
                connect();

            } catch (ConnectException e) {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException ie) {
                    throw new RuntimeException(ie);
                }
            }
        }
        if (System.currentTimeMillis() > waitUntil) {
            return false;
        }

        // Wait for and flush the initial data on the input stream
        in.skip(TELNET_STARTUP.length());

        return isConnected();
    }

    private void connect() throws IOException {
        socket = new Socket();
        socket.connect(address);
        in = new BufferedInputStream(socket.getInputStream());
        out = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    public String executeCommand(Class<? extends RuntimeException> throwOnFailure, String command) {
        if (isInJavascriptMode) {
            switchToChrome();
        }
        return sendMessageAndGetResponse(command);
    }

    //TODO: we might want to wrap the script in a function, like the SafariDriver does
    public String executeJavascript(Class<? extends RuntimeException> throwOnFailure, String script) {
        String message = "print " + script;
        if (!isInJavascriptMode) {
            switchToV8();
        }

        sendMessage(message);
        String response = nextResponse();

        // If there are no errors, we'll just return here
        if (!connectionError(response)) {
            if (!message.equals(response)) {
                throw new RuntimeException(String.format("Unexpected reply.\n" +
                        "Expected '%s' but had '%s'.", message, response));
            }
            response = nextResponse();
            if (!connectionError(response)) {
                return response;
            }
        }

        // Otherwise, we'll have to reconnect to V8
        flushErrors(response);
        switchToChrome();
        switchToV8();
        response = sendMessageAndGetResponse(message);
        return response;
    }

    private void switchToV8() {
        String response = sendMessageAndGetResponse("debug()");
        if (!response.contains("attached to ")) {
            throw new RuntimeException(String.format("Failed to connect to V8.\n" +
                    "Expected a response acknowledging connection to tab (in the form \"attached to <tab title>\"), " +
                    "but instead had \"%s\".", response));
        }
        isInJavascriptMode = true;
        try {
            Thread.sleep(500);
        } catch (Exception e) {
            
        }
    }

    private void switchToChrome() {
        sendMessage("exit");
        while (!nextResponse().equals("exit")) {}
        isInJavascriptMode = false;
    }

    private boolean connectionError(String response) {
        return response.equals(LOST_CONNECTION_TO_TAB) || response.equals(NOT_CONNECTED_TO_TAB);
    }

    private void flushErrors(String response) {
        if (response.equals(LOST_CONNECTION_TO_TAB)) {
            do {
                response = nextResponse();
            } while (!response.equals(NOT_CONNECTED_TO_TAB));
        }
    }

    protected String sendMessageAndGetResponse(String command) {
        sendMessage(command);
        expectResponse(command);
        return nextResponse();
    }

    protected void sendMessage(String message) {
        try {
            System.out.print("sendMessage(\"" + message + "\")");
            out.write(message + "\r\n");
            out.flush();
            System.out.println(";");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected String nextResponse() {
        try {
            System.out.print("nextResponse() = ");
            String line = stripPrompt(readLine());
            System.out.println(line);
            return line;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected String nextResponseAfter(String expected) {
        String response;
        do {
            response = nextResponse();
        } while (!response.equals(expected));
        return nextResponse();
    }

    protected void expectResponse(String expected) {
        String actual = nextResponse();
        if (!expected.equals(actual)) {
            throw new RuntimeException(String.format("Unexpected reply.\n" +
                    "Expected '%s' but had '%s'.", expected, actual));
        }
    }

    private String readLine() throws IOException {
        int size = 4096;
        int growBy = 1024;
        byte[] raw = new byte[size];
        int count = 0;

        for (;;) {
            int b = in.read();

            if (b == -1 || (char) b == '\n')
                break;
            raw[count++] = (byte) b;
            if (count == size) {
                size += growBy;
                byte[] temp = new byte[size];
                System.arraycopy(raw, 0, temp, 0, count);
                raw = temp;
            }
        }
        while (count > 0 && raw[count - 1] == '\r') {
            count--;
        }
        return new String(raw, 0, count, "UTF-8");
    }

    protected String stripPrompt(String response) {
        while(true) {
            if (response.startsWith(V8_PROMPT)) {
                response = response.substring(V8_PROMPT.length());
            } else if (response.startsWith(CHROME_PROMPT)) {
                response = response.substring(CHROME_PROMPT.length());
            } else {
                break;
            }
        }
        return response;
    }
}
