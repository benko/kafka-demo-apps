package com.redhat.training.kafka.quarkus;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import io.smallrye.common.annotation.Identifier;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class KafkaClientConfigsProducer {
    private static final Logger LOG = Logger.getLogger(KafkaClientConfigsProducer.class);

    @ConfigProperty(name = "consumer.client.id")
    Optional<String> clientId;

    @Produces
    @Identifier("quarkus-consumer-configs")
    public Map<String, Object> produceConsumerConfig() {
        LOG.info("Producing consumer default settings...");

        HashMap<String,Object> config = new HashMap<>();

        if (this.clientId.isPresent()) {
            LOG.warnv("Setting client ID from consumer.client.id: {0}", this.clientId.get());
            config.put(CommonClientConfigs.CLIENT_ID_CONFIG, this.clientId.get());
            config.put(ConsumerConfig.GROUP_INSTANCE_ID_CONFIG, this.clientId.get());
        } else {
            LOG.warn("Client ID not provided by consumer.client.id, not assigning.");
        }
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        config.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 1000);
        config.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, org.apache.kafka.clients.consumer.CooperativeStickyAssignor.class.getName());

        return config;
    }
}
