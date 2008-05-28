from ExtensionConnection import ExtensionConnection
from common import logger
from com.googlecode.webdriver.WebElement import WebElement

class FirefoxWebElement(WebElement):

  def __init__(self, parent, id):
    self.parent = parent
    self.conn = ExtensionConnection()
    self.id = id

  def GetText(self):
    return self._command("getElementText")

  def Click(self):
    self._command("click")

  def Submit(self):
    self._command("submitElement")

  def GetValue(self):
    return self._command("getElementValue")

  def Clear(self):
    self._command("clear")

  def GetAttribute(self, name):
    return self._command("getElementAttribute", name)

  def Toggle(self):
    self._command("toggleElement")

  def IsSelected(self):
    return self._command("getElementSelected")

  def SetSelected(self):
    self._command("setElementSelected")

  def IsEnabled(self):
    if self.GetAttribute("disabled"):
      return False
    else:
      # The "disabled" attribute may not exist
      return True

  def FindElements(self, by):
    pass

  def FindElement(self, by):
    pass

  def FindElementsByXPath(self, xpath):
    resp = self._command("findElementsByXPath", xpath)
    elems = []
    for elemId in resp.split(","):
      elem = FirefoxWebElement(self.parent, elemId)
      elems.append(elem)
    return elems

  def _command(self, _cmd, *args):
    return self.conn.command(_cmd, params = args, elementId=self.id)
