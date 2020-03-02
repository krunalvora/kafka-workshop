# Apache Kafka 101

## Installation
Download the latest Kafka binary from the [Apache Kafka Download](https://kafka.apache.org/downloads) page.

```bash
wget <kafka tgz>

tar -xvf kafka_2.12-2.4.0.tgz

# Create a symbolic link to the kafka directory to refer to it easily 
ln -s kafka_2.12-2.4.0.tgz kafka

```

## Set up Kafka and Zookeeper services

> Recommended way to setup Kafka and Zookeeper service using [systemd-services](https://github.com/krunalvora/apachekafka101/blob/master/systemd-services/README.md)

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

## Console Producer

## Console Consumer

## Java Producer

## Java Consumer
