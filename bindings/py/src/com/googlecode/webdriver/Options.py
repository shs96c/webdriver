class Options:
  
  def addCookie(self, cookie):
    raise NotImplementedError()

  def deleteCookieNamed(self, name):
    raise NotImplementedError()

  def deleteCookie(self, cookie):
    raise NotImplementedError()

  def deleteAllCookies(self):
    raise NotImplementedError()

  def getCookies(self):
    raise NotImplementedError()

  def getMouseSpeed(self):
    raise NotImplementedError()

  def setMouseSpeed(self, speed):
    raise NotImplementedError()
