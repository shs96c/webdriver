from ctypes import *

l = windll.InternetExplorerDriver

driver = l.webdriver_newDriverInstance()

l.webdriver_get(driver, u"http://www.google.com");
l.webdriver_deleteDriverInstance(driver)