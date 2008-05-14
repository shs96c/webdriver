// Copyright 2008 Google Inc.  All Rights Reserved.

package com.googlecode.webdriver.remote;

import com.googlecode.webdriver.RenderedWebElement;
import static com.googlecode.webdriver.remote.MapMaker.map;

import java.awt.*;
import java.util.Map;

public class RenderedRemoteWebElement extends RemoteWebElement implements RenderedWebElement {
  public boolean isDisplayed() {
    Response response = parent.execute("isElementDisplayed", map("id", id));
    return (Boolean) response.getValue();
  }

  @SuppressWarnings({"unchecked"})
  public Point getLocation() {
    Response response = parent.execute("getElementLocation", map("id", id));
    Map<String, Object> rawPoint = (Map<String, Object>) response.getValue();
    double x = (Double) rawPoint.get("x");
    double y = (Double) rawPoint.get("y");
    return new Point((int) x, (int)y);
  }

  @SuppressWarnings({"unchecked"})
  public Dimension getSize() {
    Response response = parent.execute("getElementSize", map("id", id));
    Map<String, Object> rawSize = (Map<String, Object>) response.getValue();
    double width = (Double) rawSize.get("width");
    double height = (Double) rawSize.get("height");
    return new Dimension((int) width, (int)height);
  }

  public void dragAndDropBy(int moveRightBy, int moveDownBy) {
    parent.execute("dragElement", map("id", id), moveRightBy, moveDownBy);
  }

  public void dragAndDropOn(RenderedWebElement element) {
    Point currentLocation = getLocation();
    Point destination = element.getLocation();
    dragAndDropBy(destination.x - currentLocation.x, destination.y - currentLocation.y);
  }
}
