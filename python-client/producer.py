#!/usr/bin/python3

from kafka import KafkaProducer

producer = KafkaProducer(bootstrap_servers="localhost:9092")

topic = "topic1"
value = "message"

try:
  # key_bytes = bytes(key, encoding='utf-8')
  value_bytes = bytes(value, encoding='utf-8')
  for i in list(range(5)):
    producer.send(topic, value=value_bytes)
  producer.flush()
  print("Messages published successfully.")
except Exception as ex:
  print(str(ex))
