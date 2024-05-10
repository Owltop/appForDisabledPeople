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
        id SERIAL PRIMARY KEY,
        name VARCHAR(50) NOT NULL,
        login VARCHAR(50) UNIQUE NOT NULL,
        password INTEGER NOT NULL,
        email VARCHAR(100) UNIQUE NOT NULL,
        age INTEGER NOT NULL
    );
""")
conn.commit()
cur.close()
conn.close()