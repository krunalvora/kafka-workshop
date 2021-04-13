package com.github.krunalvora.kafka_clients.consumer;

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;
import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializer;
import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializerConfig;
import io.confluent.kafka.serializers.subject.TopicRecordNameStrategy;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class ConsumerProtoWithSchemaRegistry {

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "generic-protobuf-consumer-group");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaProtobufDeserializer.class);
        properties.put(KafkaProtobufDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");
        // properties.put(KafkaProtobufDeserializerConfig.SPECIFIC_PROTOBUF_VALUE_TYPE, Event1.class.getName());

        properties.put(KafkaProtobufDeserializerConfig.VALUE_SUBJECT_NAME_STRATEGY, TopicRecordNameStrategy.class);
        properties.put(KafkaProtobufDeserializerConfig.DERIVE_TYPE_CONFIG, true);

        String topic = "topic6";

        KafkaConsumer<String, Message> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Collections.singleton(topic));


        while (true) {
            ConsumerRecords<String, Message> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, Message> record : records) {
                for (FieldDescriptor field : record.value().getAllFields().keySet()) {
                    System.out.println(field.getName() + ": " + record.value().getField(field));
                }
            }
            consumer.commitAsync();
        }
    }
}
