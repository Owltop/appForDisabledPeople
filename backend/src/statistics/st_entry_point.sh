#!/bin/bash
while ! curl -s rabbitmq:15672 > /dev/null; do echo waiting for rabbitmq; sleep 3; done;
sleep 5

python3 src/pg_init/pg_init.py

exec python3 src/statistics/statistics.py --port 5053
