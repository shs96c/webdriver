from ctypes import *

class IeDriver:
  def __init__(self):
    self.driverLib = windll.InternetExplorerDriver
    self.driver = self.driverLib.webdriver_newDriverInstance()
    
  def get(self, url):
    self.driverLib.webdriver_get(self.driver, url)

  def __del__(self):
    self.driverLib.webdriver_deleteDriverInstance(self.driver)
    
    
driver = IeDriver()
driver.get(u"http://www.google.com")
