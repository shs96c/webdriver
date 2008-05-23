import extension_connection
import exceptions

class FirefoxTargetLocator(object):
  def __init__(self):
    self.conn = extension_connection.ExtensionConnection()

  def window(self, windowName):
    resp = self._command("switchToWindow", windowName)
    if not resp or "No window found" == resp:
      raise exceptions.InvalidSwitchToTargetException("Window %s not found" % windowName)
    self.conn.context = resp

  def _command(self, _cmd, *args):
    return self.conn.command(_cmd, params = args)
