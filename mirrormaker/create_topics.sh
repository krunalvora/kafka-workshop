#!/bin/bash

export SOURCE=localhost:9092,localhost:9093,localhost:9094
export TARGET=localhost:8082,localhost:8083,localhost:8084

/usr/local/kafka/bin/kafka-topics.sh --bootstrap-server $SOURCE --create --topic topic1 --partitions 3 --replication-factor 1
/usr/local/kafka/bin/kafka-topics.sh --bootstrap-server $SOURCE --create --topic topic2 --partitions 3 --replication-factor 1
/usr/local/kafka/bin/kafka-topics.sh --bootstrap-server $SOURCE --create --topic topic3 --partitions 3 --replication-factor 1
