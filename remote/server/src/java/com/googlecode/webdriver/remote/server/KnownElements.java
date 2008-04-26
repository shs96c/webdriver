package com.googlecode.webdriver.remote.server;

import com.googlecode.webdriver.WebElement;

import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class KnownElements {
    private Map<String, WebElement> elements = new HashMap<String, WebElement>();
    private int nextId;

    public String add(WebElement element) {
        String id = getNextId();
        elements.put(id, proxyElement(element, id));
        return id;
    }

    public WebElement get(String elementId) {
        return elements.get(elementId);
    }

    // WebDriver is single threaded. Expect only a single thread at a time to access this
    private String getNextId() {
        return String.valueOf(nextId++);
    }

    private WebElement proxyElement(final WebElement element, final String id) {
        InvocationHandler handler = new InvocationHandler() {
            public Object invoke(Object object, Method method, Object[] objects) throws Throwable {
                if ("getId".equals(method.getName()))
                    return id;
                else
                    return method.invoke(element, objects);
            }
        };

        return (WebElement) Proxy.newProxyInstance(element.getClass().getClassLoader(),
                new Class[] { WebElement.class, HasId.class },
                handler);
    }

    private static interface HasId {
        String getId();
    }
}
