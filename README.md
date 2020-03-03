# Apache Kafka 101

Slides available [here](https://docs.google.com/presentation/d/1oj05PmkEfKmA_gFRikpfQoZabDjeBCW6eO_C1RH3Hh8/edit?usp=sharing).

> The instructions below are for a linux(optionally mac) OS. You can follow the steps in the Kafka Documentation for other OSs.

## Installation
Download the latest Kafka binary from the [Apache Kafka Download](https://kafka.apache.org/downloads) page.

```bash
wget <kafka tgz>

tar -xvf kafka_2.12-2.4.0.tgz

# Create a symbolic link to the kafka directory to refer to it easily 
ln -s kafka_2.12-2.4.0.tgz kafka

```

## Set up Kafka and Zookeeper services

> Recommended way to setup Kafka and Zookeeper service is using [systemd-services](https://github.com/krunalvora/apachekafka101/blob/master/systemd-services/README.md).

```bash
cd ~/kafka

# Start zookeeper
./bin/zookeeper-server-start.sh [-daemon] config/zookeeper.properties

# Start kafka
./bin/kafka-server-start.sh [-daemon] config/server.properties


# Stop kafka
./bin/kafka-server-stop.sh

# Stop zookeeper 
./bin/zookeeper-server-stop.sh
```
## Create a Kafka Topic
```bash
~/kafka/bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic topic1 --replication-factor 1 --partitions 2
```

## Console Producer / Consumer
```bash
~/kafka/bin/kafka-console-producer.sh --broker-list localhost:9092 --topic topic1

~/kafka/bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic topic1  [--from-beginning]
```
Experiment with producing in string messages using the console producer and viewing them back into the console consumer.


## Python-Client
Experiment with the python clients using the instructions [here](https://github.com/krunalvora/apachekafka101/tree/master/python-client).
