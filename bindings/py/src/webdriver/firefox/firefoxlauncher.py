from subprocess import Popen
from subprocess import PIPE
import time
import platform

class FirefoxLauncher(object):
  
  def LaunchBrowser(self):
    if platform.system() == "Darwin":
      cmd = "/Applications/Firefox.app/Contents/MacOS/firefox"
    else:
      cmd = Popen(["which", "firefox2"], stdout=PIPE).communicate()[0].strip()
    self.process = Popen([cmd, "-P", "WebDriver"])
    time.sleep(5)

  def CloseBrowser(self):
    Popen(["kill", "%d" % self.process.pid])

if __name__ == "__main__":
  FirefoxLauncher().LaunchBrowser()
