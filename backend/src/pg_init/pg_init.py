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
    password TEXT NOT NULL,
    token TEXT,
    name TEXT,
    age TEXT,
    email TEXT,
    account_type TEXT
    );

    CREATE TABLE IF NOT EXISTS requests (
    id serial PRIMARY KEY,
    author TEXT NOT NULL,
    executor TEXT,
    latitude TEXT,
    longitude TEXT,
    region TEXT,
    description TEXT,
    status TEXT,
    created_at TEXT,
    finished_at TEXT
    );       
    
""")
conn.commit()
cur.close()
conn.close()