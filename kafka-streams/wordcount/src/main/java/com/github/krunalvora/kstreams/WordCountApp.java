package com.github.krunalvora.kstreams;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;

import java.util.Arrays;
import java.util.Properties;

public class WordCountApp {

    public static void main(String[] args) throws Exception {
        final String STREAMS_INPUT_TOPIC = System.getenv("STREAMS_INPUT_TOPIC");
        final String STREAMS_OUTPUT_TOPIC = System.getenv("STREAMS_OUTPUT_TOPIC");
        final String STREAMS_APPLICATION_ID = System.getenv("STREAMS_APPLICATION_ID");
        final String BOOTSTRAP_SERVERS = System.getenv("BOOTSTRAP_SERVERS");

        if (STREAMS_APPLICATION_ID == null || STREAMS_INPUT_TOPIC == null ||
                STREAMS_OUTPUT_TOPIC == null || BOOTSTRAP_SERVERS == null) {
            throw new Exception("Required STREAMS_APPLICATION_ID, STREAMS_INPUT_TOPIC, " +
                    "STREAMS_OUTPUT_TOPIC, BOOTSTRAP_SERVERS");
        }

        final Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, STREAMS_APPLICATION_ID);
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> source = builder.stream(STREAMS_INPUT_TOPIC);

        final KTable<String, Long> counts = source
                .flatMapValues(value -> Arrays.asList(value.toLowerCase().split(" ")))
                .groupBy((key, value) -> value)
                .count();

        counts.toStream().to(STREAMS_OUTPUT_TOPIC, Produced.with(Serdes.String(), Serdes.Long()));

        KafkaStreams streams = new KafkaStreams(builder.build(), config);
        streams.start();
    }

}
