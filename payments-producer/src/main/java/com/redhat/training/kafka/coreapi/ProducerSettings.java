package com.redhat.training.kafka.coreapi;

import java.util.Properties;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

public class ProducerSettings {
    private static final Config cf = ConfigProvider.getConfig();

    public static Properties configureGenericProperties() {
        Properties props = new Properties();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                    cf.getValue("bootstrap.server", String.class));

        props.put(ProducerConfig.ACKS_CONFIG,
                    cf.getOptionalValue("producer.acks", String.class).orElse("all"));
        props.put(ProducerConfig.LINGER_MS_CONFIG,
                    cf.getOptionalValue("producer.linger", Integer.class).orElse(0));
        props.put(ProducerConfig.BATCH_SIZE_CONFIG,
                    cf.getOptionalValue("producer.batch", Integer.class).orElse(16384));
        return props;
    }
    public static Properties configureAccountProperties() {
        Properties props = configureGenericProperties();
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                org.apache.kafka.common.serialization.StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                com.redhat.training.kafka.serdes.BankAccountSerializer.class.getName());
        return props;
    }
    public static Properties configureRiskProperties() {
        Properties props = configureGenericProperties();
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                org.apache.kafka.common.serialization.StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                com.redhat.training.kafka.serdes.RiskAssessmentSerializer.class.getName());
        return props;
    }
    public static Properties configurePaymentProperties() {
        Properties props = configureGenericProperties();
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                org.apache.kafka.common.serialization.StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                org.apache.kafka.common.serialization.IntegerSerializer.class.getName());
        return props;
    }
}
