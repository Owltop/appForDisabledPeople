from flask import Flask, request, jsonify
import argparse
import psycopg2
import random
import string
import hashlib

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

# def register_routes(app):
#     import handlers.user_info.registration as registration
#     app.register_blueprint(registration.routes)
        

storage = set()
app = Flask(__name__)
# register_routes(app)

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

@app.route('/get_applications/', methods=['GET'])
def get_applications():
    return jsonify([{'userName': app.user_name, 'applicationName': app.app_name, 'applicationDescription': app.app_desc} for app in storage])

# Параметры подключения к БД
dbname = "main"
user = "admin"
password = "admin1234"
host = "postgresql"


# Функция для подключения к базе данных
def connect_db():
    conn = psycopg2.connect(database=dbname, user=user, password=password, host=host)
    return conn

def hash_password(password):
    password_bytes = password.encode('utf-8')
    sha256_hash = hashlib.sha256()
    sha256_hash.update(password_bytes)
    hashed_password = sha256_hash.hexdigest()

    return str(hashed_password)

def generate_token():
    return ''.join(random.choices(string.ascii_letters + string.digits, k=30))

@app.route('/register', methods=['POST'])
def register_user():
    data = request.json
    
    if not all(k in data for k in ('name', 'login', 'password', 'email', 'age')):
        return jsonify({'error': 'Missing fields'}), 400
    
    hashed_password = hash_password(data['password'])

    try:
        conn = connect_db()
        cur = conn.cursor()
        
        cur.execute("SELECT * FROM users WHERE login = %s OR email = %s;", (data['login'], data['email']))
        if cur.fetchone():
            return jsonify({'error': 'User already exists'}), 400
        token = generate_token()
        cur.execute(f"INSERT INTO users (name, login, password, email, age, token) VALUES ('{data['name']}', '{data['login']}', '{hashed_password}', '{data['email']}', '{data['age']}', '{token}') RETURNING id;")
        user_id = cur.fetchone()[0]
        conn.commit()
        
        
        
        return jsonify({'message': 'User registered successfully', 'token': token}), 201
    except Exception as e:
        conn.rollback()
        return jsonify({'error': str(e)}), 500
    finally:
        if cur is not None:
            cur.close()
        if conn is not None:
            conn.close()

@app.route('/login', methods=['POST'])
def login():
    data = request.json
    login_or_email = data.get('login_or_email')
    password = data.get('password')

    # Проверка на наличие данных
    if not login_or_email or not password:
        return jsonify({'error': 'Login/email and password are required'}), 400

    try:
        conn = connect_db()
        cur = conn.cursor()
        
        cur.execute("SELECT id, password, token FROM users WHERE login = %s OR email = %s;", (login_or_email, login_or_email))
        user = cur.fetchone()

        if user is None:
            return jsonify({'error': 'User not found'}), 404

        user_id, hashed_password, token = user

        # Проверка пароля
        if hash_password(password) == hashed_password:
            # Генерация токена/сессии для пользователя
            return jsonify({'message': 'Login successful', 'token': token}), 200
        else:
            return jsonify({'error': 'Invalid password'}), 403

    except Exception as e:
        return jsonify({'error': str(e)}), 500
    finally:
        cur.close()
        conn.close()

ip_address = "0.0.0.0"

parser = argparse.ArgumentParser()
parser.add_argument('--port', help='port', default=5050)

args = parser.parse_args()
port = args.port

print(f"Server running at http://{ip_address}:{port}/")
app.run(debug=True, host=ip_address, port=port)