FROM python:3.10
WORKDIR /app
COPY ./utils/requirements.txt ./requirements.txt
RUN pip install --no-cache-dir -r ./requirements.txt

COPY . /app

# COPY entry_point.sh /app/
RUN chmod +x /app/entry_point.sh
CMD ["/app/entry_point.sh"]
