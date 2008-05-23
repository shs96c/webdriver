import extension_connection

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
    pass

  def clear(self):
    self._command("clear")

  def getAttribute(self, name):
    pass

  def toggle(self):
    self._command("toggleElement")

  def isSelected(self):
    pass

  def setSelected(self):
    self._command("setElementSelected")

  def isEnabled(self):
    pass

  def findElements(self, by):
    pass

  def findElement(self, by):
    pass

  def _command(self, _cmd, _params=[]):
    return self.conn.command(_cmd, params = _params, elementId=self.id)
