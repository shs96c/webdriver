// Copyright 2008 Google Inc.  All Rights Reserved.

package com.googlecode.webdriver.remote.server.handler;

import com.googlecode.webdriver.By;

public class BySelector {

  public By pickFrom(String method, String selector) {
    if ("id".equals(method)) {
      return By.id(selector);
    } else if ("link text".equals(method)) {
      return By.linkText(selector);
    } else if ("name".equals(method)) {
      return By.name(selector);
    } else if ("xpath".equals(method)) {
      return By.xpath(selector);
    } else {
      throw new RuntimeException("Cannot find matching element locator to: " + method);
    }
  }
}
