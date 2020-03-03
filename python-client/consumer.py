#!/usr/bin/python3

from kafka import KafkaConsumer

consumer = KafkaConsumer(bootstrap_servers="localhost:9092")

topic = "topic1"

try:
  consumer.subscribe("topic1")
  for msg in consumer:
    print(msg)
except Exception as ex:
  print(str(ex))
