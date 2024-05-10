import psycopg2

# Параметры подключения к вашей базе данных
dbname = "main"
user = "admin"
password = "admin1234"
host = "postgresql"

conn = psycopg2.connect(dbname=dbname, user=user, password=password, host=host)

cur = conn.cursor()
cur.execute("""
    CREATE TABLE IF NOT EXISTS users (
    id serial PRIMARY KEY,
    login TEXT NOT NULL,
    password INTEGER NOT NULL,
    token TEXT,
    first_name TEXT,
    last_name TEXT,
    age TEXT,
    email TEXT,
    phone_number TEXT,
    telegram TEXT
    );
""")
conn.commit()
cur.close()
conn.close()