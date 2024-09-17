package com.redhat.training.kafka;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.config.SslConfigs;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

public class LargePaymentConsumer {
    public static Properties configureProperties() {
        Config cf = ConfigProvider.getConfig();

        Properties props = new Properties();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, cf.getValue("kafka.server", String.class));
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "largePaymentConsumer");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.IntegerDeserializer");
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, cf.getOptionalValue("kafka.protocol", String.class).orElse("PLAINTEXT"));
        if (props.get(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG).equals("SSL")) {
            props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, cf.getValue("ssl.truststore", String.class));
            props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, cf.getValue("ssl.password", String.class));
        }
        return props;
    }

    public static void main(String... args) {
        Consumer<String, Integer> consumer = new KafkaConsumer<>(configureProperties());

        String topic = ConfigProvider.getConfig().getOptionalValue("kafka.topic.largepayments", String.class).orElse("large-payments");
        consumer.subscribe(Collections.singleton(topic));

        System.out.println("Consuming large payment data from \"" + topic + "\"...");
        while (true) {
            ConsumerRecords<String, Integer> crs = consumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord<String, Integer> cr : crs) {
                System.out.println("Got large payment: " + cr.key() + " -> " + cr.value());
            }
        }
    }
}
