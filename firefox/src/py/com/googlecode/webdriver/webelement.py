import extension_connection

class FirefoxWebElement:
  
  def __init__(self, parent, id):
    self.parent = parent
    self.id = id
    

  def GetText(self):
    return self.parent.conn.command("getElementText", elementId=self.id) 
  
  
