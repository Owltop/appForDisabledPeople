from flask import Flask, request


class Application:
    def __init__(self, user_name, app_name, app_desc):
        self.user_name = user_name
        self.app_name = app_name
        self.app_desc = app_desc 
        

storage = set()
app = Flask(__name__)

@app.route('/', methods=['POST'])
def index():
    user_name = request.headers.get('UserName')
    app_name = request.headers.get('AppName')
    app_desc = request.headers.get('AppDesc')
    storage.add(Application(user_name, app_name, app_desc))

    print("Success")
    print("User name:" + user_name)
    print("Application name:" + app_name)
    print("Application description:" + app_desc)
    
    return {"message": "Received data"}


ip_address = "192.168.1.86"
port = 5000

print(f"Server running at http://{ip_address}:{port}/")
app.run(debug=True, host="192.168.1.86", port=5000)