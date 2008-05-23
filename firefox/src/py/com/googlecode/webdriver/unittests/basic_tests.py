#!/usr/bin/python
import sys
import unittest
from com.googlecode.webdriver import webdriver
from com.googlecode.webdriver import webserver
from com.googlecode.webdriver import logger

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
    elem = self.driver.findElementsByXPath("//h1")
    self.assertEquals("Heading", elem.getText())

  def testSwitchTo(self):
    self._loadPage("xhtmlTest")
    self.driver.findElementByLinkText("Open new window").click();
    self.assertEquals("XHTML Test Page", self.driver.getTitle())
    self.driver.switchTo().window("result")
    self.assertEquals("We Arrive Here", self.driver.getTitle())

  def _loadSimplePage(self):
    self.driver.get("http://localhost:8000/simpleTest.html")

  def _loadPage(self, name):
    self.driver.get("http://localhost:8000/%s.html" % name)

if __name__ == "__main__":
  webserver = webserver.SimpleWebServer()
  webserver.start()
  try:
    unittest.main()
  except:
    pass
  webserver.stop()
