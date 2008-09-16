package org.openqa.selenium.chrome;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.internal.ChromeConnectionFactory;
import org.openqa.selenium.internal.*;

import java.util.List;
import java.util.Set;

public class ChromeDriver implements WebDriver, SearchContext, JavascriptExecutor,
        FindsById, FindsByClassName, FindsByLinkText, FindsByXPath {
    protected final static String ELEMENTS = "document.webdriverElements";
    protected final static String LOADING = "document.webdriverLoading";

    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_PORT = 12345;
    private final ChromeConnection connection;

    public ChromeDriver() {
        connection = connectTo(DEFAULT_HOST, DEFAULT_PORT);

        if (!connection.isConnected()) {
            throw new RuntimeException("Unable to connect to Chrome.");
        }
    }

    // Navigation
    public void get(String url) {
        //TODO: handle anchors (i.e. if we're already on that page but on a different anchor, checking that the page has loaded will probably be done in a different way)
        connection.executeJavascript(RuntimeException.class, LOADING + " = true");
        connection.executeJavascript(RuntimeException.class,
                "window.location = \"" + escapeQuotes(url) + "\"");

        while ("true".equals(connection.executeJavascript(RuntimeException.class, LOADING))) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String getCurrentUrl() {
        return connection.executeJavascript(RuntimeException.class, "window.location.href");
    }

    public String getTitle() {
        return connection.executeJavascript(RuntimeException.class, "document.title");
    }

    public boolean getVisible() {
        return true;
    }

    public void setVisible(boolean visible) {
        // no-op
    }

    public List<WebElement> findElements(By by) {
        return by.findElements((SearchContext)this);
    }

    public WebElement findElement(By by) {
      return by.findElement((SearchContext)this);
    }

    public String getPageSource() {
        throw new UnsupportedOperationException("getPageSource");
    }

    public void close() {
        throw new UnsupportedOperationException("close");
    }

    public void quit() {
        connection.quit();
    }

    public TargetLocator switchTo() {
    	throw new UnsupportedOperationException("switchTo");
    }

    public Navigation navigate() {
    	return new ChromeNavigation();
    }

    public Options manage() {
        return new ChromeOptions();
    }

    public void waitForLoadToComplete() {
        while (!"complete".equals(
                connection.executeJavascript(RuntimeException.class, "document.readyState"))) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                // Do nothing
            }
        }
    }

    public WebElement findElementByLinkText(String using) {
        String res = connection.executeJavascript(RuntimeException.class,
                "var foundElement = false;" +
                "for (var i = 0; i < document.links.length; i++) {" +
                "  var element = document.links[i];" +
                "  if (element.text == '" + using +"') {" +
                "    foundElement = true;" +
                addToElements() +
                "    break;" +
                "  }" +
                "}" +
                "if (!foundElement) {" +
                "  \"No element found\";" +
                "}");

        if (!"No element found".equals(res)) {
            return new ChromeWebElement(this, connection, res);
        }

        throw new NoSuchElementException("Cannot find element with link text: " + using);
    }

    public List<WebElement> findElementsByLinkText(String using) {
    	throw new UnsupportedOperationException("findElementsByLinkText");
    }

    public WebElement findElementById(String using) {
        String id = connection.executeJavascript(RuntimeException.class,
                "var element = document.getElementById(\"" + using + "\");" +
                 addToElements()
                );

        if (!"No element found".equals(id)) {
            return new ChromeWebElement(this, connection, id);
        }

        throw new NoSuchElementException("Cannot find element with id: " + using);
    }

    public List<WebElement> findElementsById(String using) {
        throw new UnsupportedOperationException("findElementsById");
    }

    public WebElement findElementByXPath(String using) {
        String result = connection.executeJavascript(RuntimeException.class,
                "var element = document.evaluate(\"" + using + "\", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE,  null).singleNodeValue;" +
                addToElements());


        if (!"No element found".equals(result)) {
            return new ChromeWebElement(this, connection, result);
        }

        throw new NoSuchElementException("Cannot find element using xpath: " + using);
    }

    public List<WebElement> findElementsByXPath(String using) {
        throw new UnsupportedOperationException("findElementsByXPath");
    }

    public List<WebElement> findElementsByClassName(String using) {
    	throw new UnsupportedOperationException("findElementsByClassName");
    }

    public WebElement findElementByClassName(String using) {
    	throw new UnsupportedOperationException("findElementByClassName");
    }

    public Object executeScript(String script, Object... args) {
        throw new UnsupportedOperationException("executeScript");
    }

    private class ChromeOptions implements Options {
        public void addCookie(Cookie cookie) {
            throw new UnsupportedOperationException("addCookie");
        }

        public void deleteCookieNamed(String name) {
            throw new UnsupportedOperationException("deleteCookieNamed");
        }

        public void deleteCookie(Cookie cookie) {
            throw new UnsupportedOperationException("deleteCookie");
        }

        public void deleteAllCookies() {
            throw new UnsupportedOperationException("deleteAllCookies");
        }

        public Set<Cookie> getCookies() {
            throw new UnsupportedOperationException("getCookies");
        }

        public Speed getSpeed() {
            throw new UnsupportedOperationException("getMouseSpeed");
        }

        public void setSpeed(Speed speed) {
            throw new UnsupportedOperationException("setMouseSpeed");
        }

        private String getCurrentHost() {
            throw new UnsupportedOperationException("getCurrentHost");
		}
    }

    private class ChromeNavigation implements Navigation {
      public void back() {
          connection.executeJavascript(RuntimeException.class, "history.go(-1)");
      }

      public void forward() {
          connection.executeJavascript(RuntimeException.class, "history.go(1)");
      }

      public void to(String url) {
        get(url);
      }
    }

    protected ChromeConnection connectTo(String host, int port) {
        return ChromeConnectionFactory.connectTo(host, port);
    }

    private String escapeQuotes(String text) {
        return text.replaceAll("\"", "\\\"");
    }

    private String addToElements()  {
        return "if (element) {" +
               "    if (!" + ELEMENTS + ")" +
               "      " + ELEMENTS + " = new Array();" +
               "    " + ELEMENTS + ".push(element) - 1;" +
               "} else " +
               "    \"No element found\";";
    }

    private String addManyElements() {
        return "var toReturn = \"\"" +
        "for (var i = 0; i < elements.length; i++) {" +
        "  toReturn += (" + ELEMENTS + ".push(elements[i]) - 1) + \" \"" +
        "}" +
        "toReturn;";
    }
}
