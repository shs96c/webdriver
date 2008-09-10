import re
import sys

from navigation import *
from options import *
from targetlocator import *
from webelement import *

# import the base webdriver package
import webdriver

class WebDriver(webdriver.WebDriver):
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

  def SetVisible(self, visible):
    pass

  def FindElementByXPath(self, xpath):
    elemId = self.conn.command("selectElementUsingXPath", [xpath])
    assert type(elemId) == int, "Bad response format: %s" % str(elemId)
    elem = WebElement(self, elemId)
    return elem
  
  def FindElementByLinkText(self, link):
    elemId = self.conn.command("selectElementUsingLink", [link])
    assert type(elemId) == int, "Bad response format"
    elem = WebElement(self, elemId)
    return elem

  def FindElementById(self, id):
    return self.FindElementByXPath("//*[@id=\"%s\"]" % id)


  def FindElementByName(self, name):
    return self.FindElementByXPath("//*[@name=\"%s\"]" % name)
        
  def GetPageSource(self):
    return self.conn.command("getPageSource")
  
  def Close(self):
    self.conn.command("close")

  def Quit(self):
    self.conn.command("quit")

  def SwitchTo(self):
    return TargetLocator()

  def Navigate(self):
    return Navigation()

  def Manage(self):
    return Options()

