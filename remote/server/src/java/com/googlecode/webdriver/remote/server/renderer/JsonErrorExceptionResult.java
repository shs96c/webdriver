package com.googlecode.webdriver.remote.server.renderer;

import com.googlecode.webdriver.remote.Response;
import com.googlecode.webdriver.remote.server.rest.Handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JsonErrorExceptionResult extends ErrorJsonResult {
    private final String exceptionName;

    public JsonErrorExceptionResult(String exceptionName, String responseOn) {
        super(responseOn);
        this.exceptionName = exceptionName.substring(1);
    }

    public void render(HttpServletRequest request, HttpServletResponse response, Handler handler) throws Exception {
        Exception e = (Exception) request.getAttribute(exceptionName);

        Response res = new Response();
        res.setError(true);
        res.setValue(e);

        request.setAttribute(propertyName, res);

        super.render(request, response, handler);
    }
}
