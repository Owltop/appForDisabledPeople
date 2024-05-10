from flask import Flask, request, jsonify
import psycopg2
import bcrypt
import random
import string
from server import app

# Параметры подключения к БД
dbname = "main"
user = "admin"
password = "admin1234"
host = "postgresql"

# Функция для подключения к базе данных
def connect_db():
    conn = psycopg2.connect(database=dbname, user=user, password=password, host=host)
    return conn

def generate_token():
    return ''.join(random.choices(string.ascii_letters + string.digits, k=30))

@app.route('/register', methods=['POST'])
def register_user():
    data = request.json
    
    if not all(k in data for k in ('login', 'password', 'email', 'age', 'first_name', 'last_name', 'phone_number', 'telegram')):
        return jsonify({'error': 'Missing fields'}), 400
    
    hashed_password = bcrypt.hashpw(data['password'].encode('utf-8'), bcrypt.gensalt())

    try:
        conn = connect_db()
        cur = conn.cursor()
        
        cur.execute("SELECT * FROM users WHERE login = %s OR email = %s;", (data['login'], data['email']))
        if cur.fetchone():
            return jsonify({'error': 'User already exists'}), 400
        token = generate_token()
        cur.execute("INSERT INTO users (login, password, email, age ,first_name, last_name, phone_number, telegram, token) VALUES (%s, %d, %s, %s, %s, %s, %s, %s, %s) RETURNING id;", 
                    (data['login'], hashed_password, data['email'], data['age'], data['first_name'], data['last_name'], data['phone_number'], data['telegram'], token))
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
