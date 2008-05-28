class WebElement(object):

  def GetText(self):
    raise NotImplementedError()

  def Click(self):
    raise NotImplementedError()

  def Submit(self):
    raise NotImplementedError()

  def GetValue(self):
    raise NotImplementedError()

  def Clear(self):
    raise NotImplementedError()

  def GetAttribute(self, name):
    raise NotImplementedError()

  def Toggle(self):
    raise NotImplementedError()

  def IsSelected(self):
    raise NotImplementedError()

  def SetSelected(self):
    raise NotImplementedError()

  def IsEnabled(self):
    raise NotImplementedError()

  def FindElements(self, by):
    raise NotImplementedError()

  def FindElement(self, by):
    raise NotImplementedError()

  def FindElementsByXPath(self, xpath):
    raise NotImplementedError()
