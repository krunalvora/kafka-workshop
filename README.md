# Apache Kafka 101

## Table of Contents
1. [Slides](#slides)
2. [Installation](#installation)
3. [Start Zookeeper](#start-zookeeper)
    1. [Using Zookeeper shell scripts](#using-zookeeper-shell-scripts)
    2. [Setup Zookeeper Service](#setup-zookeeper-service)
4. [Start Kafka](#start-kafka)
    1. [Using Kafka shell scripts](#using-kafka-shell-scripts)
    2. [Setup Kafka service](#setup-kafka-service)
4. [Create Kafka Topic](#create-kafka-topic)
5. [Kafka Clients](#kafka-clients)
    1. [Console Producer](#console-producer)
    2. [Console Consumer](#console-consumer)
    3. [Python-Client](#python-client)

## Slides
Slides are available [here](https://docs.google.com/presentation/d/1oj05PmkEfKmA_gFRikpfQoZabDjeBCW6eO_C1RH3Hh8/edit?usp=sharing).

> The instructions below are for a linux(optionally mac) OS. You can follow the steps in the Kafka Documentation for other OSs.

## Installation
Download the latest Kafka binary from the [Apache Kafka Download](https://kafka.apache.org/downloads) page.

```bash
wget <kafka tgz>

tar -xvf kafka_2.12-2.4.0.tgz

# Create a symbolic link to the kafka directory to refer to it easily 
ln -s kafka_2.12-2.4.0.tgz kafka

```

## Start Zookeeper

### Zookeeper Service

> Recommended way to setup Kafka and Zookeeper service is using [systemd-services](https://github.com/krunalvora/apachekafka101/blob/master/systemd-services/README.md).

```bash
cd ~/kafka

> Command options within [] are optional. Please make the relevant changes to your command before running them.

# Start zookeeper
# -daemon runs the process in background
./bin/zookeeper-server-start.sh [-daemon] config/zookeeper.properties

# Start kafka
# -daemon runs the process in background
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
