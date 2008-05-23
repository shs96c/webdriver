import extension_connection

class InvalidSwitchToTargetException(Exception):
  def __init__(self, *args):
    Exception.__init__(self, *args)

class FirefoxTargetLocator(object):
  def __init__(self):
    self.conn = extension_connection.ExtensionConnection()

  def window(self, windowName):
    resp = self._command("switchToWindow", [windowName])
    if not resp or "No window found" == resp:
      raise InvalidSwitchToTargetException("Window %s not found" % windowName)
    self.conn.context = resp

  def _command(self, _cmd, _params=[]):
    return self.conn.command(_cmd, params = _params)
