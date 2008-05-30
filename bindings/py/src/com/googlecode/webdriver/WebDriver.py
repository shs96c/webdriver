
class WebDriver (object):
  
  def Get(self):
    raise NotImplementedError()

  def GetCurrentUrl(self):
    raise NotImplementedError()

  def GetTitle(self):
    raise NotImplementedError()

  def GetVisible(self):
    raise NotImplementedError()

  def SetVisible(self, visible):
    raise NotImplementedError()

  def FindElements(self, by):
    raise NotImplementedError()

  def FindElement(self, by):
    raise NotImplementedError()
 
  def GetPageSource(self):
    raise NotImplementedError()

  def Close(self):
    raise NotImplementedError()

  def Quit(self):
    raise NotImplementedError()

  def SwitchTo(self):
    raise NotImplementedError()

  def Navigate(self):
    raise NotImplementedError()

  def Manage(self):
    raise NotImplementedError()

