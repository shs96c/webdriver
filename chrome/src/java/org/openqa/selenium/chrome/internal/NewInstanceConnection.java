package org.openqa.selenium.chrome.internal;

import org.openqa.selenium.chrome.ChromeLauncher;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.BindException;

public class NewInstanceConnection extends AbstractChromeConnection {
    private static long TIMEOUT_IN_SECONDS = 20;
    private static long MILLIS_IN_SECONDS = 1000;
    private ChromeBinary binary;
    private Socket lockSocket;

    public NewInstanceConnection(String host, int port) throws IOException {
        getLock(port);
        try {
          int portToUse = determineNextFreePort(host, port);

          binary = new ChromeLauncher(portToUse).start();

          setAddress(host, portToUse);

          connectToBrowser(TIMEOUT_IN_SECONDS * MILLIS_IN_SECONDS);
        } finally {
          releaseLock();
        }
    }

    protected void getLock(int port) throws IOException {
      InetSocketAddress address = getAddressForLock(port);

      lockSocket = new Socket();
      long maxWait = System.currentTimeMillis() + 45000;  // 45 seconds

      while (System.currentTimeMillis() < maxWait) {
        try {
          if (isLockFree(address))
            return;
          Thread.sleep(500);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }

      throw new IOException("Unable to bind to locking port");
    }

    protected InetSocketAddress getAddressForLock(int port) {
      int lockPort = port - 1;
      return new InetSocketAddress("localhost", lockPort);
    }

    private boolean isLockFree(InetSocketAddress address) throws IOException {
      try {
        lockSocket.bind(address);
        return true;
      } catch (BindException e) {
        return false;
      }
    }

    protected void releaseLock() throws IOException {
      if (lockSocket != null && lockSocket.isBound())
        lockSocket.close();
    }

    protected int determineNextFreePort(String host, int port) throws IOException {
      // Attempt to connect to the given port on the host
      // If we can't connect, then we're good to use it
      int newport;

      for (newport = port; newport < port + 200; newport++) {
        Socket socket = new Socket();
        InetSocketAddress address = new InetSocketAddress(host, newport);

        try {
          socket.bind(address);
          return newport;
        } catch (BindException e) {
          // Port is already bound. Skip it and continue
        } finally {
          socket.close();
        }
      }

      throw new RuntimeException(String.format("Cannot find free port in the range %d to %d ", port, newport));
    }

    public void quit() {
        binary.kill();
    }
}
