import json
from flask import Flask, request, jsonify
import argparse
import psycopg2
import pika
import threading
import logging


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

def callback(ch, method, properties, body):
    data = json.loads(body)
    login = data['login']
    token = data['token']
    print(f"Received username: {login}, token: {token}")

    try:
        conn = connect_db()
        with conn:
            with conn.cursor() as cur:
                cur.execute("SELECT rating FROM users WHERE login = %s", (data['execloginutor'],))
                rating = 0
                prev_rating = cur.fetchone()
                if prev_rating is None:
                    cur.execute(f"INSERT INTO ratings (login, token, rating) VALUES ('{login}', '{token}', '{rating}') RETURNING id;")
                else:
                    rating = prev_rating[0]
                
                cur.execute("""
                    UPDATE ratings
                    SET rating = %s
                    WHERE login = %s;
                """, (rating, login))

                print(f"Rating was updated for user = {login}")

                conn.commit()
    except Exception as e:
        print(str(e))
    finally:
        if conn:
            conn.close()

def consume():
    connection = pika.BlockingConnection(pika.ConnectionParameters('rabbitmq'))
    channel = connection.channel()
    
    channel.queue_declare(queue='user_events')
    
    channel.basic_consume(queue='user_events', on_message_callback=callback, auto_ack=True)
    
    print('Waiting for messages. To exit press CTRL+C')
    channel.start_consuming()

def start_rabbitmq_consumer():
    t = threading.Thread(target=consume)
    t.start()



@app.route('/get_rating', methods=['GET'])
def get_rating():
    volunteer = request.args.get('volunteer')
    token = request.args.get('token')

    if not volunteer or not token:
        return jsonify({'error': 'Missing parameters'}), 400
    try:
        conn = connect_db()
        with conn:
            with conn.cursor() as cur:
                cur.execute("SELECT rating FROM ratings WHERE login = %s", (volunteer,))
                rating = cur.fetchone()
                if rating is None:
                    return jsonify({'error': 'No such user'}), 404

                return jsonify({'rating': rating[0]}), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500
    finally:
        if conn:
            conn.close()



ip_address = "0.0.0.0"
logging.basicConfig()
parser = argparse.ArgumentParser()
parser.add_argument('--port', help='port', default=5053)

args = parser.parse_args()
port = args.port

start_rabbitmq_consumer()
logging.debug("rabit mq has started")
print(f"Server running at http://{ip_address}:{port}/")
app.run(debug=True, host=ip_address, port=port)