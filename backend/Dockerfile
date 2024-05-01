FROM python:3.10
WORKDIR /app
COPY ./utils/requirements.txt ./requirements.txt
RUN pip install --no-cache-dir -r ./requirements.txt

COPY . /app

CMD ["python3", "src/server.py", "--port", "5050"]
