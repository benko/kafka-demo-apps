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
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, cf.getOptionalValue("kafka.protocol", String.class).orElse("PLAINTEXT"));
        if (props.get(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG).equals("SSL")) {
            props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, cf.getValue("ssl.truststore", String.class));
            props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, cf.getValue("ssl.password", String.class));
        }

        return props;
    }

    public static void main(String[] args) {
        // Check properties first.
        Properties cfg = configureProperties();
        String srcTopic = ConfigProvider.getConfig().getOptionalValue("kafka.topic.payments", String.class).orElse("payments");
        String dstTopic = ConfigProvider.getConfig().getOptionalValue("kafka.topic.largepayments", String.class).orElse("large-payments");

        Serde<String> ks = Serdes.String();
        Serde<Integer> vs = Serdes.Integer();

        StreamsBuilder b = new StreamsBuilder();

        // returns a kstream that you can append processors to
        KStream<String, Integer> src = b.stream(srcTopic, Consumed.with(ks, vs));

        // logs everything and sends records above $foo for further processing using a filter
        src.foreach((key, val) -> System.out.println("Received key: " + key + ", value: " + val));

        // either filter for interesting things and drop everything else...
        // src.filter((key, value) -> value > 2500)
        //         .to(dstTopic, Produced.with(ks, vs));

        // or use the split processor (2.8.0+) to create substreams per-criteria and attach processors to them
        Map<String, KStream<String, Integer>> splits = src.split(Named.as("stream-"))
                        .branch((k, v) -> v <= 2500, Branched.as("log"))
                        .defaultBranch(Branched.as("proc"));

        System.out.println("Got the following streams:");
        for (String x : splits.keySet()) {
            System.out.println(" - " + x);
        }

        splits.get("stream-log").foreach((key, val) -> System.out.println("Received LOW PAYMENT key: " + key + ", value: " + val));

        splits.get("stream-proc").foreach((key, val) -> System.out.println("Received HIGH PAYMENT key: " + key + ", value: " + val));
        splits.get("stream-proc").to(dstTopic, Produced.with(ks, vs));

        // this builds the topology for streams
        Topology t = b.build();

        // print the topology on stdout
        TopologyDescription td = t.describe();
        System.out.println("**** TOPOLOGY ****\n" + td.toString());

        // manually create a streams object
        KafkaStreams str = new KafkaStreams(t, cfg);

        // shutdown handler
        final CountDownLatch cd = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown") {
            @Override
            public void run() {
                str.close();
                cd.countDown();
            }
        });

        // start the application
        try {
            System.out.println("Starting payment stream processor...");
            str.start();
            cd.await();
        } catch (InterruptedException ie) {
            System.out.println("Interrupted during await()...");
        }
    }
}
