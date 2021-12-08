package com.github.krunalvora.kafka_clients.kafka_java_proto_client.protobuf;

import com.github.daniel.shuy.kafka.protobuf.serde.KafkaProtobufSerializer;
import com.github.krunalvora.kafka_clients.kafka_java_proto_client.Event1OuterClass.Event1;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class ProtoProducer {
  public static void main(String[] args) {
    Properties properties = new Properties();
    properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaProtobufSerializer.class);

    String topic = "topic4";

    Event1 event1Message = Event1.newBuilder()
            .setContent("Hello world")
            .setIsWhatever(true)
            .build();

    ProducerRecord<String, Event1> record = new ProducerRecord<String, Event1>(topic, null, event1Message);

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
}
