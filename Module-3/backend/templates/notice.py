import uuid

class Notice:
  def __init__(self,title,date,message):
    self.notice_id = str(uuid.uuid4())[:8]
    self.title = title
    self.date = date
    self.message = message
  
  def to_dict(self):
    return {
      "id": self.notice_id,
      "title": self.title,
      "date": self.date,
      "message": self.message
    }