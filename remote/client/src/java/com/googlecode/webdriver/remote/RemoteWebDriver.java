package com.googlecode.webdriver.remote;

import com.googlecode.webdriver.By;
import com.googlecode.webdriver.Cookie;
import com.googlecode.webdriver.NoSuchElementException;
import com.googlecode.webdriver.Speed;
import com.googlecode.webdriver.WebDriver;
import com.googlecode.webdriver.WebElement;
import com.googlecode.webdriver.internal.FindsById;
import com.googlecode.webdriver.internal.FindsByLinkText;
import com.googlecode.webdriver.internal.FindsByName;
import com.googlecode.webdriver.internal.FindsByXPath;
import com.googlecode.webdriver.internal.ReturnedCookie;

import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RemoteWebDriver implements WebDriver, FindsById, FindsByLinkText, FindsByName, FindsByXPath {
	private CommandExecutor executor;
    private Capabilities capabilities;
    private SessionId sessionId;

    public RemoteWebDriver(Capabilities desiredCapabilities) throws Exception {
		executor = new HttpCommandExecutor();

        Response response = execute("newSession", desiredCapabilities);

//        capabilities = new JsonToBeanConverter().convert(DesiredCapabilities.class, rawText);
        sessionId = new SessionId(response.getSessionId());
    }

    public Capabilities getCapabilities() {
        return capabilities;
    }

    public void get(String url) {
        execute("get", url);
    }

    public String getTitle() {
        Response response = execute("getTitle");
        return response.getValue().toString();
    }

    public String getCurrentUrl() {
        return execute("currentUrl").getValue().toString();
    }


    public boolean getVisible() {
        Response response = execute("getVisible");
        return (Boolean) response.getValue();
    }

    public void setVisible(boolean visible) {
        execute("setVisible", visible);
    }

    public List<WebElement> findElements(By by) {
        throw new UnsupportedOperationException();
    }

    public WebElement findElement(By by) {
        return by.findElement(this);
    }


    public WebElement findElementById(String using) {
        Response response = execute(NoSuchElementException.class, "findElement", "id", using);
        return getElementFrom(response);
    }

    public List<WebElement> findElementsById(String using) {
        throw new UnsupportedOperationException();
    }


    public WebElement findElementByLinkText(String using) {
        Response response = execute(NoSuchElementException.class, "findElement", "link text", using);
        return getElementFrom(response);
    }

    public List<WebElement> findElementsByLinkText(String using) {
        throw new UnsupportedOperationException();
    }

    public WebElement findElementByName(String using) {
        Response response = execute(NoSuchElementException.class, "findElement", "name", using);
        return getElementFrom(response);
    }

    public List<WebElement> findElementsByName(String using) {
        throw new UnsupportedOperationException();
    }

    public WebElement findElementByXPath(String using) {
        Response response = execute(NoSuchElementException.class, "findElement", "xpath", using);
        return getElementFrom(response);
    }

    public List<WebElement> findElementsByXPath(String using) {
        throw new UnsupportedOperationException();
    }// Misc

    public String getPageSource() {
        return (String) execute("pageSource").getValue();
    }

    public void close() {
        throw new UnsupportedOperationException();
    }

    public void quit() {
        throw new UnsupportedOperationException();
    }

    public TargetLocator switchTo() {
        throw new UnsupportedOperationException();
    }

    public Navigation navigate() {
        throw new UnsupportedOperationException();
    }

    public Options manage() {
        return new RemoteWebDriverOptions();
    }

    @SuppressWarnings("unchecked")
	private WebElement getElementFrom(Response response) {
        try {
            Map<Object, Object> rawResponse = (Map<Object, Object>) response.getValue();
            RemoteWebElement toReturn = new RemoteWebElement();
            toReturn.setParent(this);
            toReturn.setId(String.valueOf(rawResponse.get("id")));
            return toReturn;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected Response execute(String commandName, Object... parameters) {
        return execute(RuntimeException.class, commandName, parameters);
    }

    @SuppressWarnings("unchecked")
	protected Response execute(Class<? extends RuntimeException> throwOnFailure, String commandName, Object... parameters) {
        Command command = new Command(sessionId, new Context("foo"), commandName, parameters);

        Response response = new Response();

        try {
            response = executor.execute(command);
        } catch (Exception e) {
            response.setError(true);
            response.setValue(e.getStackTrace());
        }

        if (response.isError()) {
            RuntimeException toThrow;
            try {
                Constructor<? extends RuntimeException> constructor = throwOnFailure.getConstructor(String.class);
                toThrow = constructor.newInstance("Place holder");
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Place holder in backup exception");
            }

            List elements = (List) response.getValue();
            StackTraceElement[] trace = new StackTraceElement[elements.size()];

            for (int i = 0; i < elements.size(); i++) {
              Map values = (Map) elements.get(i);

              // I'm so sorry.
              long lineNumber = (Long) values.get("lineNumber");

              trace[i] = new StackTraceElement((String) values.get("className"),
                                               (String) values.get("methodName"),
                                               (String) values.get("fileName"),
                                               (int) lineNumber);
            }

            toThrow.setStackTrace(trace);
            throw toThrow;
        }

        return response;
    }

  private class RemoteWebDriverOptions implements Options {
    public void addCookie(Cookie cookie) {
      execute(RuntimeException.class, "addCookie", cookie);
    }

    public void deleteCookieNamed(String name) {
      HashMap<String, String> map = new HashMap<String, String>();
      map.put("name", name);
      execute(RuntimeException.class, "deleteCookie", map);
    }

    public void deleteCookie(Cookie cookie) {
      deleteCookieNamed(cookie.getName());
    }

    public void deleteAllCookies() {
      execute(RuntimeException.class, "deleteAllCookies");
    }

    public Set<Cookie> getCookies() {
      Object returned = execute(RuntimeException.class, "getAllCookies").getValue();

      try {
        List<Map<String, Object>> cookies = new JsonToBeanConverter().convert(List.class, returned);
        Set<Cookie> toReturn = new HashSet<Cookie>();
        for (Map<String, Object> rawCookie : cookies) {
          String name = (String) rawCookie.get("name");
          String value = (String) rawCookie.get("value");
          String path = (String) rawCookie.get("path");
          String domain = (String) rawCookie.get("domain");
          Boolean secure = (Boolean) rawCookie.get("secure");
          toReturn.add(new ReturnedCookie(name, value, domain, path, null, secure));
        }

        return toReturn;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }

    }

    public Speed getMouseSpeed() {
      throw new UnsupportedOperationException("getMouseSpeed");
    }

    public void setMouseSpeed(Speed speed) {
      throw new UnsupportedOperationException("setMouseSpeed");

    }
  }
}
