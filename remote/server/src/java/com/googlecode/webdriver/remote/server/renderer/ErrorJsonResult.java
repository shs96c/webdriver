// Copyright 2008 Google Inc.  All Rights Reserved.

package com.googlecode.webdriver.remote.server.renderer;

import com.googlecode.webdriver.remote.server.rest.Renderer;
import com.googlecode.webdriver.remote.server.rest.Handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author simonstewart@google.com (Simon Stewart)
 */
public class ErrorJsonResult extends JsonResult {
  public ErrorJsonResult(String propertyName) {
    super(propertyName);
  }

  public void render(HttpServletRequest request, HttpServletResponse response, Handler handler)
      throws Exception {
    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    super.render(request, response, handler);
  }
}
