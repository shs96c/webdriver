import extension_connection
import exceptions
from common import logger

class FirefoxWebElement(object):

  def __init__(self, parent, id):
    self.parent = parent
    self.conn = extension_connection.ExtensionConnection()
    self.id = id

  def getText(self):
    return self._command("getElementText")

  def click(self):
    self._command("click")

  def submit(self):
    self._command("submitElement")

  def getValue(self):
    return self._command("getElementValue")

  def clear(self):
    self._command("clear")

  def getAttribute(self, name):
    return self._command("getElementAttribute", name)

  def toggle(self):
    self._command("toggleElement")

  def isSelected(self):
    return self._command("getElementSelected")

  def setSelected(self):
    self._command("setElementSelected")

  def isEnabled(self):
    if self.getAttribute("disabled"):
      return False
    else:
      # The "disabled" attribute may not exist
      return True

  def findElements(self, by):
    pass

  def findElement(self, by):
    pass

  def findElementsByXPath(self, xpath):
    resp = self._command("findElementsByXPath", xpath)
    elems = []
    for elemId in resp.split(","):
      elem = FirefoxWebElement(self.parent, elemId)
      elems.append(elem)
    return elems

  def _command(self, _cmd, *args):
    return self.conn.command(_cmd, params = args, elementId=self.id)
