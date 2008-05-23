import socket
import re
import threading
from com.googlecode.webdriver.lib import demjson
from com.googlecode.webdriver import logger

class ExtensionConnection(object):
  #Borg pattern hive mind state
  __shared_state = {}

  def __init__(self):
    logger.debug("extension connection initiated")
    self.__dict__ = self.__shared_state
    try:
      self.socket
    except AttributeError:
      self.lock = threading.RLock()
      self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
      self.socket.connect(("localhost",7055))
      self.socket.settimeout(20)
      self.context = "null"
      resp = self.command("findActiveDriver",[])
      self.context = "%d" % resp

  def command(self, cmd, params=[], elementId="null"):

    json_dump = demjson.encode({"parameters": params,
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
      return demjson.decode(json_content)['response']
    else :
      return None
