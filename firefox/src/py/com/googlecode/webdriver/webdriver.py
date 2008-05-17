
import re

import extension_connection

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

  def FindElements(self, by):
    pass

  def FindElement(self, by):
    pass
 
  def GetPageSource(self):
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
  driver.Get("http://www.google.com")
  print driver.GetCurrentUrl()
  
  print driver.GetTitle()
  driver.Close()
