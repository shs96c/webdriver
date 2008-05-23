#!/usr/bin/python
import sys
import unittest
from com.googlecode.webdriver import webdriver
from com.googlecode.webdriver import webserver
from com.googlecode.webdriver.logger import logger

class BasicTest (unittest.TestCase):
  def setUp(self):
    self.webserver = webserver.SimpleWebServer()
    self.webserver.start()
    self.driver = webdriver.FirefoxWebDriver()

  def tearDown(self):
    self.webserver.stop()

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

  def _loadSimplePage(self):
    self.driver.get("http://localhost:8000/simpleTest.html")

if __name__ == "__main__":
  unittest.main()

  #elem = driver.FindElementsByXPath("//a")
  #print elem.GetText()
  #driver.Close()


