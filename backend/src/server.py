from flask import Flask, request, jsonify
import argparse


class Application:
    def __init__(self, user_name, app_name, app_desc):
        self.user_name = user_name
        self.app_name = app_name
        self.app_desc = app_desc
    
    def __eq__(self, other) -> bool:
        return self.user_name == other.user_name and self.app_name == other.app_name and self.app_desc == other.app_desc
    
    def __ne__(self, other) -> bool:
        return not (self == other)

    def __hash__(self):
        return hash((self.user_name, self.app_name, self.app_desc))
        

storage = set()
app = Flask(__name__)

@app.route('/', methods=['POST'])
def save_applications():
    user_name = request.headers.get('UserName')
    app_name = request.headers.get('AppName')
    app_desc = request.headers.get('AppDesc')
    storage.add(Application(user_name, app_name, app_desc))

    print("Success")
    print("User name:" + user_name)
    print("Application name:" + app_name)
    print("Application description:" + app_desc)
    
    return {"message": "Received data"}

@app.route('/', methods=['GET'])
def get_applications():
    return jsonify([{'userName': app.user_name, 'appName': app.app_name, 'appDesc': app.app_desc} for app in storage])

ip_address = "0.0.0.0"

parser = argparse.ArgumentParser()
parser.add_argument('--port', help='port', default=5050)

args = parser.parse_args()
port = args.port

print(f"Server running at http://{ip_address}:{port}/")
app.run(debug=True, host=ip_address, port=port)