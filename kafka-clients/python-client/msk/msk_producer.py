#!/usr/bin/python3

from kafka import KafkaProducer
import boto3
import logging as log
import sys

log.basicConfig(format='%(asctime)s %(name)-12s %(levelname)-8s %(message)s',
                datefmt='%Y-%m-%d %H:%M:%S',
                level=log.INFO)


def main(args):
    msk_cluster_arn = '<cluster-arn>'
    topic = "topic1"
    message = "hello-from-msk-client"
    try:
        # Get the bootstrap servers using MSK api
        msk = boto3.client('kafka', region_name = 'us-west-2')
        response = msk.get_bootstrap_brokers(ClusterArn=msk_cluster_arn)
        bootsrap_server = response['BootstrapBrokerString']

        # produce message using KafkaProducer
        producer = KafkaProducer(bootstrap_servers=bootsrap_server)
        message_bytes = bytes(message, encoding='utf-8')
        producer.send(topic, value=message_bytes)
        producer.flush()
        log.info("Message published successfully.")
    except Exception as ex:
        log.error(str(ex))


if __name__ == "__main__":
    main(sys.argv)
