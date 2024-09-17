package com.redhat.training.kafka.coreapi;

import java.time.Duration;
import java.util.Properties;
import java.util.Collections;

import org.apache.kafka.common.config.SslConfigs;

import com.redhat.training.kafka.model.Weather;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

public class ConsumerApp
{
    public static void main(String[] args) {
        Consumer<Void, Weather> consumer = new KafkaConsumer<>(configureProperties());
        consumer.subscribe(Collections.singletonList("weather-forecast"));

        while (true) {
            ConsumerRecords<Void, Weather> records = consumer.poll(Duration.ofMillis(Long.MAX_VALUE));

            for (ConsumerRecord<Void, Weather> record : records) {
                System.out.println("Current weather forecast from partition " + record.partition() + "): " + record.value().toString());
            }

            // consumer.commitSync();
            // consumer.commitAsync();
        }
    }

    private static Properties configureProperties() {
        Properties props = new Properties();

        // configuration properties
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
            "localhost:9092,localhost:9192");
            //"my-cluster-kafka-bootstrap-gcbtjd-kafka-cluster.apps.eu46a.prod.ole.redhat.com:443");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "weatherApp");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                     org.apache.kafka.common.serialization.VoidDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                        com.redhat.training.kafka.model.WeatherDeserializer.class.getName());
        // props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        // props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 500);

        // custom partition assignment strategy
        // props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,
        //                         org.apache.kafka.clients.consumer.RangeAssignor.class.getName());
        // props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,
        //                         org.apache.kafka.clients.consumer.RoundRobinAssignor.class.getName());
        // props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,
        //                         org.apache.kafka.clients.consumer.StickyAssignor.class.getName());
        // props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,
        //                         org.apache.kafka.clients.consumer.CooperativeStickyAssignor.class.getName());

        // TLS properties, if necessary
        // props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
        // props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, "/home/johndoe/AD482/truststore.jks");
        // props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, "password");

        return props;
    }
}
