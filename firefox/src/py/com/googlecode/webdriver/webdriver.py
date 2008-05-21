
import re
import extension_connection
from webelement import FirefoxWebElement

class FirefoxWebDriver:
  def __init__(self):
    self.conn = extension_connection.ExtensionConnection()

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

  def FindElementsByXPath(self, xpath):
    elemId = self.conn.command("selectElementUsingXPath", [xpath])
    print elemId
    assert type(elemId) == int, "Bad response format"
    elem = FirefoxWebElement(self, elemId)
    return elem
  
  def FindElements(self, by):
    #Maybe there is easier way to do this in a dynamic language
    pass

  def FindElement(self, by):
    pass
 
  def GetPageSource(self):
    #Seesm the json parser doesn't like handling
    #very large message
    pass

  def Close(self):
    self.conn.command("close")

  def Quit(self):
    self.conn.command("quit")

  def SwitchTo(self):
    pass

  def Navigate(self):
    pass

  def Manage(self):
    pass
  
if __name__ == "__main__":
  driver = FirefoxWebDriver()
  driver.Get("http://localhost:8080")
  print driver.GetCurrentUrl()
  
  print driver.GetTitle()
  elem = driver.FindElementsByXPath("//a")
  print elem.GetText()
  #driver.Close()
