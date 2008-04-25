package com.googlecode.webdriver.remote.server.rest;

import com.googlecode.webdriver.remote.server.rest.Handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Renderer {
	void render(HttpServletRequest request, HttpServletResponse response, Handler handler) throws Exception;
}
