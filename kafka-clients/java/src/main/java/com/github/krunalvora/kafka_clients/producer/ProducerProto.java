package com.github.krunalvora.kafka_clients.producer;

import com.github.krunalvora.kafka_clients.protobuf.SimpleMessageOuterClass.SimpleMessage;
import com.github.krunalvora.kafka_clients.protobuf.SimpleOuterClass;
import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer;
import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializerConfig;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Instant;
import java.util.Properties;

public class ProducerProto {

    public static void main(String[] args) {

        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaProtobufSerializer.class);
        properties.put(KafkaProtobufSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");

        Producer<String, SimpleMessage> producer = new KafkaProducer<String, SimpleMessage>(properties);

        String topic = "simplemessage-proto";

        SimpleMessage simpleMessage = SimpleMessage.newBuilder()
                .setContent("Hello world")
                .setDateTime(Instant.now().toString())
                .build();

        ProducerRecord<String, SimpleMessage> record = new ProducerRecord<String, SimpleMessage>(topic, null, simpleMessage);

        System.out.println(simpleMessage);
        producer.send(record, new Callback() {
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