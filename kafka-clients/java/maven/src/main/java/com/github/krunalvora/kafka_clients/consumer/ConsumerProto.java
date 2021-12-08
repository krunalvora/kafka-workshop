package com.github.krunalvora.kafka_clients.consumer;

import com.github.daniel.shuy.kafka.protobuf.serde.KafkaProtobufDeserializer;
import com.github.krunalvora.kafka_clients.protobuf.Event1OuterClass.Event1;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Parser;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class ConsumerProto {
  public static void main(String[] args) {
    Properties properties = new Properties();
    properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    properties.put(ConsumerConfig.GROUP_ID_CONFIG, "generic-protobuf-consumer-group");
    properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

    // properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    // properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaProtobufDeserializer.class);

    String topic = "topic7";

    KafkaConsumer<String, Event1> consumer = new KafkaConsumer<String, Event1>(properties,
          new StringDeserializer(),
          new KafkaProtobufDeserializer<>(Event1.parser()));

    consumer.subscribe(Collections.singleton(topic));


    while (true) {
      ConsumerRecords<String, Event1> records = consumer.poll(Duration.ofMillis(100));
      records.forEach(record -> {
        Event1 value = record.value();
        System.out.println(value.getContent() + ", " + value.getIsWhatever());
      });
      consumer.commitAsync();
    }
  }
}
