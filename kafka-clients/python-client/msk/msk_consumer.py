#!/usr/bin/python3

from kafka import KafkaConsumer
import boto3
import logging as log
import sys

log.basicConfig(format='%(asctime)s %(name)-12s %(levelname)-8s %(message)s',
                datefmt='%Y-%m-%d %H:%M:%S',
                level=log.INFO)


def main(args):
    region_name = 'us-west-2'
    msk_cluster_arn = '<cluster-arn>'
    topic = "topic1"
    try:
        # Get the bootstrap servers using MSK api
        msk = boto3.client('kafka', region_name = region_name)
        response = msk.get_bootstrap_brokers(ClusterArn=msk_cluster_arn)
        bootsrap_server = response['BootstrapBrokerString']

        # consume messages using KafkaConsumer
        consumer = KafkaConsumer(bootstrap_servers=bootsrap_server)
        consumer.subscribe(topic)
        for msg in consumer:
            log.info("Consumer Record: \n{}".format(msg))
    except Exception as ex:
        log.error(str(ex))


if __name__ == "__main__":
    main(sys.argv)
