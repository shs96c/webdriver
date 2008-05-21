import socket
import simplejson
import re
import logging

class ExtensionConnection:
#TODO: Make this a singleton?
  def __init__(self):
    self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    self.socket.connect(("localhost",7055))
    self.socket.settimeout(20)
    self.context = "null"
    resp = self.command("findActiveDriver",[])
    self.context = "%d" % resp

  def command(self, cmd, params=[], elementId="null"):
    packet = 'Length: 1\n\n'
    packet += simplejson.dumps({"parameters": params,
                                "context": self.context,
                                "elementId": elementId,
                                "commandName":cmd})
    packet += "\n"
    logging.info(packet)
    self.socket.send(packet)
    resp = self.socket.recv(1000)
    sections = re.findall(r'{.*}', resp)
    if sections:
      print resp
      json_content = sections[0];
      return simplejson.loads(json_content)['response']
    else :
      return None
