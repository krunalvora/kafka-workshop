# Apache Kafka 101

## Table of Contents
1. [Slides](#slides)
2. [Kafka on Host Machine](#kafka-on-host-machine)
	1. [Installation](#installation)
	2. [Set KAFKA_HOME (IMPORTANT)](#set-kafka_home)
	3. [Start Zookeeper](#start-zookeeper)
    	1. [Using Zookeeper shell scripts](#using-zookeeper-shell-scripts)
    	2. [Setup Zookeeper Service](#setup-zookeeper-service)
	4. [Start Kafka](#start-kafka)
    	1. [Using Kafka shell scripts](#using-kafka-shell-scripts)
    	2. [Setup Kafka service](#setup-kafka-service)
3. [Kafka on Docker](#kafka-on-docker)
4. [Setup tools for Zookeeper and Kafka on Docker](#setup-tools-for-zookeeper-and-kafka-on-docker)
4. [Create Kafka Topic](#create-kafka-topic)
5. [Produce messages to Kafka](#produce-messages-to-kafka)
6. [Consume messages from Kafka](#consume-messages-from-kafka)
5. [Produce/Consume using Kafka Clients](#produceconsume-using-kafka-clients)
    1. [Python-Client](#python-client)

## Slides
Slides are available [here](https://docs.google.com/presentation/d/1oj05PmkEfKmA_gFRikpfQoZabDjeBCW6eO_C1RH3Hh8/edit?usp=sharing).

> The instructions below are for a linux(optionally mac) OS. You can follow the steps in the Kafka Documentation for other OSs.

## Kafka on Host Machine
### Installation
Download the latest Kafka binary from the [Apache Kafka Download](https://kafka.apache.org/downloads) page.

```bash
cd <KAFKA_INSTALLATION_DIRECTORY>

wget <kafka tgz>

tar -xvf kafka_2.12-2.4.0.tgz

# Create a symbolic link to the kafka directory to refer to it easily 
ln -s kafka_2.12-2.4.0 kafka

```

### Setup KAFKA_HOME

Depending on where you install `kafka` in the above step, set `KAFKA_HOME`:
```bash
export KAFKA_HOME=<KAFKA_INSTALLATION_DIRECTORY>/kafka

```

> If you want to use Kafka commands directly without using `KAFKA_HOME`, add the below export command to your `.bashrc` / `.bash_profile` and source the file:
```bash
# Add KAFKA_HOME to PATH
export PATH=$KAFKA_HOME/bin:$PATH
```

### Start Zookeeper

#### Using Zookeeper shell scripts

```bash

# Command options within [] are optional. Please make the relevant changes to your command before running them.
# -daemon runs the process in background
$KAFKA_HOME/bin/zookeeper-server-start.sh [-daemon] $KAFKA_HOME/config/zookeeper.properties
```

#### Stop zookeeper
```bash 
$KAFKA_HOME/bin/zookeeper-server-stop.sh
```

#### Setup Zookeeper Service
 
##### Using Systemd
```bash
sudo vim /etc/systemd/system/zookeeper.service

# Paste the content of zookeeper.service from systemd-services/zookeeper.service into the opened file

sudo systemctl enable zookeeper

sudo systemctl [status | start | stop] zookeeper
```

### Start Kafka

#### Using Kafka shell scripts
```bash
cd ~/kafka

# -daemon runs the process in background
$KAFKA_HOME/bin/kafka-server-start.sh [-daemon] $KAFKA_HOME/config/server.properties
```
#### Stop kafka
```bash
$KAFKA_HOME/bin/kafka-server-stop.sh
```

#### Setup Kafka Service
 
##### Using Systemd
```bash
sudo vim /etc/systemd/system/kafka.service

# Paste the content of kafka.service from systemd-services/kafka.service into the opened file

sudo systemctl enable kafka

sudo systemctl [status | start | stop] kafka
```

## Kafka on Docker
```bash
cd kafka-docker

export ADVERTISED_HOST_NAME=`ipconfig getifaddr en0`

# Start zookeeper and 3 kafka containers
docker-compose up -d

# Stop zookeeper and kafka containers
docker-compose stop
```

## Setup tools for Zookeeper and Kafka on Docker
```bash
cd kafka-manager

docker-compose up -d
```

```bash
cd zoonavigator

docker-compose up -d
```

## Create Kafka Topic
```bash
$KAFKA_HOME/bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic topic1 --replication-factor 1 --partitions 2
```

## Produce messages to Kafka
```bash
$KAFKA_HOME/kafka-console-producer.sh --broker-list localhost:9092 --topic topic1
```
Open a new terminal window to start consuming while leaving this window untouched.

## Consume messages from Kafka
```bash
$KAFKA_HOME/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic topic1  [--from-beginning]
```
Experiment with producing in string messages using the console producer and viewing them back into the console consumer.

## Produce/Consume using Kafka Clients

### Python-Client
```bash
pip3 install kafka-python

python3 python-client/consumer.py

# In a new shell window:
python3 python-client/producer.py
```
