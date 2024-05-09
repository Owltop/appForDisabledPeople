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
        
        # Поиск пользователя по логину или почте
        cur.execute("SELECT id, password FROM users WHERE login = %s OR email = %s;", (login_or_email, login_or_email))
        user = cur.fetchone()

        if user is None:
            return jsonify({'error': 'User not found'}), 404

        user_id, hashed_password = user

        # Проверка пароля
        if bcrypt.checkpw(password.encode('utf-8'), hashed_password):
            # Генерация токена/сессии для пользователя
            token = secrets.token_hex(16)
            return jsonify({'message': 'Login successful', 'token': token}), 200
        else:
            return jsonify({'error': 'Invalid password'}), 403

    except Exception as e:
        return jsonify({'error': str(e)}), 500
    finally:
        cur.close()
        conn.close()