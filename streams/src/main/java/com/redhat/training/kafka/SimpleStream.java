package com.redhat.training.kafka;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyDescription;
import org.apache.kafka.streams.kstream.Branched;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Produced;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

public class SimpleStream {

    public static Properties configureProperties() {
        Config cf = ConfigProvider.getConfig();

        Properties props = new Properties();

        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "simpleStreamProcessor");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, cf.getValue("kafka.server", String.class));
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
        props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, cf.getValue("ssl.truststore", String.class));
        props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, cf.getValue("ssl.password", String.class));

        return props;
    }

    public static void main(String[] args) {
        // Check properties first.
        Properties cfg = configureProperties();

        Serde<String> ks = Serdes.String();
        Serde<Integer> vs = Serdes.Integer();

        StreamsBuilder b = new StreamsBuilder();

        KStream<String, Integer> src = b.stream("payments", Consumed.with(ks, vs));

        Map<String, KStream<String, Integer>> splits = src.split(Named.as("stream-"))
                        .branch((k, v) -> true, Branched.as("orig"))
                        .branch((k, v) -> true, Branched.as("copy"))
                        .noDefaultBranch();

        System.out.println("Got the following streams:");
        for (String x : splits.keySet()) {
            System.out.println(" - " + x);
        }

        splits.get("stream-copy")
                .foreach((key, val) -> System.out.println("Received key: " + key + ", value: " + val));

        splits.get("stream-orig")
                .filter((key, value) -> value > 1000)
                .to("large-payments", Produced.with(ks, vs));

        Topology t = b.build();
        TopologyDescription td = t.describe();
        System.out.println("**** TOPOLOGY ****\n" + td.toString());

        KafkaStreams str = new KafkaStreams(t, cfg);
        final CountDownLatch cd = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown") {
            @Override
            public void run() {
                str.close();
                cd.countDown();
            }
        });

        try {
            str.start();
            cd.await();
        } catch (InterruptedException ie) {
            System.out.println("Interrupted during await()...");
        }
    }
}
