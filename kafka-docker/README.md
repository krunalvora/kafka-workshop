# Kafka on Docker

```bash
cd kafka-docker

export ADVERTISED_HOST_NAME=`ipconfig getifaddr en0`

# Start zookeeper and 3 kafka containers
docker-compose up -d

# Stop zookeeper and kafka containers
docker-compose stop
```
