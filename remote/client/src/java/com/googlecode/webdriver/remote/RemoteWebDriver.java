package com.googlecode.webdriver.remote;

import com.googlecode.webdriver.WebDriver;
import com.googlecode.webdriver.WebElement;
import com.googlecode.webdriver.By;
import com.googlecode.webdriver.internal.FindsById;
import com.googlecode.webdriver.internal.FindsByLinkText;
import com.googlecode.webdriver.internal.FindsByName;
import com.googlecode.webdriver.internal.FindsByXPath;

import java.util.List;

public class RemoteWebDriver implements WebDriver, FindsById, FindsByLinkText, FindsByName, FindsByXPath {
	private CommandExecutor executor;
    private Capabilities capabilities;
    private SessionId sessionId;

    public RemoteWebDriver(Capabilities desiredCapabilities) throws Exception {
		executor = new HttpCommandExecutor();

        Response response = execute("newSession", desiredCapabilities);
        String rawText = response.getResponseText();

        capabilities = new JsonToBeanConverter().convert(DesiredCapabilities.class, rawText);
        sessionId = response.getSessionId();
    }

    public Capabilities getCapabilities() {
        return capabilities;
    }

    public void get(String url) {
        execute("get", url);
    }

    public String getTitle() {
        Response response = execute("getTitle");
        return response.getResponseText();
    }

    public String getCurrentUrl() {
        return execute("currentUrl").getResponseText();
    }


    public boolean getVisible() {
        Response response = execute("getVisible");
        return Boolean.valueOf(response.getResponseText());
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
        Response response = execute("findElement", "id", using);
        return getElementFrom(response);
    }

    public List<WebElement> findElementsById(String using) {
        throw new UnsupportedOperationException();
    }


    public WebElement findElementByLinkText(String using) {
        Response response = execute("findElement", "link text", using);
        System.out.println("response = " + response);
        return getElementFrom(response);
    }

    public List<WebElement> findElementsByLinkText(String using) {
        throw new UnsupportedOperationException();
    }

    public WebElement findElementByName(String using) {
        Response response = execute("findElement", "name", using);
        System.out.println("response = " + response);
        return getElementFrom(response);
    }

    public List<WebElement> findElementsByName(String using) {
        throw new UnsupportedOperationException();
    }

    public WebElement findElementByXPath(String using) {
        Response response = execute("findElement", "xpath", using);
        System.out.println("response = " + response);
        return getElementFrom(response);
    }

    public List<WebElement> findElementsByXPath(String using) {
        throw new UnsupportedOperationException();
    }// Misc
    public String getPageSource() {
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
    }

    private WebElement getElementFrom(Response response) {
        try {
            RemoteWebElement toReturn = new JsonToBeanConverter().convert(RemoteWebElement.class, response.getResponseText());
            toReturn.setParent(this);
            return toReturn;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected Response execute(String commandName, Object... parameters) {
        Command command = new Command(sessionId, new Context("foo"), commandName, parameters);
        try {
            return executor.execute(command);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
