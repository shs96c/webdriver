package com.googlecode.webdriver.remote;

import com.googlecode.webdriver.By;
import com.googlecode.webdriver.Cookie;
import com.googlecode.webdriver.Speed;
import com.googlecode.webdriver.WebDriver;
import com.googlecode.webdriver.WebElement;
import com.googlecode.webdriver.internal.FindsById;
import com.googlecode.webdriver.internal.FindsByLinkText;
import com.googlecode.webdriver.internal.FindsByName;
import com.googlecode.webdriver.internal.FindsByXPath;
import com.googlecode.webdriver.internal.ReturnedCookie;
import static com.googlecode.webdriver.remote.MapMaker.map;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
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
        return by.findElements(this);
    }

    public WebElement findElement(By by) {
        return by.findElement(this);
    }


    public WebElement findElementById(String using) {
        Response response = execute("findElement", "id", using);
        return getElementFrom(response);
    }

    public List<WebElement> findElementsById(String using) {
      Response response = execute("findElements", "id", using);
      return getElementsFrom(response);
    }


    public WebElement findElementByLinkText(String using) {
        Response response = execute("findElement", "link text", using);
        return getElementFrom(response);
    }

    public List<WebElement> findElementsByLinkText(String using) {
        Response response = execute("findElements", "link text", using);
        return getElementsFrom(response);
    }

  public WebElement findElementByName(String using) {
        Response response = execute("findElement", "name", using);
        return getElementFrom(response);
    }

    public List<WebElement> findElementsByName(String using) {
      Response response = execute("findElements", "name", using);
      return getElementsFrom(response);
    }

    public WebElement findElementByXPath(String using) {
        Response response = execute("findElement", "xpath", using);
        return getElementFrom(response);
    }

    public List<WebElement> findElementsByXPath(String using) {
      Response response = execute("findElements", "xpath", using);
      return getElementsFrom(response);
    }

    // Misc

    public String getPageSource() {
        return (String) execute("pageSource").getValue();
    }

    public void close() {
        execute("close");
    }

    public void quit() {
        execute("quit");
    }

    public TargetLocator switchTo() {
        return new RemoteTargetLocator();
    }

    public Navigation navigate() {
        return new RemoteNavigation();
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

    protected List<WebElement> getElementsFrom(Response response) {
        List<WebElement> toReturn = new ArrayList<WebElement>();
        List<String> urls = (List<String>) response.getValue();
        for (String url : urls) {
            // We cheat here, because we know that the URL for an element ends with its ID.
            // This is lazy and bad. We should, instead, go to each of the URLs in turn.
            String[] parts = url.split("/");
            RemoteWebElement element = new RemoteWebElement();
            element.setId(parts[parts.length - 1]);
            element.setParent(this);
            toReturn.add(element);
        }

      return toReturn;
    }

    @SuppressWarnings({"unchecked"})
    protected Response execute(String commandName, Object... parameters) {
      Command command = new Command(sessionId, new Context("foo"), commandName, parameters);

      Response response = new Response();

      try {
        response = executor.execute(command);
      } catch (Exception e) {
        response.setError(true);
        response.setValue(e.getStackTrace());
      }

      if (response.isError()) {
        Map rawException = (Map) response.getValue();

        String message = (String) rawException.get("message");
        String className = (String) rawException.get("class");

        RuntimeException toThrow;
        try {
          Class<?> aClass;
          try {
            aClass = Class.forName(className);
            if (!RuntimeException.class.isAssignableFrom(aClass)) {
              aClass = RuntimeException.class;
            }
          } catch (ClassNotFoundException e) {
            aClass = RuntimeException.class;
          }

          try {
            Constructor<? extends RuntimeException> constructor =
                (Constructor<? extends RuntimeException>) aClass.getConstructor(String.class);
            toThrow = constructor.newInstance(message);
          } catch (NoSuchMethodException e) {
            toThrow = (RuntimeException) aClass.newInstance();
          }

          List<Map> elements = (List<Map>) rawException.get("stackTrace");
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
        } catch (Exception e) {
          toThrow = new RuntimeException(e);
        }
        throw toThrow;
      }

      return response;
    }

    private class RemoteWebDriverOptions implements Options {

      public void addCookie(Cookie cookie) {
        execute("addCookie", cookie);
      }

      public void deleteCookieNamed(String name) {
        execute("deleteCookie", map("name", name));
      }

      public void deleteCookie(Cookie cookie) {
        deleteCookieNamed(cookie.getName());
      }

      public void deleteAllCookies() {
        execute("deleteAllCookies");
      }

      public Set<Cookie> getCookies() {
        Object returned = execute("getAllCookies").getValue();

        try {
          List<Map<String, Object>> cookies =
              new JsonToBeanConverter().convert(List.class, returned);
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

    private class RemoteNavigation implements Navigation {

      public void back() {
        execute("back");
      }

      public void forward() {
        execute("forward");
      }

      public void to(String url) {
        get(url);
      }
    }

    private class RemoteTargetLocator implements TargetLocator {

      public WebDriver frame(int frameIndex) {
        execute("switchToFrame", map("id", frameIndex));
        return RemoteWebDriver.this;
      }

      public WebDriver frame(String frameName) {
        execute("switchToFrame", map("id", frameName));
        return RemoteWebDriver.this;
      }

      public WebDriver window(String windowName) {
        execute("switchToWindow", map("name", windowName));
        return RemoteWebDriver.this;
      }

      public WebDriver defaultContent() {
        execute("switchToFrame", map("id", null));
        return RemoteWebDriver.this;
      }
    }
  }
