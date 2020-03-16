#!/usr/bin/python3

from kafka import KafkaConsumer
import argparse
import logging as log
import sys

log.basicConfig(format='%(asctime)s %(name)-12s %(levelname)-8s %(message)s', 
                datefmt='%Y-%m-%d %H:%M:%S', 
                level=log.INFO) 


def consume(bootstrap_server, topic):
  try:
    consumer = KafkaConsumer(bootstrap_servers=bootstrap_server)
    consumer.subscribe(topic)
    for msg in consumer:
      log.info("Consumer Record: \n{}".format(msg))
  except Exception as ex:
    log.error(str(ex))

def main(args):
  parser = argparse.ArgumentParser()
  parser.add_argument("-t", "--topic", help="topic", required=True)
  parser.add_argument("-b", "--bootstrap-server", help="bootstrap-server", default="localhost:9092")
  args = parser.parse_args()

  consume(args.bootstrap_server, args.topic)


if __name__ == "__main__":
  main(sys.argv)