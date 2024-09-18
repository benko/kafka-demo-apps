package com.redhat.training.kafka.coreapi;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.eclipse.microprofile.config.ConfigProvider;

public class PaymentProducer {
    static final Logger LOG = Logger.getLogger(PaymentProducer.class.getName());

    public static void main(String[] args) {
        LOG.info("Sending payment transaction information...");

        Random random = new Random();
        String topic = ConfigProvider.getConfig().getOptionalValue("topic.payments", String.class).orElse("payments");

        Producer<String,Integer> pp = new KafkaProducer<>(ProducerSettings.configurePaymentProperties());
        while (true) {
            String k = GeneratedData.getRandomAccountId();
            Integer v = random.nextInt(100000);
            try {
                LOG.info("Sending payment: " + k + " -> " + v);
                ProducerRecord<String,Integer> pr = new ProducerRecord<String,Integer>(topic, k, v);
                pp.send(pr).get();
            } catch (ExecutionException | InterruptedException e) {
                LOG.warning(e.getMessage());
                continue;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ie) {
                LOG.warning(ie.getMessage());
                break;
            }
        }

        LOG.info("Done sending payment transactions. Closing producer.");

        pp.close();

        LOG.info("Finished. So long.");
    }
}
