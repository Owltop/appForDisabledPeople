#!/bin/bash

sleep 10

python3 src/pg_init/pg_init.py

exec python3 src/server.py --port 5050
