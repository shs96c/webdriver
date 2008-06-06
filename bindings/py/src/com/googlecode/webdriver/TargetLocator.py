class TargetLocator(object):

  def FrameByIndex(self, index):
    raise NotImplementedError()

  def FrameByName(self, name):
    raise NotImplementedError()

  def Window(self, name):
    raise NotImplementedError()

  def DefaultContent(self):
    raise NotImplementedError()
