import os
import threading
from BaseHTTPServer import BaseHTTPRequestHandler, HTTPServer
from com.googlecode.webdriver import logger
import urllib

HTML_ROOT = "../../../common/src/web/"

class MyHandler(BaseHTTPRequestHandler):
  def do_GET(self):
    try:
      self.path = self.path[1:]
      logger.info("Get request for %s" % self.path)
      if self.path.endswith(".html"):
        logger.info("Match to %s" % 
                    os.path.abspath(os.path.join(HTML_ROOT, self.path)))
        f = open(os.path.join(HTML_ROOT, self.path))
        self.send_response(200)
        self.send_header('Content-type', 'text/html')
        self.end_headers()
        self.wfile.write(f.read())
        f.close()
        return
    except IOError:
      self.send_error(404,'File Not Found: %s' % self.path)

class SimpleWebServer(object):
  def __init__(self):
    self.stop_serving = False

  def _runWebServer(self):
    server = HTTPServer(('', 8000), MyHandler)
    logger.info("web server started")

    while not self.stop_serving:
      server.handle_request()
    logger.info("web server stopped")

  def start(self):
    self.thread = threading.Thread(target=self._runWebServer)
    self.thread.start()
    
  def stop(self):
    self.stop_serving = True
    try:
      urllib.URLopener().open("http://localhost:8000")
    except IOError:
      pass  #the server has shutdown
    self.thread.join()

if __name__ == "__main__":
  server = SimpleWebServer()
  t = server.runWebServerInThread()
  t.join()
