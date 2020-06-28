package com.github.krunalvora.kafka_clients.consumer;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class ConsumerKerberos {
    public static void main(String[] args) {

        Logger logger = LoggerFactory.getLogger(ConsumerKerberos.class.getName());

        String bootstrap_servers = "127.0.0.1:9094";
        String security_protocol = "SASL_PLAINTEXT";
        String sasl_kerberos_service_name = "kafka";
        String auto_offset_reset_config = "earliest";
        String groupId = "my_app";
        String topic = "topic1";
        String jaasFile = "/Users/krunalvora/personal/kafka-workshop/kafka-security/kerberos/reader.kafka_client_jaas.conf";

        // JaasConfig options
        String useKeyTab = "true";
        String storeKey = "true";
        String principal = "reader@KAFKA.SECURE";
        String keyTab = "/tmp/reader.user.keytab";

        String jaasTemplate = "com.sun.security.auth.module.Krb5LoginModule required useKeyTab=\"%s\" storeKey=\"%s\" principal=\"%s\" keyTab=\"%s\";";
        String jaasConfig = String.format(jaasTemplate, useKeyTab, storeKey, principal, keyTab);

        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap_servers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, auto_offset_reset_config);
        properties.setProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, security_protocol);
        properties.setProperty(SaslConfigs.SASL_KERBEROS_SERVICE_NAME, sasl_kerberos_service_name);
        properties.setProperty(SaslConfigs.SASL_JAAS_CONFIG, jaasConfig);

        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);
        consumer.subscribe(Arrays.asList(topic));

        while(true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

            for (ConsumerRecord<String, String> record : records) {
                logger.info("Key: " + record.key() + "Value: " + record.value());
            }
        }
    }
}
