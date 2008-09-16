/*
 * Copyright 2007 ThoughtWorks, Inc
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.openqa.selenium.htmlunit;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.ScriptResult;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.WebWindow;
import com.gargoylesoftware.htmlunit.WebWindowEvent;
import com.gargoylesoftware.htmlunit.WebWindowListener;
import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.html.FrameWindow;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlInlineFrame;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.host.HTMLElement;
import org.apache.commons.httpclient.HttpState;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ScriptableObject;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.Speed;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.FindsById;
import org.openqa.selenium.internal.FindsByLinkText;
import org.openqa.selenium.internal.FindsByName;
import org.openqa.selenium.internal.FindsByXPath;
import org.openqa.selenium.internal.ReturnedCookie;

import java.net.ConnectException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HtmlUnitDriver implements WebDriver, SearchContext, JavascriptExecutor,
        FindsById, FindsByLinkText, FindsByXPath, FindsByName {
    private WebClient webClient;
    private WebWindow currentWindow;
    /** window name => history. */
    private Map<String, History> histories = new HashMap<String, History>();
    private boolean enableJavascript;
    private ProxyConfig proxyConfig;

    public HtmlUnitDriver(boolean enableJavascript) {
    this.enableJavascript = enableJavascript;

    webClient = newWebClient();
        webClient.addWebWindowListener(new WebWindowListener() {
            private boolean waitingToLoad;

            public void webWindowOpened(WebWindowEvent webWindowEvent) {
                waitingToLoad = true;
            }

            public void webWindowContentChanged(WebWindowEvent webWindowEvent) {
                WebWindow window = webWindowEvent.getWebWindow();
                if (waitingToLoad) {
                    waitingToLoad = false;
                    webClient.setCurrentWindow(window);
                }
                String windowName = window.getName();
                History history = histories.get(windowName);
                if (history == null) {
                    history = new History(window);
                    histories.put(windowName, history);
                }
                history.addNewPage(webWindowEvent.getNewPage());
            }

            public void webWindowClosed(WebWindowEvent webWindowEvent) {
                WebWindow window = webWindowEvent.getWebWindow();
                String windowName = window.getName();
                histories.remove(windowName);
                pickWindow();
            }
        });
    }

    public HtmlUnitDriver() {
      this(false);
    }

    private HtmlUnitDriver(boolean enableJavascript, WebWindow currentWindow) {
        this(enableJavascript);
        this.currentWindow = currentWindow;
    }

    /**
     * @return WebClient to use
     * @deprecated Please override modifyWebClient. This method will become private
     */
    @Deprecated
    protected WebClient newWebClient() {
        WebClient client = new WebClient();
        client.setThrowExceptionOnFailingStatusCode(false);
        client.setPrintContentOnFailingStatusCode(false);
        client.setJavaScriptEnabled(enableJavascript);
        client.setRedirectEnabled(true);
        try {
			client.setUseInsecureSSL(true);
		} catch (GeneralSecurityException e) {
			throw new RuntimeException(e);
		}

        // Ensure that we've set the proxy if necessary
        if (proxyConfig != null)
            client.setProxyConfig(proxyConfig);

        return modifyWebClient(client);
    }

    /**
     * Child classes can override this method to customise the webclient that
     * the HtmlUnit driver uses.
     *
     * @param client The client to modify
     * @return The modified client
     */
    protected WebClient modifyWebClient(WebClient client) {
        // Does nothing here to be overridden.
        return client;
    }

    public void setProxy(String host, int port) {
        proxyConfig = new ProxyConfig(host, port);
        webClient.setProxyConfig(proxyConfig);
    }

    public void get(String url) {
        try {
            URL fullUrl = new URL(url);
            webClient.getPage(fullUrl);
        } catch (UnknownHostException e) {
          // This should be fine
        } catch (ConnectException e) {
          // This might be expected
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        pickWindow();
    }

    protected void pickWindow() {
        currentWindow = webClient.getCurrentWindow();
        Page page = webClient.getCurrentWindow().getEnclosedPage();

        if (page == null)
          return;

        if (((HtmlPage) page).getFrames().size() > 0) {
            FrameWindow frame = ((HtmlPage) page).getFrames().get(0);
            if (!(frame.getFrameElement() instanceof HtmlInlineFrame))
                switchTo().frame(0);
        }
    }

    public String getCurrentUrl() {
        return lastPage().getWebResponse().getUrl().toString();
    }

    public String getTitle() {
        HtmlPage htmlPage = lastPage();
        if (htmlPage == null) {
            return null; // no page so there is no title
        }
        return htmlPage.getTitleText();
    }

    public boolean getVisible() {
        return false;
    }

    public void setVisible(boolean visible) {
        // no-op
    }

    public WebElement findElement(By by) {
        return by.findElement((SearchContext)this);
    }

    public List<WebElement> findElements(By by) {
        return by.findElements((SearchContext)this);
    }

    public String getPageSource() {
        WebResponse webResponse = lastPage().getWebResponse();
        return webResponse.getContentAsString();
    }

    public void close() {
        webClient = newWebClient();
    }

    public void quit() {
    	webClient = null;
    	currentWindow = null;
    }

    public Object executeScript(String script, Object... args) {
        if (!isJavascriptEnabled())
            throw new UnsupportedOperationException("Javascript is not enabled for this HtmlUnitDriver instance");

        Object[] parameters = new Object[args.length];

        for (int i = 0; i < args.length; i++) {
            if (!(args[i] instanceof HtmlUnitWebElement ||
                  args[i] instanceof Number ||
                  args[i] instanceof String ||
                  args[i] instanceof Boolean)) {
              throw new IllegalArgumentException(
                  "Argument must be a string, number, boolean or WebElement: " +
                      args[i] + " (" + args[i].getClass() + ")");                                  
            }

            if (args[i] instanceof HtmlUnitWebElement) {
                HtmlElement element = ((HtmlUnitWebElement) args[i]).getElement();
                parameters[i] = element.getScriptObject();
            } else {
                parameters[i] = args[i];
            }
        }

        script = "function() {" + script + "};";
        ScriptResult result = lastPage().executeJavaScript(script);
        Function func = (Function) result.getJavaScriptResult();
        
        result = lastPage().executeJavaScriptFunctionIfPossible(
        		func, 
        		(ScriptableObject) currentWindow.getScriptObject(), 
        		parameters, 
        		lastPage().getDocumentElement());
        
        Object value = result.getJavaScriptResult();

        if (value instanceof HTMLElement) {
            return new HtmlUnitWebElement(this, ((HTMLElement) value).getHtmlElementOrDie());
        }

        if (value instanceof Number) {
            return ((Number) value).longValue();
        }

        return result.getJavaScriptResult();
    }

  public TargetLocator switchTo() {
        return new HtmlUnitTargetLocator();
    }


  public Navigation navigate() {
    return new HtmlUnitNavigation();
  }

  private synchronized HtmlPage lastPage() {
        return (HtmlPage) currentWindow.getEnclosedPage();
    }

  public WebElement findElementByLinkText(String selector) {
    int equalsIndex = selector.indexOf('=') + 1;
    String expectedText = selector.substring(equalsIndex).trim();

    List<HtmlAnchor> anchors = lastPage().getAnchors();
    for (HtmlAnchor anchor : anchors) {
      if (expectedText.equals(anchor.asText())) {
        return newHtmlUnitWebElement(anchor);
      }
    }
    throw new NoSuchElementException("No link found with text: " + expectedText);
  }

  protected WebElement newHtmlUnitWebElement(HtmlElement element) {
    if (isJavascriptEnabled()) {
      return new RenderedHtmlUnitDriverWebElement(this, element);
    }
    return new HtmlUnitWebElement(this, element);
  }

  public List<WebElement> findElementsByLinkText(String selector) {
    int equalsIndex = selector.indexOf('=') + 1;
    String expectedText = selector.substring(equalsIndex).trim();

    List<HtmlAnchor> anchors = lastPage().getAnchors();
    Iterator<HtmlAnchor> allAnchors = anchors.iterator();
    List<WebElement> elements = new ArrayList<WebElement>();
    while (allAnchors.hasNext()) {
      HtmlAnchor anchor = allAnchors.next();
      if (expectedText.equals(anchor.asText())) {
        elements.add(newHtmlUnitWebElement(anchor));
      }
    }
    return elements;
  }

    public WebElement findElementById(String id) {
        try {
            HtmlElement element = lastPage().getHtmlElementById(id);
            return newHtmlUnitWebElement(element);
        } catch (ElementNotFoundException e) {
            throw new NoSuchElementException("Cannot find element with ID: " + id);
        }
    }

    public List<WebElement> findElementsById(String id) {
        return findElementsByXPath("//*[@id='" + id + "']");
    }

  public WebElement findElementByName(String name) {
    List<HtmlElement> allElements = lastPage().getHtmlElementsByName(name);
    if (allElements.size() > 0) {
        return newHtmlUnitWebElement(allElements.get(0));
    }

    throw new NoSuchElementException("Cannot find element with name: " + name);
  }

  @SuppressWarnings("unchecked")
  public List<WebElement> findElementsByName(String using) {
    List allElements = lastPage().getHtmlElementsByName(using);
    return convertRawHtmlElementsToWebElements(allElements);
  }

  public WebElement findElementByXPath(String selector) {
    	Object node = lastPage().getFirstByXPath(selector);
        if (node == null)
            throw new NoSuchElementException("Cannot locate a node using " + selector);
        if (node instanceof HtmlElement)
        	return newHtmlUnitWebElement((HtmlElement) node);
        throw new NoSuchElementException(String.format("Cannot find element with xpath %s", selector));
    }

    public List<WebElement> findElementsByXPath(String selector) {
    	List<?> nodes = lastPage().getByXPath(selector);
        return convertRawHtmlElementsToWebElements(nodes);
    }

    private List<WebElement> convertRawHtmlElementsToWebElements(List<?> nodes) {
        List<WebElement> elements = new ArrayList<WebElement>();

      for (Object node : nodes) {
    	if (node instanceof HtmlElement)
    		elements.add(newHtmlUnitWebElement((HtmlElement) node));
      }

        return elements;
    }

  public boolean isJavascriptEnabled() {
    return webClient.isJavaScriptEnabled();
  }

  public void setJavascriptEnabled(boolean enableJavascript) {
    this.enableJavascript = enableJavascript;
    webClient.setJavaScriptEnabled(enableJavascript);
  }

  private class HtmlUnitTargetLocator implements TargetLocator {
        public WebDriver frame(int frameIndex) {
            HtmlPage page = (HtmlPage) webClient.getCurrentWindow().getEnclosedPage();
            try {
                currentWindow = page.getFrames().get(frameIndex);
            } catch (IndexOutOfBoundsException e) {
                throw new NoSuchFrameException("Cannot find frame: " + frameIndex);
            }
            return HtmlUnitDriver.this;
        }

        public WebDriver frame(String name) {
            HtmlPage page = (HtmlPage) webClient.getCurrentWindow().getEnclosedPage();
            WebWindow window = webClient.getCurrentWindow();

            String[] names = name.split("\\.");
            for (String frameName : names) {
                try {
                    int index = Integer.parseInt(frameName);
                    window = page.getFrames().get(index);
                } catch (NumberFormatException e) {
                    window = null;
                    for (Object frame : page.getFrames()) {
                        FrameWindow frameWindow = (FrameWindow) frame;
                        if (frameName.equals(frameWindow.getFrameElement().getId())) {
                            window = frameWindow;
                            break;
                        } else if (frameName.equals(frameWindow.getName())) {
                            window = frameWindow;
                            break;
                        }
                    }
                    if (window == null) {
                        throw new NoSuchFrameException("Cannot find frame: " + name);
                    }
                } catch (IndexOutOfBoundsException e) {
                    throw new NoSuchFrameException("Cannot find frame: " + name);
                }

                page = (HtmlPage) window.getEnclosedPage();
            }

            currentWindow = window;
            return HtmlUnitDriver.this;
        }

        public WebDriver window(String windowId) {
            WebWindow window = webClient.getWebWindowByName(windowId);
            webClient.setCurrentWindow(window);
            pickWindow();
            return HtmlUnitDriver.this;
        }

        public Iterable<WebDriver> windowIterable() {
            return new Iterable<WebDriver>() {
              public Iterator<WebDriver> iterator() {
                return new HtmlUnitDriverIterator();
              }
            };
        }

        public WebDriver defaultContent() {
            pickWindow();
            return HtmlUnitDriver.this;
        }

    public WebElement activeElement() {
      Page page = currentWindow.getEnclosedPage();
      if (page instanceof HtmlPage) {
        HtmlElement element = ((HtmlPage) page).getFocusedElement();
        if (element == null) {
          List<? extends HtmlElement> allBodies =
              ((HtmlPage) page).getDocumentElement().getHtmlElementsByTagName("body");
          if (allBodies.size() > 0) {
            return newHtmlUnitWebElement(allBodies.get(0));
          }
        }
      }

      throw new NoSuchElementException("Unable to locate element with focus or body tag");
    }

    public Alert alert() {
            return null;
        }
    }

    protected WebDriver findActiveWindow() {
        WebWindow window = webClient.getCurrentWindow();
        HtmlPage page = (HtmlPage) window.getEnclosedPage();

        if (page != null && page.getFrames().size() > 0) {
            FrameWindow frame = page.getFrames().get(0);
            if (!(frame.getFrameElement() instanceof HtmlInlineFrame))
                return new HtmlUnitDriver(isJavascriptEnabled(), frame);
        }

        if (currentWindow != null && currentWindow.equals(window))
            return this;
        return new HtmlUnitDriver(isJavascriptEnabled(), window);
    }


    protected WebClient getWebClient() {
        return webClient;
    }

    protected WebWindow getCurrentWindow() {
        return currentWindow;
    }

    private class History {
        private final WebWindow window;
        private List<Page> history = new ArrayList<Page>();
        private int index = -1;

        private History(WebWindow window) {
            this.window = window;
        }

        public void addNewPage(Page newPage) {
            ++index;
            while (history.size() > index) {
                history.remove(index);
            }
            history.add(newPage);
        }

        public void goBack() {
            if (index > 0) {
                --index;
                window.setEnclosedPage(history.get(index));
            }
        }

        public void goForward() {
            if (index < history.size() - 1) {
                ++index;
                window.setEnclosedPage(history.get(index));
            }
        }
    }

    private class HtmlUnitNavigation implements Navigation {
      public void back() {
        String windowName = currentWindow.getName();
        History history = histories.get(windowName);
        history.goBack();
      }


      public void forward() {
          String windowName = currentWindow.getName();
          History history = histories.get(windowName);
          history.goForward();
      }


      public void to(String url) {
        get(url);
      }
    }

    public Options manage() {
        return new HtmlUnitOptions();
    }

    private class HtmlUnitOptions implements Options {
        private HttpState state;

        HtmlUnitOptions() {
            state = webClient.getWebConnection().getState();
        }

        public void addCookie(Cookie cookie) {
          String domain = getDomainForCookie(cookie);

            state.addCookie(new org.apache.commons.httpclient.Cookie(domain,
                    cookie.getName(), cookie.getValue(), cookie.getPath(), cookie.getExpiry(),
                    cookie.isSecure()));
        }

        public void deleteCookieNamed(String name) {
            //Assume the cookie either doesn't have a domain or has the same domain as the current
            //page. Delete the cookie for both cases.
            state.addCookie(new org.apache.commons.httpclient.Cookie(getHostName(), name, "", "/",
                    new Date(0), false));
            state.addCookie(new org.apache.commons.httpclient.Cookie("", name, "", "/", new Date(0),
                    false));
        }

        public void deleteCookie(Cookie cookie) {
            String domain = getDomainForCookie(cookie);

            state.addCookie(new org.apache.commons.httpclient.Cookie(domain,
                    cookie.getName(), cookie.getValue(), cookie.getPath(), new Date(0),
                    cookie.isSecure()));
        }

        public void deleteAllCookies() {
            state.clearCookies();
        }

        public Set<Cookie> getCookies() {
            HttpState state = webClient.getWebConnection().getState();
            org.apache.commons.httpclient.Cookie[] rawCookies = state.getCookies();
            
            Set<Cookie> retCookies = new HashSet<Cookie>();
            for(org.apache.commons.httpclient.Cookie c : rawCookies) {
                if("".equals(c.getDomain()) || getHostName().indexOf(c.getDomain()) != -1) {
                	if (c.getPath() != null && getPath().startsWith(c.getPath())) {
                		retCookies.add(new ReturnedCookie(c.getName(), c.getValue(), c.getDomain(), c.getPath(),
                            c.getExpiryDate(), c.getSecure()));
                	}
                }
            }
            return retCookies;  
        }

        private String getHostName() {
            return lastPage().getWebResponse().getUrl().getHost().toLowerCase();
        }
        
        private String getPath() {
        	return lastPage().getWebResponse().getUrl().getPath();
        }

        public Speed getSpeed() {
            throw new UnsupportedOperationException();
        }

        public void setSpeed(Speed speed) {
            throw new UnsupportedOperationException();
        }

        private String getDomainForCookie(Cookie cookie) {
            URL current = lastPage().getWebResponse().getUrl();
            String hostName = cookie.getDomain();
            if (hostName == null || "".equals(hostName)) {
                hostName = String.format("%s:%s", current.getHost(), current.getPort());
            }
            return hostName;
        }
    }

    private class HtmlUnitDriverIterator implements Iterator<WebDriver> {
      private Iterator<WebWindow> underlyingIterator;

      public HtmlUnitDriverIterator() {
        List<WebWindow> allWindows = new ArrayList<WebWindow>();
        for (WebWindow window : webClient.getWebWindows()) {
          WebWindow top = window.getTopWindow();
          if (!allWindows.contains(top))
            allWindows.add(top);
        }

        underlyingIterator = allWindows.iterator();
      }

      public boolean hasNext() {
        return underlyingIterator.hasNext();
      }

      public WebDriver next() {
        WebWindow window = underlyingIterator.next();
        return new HtmlUnitDriver(isJavascriptEnabled(), window);
      }

      public void remove() {
        throw new UnsupportedOperationException("remove");
      }
    }
}
