import re
import extension_connection
from webelement import FirefoxWebElement
from firefox_target_locator import FirefoxTargetLocator

class FirefoxWebDriver(object):
  def __init__(self):
    self.conn = extension_connection.ExtensionConnection()

  def get(self, url):
    self.conn.command("get", [url])

  def getCurrentUrl(self):
    return self.conn.command("getCurrentUrl")

  def getTitle(self):
    return self.conn.command("title")

  def getVisible(self):
    return True

  def setVisible(self, visible):
    pass

  def findElementsByXPath(self, xpath):
    elemId = self.conn.command("selectElementUsingXPath", [xpath])
    assert type(elemId) == int, "Bad response format"
    elem = FirefoxWebElement(self, elemId)
    return elem
  
  def findElementByLinkText(self, link):
    elemId = self.conn.command("selectElementUsingLink", [link])
    assert type(elemId) == int, "Bad response format"
    elem = FirefoxWebElement(self, elemId)
    return elem

  def findElements(self, by):
    #Maybe there is easier way to do this in a dynamic language
    pass

  def findElement(self, by):
    pass
 
  def getPageSource(self):
    #Seesm the json parser doesn't like handling
    #very large message
    pass

  def close(self):
    self.conn.command("close")

  def quit(self):
    self.conn.command("quit")

  def switchTo(self):
    return FirefoxTargetLocator()

  def navigate(self):
    pass

  def manage(self):
    pass

  
if __name__ == "__main__":
  driver = FirefoxWebDriver()
  driver.get("http://localhost:8080")
  print driver.getCurrentUrl()
  
  print driver.getTitle()
  elem = driver.findElementsByXPath("//a")
  print elem.getText()
  #driver.Close()
