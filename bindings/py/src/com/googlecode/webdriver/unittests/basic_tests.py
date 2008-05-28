#!/usr/bin/python

import re
import sys
import unittest
from com.googlecode.webdriver.firefox.FirefoxWebDriver import FirefoxWebDriver
from com.googlecode.webdriver.firefox.webserver import SimpleWebServer
from com.googlecode.webdriver.firefox.common import logger
from com.googlecode.webdriver.firefox.FirefoxLauncher import FirefoxLauncher

class BasicTest (unittest.TestCase):
  def setUp(self):
    self.driver = FirefoxWebDriver()

  def tearDown(self):
    pass

  def testGetTitle(self):
    self._loadSimplePage()
    title = self.driver.GetTitle()
    self.assertEquals("Hello WebDriver", title)

  def testGetCurrentUrl(self):
    self._loadSimplePage()
    url = self.driver.GetCurrentUrl()
    self.assertEquals("http://localhost:8000/simpleTest.html", url)

  def testFindElementsByXPath(self):
    self._loadSimplePage()
    elem = self.driver.FindElementByXPath("//h1")
    self.assertEquals("Heading", elem.GetText())

  def testSwitchTo(self):
    self._loadPage("xhtmlTest")
    self.driver.FindElementByLinkText("Open new window").Click()
    self.assertEquals("XHTML Test Page", self.driver.GetTitle())
    self.driver.SwitchTo().Window("result")
    self.assertEquals("We Arrive Here", self.driver.GetTitle())

  def testGetPageSource(self):
    self._loadSimplePage()
    source = self.driver.GetPageSource()
    self.assertTrue(len(re.findall(r'<html>.*</html>', source, re.DOTALL)) > 0)

  def testIsEnabled(self):
    self._loadPage("formPage")
    elem = self.driver.FindElementByXPath("//input[@id='working']")
    self.assertTrue(elem.IsEnabled())
    elem = self.driver.FindElementByXPath("//input[@id='notWorking']")
    self.assertFalse(elem.IsEnabled())

  def testIsSelectedAndToggle(self):
    self._loadPage("formPage")
    elem = self.driver.FindElementById("multi")
    option_elems = elem.FindElementsByXPath("option")
    self.assertTrue(option_elems[0].IsSelected())
    option_elems[0].Toggle()
    self.assertFalse(option_elems[0].IsSelected())
    option_elems[0].Toggle()
    self.assertTrue(option_elems[0].IsSelected())
    self.assertTrue(option_elems[2].IsSelected())

  def testManage(self):
    self.driver.Manage()

  def _loadSimplePage(self):
    self.driver.Get("http://localhost:8000/simpleTest.html")

  def _loadPage(self, name):
    self.driver.Get("http://localhost:8000/%s.html" % name)

if __name__ == "__main__":
  webserver = SimpleWebServer()
  webserver.start()
  firefox = FirefoxLauncher()
  firefox.LaunchBrowser()

  try:
    unittest.main()
  except:
    pass

  firefox.closeBrowser()
  webserver.stop()
