import re
import sys
from ExtensionConnection import ExtensionConnection
from FirefoxWebElement import FirefoxWebElement
from FirefoxTargetLocator import FirefoxTargetLocator

from com.googlecode.webdriver.WebDriver import WebDriver

class FirefoxWebDriver(WebDriver):
  def __init__(self):
    self.conn = ExtensionConnection()

  def Get(self, url):
    self.conn.command("get", [url])

  def GetCurrentUrl(self):
    return self.conn.command("getCurrentUrl")

  def GetTitle(self):
    return self.conn.command("title")

  def GetVisible(self):
    return True

  def GetVisible(self, visible):
    pass

  def FindElementByXPath(self, xpath):
    elemId = self.conn.command("selectElementUsingXPath", [xpath])
    assert type(elemId) == int, "Bad response format: %s" % str(elemId)
    elem = FirefoxWebElement(self, elemId)
    return elem
  
  def FindElementByLinkText(self, link):
    elemId = self.conn.command("selectElementUsingLink", [link])
    assert type(elemId) == int, "Bad response format"
    elem = FirefoxWebElement(self, elemId)
    return elem

  def FindElementById(self, id):
    return self.FindElementByXPath("//*[@id=\"%s\"]" % id)
        
  def FindElements(self, by):
    #Maybe there is easier way to do this in a dynamic language
    pass

  def FindElement(self, by):
    pass
 
  def GetPageSource(self):
    return self.conn.command("getPageSource")
  
  def Close(self):
    self.conn.command("close")

  def Quit(self):
    self.conn.command("quit")

  def SwitchTo(self):
    return FirefoxTargetLocator()

  def Navigate(self):
    pass

  def Manage(self):
    pass

  
if __name__ == "__main__":
  driver = FirefoxWebDriver()
  driver.get("http://localhost:8080")
  print driver.getCurrentUrl()
  
  print driver.getTitle()
  elem = driver.findElementsByXPath("//a")
  print elem.getText()
  #driver.Close()
