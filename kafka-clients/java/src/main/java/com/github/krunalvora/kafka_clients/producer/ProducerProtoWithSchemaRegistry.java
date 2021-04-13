package com.github.krunalvora.kafka_clients.producer;

import com.github.krunalvora.kafka_clients.protobuf.Event1OuterClass.Event1;
import com.github.krunalvora.kafka_clients.protobuf.Event2OuterClass.Event2;
import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer;
import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializerConfig;
import io.confluent.kafka.serializers.subject.TopicRecordNameStrategy;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class ProducerProtoWithSchemaRegistry {
    public static void produceSimpleMessage(String topic, Properties properties) {
        Event1 simpleMessage = Event1.newBuilder()
              .setContent("Hello world")
              .setIsWhatever(true)
              .build();
        ProducerRecord<String, Event1> record = new ProducerRecord<String, Event1>(topic, null, simpleMessage);
        System.out.println(simpleMessage);
        Producer<String, Event1> producer = new KafkaProducer<String, Event1>(properties);
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


    public static void produceSimpleMessage2(String topic, Properties properties) {
        Event2 simpleMessage2 = Event2.newBuilder()
              .setContent1("Hello world 2")
              .setContent2("Hello world again")
              .build();
        ProducerRecord<String, Event2> record2 = new ProducerRecord<String, Event2>(topic, null, simpleMessage2);
        System.out.println(simpleMessage2);
        Producer<String, Event2> producer = new KafkaProducer<String, Event2>(properties);
        producer.send(record2, new Callback() {
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

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaProtobufSerializer.class);
        properties.put(KafkaProtobufSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");
        // properties.put(KafkaProtobufSerializerConfig.AUTO_REGISTER_SCHEMAS, false);
        properties.put(KafkaProtobufSerializerConfig.VALUE_SUBJECT_NAME_STRATEGY, TopicRecordNameStrategy.class);

        String topic = "topic6";
        produceSimpleMessage(topic, properties);
        //produceSimpleMessage2(topic, properties);

    }
}