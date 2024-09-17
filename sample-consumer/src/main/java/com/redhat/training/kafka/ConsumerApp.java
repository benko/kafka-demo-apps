package com.redhat.training.kafka;

import java.time.Duration;
import java.util.Properties;
import java.util.Collections;

import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;


public class ConsumerApp
{
    public static void main(String[] args) {
        Consumer<Integer, String> consumer = new KafkaConsumer<>(configureProperties());
        consumer.subscribe(Collections.singletonList("my-topic"));

        while (true) {
            ConsumerRecords<Integer, String> records = consumer.poll(Duration.ofMillis(Long.MAX_VALUE));

            for (ConsumerRecord<Integer, String> record : records) {
                System.out.println("Received quote (partition " + record.partition() + "): " + record.key() + " -> " + record.value());
            }
        }
    }

    private static Properties configureProperties() {
        Properties props = new Properties();

        // configuration properties
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
            "localhost:9092,localhost:9192");
            //"my-cluster-kafka-bootstrap-gcbtjd-kafka-cluster.apps.eu46a.prod.ole.redhat.com:443");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "famousQuotes");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.IntegerDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        // props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        // props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 500);
        // props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
        // props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, "/home/johndoe/AD482/truststore.jks");
        // props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, "password");

        return props;
    }
}
