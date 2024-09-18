package com.redhat.training.kafka.coreapi;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.eclipse.microprofile.config.ConfigProvider;

import com.redhat.training.kafka.model.RiskAssessment;

public class RiskAssessmentProducer {
    private static final Logger LOG = Logger.getLogger(RiskAssessmentProducer.class.getName());

    public static void main(String... args) {
        LOG.info("Updating risk assessment data...");

        Random rand = new Random();
        String topic = ConfigProvider.getConfig().getOptionalValue("topic.risk-assessments", String.class).orElse("customer-risk-status");

        Producer<String,RiskAssessment> rap = new KafkaProducer<>(ProducerSettings.configureRiskProperties());

        // reset everyone's risk score to zero at start
        LOG.info("Resetting risk status for all customers to 0...");
        int cid = 0;
        while (true) {
            String customerId = GeneratedData.getCustomerId(cid);
            cid++;

            if (customerId == null) {
                break;
            }
            RiskAssessment ra = new RiskAssessment();
            ra.setCustomerId(customerId);
            ra.setAssessmentScore(0);
            LOG.info(" - " + customerId);
            try {
                ProducerRecord<String,RiskAssessment> rapr =
                        new ProducerRecord<String,RiskAssessment>(topic, ra.getCustomerId(), ra);
                rap.send(rapr).get();
            } catch (ExecutionException | InterruptedException e) {
                LOG.warning(e.getMessage());
                continue;
            }
        }

        while (true) {
            RiskAssessment ra = new RiskAssessment();

            ra.setCustomerId(GeneratedData.getRandomCustomerId());
            ra.setAssessmentScore(rand.nextInt(10) + 1);

            try {
                LOG.info("Updating customer risk status for " + ra.getCustomerId() + " to " + ra.getAssessmentScore());
                ProducerRecord<String,RiskAssessment> rapr =
                    new ProducerRecord<String,RiskAssessment>(topic, ra.getCustomerId(), ra);
                rap.send(rapr).get();
            } catch (ExecutionException | InterruptedException e) {
                LOG.warning(e.getMessage());
                continue;
            }

            try {
                Thread.sleep(rand.nextInt(5000));
            } catch (InterruptedException ie) {
                LOG.warning(ie.getMessage());
                break;
            }
        }

        LOG.info("Done sending risk assessments. Closing producer.");

        rap.close();

        LOG.info("Finished. Exiting.");
    }
}
