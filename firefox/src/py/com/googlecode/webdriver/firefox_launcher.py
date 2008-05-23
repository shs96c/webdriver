from subprocess import Popen
from subprocess import PIPE
import time

class FirefoxLauncher(object):
  
  def launchBrowser(self):
    firefox_path = Popen(["which", "firefox2"], stdout=PIPE).communicate()[0].strip()
    self.process = Popen([firefox_path, "-P", "WebDriver"])
    time.sleep(5)

  def closeBrowser(self):
    Popen(["kill", "%d" % self.process.pid])

if __name__ == "__main__":
  launchBrowser()
