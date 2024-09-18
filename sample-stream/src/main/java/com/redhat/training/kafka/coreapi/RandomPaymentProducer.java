package com.redhat.training.kafka.coreapi;

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
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

public class RandomPaymentProducer {
    public static Properties configureProperties() {
        Config cf = ConfigProvider.getConfig();

        Properties props = new Properties();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, cf.getValue("kafka.server", String.class));
        props.put(ProducerConfig.ACKS_CONFIG, "1");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.IntegerSerializer");
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, cf.getOptionalValue("kafka.protocol", String.class).orElse("PLAINTEXT"));
        if (props.get(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG).equals("SSL")) {
            props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, cf.getValue("ssl.truststore", String.class));
            props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, cf.getValue("ssl.password", String.class));
        }
        return props;
    }

    public static void main(String... args) {
        Random random = new Random();
        Producer<String, Integer> producer = new KafkaProducer<>(configureProperties());

        String topic = ConfigProvider.getConfig().getOptionalValue("kafka.topic.payments", String.class).orElse("payments");

        System.out.println("Sending random payment data to \"" + topic + "\"...");
        while (true) {
	        int sum = random.nextInt(5000);
            ProducerRecord<String, Integer> record = new ProducerRecord<>(
                    topic,
                    sum
                );

            producer.send(record, (rm, e) -> {
                        if (e != null) {
                                System.out.println(e.getStackTrace());
                        } else {
				            System.out.println("Sent new payment: " + record.value());
                        }
                    });

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ie) {
                break;
            }
        }
        producer.close();
    }
}
