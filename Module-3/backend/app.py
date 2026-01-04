from flask import Flask, request, jsonify
from flask_cors import CORS
from templates.notice import Notice

app = Flask(__name__)
CORS(app)

notices_db = {}

@app.route("/")
def home():
  return "Server has been initialized"

@app.route('/api/notices',methods=["POST"])
def create_notice():
  data = request.json

  new_notice = Notice(
    title = data.get('title'),
    date = data.get('date'),
    message = data.get('message')
  )

  notices_db[new_notice.notice_id] = new_notice

  print(f"Stored Notice: {new_notice.notice_id} - {new_notice.title}")
  for key in notices_db:
    print(notices_db[key].title,notices_db[key].date,notices_db[key].message)
  print(f"Total Notices in memory: {len(notices_db)}")

  return jsonify({"status": "success","message": "Notice received"},201)

@app.route('/api/notices', methods=['GET'])
def get_all_notices():
    notices_list = [notice.to_dict() for notice in notices_db.values()]
    return jsonify(notices_list), 200

@app.route('/api/notices/<notice_id>', methods=['PUT'])
def update_notice(notice_id):
    if notice_id not in notices_db:
        return jsonify({"error": "Notice not found"}), 404

    data = request.json
    notice = notices_db[notice_id]

    notice.title = data.get('title', notice.title)
    notice.date = data.get('date', notice.date)
    notice.message = data.get('message', notice.message)

    print(f"Updated Notice: {notice_id}")
    for key in notices_db:
       print(notices_db[key].title,notices_db[key].date,notices_db[key].message)
    return jsonify({"status": "success", "notice": notice.to_dict()}), 200

@app.route('/api/notices/<notice_id>', methods=['DELETE'])
def delete_notice(notice_id):
    if notice_id in notices_db:
        del notices_db[notice_id]
        print(f"Deleted Notice: {notice_id}")
        return jsonify({"status": "success", "message": "Notice deleted"}), 200
    
    return jsonify({"error": "Notice not found"}), 404

if __name__ == '__main__':
  app.run(debug=True,port = 5000)