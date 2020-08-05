package com.github.krunalvora.kafka_clients.consumer;

import com.github.krunalvora.kafka_clients.protobuf.SimpleMessageOuterClass.SimpleMessage;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.Descriptors.FieldDescriptor;
import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializer;
import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializerConfig;
import org.apache.kafka.clients.consumer.*;
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

        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaProtobufDeserializer.class);
        properties.put(KafkaProtobufDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");

        String topic = "simplemessage-proto";

        KafkaConsumer<String, DynamicMessage> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Collections.singleton(topic));


        while (true) {
            ConsumerRecords<String, DynamicMessage> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, DynamicMessage> record : records) {
                for (FieldDescriptor field : record.value().getAllFields().keySet()) {
                    System.out.println(field.getName() + ": " + record.value().getField(field));
                }
            }
            consumer.commitAsync();
        }
    }
}
