from flask import Flask, request, jsonify
import psycopg2
import bcrypt
import secrets
from server import app

# Параметры подключения к БД
DB_NAME = "users"
DB_USER = "admin"
DB_PASS = "admin1234"
DB_HOST = "user_db"
DB_PORT = "5432"

# Функция для подключения к базе данных
def connect_db():
    conn = psycopg2.connect(database=DB_NAME, user=DB_USER, password=DB_PASS, host=DB_HOST, port=DB_PORT)
    return conn

def generate_token():
    return secrets.token_hex(16)

@app.route('/register', methods=['POST'])
def register_user():
    data = request.json
    
    if not all(k in data for k in ('name', 'login', 'password', 'email', 'age')):
        return jsonify({'error': 'Missing fields'}), 400
    
    hashed_password = bcrypt.hashpw(data['password'].encode('utf-8'), bcrypt.gensalt())

    try:
        conn = connect_db()
        cur = conn.cursor()
        
        cur.execute("SELECT * FROM users WHERE login = %s OR email = %s;", (data['login'], data['email']))
        if cur.fetchone():
            return jsonify({'error': 'User already exists'}), 400
        
        cur.execute("INSERT INTO users (name, login, password, email, age) VALUES (%s, %s, %s, %s, %s) RETURNING id;", 
                    (data['name'], data['login'], hashed_password, data['email'], data['age']))
        user_id = cur.fetchone()[0]
        conn.commit()
        
        token = generate_token()
        
        return jsonify({'message': 'User registered successfully', 'token': token}), 201
    except Exception as e:
        conn.rollback()
        return jsonify({'error': str(e)}), 500
    finally:
        if cur is not None:
            cur.close()
        if conn is not None:
            conn.close()
