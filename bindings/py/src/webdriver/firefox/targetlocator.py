from extensionconnection import ExtensionConnection
import webdriver
import exceptions

class TargetLocator(webdriver.TargetLocator):
  
  def __init__(self):
    self.conn = ExtensionConnection()
 
  def Window(self, windowName):
    resp = self.conn.command("switchToWindow", windowName)
    if not resp or "No window found" == resp:
      raise exceptions.InvalidSwitchToTargetException("Window %s not found" % windowName)
    self.conn.context = resp

  def FrameByIndex(self, index):
    resp = self.conn.command("switchToFrame", str(index))
