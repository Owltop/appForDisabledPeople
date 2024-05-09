import sqlite3

DATABASE = './users_info.db'


def create_tables():
    conn = sqlite3.connect(DATABASE)
    cursor = conn.cursor()
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS users_info (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            username TEXT NOT NULL,
            password TEXT NOT NULL,
            token TEXT,
            first_name TEXT,
            last_name TEXT,
            date_of_birth TEXT,
            email TEXT,
            phone_number TEXT,
            telgram TEXT
        );
    ''')
    conn.commit()
    conn.close()


if __name__ == '__main__':
    create_tables()