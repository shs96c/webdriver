#!/usr/bin/python

import re
import sys
import unittest
from com.googlecode.webdriver import webdriver
from com.googlecode.webdriver import webserver
from com.googlecode.webdriver.common import logger
from com.googlecode.webdriver import firefox_launcher

class BasicTest (unittest.TestCase):
  def setUp(self):
    self.driver = webdriver.FirefoxWebDriver()

  def tearDown(self):
    pass

  def testGetTitle(self):
    self._loadSimplePage()
    title = self.driver.getTitle()
    self.assertEquals("Hello WebDriver", title)

  def testGetCurrentUrl(self):
    self._loadSimplePage()
    url = self.driver.getCurrentUrl()
    self.assertEquals("http://localhost:8000/simpleTest.html", url)

  def testFindElementsByXPath(self):
    self._loadSimplePage()
    elem = self.driver.findElementByXPath("//h1")
    self.assertEquals("Heading", elem.getText())

  def testSwitchTo(self):
    self._loadPage("xhtmlTest")
    self.driver.findElementByLinkText("Open new window").click()
    self.assertEquals("XHTML Test Page", self.driver.getTitle())
    self.driver.switchTo().window("result")
    self.assertEquals("We Arrive Here", self.driver.getTitle())

  def testGetPageSource(self):
    self._loadSimplePage()
    source = self.driver.getPageSource()
    self.assertTrue(len(re.findall(r'<html>.*</html>', source, re.DOTALL)) > 0)

  def testIsEnabled(self):
    self._loadPage("formPage")
    elem = self.driver.findElementByXPath("//input[@id='working']")
    self.assertTrue(elem.isEnabled())
    elem = self.driver.findElementByXPath("//input[@id='notWorking']")
    self.assertFalse(elem.isEnabled())

  def testIsSelectedAndToggle(self):
    self._loadPage("formPage")
    elem = self.driver.findElementById("multi")
    option_elems = elem.findElementsByXPath("option")
    self.assertTrue(option_elems[0].isSelected())
    option_elems[0].toggle()
    self.assertFalse(option_elems[0].isSelected())
    option_elems[0].toggle()
    self.assertTrue(option_elems[0].isSelected())
    self.assertTrue(option_elems[2].isSelected())

  def _loadSimplePage(self):
    self.driver.get("http://localhost:8000/simpleTest.html")

  def _loadPage(self, name):
    self.driver.get("http://localhost:8000/%s.html" % name)

if __name__ == "__main__":
  webserver = webserver.SimpleWebServer()
  webserver.start()
  firefox = firefox_launcher.FirefoxLauncher()
  firefox.launchBrowser()

  try:
    unittest.main()
  except:
    pass

  firefox.closeBrowser()
  webserver.stop()
