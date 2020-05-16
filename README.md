# Apache Kafka 101

## Table of Contents
1. [Slides](#slides)
2. [Kafka on Docker](#kafka-on-docker)
2. [Kafka on Host Machine](#kafka-on-host-machine)
	1. [Installation](#Installation)
	2. [Start Zookeeper and Kafka](#start-zookeeper-and-kafka)
	3. [Setup Zookeeper and Kafka Systemd Services for Linux](#setup-zookeeper-and-kafka-systemd-services-for-linux)
4. [Create Kafka Topic](#create-kafka-topic)
5. [Produce messages to Kafka](#produce-messages-to-kafka)
6. [Consume messages from Kafka](#consume-messages-from-kafka)
5. [Produce/Consume using Kafka Clients](#produceconsume-using-kafka-clients)
    1. [Python-Client](#python-client)
4. [Tools for Kafka and Zookeeper](#tools-for-kafka-and-zookeeper)
	1. [CMAK](#cmak-cluster-manager-for-apache-kafka)
	2. [ZooNavigator](#zoonavigator)

# Slides
Slides are available [here](https://docs.google.com/presentation/d/1oj05PmkEfKmA_gFRikpfQoZabDjeBCW6eO_C1RH3Hh8/edit?usp=sharing).

> The instructions below are for a linux(optionally mac) OS. You can follow the steps in the Kafka Documentation for other OSs.

# Kafka on Docker

Refer the steps [here](https://github.com/krunalvora/kafka-workshop/tree/master/kafka-docker).
If you want to run Kafka on Docker, skip the next step of `Kafka on Host Machine` and jump to [Create Kafka Topic](#create-kafka-topic). 

# Kafka on Host Machine
### Installation
Download the latest Kafka binary from the [Apache Kafka Download](https://kafka.apache.org/downloads) page.

```bash

wget <kafka tgz>

tar -xvf <kafka tgz> -C /usr/local/

# Create a symbolic link to the kafka directory to refer to it easily 
ln -s <kafka_dir> kafka

```

> If you want to use Kafka commands directly without using , add the below export command to your `.bashrc` / `.bash_profile` and source the file:
```bash
# Add kafka bin to PATH
export PATH=/usr/local/kafka/bin:$PATH
```

`source ~/.bashrc`  or `source ~/.bash_profile`


### Start Zookeeper and Kafka

```bash
# Command options within [] are optional. Please make the relevant changes to your command before running them.
# -daemon runs the process in background
/usr/local/kafka/bin/zookeeper-server-start.sh [-daemon] /usr/local/kafka/config/zookeeper.properties
```

> To stop zookeeper, `/usr/local/kafka/bin/zookeeper-server-stop.sh`


```bash
# Setting environment variables for Kafka
export KAFKA_HEAP_OPTS="-Xmx1G -Xms1G"

# -daemon runs the process in background
/usr/local/kafka/bin/kafka-server-start.sh [-daemon] /usr/local/kafka/config/server.properties
```

> To stop kafka, `/usr/local/kafka/bin/kafka-server-stop.sh`


### Setup Zookeeper and Kafka Systemd Services for Linux

Refer to the steps [here](https://github.com/krunalvora/kafka-workshop/tree/master/systemd-services) to setup Systemd services for Kafka and Zookeeper to automate the start/stop commands and make your life easier.

# Create Kafka Topic
```bash
$KAFKA_HOME/bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic topic1 --replication-factor 1 --partitions 2
```

# Produce messages to Kafka
```bash
/usr/local/kafka/bin/kafka-console-producer.sh --broker-list localhost:9092 --topic topic1
```
Open a new terminal window to start consuming while leaving this window untouched.

# Consume messages from Kafka
```bash
/usr/local/kafka/bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic topic1  [--from-beginning]
```
Experiment with producing in string messages using the console producer and viewing them back into the console consumer.

# Produce/Consume using Kafka Clients

### Python-Client
```bash
pip3 install kafka-python

python3 python-client/consumer.py

# In a new shell window:
python3 python-client/producer.py
```

# Tools for Kafka and Zookeeper

### CMAK - Cluster Manager for Apache Kafka
```bash
cd kafka-manager

docker-compose up -d
```

### ZooNavigator
```bash
cd zoonavigator

docker-compose up -d
```
