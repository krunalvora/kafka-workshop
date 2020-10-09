# Apache Kafka Workshop

## Table of Contents
1. [Slides](#slides)
2. [Assignment](#assignment)
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
5. [Schema Registry and Schema Registry UI](#schema-registry-and-schema-registry-ui)
6. [Rest Proxy and Kafka Topics UI](#rest-proxy-and-kafka-topics-ui)
4. [Tools for Kafka and Zookeeper](#tools-for-kafka-and-zookeeper)
	1. [CMAK](#cmak-cluster-manager-for-apache-kafka)
	2. [ZooNavigator](#zoonavigator)
5. [Reassigning Partitions](#reassigning-partitions)

# Slides
Slides are available [here](https://docs.google.com/presentation/d/1oj05PmkEfKmA_gFRikpfQoZabDjeBCW6eO_C1RH3Hh8/edit?usp=sharing).

> The instructions below are for a linux(optionally mac) OS. You can follow the steps in the Kafka Documentation for other OSs.

# Assignment

1. Spin up dockerized containers for Kafka and Zookeeper using [Kafka on Docker](#kafka-on-docker).
2. Install [kafkacat](https://github.com/edenhill/kafkacat).
3. Create a topic in the Kafka cluster using kafkacat.
4. Produce to and consume from the topic using kafkacat.

# Kafka on Docker

There are several Kafka Docker images available. We are going to use [wurstmeister/kafka-docker](https://github.com/wurstmeister/kafka-docker) image here. 

```bash
cd kafka

# Start zookeeper and kafka broker
docker-compose up -d

# Stop zookeeper and kafka containers
docker-compose down
```

> Even if run Kafka on Docker, you would still want to follow the steps for [Kafka on Host Machine](#kafka-on-host-machine) if you want to use `kafka-client-producer` and `kafka-client-consumer` that is shipped along with Kafka. 

# Kafka on Host Machine
### Installation
Download the latest Kafka binary from the [Apache Kafka Download](https://kafka.apache.org/downloads) page.

```bash

wget <kafka tgz>

sudo tar -xvf <kafka tgz> -C /usr/local/

# Create a symbolic link to the kafka directory to refer to it easily 
sudo ln -s /usr/local/<kafka_dir> /usr/local/kafka

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


## Setup Zookeeper and Kafka Systemd Services for Linux

Refer to the steps [here](https://github.com/krunalvora/kafka-workshop/tree/master/systemd-services) to setup Systemd services for Kafka and Zookeeper to automate the start/stop commands and make your life easier.

# Create Kafka Topic
```bash
/usr/local/kafka/bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic topic1 --replication-factor 1 --partitions 2
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

# Schema Registry and Schema Registry UI

Reference: [Confluent Schema Registry](https://docs.confluent.io/current/schema-registry/index.html)
```bash
cd schema-registry

docker-compose up -d
```

This creates two services:
- Schema Registry at `http://localhost:8081`. You can then use curl or a Schema Registry UI to play with the registry.
- Schema Registry UI at [http://localhost:8001](http://localhost:8001)


# Rest Proxy and Kafka Topics UI

Reference: [Confluent Rest Proxy](https://docs.confluent.io/current/kafka-rest/index.html)
```bash
cd kafka-rest-proxy/

docker-compose up -d
```

This creates two services:
- Rest Proxy at `http://localhost:8082`.
- Kafka Topics UI at [http://localhost:8002](http://localhost:8002)



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
Zoonavigator should be available on [http://localhost:9000](http://localhost:9000)

# Reassigning Partitions

Documentation available [here](https://kafka.apache.org/documentation/#basic_ops_cluster_expansion).

Create a `topics-to-move.json` file
```json
{"topics":  [{"topic": "topic1"}],
 "version":1
}
```

```bash
kafka-reassign-partitions.sh --zookeeper localhost:2181 --topics-to-move-json-file topics-to-move.json --broker-list "1001,1002,1003" --generate
```

Save the proposed partition reassignment configuration into a file `reassignment.json`
```json
{
  "version":1,
  "partitions":[{"topic":"topic1","partition":0,"replicas":[1002],"log_dirs":["any"]},
                {"topic":"topic1","partition":2,"replicas":[1001],"log_dirs":["any"]},
                {"topic":"topic1","partition":1,"replicas":[1003],"log_dirs":["any"]}]
}
```

```bash
kafka-reassign-partitions.sh --zookeeper localhost:2181 --reassignment-json-file reassignment.json --execute
```

```bash
kafka-reassign-partitions.sh --zookeeper localhost:2181 --reassignment-json-file reassignment.json --verify
```
