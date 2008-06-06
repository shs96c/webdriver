#!/usr/bin/python

import re
import sys
import unittest
from com.googlecode.webdriver.firefox.FirefoxWebDriver import FirefoxWebDriver
from com.googlecode.webdriver.firefox.webserver import SimpleWebServer
from com.googlecode.webdriver.firefox.common import logger
from com.googlecode.webdriver.firefox.FirefoxLauncher import FirefoxLauncher
from com.googlecode.webdriver.firefox.FirefoxNavigation import FirefoxNavigation

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

  def testSwitchToWindow(self):
    title_1 = "XHTML Test Page"
    title_2 = "We Arrive Here"
    self._loadPage("xhtmlTest")
    self.driver.FindElementByLinkText("Open new window").Click()
    self.assertEquals(title_1, self.driver.GetTitle())
    self.driver.SwitchTo().Window("result")
    self.assertEquals(title_2, self.driver.GetTitle())
    
  def testSwitchToFrame(self):
    self._loadPage("frameset")
    self.driver.SwitchTo().FrameByIndex(2)
    checkbox = self.driver.FindElementById("checky")
    checkbox.Toggle()
    checkbox.Submit()

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
    self.assertTrue(self.driver.Manage())

  def testNavigate(self):
    self._loadPage("formPage")
    self.driver.FindElementById("imageButton").Submit()
    self.assertEquals("We Arrive Here", self.driver.GetTitle())
    self.driver.Navigate().Back()
    self.assertEquals("We Leave From Here", self.driver.GetTitle())
    self.driver.Navigate().Forward()
    self.assertEquals("We Arrive Here", self.driver.GetTitle())

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
  driver = FirefoxWebDriver()
  driver.Quit()
  webserver.stop()