package com.github.krunalvora.kafka_clients.producer;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerKerberos {

    public static void main(String[] args) {

        Logger logger = LoggerFactory.getLogger(ProducerKerberos.class.getName());

        String bootstrap_servers = "127.0.0.1:9094";
        String security_protocol = "SASL_PLAINTEXT";
        String sasl_kerberos_service_name = "kafka";
        String topic = "topic1";

        // JaasConfig options
        String useKeyTab = "true";
        String storeKey = "true";
        String principal = "writer@KAFKA.SECURE";
        String keyTab = "/tmp/writer.user.keytab";

        String jaasTemplate = "com.sun.security.auth.module.Krb5LoginModule required useKeyTab=\"%s\" storeKey=\"%s\" principal=\"%s\" keyTab=\"%s\";";
        String jaasConfig = String.format(jaasTemplate, useKeyTab, storeKey, principal, keyTab);

        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap_servers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, security_protocol);
        properties.setProperty(SaslConfigs.SASL_KERBEROS_SERVICE_NAME, sasl_kerberos_service_name);
        properties.setProperty(SaslConfigs.SASL_JAAS_CONFIG, jaasConfig);

        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);
        for (int i=0; i<25; i++) {
            ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic, "hello" + i);
            logger.info("Topic: " + record.topic() + " Value: " + record.value());
            producer.send(record);
        }
        logger.info("Sent the messages. Closing producer...");
        producer.close();
    }
}
