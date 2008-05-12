package com.googlecode.webdriver.remote.server.renderer;

import com.googlecode.webdriver.remote.server.rest.Handler;
import com.googlecode.webdriver.remote.server.rest.Renderer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EmptyResult implements Renderer {
    public void render(HttpServletRequest request, HttpServletResponse response, Handler handler) throws Exception {
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
