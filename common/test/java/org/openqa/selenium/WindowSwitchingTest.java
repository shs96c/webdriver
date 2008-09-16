/*
 * Copyright 2008 Google Inc.  All Rights Reserved.
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

package org.openqa.selenium;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.Iterator;

public class WindowSwitchingTest extends AbstractDriverTestCase {

  @Ignore("ie")
  public void testShouldSwitchFocusToANewWindowWhenItIsOpenedAndNotStopFutureOperations() {
    driver.get(xhtmlTestPage);

    driver.findElement(By.linkText("Open new window")).click();
    assertThat(driver.getTitle(), equalTo("XHTML Test Page"));

    driver.switchTo().window("result");
    assertThat(driver.getTitle(), equalTo("We Arrive Here"));

    driver.get(iframePage);
    driver.findElement(By.id("iframe_page_heading"));
  }

  @NeedsFreshDriver
  @NoDriverAfterTest
  @Ignore("ie, remote")
  public void testShouldBeAbleToIterateOverAllOpenWindows() {
    driver.get(xhtmlTestPage);
    driver.findElement(By.name("windowOne")).click();
    driver.findElement(By.name("windowTwo")).click();

    Iterator<WebDriver> allWindows = driver.switchTo().windowIterable().iterator();

    // There should be three windows. We should also see each of the window titles at least once.
    //
    allWindows.next();
    allWindows.next();
    allWindows.next();

    assertFalse(allWindows.hasNext());
  }


  public void testRemovingDriversFromTheWindowIteratorShouldCloseTheUnderlyingWindow() {
    // Not implemented yet  
  }
}
