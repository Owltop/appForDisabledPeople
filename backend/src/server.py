import json
from flask import Flask, request, jsonify
import argparse
import psycopg2
import random
import string
import hashlib
import redis
from datetime import datetime


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

r = redis.Redis(host='redis', port=6379, decode_responses=True)

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
    print(data)
    
    if not all(k in data for k in ('name', 'login', 'password', 'email', 'age', 'account_type')):
        return jsonify({'error': 'Missing fields'}), 400
    
    if data['account_type'] != 'volunteer' and data['account_type'] != 'customer':
        return jsonify({'error': 'Account type has to be either volunteer or customer'}), 400

    hashed_password = hash_password(data['password'])

    try:
        conn = connect_db()
        cur = conn.cursor()
        
        cur.execute("SELECT * FROM users WHERE login = %s OR email = %s;", (data['login'], data['email']))
        if cur.fetchone():
            return jsonify({'error': 'User already exists'}), 400
        token = generate_token()
        cur.execute(f"INSERT INTO users (name, login, password, email, age, account_type, token) VALUES ('{data['name']}', '{data['login']}', '{hashed_password}', '{data['email']}', '{data['age']}', '{data['account_type']}', '{token}') RETURNING id;")
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

@app.route('/update', methods=['POST'])
def update_user():
    data = request.json
    
    if not all(k in data for k in ('name', 'login', 'password', 'email', 'age', 'account_type', 'token')):
        return jsonify({'error': 'Missing fields'}), 400
    if data['account_type'] != 'volunteer' and data['account_type'] != 'customer':
        return jsonify({'error': 'Account type has to be either volunteer or customer'}), 400

    try:
        conn = connect_db()
        cur = conn.cursor()
        
        cur.execute("SELECT token FROM users WHERE login = %s OR email = %s LIMIT 1;", (data['login'], data['email']))
        real_token = cur.fetchone()
        if real_token is None:
            return jsonify({'error': 'No such user'}), 404
        print(len(real_token))
        if real_token[0] != data['token']:
            return jsonify({'error': 'Unauthorized'}), 401

        params = [data.get(key) for key in ['name', 'login', 'email', 'age','account_type']]
        params.append(data.get('login'))
        cur.execute("""
                    UPDATE users
                    SET name = %s,
                    login = %s,
                    email = %s,
                    age = %s,
                    account_type = %s
                    WHERE login = %s;
                """, (params))
        conn.commit()
        
        
        
        return jsonify({'message': 'User info was updated successfully'}), 200
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
        
        cur.execute("SELECT id, password, token, account_type FROM users WHERE login = %s OR email = %s;", (login_or_email, login_or_email))
        user = cur.fetchone()

        if user is None:
            return jsonify({'error': 'User not found'}), 404

        user_id, hashed_password, token, account_type = user

        # Проверка пароля
        if hash_password(password) == hashed_password:
            # Генерация токена/сессии для пользователя
            return jsonify({'message': 'Login successful', 'token': token, 'account_type': account_type}), 200
        else:
            return jsonify({'error': 'Invalid password'}), 403

    except Exception as e:
        return jsonify({'error': str(e)}), 500
    finally:
        cur.close()
        conn.close()


@app.route('/accept_request', methods=['POST'])
def accept_request():
    data = request.json
    
    if not all(k in data for k in ('executor', 'region', 'account_type', 'token')):
        return jsonify({'error': 'Missing fields'}), 400
    
    if data['account_type'] != 'volunteer':
        return jsonify({'error': 'Account type has to be volunteer to accept requests'}), 400
    status = 'assigned'

    try:
        conn = connect_db()
        with conn:
            with conn.cursor() as cur:
                cur.execute("SELECT token FROM users WHERE login = %s", (data['executor'],))
                real_token = cur.fetchone()
                if real_token is None or real_token[0] != data['token']:
                    return jsonify({'error': 'Unauthorized'}), 401

                cur.execute("""
                    SELECT id FROM requests
                    WHERE executor IS NULL AND region = %s
                    ORDER BY created_at ASC
                    FOR UPDATE SKIP LOCKED
                    LIMIT 1;
                """, (data['region'],))
                picked_request = cur.fetchone()
                if picked_request is None:
                    return jsonify({'error': 'No requests available'}), 404

                request_id = picked_request[0]
                cur.execute("""
                    UPDATE requests
                    SET executor = %s,
                    status = %s
                    WHERE id = %s;
                """, (data['executor'], status, request_id))
                
                conn.commit()

        return jsonify({'message': 'Request accepted successfully', 'request_id': request_id}), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500
    finally:
        if conn:
            conn.close()


@app.route('/finish_request', methods=['POST'])
def finish_request():
    data = request.json

    if not all(k in data for k in ('executor', 'request_id', 'account_type', 'token')):
        return jsonify({'error': 'Missing fields'}), 400

    if data['account_type'] != 'volunteer':
        return jsonify({'error': 'Account type has to be volunteer to finish requests'}), 400

    try:
        conn = connect_db()
        with conn:
            with conn.cursor() as cur:
                cur.execute("SELECT token FROM users WHERE login = %s", (data['executor'],))
                real_token = cur.fetchone()
                if real_token is None or real_token[0] != data['token']:
                    return jsonify({'error': 'Unauthorized'}), 401

                now = datetime.now()
                date_now = now.strftime('%Y-%m-%d %H:%M:%S')
                cur.execute("""
                    UPDATE requests
                    SET status = 'finish', finished_at = %s
                    WHERE id = %s AND executor = %s;
                """, (date_now, data['request_id'], data['executor']))
                
                if cur.rowcount == 0:
                    return jsonify({'error': 'No matches found or you are not the executor of this request'}), 404

                conn.commit()

        return jsonify({'message': 'Request finished successfully', 'request_id': data['request_id'], 'finished_at': date_now}), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500
    finally:
        if conn:
            conn.close()


@app.route('/get_active_requests', methods=['GET'])
def get_active_requests():
    volunteer = request.args.get('volunteer')
    token = request.args.get('token')
    region = request.args.get('region')

    if not volunteer or not token or not region:
        return jsonify({'error': 'Missing parameters'}), 400     

    redis_key = f"status:created"
    
    if r.exists(redis_key):
        results = r.get(redis_key)
        return jsonify({'active_requests': json.loads(results)}), 200

    try:
        conn = connect_db()
        with conn:
            with conn.cursor() as cur:
                cur.execute("SELECT token, account_type FROM users WHERE login = %s", (volunteer,))
                user_data = cur.fetchone()
                if not user_data or user_data[0] != token or user_data[1] != 'volunteer':
                    return jsonify({'error': 'Unauthorized or incorrect account type'}), 401

                cur.execute("""
                    SELECT id, author, description, latitude, longitude, region, created_at
                    FROM requests
                    WHERE executor IS NULL AND region = %s
                    FOR UPDATE SKIP LOCKED;
                """, (region,))
                requests = cur.fetchall()

                results = [
                    {
                        'id': req[0],
                        'author': req[1],
                        'description': req[2],
                        'latitude': req[3],
                        'longitude': req[4],
                        'region': req[5],
                        'created_at': req[6]
                    }
                    for req in requests
                ]
                r.set(redis_key, json.dumps(results))
                return jsonify({'active_requests': results}), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500
    finally:
        if conn:
            conn.close()


@app.route('/get_customer_requests', methods=['GET'])
def get_customer_requests():
    customer = request.args.get('customer')
    token = request.args.get('token')

    if not customer or not token:
        return jsonify({'error': 'Missing parameters'}), 400

    redis_key = f"customer:{customer}"

    if r.exists(redis_key):
        results = r.get(redis_key)
        return jsonify({'active_requests': json.loads(results)}), 200
    try:
        conn = connect_db()
        with conn:
            with conn.cursor() as cur:
                cur.execute("SELECT token FROM users WHERE login = %s", (customer,))
                real_token = cur.fetchone()
                if real_token is None or real_token[0] != token:
                    return jsonify({'error': 'Unauthorized'}), 401

                cur.execute("""
                    SELECT id, author, description, latitude, longitude, region, created_at, executor, status, finished_at
                    FROM requests
                    WHERE author = %s;
                """, (customer,))
                requests = cur.fetchall()

                results = [
                    {
                        'id': request[0],
                        'author': request[1],
                        'description': request[2],
                        'latitude': request[3],
                        'longitude': request[4],
                        'region': request[5],
                        'created_at': request[6],
                        'executor': request[7],
                        'status': request[8],
                        'finished_at': request[9]
                    }
                    for request in requests
                ]
                r.set(redis_key, json.dumps(results))
                return jsonify({'requests': results}), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500
    finally:
        if conn:
            conn.close()


@app.route('/create_request', methods=['POST'])
def create_request():
    data = request.json
    
    if not all(k in data for k in ('author', 'description', 'latitude', 'longitude', 'region', 'account_type', 'token')):
        return jsonify({'error': 'Missing fields'}), 400

    if data['account_type'] != 'customer':
        return jsonify({'error': 'Account type has to be customer to create requests'}), 400

    redis_key = f"customer:{data['author']}"

    if r.exists(redis_key):
        results = r.delete(redis_key)

    redis_reg = f"status:created"

    if r.exists(redis_reg):
        r.delete(redis_reg)

    now = datetime.now()
    date_now = now.strftime('%Y-%m-%d %H:%M:%S')
    status = 'created'
    try:
        conn = connect_db()
        cur = conn.cursor()
        
        cur.execute("SELECT token FROM users WHERE login = %s", (data['author'],))
        real_token = cur.fetchone()[0]
        if real_token != data['token']:
            return jsonify({'error': 'Unauthorized'}), 401

        cur.execute("INSERT INTO requests (author, description, latitude, longitude, region, created_at, status) VALUES (%s, %s, %s, %s, %s, %s, %s) RETURNING id;", (data['author'], data['description'], data['latitude'], data['longitude'], data['region'], date_now, status))
        request_id = cur.fetchone()[0]
        conn.commit()
        return jsonify({'message': 'Request was created successfully', 'created_at': date_now, 'request_id': request_id}), 201

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