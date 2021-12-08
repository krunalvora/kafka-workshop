package com.github.krunalvora.kafka_clients.producer;

import com.github.krunalvora.kafka_clients.avro.Customer;
import com.github.krunalvora.kafka_clients.avro.Student;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class ProducerAvro {

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "localhost:9092,localhost:9093,localhost:9094");
        properties.setProperty("acks", "all");
        properties.setProperty("retries", "10");
        properties.setProperty("key.serializer", StringSerializer.class.getName());
        properties.setProperty("value.serializer", KafkaAvroSerializer.class.getName());
        properties.setProperty("schema.registry.url", "http://localhost:8081");

        Producer<String, Student> producer = new KafkaProducer<String, Student>(properties);

        String topic = "student-avro";

        Student student = Student.newBuilder()
                .setAge(33)
                .setFirstName("John")
                .setLastName("Doe")
                .build();

        ProducerRecord<String, Student> producerRecord = new ProducerRecord<String, Student>(
                topic, student
        );

        System.out.println(student);
        producer.send(producerRecord, new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                if (exception == null) {
                    System.out.println(metadata);
                } else {
                    exception.printStackTrace();
                }
            }
        });

        producer.flush();
        producer.close();

    }
}