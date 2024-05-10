CREATE USER admin WITH ENCRYPTED PASSWORD 'admin1234';
CREATE DATABASE main WITH OWNER admin;
GRANT ALL PRIVILEGES ON DATABASE main TO admin;
-- CREATE TABLE users
-- (
--     id INTEGER PRIMARY KEY AUTOINCREMENT,
--     username TEXT NOT NULL,
--     password INTEGER NOT NULL,
--     token TEXT,
--     first_name TEXT,
--     last_name TEXT,
--     date_of_birth TEXT,
--     email TEXT,
--     phone_number TEXT,
--     telgram TEXT
-- );

