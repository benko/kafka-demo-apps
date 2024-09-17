package com.redhat.training.kafka;

import java.util.Properties;
import java.util.Random;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.config.SslConfigs;

public class ProducerApp {
    public static Properties configureProperties() {
        Properties props = new Properties();

        // the bootstrap server(s)
        props.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "localhost:9092,localhost:9192"
                // "my-cluster-kafka-bootstrap-gcbtjd-kafka-cluster.apps.eu46a.prod.ole.redhat.com:443"
        );

        // key and value serializers
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                    org.apache.kafka.common.serialization.VoidSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                    com.redhat.training.kafka.WeatherSerializer.class.getName());

        // configure the SSL connection (if necessary)
        // props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
        // props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG,
        //              "/home/johndoe/AD482/truststore.jks");
        // props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, "password");

        // acknowledgment level
        // props.put(ProducerConfig.ACKS_CONFIG, "1"); // ask leader to ack

        // use a custom partitioner
        // props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG,
        //             org.apache.kafka.clients.producer.RoundRobinPartitioner.class.getName());

        return props;
    }

    public static void main(String[] args) {
        Producer<Void, Weather> producer = new KafkaProducer<>(configureProperties());
        Random wgen = new Random();

        while (true) {
            Weather w = new Weather();
            w.setWeather(WeatherType.values()[wgen.nextInt(WeatherType.values().length)]);
            ProducerRecord<Void, Weather> r = new ProducerRecord<Void,Weather>("weather-forecast", w);
            producer.send(r,
                (rm, e) -> {
                    if (e != null) {
                        throw new RuntimeException(e);
                    } else {
                        printRecord(r, rm);
                    }
                });
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ie) {
                throw new RuntimeException(ie);
            }
        }
        // it's an endless loop, so this is unreachable code
        // producer.close();
    }

    private static void printRecord(ProducerRecord<Void,Weather> record, RecordMetadata meta) {
        System.out.println("Sent record:");
        System.out.println("\tKey = " + record.key());
        System.out.println("\tValue = " + record.value());
        System.out.println("\tTopic = " + meta.topic());
        System.out.println("\tPartition = " + meta.partition());
    }
}
