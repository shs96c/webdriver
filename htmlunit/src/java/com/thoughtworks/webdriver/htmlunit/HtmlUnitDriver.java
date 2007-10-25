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

package com.thoughtworks.webdriver.htmlunit;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.html.xpath.HtmlUnitXPath;
import com.thoughtworks.webdriver.Alert;
import com.thoughtworks.webdriver.NoSuchElementException;
import com.thoughtworks.webdriver.WebDriver;
import com.thoughtworks.webdriver.WebElement;
import com.thoughtworks.webdriver.By;
import org.jaxen.JaxenException;
import org.jaxen.XPath;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HtmlUnitDriver implements WebDriver {
    private WebClient webClient;
    private WebWindow currentWindow;

    public HtmlUnitDriver() {
        newWebClient();
        webClient.addWebWindowListener(new WebWindowListener() {
            private boolean waitingToLoad;

            public void webWindowOpened(WebWindowEvent webWindowEvent) {
                waitingToLoad = true;
            }

            public void webWindowContentChanged(WebWindowEvent webWindowEvent) {
                if (waitingToLoad) {
                    waitingToLoad = false;
                    webClient.setCurrentWindow(webWindowEvent.getWebWindow());
                }
            }

            public void webWindowClosed(WebWindowEvent webWindowEvent) {
                pickWindow();
            }
        });
    }

    private HtmlUnitDriver(WebWindow currentWindow) {
        this();
        this.currentWindow = currentWindow;
    }

    private void newWebClient() {
        webClient = new WebClient();
        webClient.setThrowExceptionOnFailingStatusCode(true);
        webClient.setJavaScriptEnabled(false);
        webClient.setRedirectEnabled(true);
    }

    public WebDriver get(String url) {
        try {
            URL fullUrl = new URL(url);
            Page page = webClient.getPage(fullUrl);
            page.initialize();
            pickWindow();
            return this;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void pickWindow() {
        currentWindow = webClient.getCurrentWindow();
        Page page = webClient.getCurrentWindow().getEnclosedPage();

        if (((HtmlPage) page).getFrames().size() > 0) {
            FrameWindow frame = (FrameWindow) ((HtmlPage) page).getFrames().get(0);
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

    public WebDriver setVisible(boolean visible) {
        // no-op
        return this;
    }

    @SuppressWarnings("unchecked")
    public List<WebElement> selectElements(String selector) {
        try {
            HtmlUnitXPath xpath = new HtmlUnitXPath(selector);
            List<HtmlElement> nodes = xpath.selectNodes(lastPage());
            List<WebElement> elements = new ArrayList<WebElement>();

            for (HtmlElement node : nodes) {
                elements.add(new HtmlUnitWebElement(this, node));
            }

            return elements;
        } catch (JaxenException e) {
            throw new RuntimeException(e);
        }
    }

    public WebElement findElement(By method) {
        switch(method.getHow()) {
            case ID:
                return selectElementById(method.getValue());

            case LINK:
                return selectLinkWithText(method.getValue());

            case XPATH:
                return selectElementUsingXPath(method.getValue());

            default:
                throw new RuntimeException("Unsupported element finder mechanism: " + method.getHow());
        }
    }

    public String getPageSource() {
        WebResponse webResponse = lastPage().getWebResponse();
        return webResponse.getContentAsString();
    }

    public WebDriver close() {
        newWebClient();
        return findActiveWindow();
    }


    public TargetLocator switchTo() {
        return new HtmlUnitTargetLocator();
    }

    private synchronized HtmlPage lastPage() {
        return (HtmlPage) currentWindow.getEnclosedPage();
    }

    @SuppressWarnings("unchecked")
    private WebElement selectLinkWithText(String expectedText) {
        List<HtmlAnchor> anchors = lastPage().getAnchors();
        for (HtmlAnchor anchor : anchors) {
            if (expectedText.equals(anchor.asText())) {
                return new HtmlUnitWebElement(this, anchor);
            }
        }
        throw new NoSuchElementException("No link found with text: " + expectedText);
    }

    private WebElement selectElementById(String id) {
        try {
            HtmlElement element = lastPage().getHtmlElementById(id);
            return new HtmlUnitWebElement(this, element);
        } catch (ElementNotFoundException e) {
            throw new NoSuchElementException("Cannot find element with ID: " + id);
        }
    }

    private WebElement selectElementUsingXPath(String selector) {
        try {
            XPath xpath = new HtmlUnitXPath(selector);
            Object node = xpath.selectSingleNode(lastPage());
            if (node == null)
                throw new NoSuchElementException("Cannot locate a node using " + selector);
            return new HtmlUnitWebElement(this, (HtmlElement) node);
        } catch (JaxenException e) {
            throw new RuntimeException(e);
        }
    }

    private class HtmlUnitTargetLocator implements TargetLocator {
        public WebDriver frame(int frameIndex) {
            HtmlPage page = (HtmlPage) webClient.getCurrentWindow().getEnclosedPage();
            currentWindow = (WebWindow) page.getFrames().get(frameIndex);
            return HtmlUnitDriver.this;
        }

        public WebDriver window(String windowId) {
            WebWindow window = webClient.getWebWindowByName(windowId);
            webClient.setCurrentWindow(window);
            pickWindow();
            return HtmlUnitDriver.this;
        }

        public WebDriver defaultContent() {
            pickWindow();
            return HtmlUnitDriver.this;
        }

        public Alert alert() {
            return null;
        }
    }

    protected WebDriver findActiveWindow() {
        WebWindow window = webClient.getCurrentWindow();
        HtmlPage page = (HtmlPage) window.getEnclosedPage();

        if (page != null && page.getFrames().size() > 0) {
            FrameWindow frame = (FrameWindow) page.getFrames().get(0);
            if (!(frame.getFrameElement() instanceof HtmlInlineFrame))
                return new HtmlUnitDriver(frame);
        }

        if (currentWindow != null && currentWindow.equals(window))
            return this;
        return new HtmlUnitDriver(window);
    }
}
