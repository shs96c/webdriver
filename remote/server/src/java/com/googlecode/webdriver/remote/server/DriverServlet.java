package com.googlecode.webdriver.remote.server;

import com.googlecode.webdriver.remote.server.handler.*;
import com.googlecode.webdriver.remote.server.renderer.EmptyResult;
import com.googlecode.webdriver.remote.server.renderer.ForwardResult;
import com.googlecode.webdriver.remote.server.renderer.JsonResult;
import com.googlecode.webdriver.remote.server.renderer.RedirectResult;
import com.googlecode.webdriver.remote.server.rest.ResultConfig;
import com.googlecode.webdriver.remote.server.rest.ResultType;
import com.googlecode.webdriver.remote.server.rest.UrlMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DriverServlet extends HttpServlet {
	private UrlMapper getMapper;
	private UrlMapper postMapper;
	
	@Override
	public void init() throws ServletException {
		super.init();
	
		DriverSessions driverSessions = new DriverSessions();

        getMapper = new UrlMapper(driverSessions);
		postMapper = new UrlMapper(driverSessions);
		
		postMapper.bind("/session", NewSession.class).on(ResultType.SUCCESS, new RedirectResult("/session/:sessionId/:context"));
		getMapper.bind("/session/:sessionId/:context", GetSessionCapabilities.class)
			.on(ResultType.SUCCESS, new ForwardResult("/WEB-INF/views/sessionCapabilities.jsp"))
			.on(ResultType.SUCCESS, new JsonResult(":capabilities"), "application/json");

        postMapper.bind("/session/:sessionId/:context/url", ChangeUrl.class).on(ResultType.SUCCESS, new EmptyResult());
        getMapper.bind("/session/:sessionId/:context/url", GetCurrentUrl.class).on(ResultType.SUCCESS, new JsonResult(":url"));

        getMapper.bind("/session/:sessionId/:context/title", GetTitle.class).on(ResultType.SUCCESS, new JsonResult(":title"));

        postMapper.bind("/session/:sessionId/:context/visible", SetVisible.class).on(ResultType.SUCCESS, new EmptyResult());
        getMapper.bind("/session/:sessionId/:context/visible", GetVisible.class).on(ResultType.SUCCESS, new JsonResult(":visible"));

        postMapper.bind("/session/:sessionId/:context/element", FindElement.class).on(ResultType.SUCCESS, new RedirectResult("/session/:sessionId/:context/element/:element"));
        getMapper.bind("/session/:sessionId/:context/element/:elementId", DescribeElement.class).on(ResultType.SUCCESS, new JsonResult(":element"));

        getMapper.bind("/session/:sessionId/:context/element/:id/value", GetElementValue.class).on(ResultType.SUCCESS, new JsonResult(":value"));
        getMapper.bind("/session/:sessionId/:context/element/:id/:name", GetElementAttribute.class).on(ResultType.SUCCESS, new JsonResult(":value"));
    }
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			ResultConfig config = getMapper.getConfig(request.getPathInfo());
			config.handle(request.getPathInfo(), request, response);
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            ResultConfig config = postMapper.getConfig(request.getPathInfo());
			config.handle(request.getPathInfo(), request, response);
		} catch (Exception e) {
			throw new ServletException(e);
		}
    }
}
