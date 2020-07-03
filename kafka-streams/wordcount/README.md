# Steps to run the Wordcount Streams App

Create the input topic
```bash
kafka-topics.sh --bootstrap-server localhost:9092 --create --topic streams-plaintext-input --partitions 1 --replication-factor 1
```

Create the output topic
```bash
kafka-topics.sh --bootstrap-server localhost:9092 --create --topic streams-wordcount-output --partitions 1 --replication-factor 1
```

Build the jar
```bash
mvn clean package
```

Kafka console consumer
```bash
kafka-console-consumer.sh --bootstrap-server localhost:9--topic streams-wordcount-output --value-deserializer org.apache.kafka.common.serialization.LongDeserializer --property print.key=true --property key.separator=" "
```

Kafka console producer
```bash
kafka-console-producer.sh --broker-list localhost:9092 --topic streams-plaintext-input
```

Run the App
```bash
java -cp target/kafka-streams-wordcount-1.0-jar-with-dependencies.j com.github.krunalvora.kafka_streams.WordCountApp
```

Produce messages into the input topic and the count of words should show up in the console consumer.