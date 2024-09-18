package com.redhat.training.kafka.coreapi;

import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.eclipse.microprofile.config.ConfigProvider;

import com.redhat.training.kafka.model.BankAccount;

public class BankAccountProducer {
    private static final Logger LOG = Logger.getLogger(BankAccountProducer.class.getName());

    public static void main(String... args) {
        LOG.info("Initializing account data...");

        String topic = ConfigProvider.getConfig().getOptionalValue("topic.bank-accounts", String.class).orElse("account-data");

        Producer<String,BankAccount> adp = new KafkaProducer<>(ProducerSettings.configureAccountProperties());

        int ctr = 0;
        while (true) {
            BankAccount ba = GeneratedData.getBankAccount(ctr);
            if (ba == null) {
                break;
            } else {
                ctr++;
            }

            try {
                LOG.info("Sending account: " + ba.toString());
                ProducerRecord<String,BankAccount> ad = new ProducerRecord<String,BankAccount>(topic, ba.getAccountNumber(), ba);
                adp.send(ad).get();
            } catch (ExecutionException | InterruptedException e) {
                LOG.warning(e.getMessage());
                continue;
            }
        }

        LOG.info("Done sending account data. Closing producer.");

        adp.close();

        LOG.info("Finished. Good bye.");
    }
}
