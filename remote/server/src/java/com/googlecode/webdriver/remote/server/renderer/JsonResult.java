package com.googlecode.webdriver.remote.server.renderer;

import com.googlecode.webdriver.remote.BeanToJsonConverter;
import com.googlecode.webdriver.remote.server.rest.Handler;
import com.googlecode.webdriver.remote.server.rest.Renderer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JsonResult implements Renderer {
    private String propertyName;

    public JsonResult(String propertyName) {
        if (propertyName.startsWith(":"))
            this.propertyName = propertyName.substring(1);
        else
            this.propertyName = propertyName;
    }

	public void render(HttpServletRequest request, HttpServletResponse response, Handler handler) throws Exception {
        Object result = request.getAttribute(propertyName);
        String json = new BeanToJsonConverter().convert(result);

        int length = json == null ? 0 : json.getBytes().length;

        response.setContentLength(length);
        response.setContentType("application/json");
        response.getWriter().append(json).flush();
    }
}
