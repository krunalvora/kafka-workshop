package com.github.krunalvora.kafka_clients.consumer;

import com.github.krunalvora.kafka_clients.avro.Student;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class ConsumerAvro {

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "localhost:9092,localhost:9093,localhost:9094");
        properties.setProperty("group.id", "customer-consumer-group-v1");
        properties.setProperty("auto.commit.enable", "false");
        properties.setProperty("auto.offset.reset", "earliest");
        properties.setProperty("key.deserializer", StringDeserializer.class.getName());
        properties.setProperty("value.deserializer", KafkaAvroDeserializer.class.getName());
        properties.setProperty("schema.registry.url", "http://localhost:8081");
        properties.setProperty("specific.avro.reader", "true");

        KafkaConsumer<String, Student> kafkaConsumer = new KafkaConsumer<>(properties);
        String topic = "student-avro";
        kafkaConsumer.subscribe(Collections.singleton(topic));

        System.out.println("Waiting for data...");

        while (true){
            System.out.println("Polling...");
            ConsumerRecords<String, Student> records = kafkaConsumer.poll(Duration.ofMillis(5000));

            for (ConsumerRecord<String, Student> record : records){
                Student customer = record.value();
                System.out.println(customer);
            }

            kafkaConsumer.commitSync();
        }
    }
}
