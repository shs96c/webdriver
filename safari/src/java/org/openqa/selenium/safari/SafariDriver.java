package org.openqa.selenium.safari;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Speed;
import org.openqa.selenium.internal.FindsByLinkText;
import org.openqa.selenium.internal.FindsById;
import org.openqa.selenium.internal.FindsByXPath;
import org.openqa.selenium.internal.ReturnedCookie;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Date;
import java.util.HashSet;
import java.net.URL;
import java.net.MalformedURLException;

public class SafariDriver implements WebDriver, FindsByLinkText, FindsById, 
		FindsByXPath, SearchContext {
    protected final static String ELEMENTS = "document.webdriverElements";
    private AppleScript appleScript;

    public SafariDriver() {
        appleScript = new AppleScript();
        appleScript.executeApplescript("tell application \"" + AppleScript.APP + "\"\ractivate\rend tell");
        appleScript.executeJavascript("if (!" + ELEMENTS + ") { " + ELEMENTS + " = new Array(); }");
    }

    // Navigation
    public void get(String url) {
        appleScript.executeApplescript("tell application \"" + AppleScript.APP + "\"\rset URL in document 1 to \"" + url + "\"\rend tell");
        waitForLoadToComplete();
    }

    public String getCurrentUrl() {
        return appleScript.executeJavascript("return document.location");
    }

    public String getTitle() {
        return appleScript.executeJavascript("return document.title");
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
    	appleScript.executeApplescript("tell application \"" + AppleScript.APP + "\" to quit");
    }
    
    public TargetLocator switchTo() {
    	throw new UnsupportedOperationException("switchTo");
    }

    public Navigation navigate() {
    	throw new UnsupportedOperationException("navigate");
    }

    public Options manage() {
        return new SafariOptions();
    }

    public void waitForLoadToComplete() {
        while (!"complete".equals(appleScript.executeJavascript("return document.readyState"))) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                // Do nothing
            }
        }
    }

    public WebElement findElementByLinkText(String using) {
        String res = appleScript.executeJavascript(
                "for (var i = 0; i < document.links.length; i++) {\r" +
                "  var element = document.links[i];\r" +
                "  if (element.text == '" + using +"') {\r" +
                addToElements() +
                "  }\r" +
                "} return \"No element found\";");

        if (!"No element found".equals(res)) {
            return new SafariWebElement(this, res);
        }

        throw new NoSuchElementException("Cannot find element with link text: " + using);
    }

    public List<WebElement> findElementsByLinkText(String using) {
    	throw new UnsupportedOperationException("findElementsByLinkText");
    }


    public WebElement findElementById(String using) {
        String id = appleScript.executeJavascript(
                "var element = document.getElementById(\"" + using + "\");" +
                 addToElements()
                );

        if (!"No element found".equals(id)) {
            return new SafariWebElement(this, id);
        }

        throw new NoSuchElementException("Cannot find element with id: " + using);
    }

    public List<WebElement> findElementsById(String using) {
        throw new UnsupportedOperationException("findElementsById");
    }


    public WebElement findElementByXPath(String using) {
        String result = appleScript.executeJavascript(
                "var element = document.evaluate(\"" + using + "\", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE,  null).singleNodeValue;\r" +
                addToElements());


        if (!"No element found".equals(result)) {
            return new SafariWebElement(this, result);
        }

        throw new NoSuchElementException("Cannot find element using xpath: " + using);
    }

    public List<WebElement> findElementsByXPath(String using) {
        String result = appleScript.executeJavascript(
            "var result = document.evaluate(\"" + using + "\", document, null, XPathResult.ORDERED_NODE_ITERATOR_TYPE,  null).singleNodeValue;\r" +
            "var elements = new Array();\r" +
            "var element = result.iterateNext();\r" +
            "while (element) {" +
            "  elements.push(element);\r" +
            "  element = result.iterateNext();\r" +
            "}\r" +
            addManyElements()
        );

        String[] ids = result.split(" ");
        List<WebElement> toReturn = new ArrayList<WebElement>();
        for (String id : ids)
            toReturn.add(new SafariWebElement(this, id));
        return toReturn;
    }

    public List<WebElement> findElementsByClassName(String using) {
    	throw new UnsupportedOperationException("findElementsByClassName");
    }
      
    public WebElement findElementByClassName(String using) {
    	throw new UnsupportedOperationException("findElementByClassName");
    }
      
    private String addToElements()  {
        return "if (element) { " +
               "    if (!" + ELEMENTS + ")\r" +
               "      " + ELEMENTS + " = new Array();\r" +
               "    return " + ELEMENTS + ".push(element) - 1;\r" +
               "} return \"No element found\"";
    }

    private String addManyElements() {
        return "var toReturn = \"\"\r" +
        "for (var i = 0; i < elements.length; i++) {\r" +
        "  toReturn += (" + ELEMENTS + ".push(elements[i]) - 1) + \" \"\r" +
        "}\r" +
        "return toReturn;";
    }

    private class SafariOptions implements Options {
        public void addCookie(Cookie cookie) {
            appleScript.executeJavascript("document.cookie = \"" + cookie.toString() + "\"");
        }

        public void deleteCookieNamed(String name) {
            deleteCookie(new ReturnedCookie(name, "", getCurrentHost(), "", null, false));
        }

        public void deleteCookie(Cookie cookie) {
            Date dateInPast = new Date(0);
			Cookie toDelete = new ReturnedCookie(cookie.getName(), cookie.getValue(), cookie.getDomain(), cookie.getPath(), dateInPast, false);
			addCookie(toDelete);
        }

        public void deleteAllCookies() {
            Set<Cookie> cookies = getCookies();
			for (Cookie cookie : cookies) {
				deleteCookie(cookie);
			}
        }

        public Set<Cookie> getCookies() {
            String currentUrl = getCurrentHost();

			Set<Cookie> toReturn = new HashSet<Cookie>();
			String allDomainCookies = appleScript.executeJavascript("return document.cookie");

            String[] cookies = allDomainCookies.split("; ");
			for (String cookie : cookies) {
				String[] parts = cookie.split("=");
				if (parts.length != 2) {
					continue;
				}

				toReturn.add(new ReturnedCookie(parts[0], parts[1], currentUrl, "", null, false));
			}

	        return toReturn;
        }

        public Speed getSpeed() {
            throw new UnsupportedOperationException("getMouseSpeed");
        }

        public void setSpeed(Speed speed) {
            throw new UnsupportedOperationException("setMouseSpeed");
        }

        private String getCurrentHost() {
			try {
				URL url = new URL(getCurrentUrl());
				return url.getHost();
			} catch (MalformedURLException e) {
				return "";
			}
		}
    }
}
