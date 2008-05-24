import socket
import re
import threading
from com.googlecode.webdriver.lib import simplejson
from common import logger
import firefox_launcher
import exceptions

class ExtensionConnection(object):
  #Borg pattern hive mind state
  __shared_state = {}

  def __init__(self):
    logger.debug("extension connection initiated")
    self.__dict__ = self.__shared_state
    if "socket" not in self.__dict__:
      self.lock = threading.RLock()
      self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
      self.socket.connect(("localhost",7055))

      self.socket.settimeout(20)
      self.context = "null"
      resp = self.command("findActiveDriver",[])
      self.context = "%d" % resp

  def command(self, cmd, params=[], elementId="null"):
    json_dump = simplejson.dumps({"parameters": params,
                                "context": self.context,
                                "elementId": elementId,
                                "commandName":cmd})
    length = len(json_dump.split("\n"))
    packet = 'Length: %d\n\n' % length
    packet += json_dump
    packet += "\n"
    logger.debug(packet)

    self.lock.acquire()
    self.socket.send(packet)
    resp = ""
    while True:
      resp += self.socket.recv(128)
      if re.findall(r'{.*}', resp):
        break
    self.lock.release()

    logger.debug(resp)
    sections = re.findall(r'{.*}', resp)
    if sections:
      json_content = sections[0];
      decoded = simplejson.loads(json_content)
      if decoded["isError"]:
        raise exceptions.ErrorInResponseException()
      return decoded["response"]
    else :
      return None
