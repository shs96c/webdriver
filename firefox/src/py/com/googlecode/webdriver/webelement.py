import extension_connection

class FirefoxWebElement(object):
  
  def __init__(self, parent, id):
    self.parent = parent
    self.conn = extension_connection.ExtensionConnection()
    self.id = id

  def getText(self):
    return self.conn.command("getElementText", elementId=self.id) 
  
  
