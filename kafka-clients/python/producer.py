#!/usr/bin/python3

from kafka import KafkaProducer
import argparse
import logging as log
import random
import string
import sys
import time

log.basicConfig(format='%(asctime)s %(name)-12s %(levelname)-8s %(message)s', 
                datefmt='%Y-%m-%d %H:%M:%S', 
                level=log.INFO) 


def produce_message(broker_list, topic, message=None):
  try:
    producer = KafkaProducer(bootstrap_servers=broker_list)
    message_bytes = bytes(message, encoding='utf-8')
    producer.send(topic, value=message_bytes)
    producer.flush()
    log.info("Messages published successfully.")
  except Exception as ex:
    log.error(str(ex))


def produce_dummy_messages(broker_list, topic):
  try:
    producer = KafkaProducer(bootstrap_servers=broker_list)
    while True:
      message = ''.join(random.choice(string.ascii_letters) for i in range(5))
      message_bytes = bytes(message, encoding='utf-8')
      producer.send(topic, value=message_bytes)
      time.sleep(5)
    producer.flush()
    log.info("Messages published successfully.")
  except Exception as ex:
    log.error(str(ex))


def main(args):
  parser = argparse.ArgumentParser()
  parser.add_argument("-t", "--topic", help="topic", required=True)
  parser.add_argument("-b", "--broker-list", help="broker-list", default="localhost:9092")
  message = parser.add_mutually_exclusive_group(required=True)
  message.add_argument("--dummy-messages", help="produce dummy messages indefinitely every 5s", action="store_true")
  message.add_argument("-m", "--message", help="single message to be produced")
  args = parser.parse_args()

  if args.dummy_messages:
    produce_dummy_messages(args.broker_list, args.topic)
  else:
    produce_message(args.broker_list, args.topic, args.message)


if __name__ == "__main__":
  main(sys.argv)
