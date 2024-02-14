from http.server import HTTPServer, BaseHTTPRequestHandler


class Application:
    def __init__(self, user_name, app_name, app_desc):
        self.user_name = user_name
        self.app_name = app_name
        self.app_desc = app_desc 
        

storage = set()

class SimpleHTTPRequestHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        self.send_response(200)
        self.end_headers()
        self.wfile.write(b'Hello, world!')

    def do_POST(self):
        user_name = self.headers['UserName']
        app_name = self.headers['AppName']
        app_desc = self.headers['AppDesc']

        storage.add(Application(user_name, app_name, app_desc))

        print("Success")
        print("User name:" + user_name)
        print("Application name:" + app_name)
        print("Application description:" + app_desc)



ip_address = "192.168.1.86"
port = 5000

httpd = HTTPServer((ip_address, port), SimpleHTTPRequestHandler)
print(f"Server running at http://{ip_address}:{port}/")

httpd.serve_forever()