package com.redhat.training.kafka.coreapi;

import java.util.Properties;
import java.util.Random;
import java.util.logging.Logger;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.redhat.training.kafka.model.BankAccount;
import com.redhat.training.kafka.model.RiskAssessment;

public class ProducerApp {
    static final Logger LOG = Logger.getLogger(ProducerApp.class.getName());

    private static final String[] accounts = {
        "4f1fabc1-2dfc-475d-ad59-dbe9be76f381",
        "c2119898-eae8-45a8-b24a-83e964c3440f",
        "a29112f1-ffc8-486d-b8aa-07f14daa4ea1",
        "961eb104-ef35-46f6-9fa5-9493513157ca",
        "70998997-6acf-43f5-98c7-41315975c5cc",
        "96686115-6ca7-4739-9198-5dd52084f563",
        "cc151e37-694c-46ef-a5e5-3ece1939485c",
        "8ae565a0-0d76-464b-8f32-be4a116c0d4c",
        "ea4e728a-a33c-4fcc-a43b-aba37b58f598",
        "8e81d57f-eb56-4a39-80c3-89b0019ea316",
    };

    private static final String[] users = {
        "jdoe",
        "janed",
        "tjones",
        "ljohnson",
        "mikep",
        "catbat",
        "qmd",
        "py",
        "aletter",
        "abug",
    };

    public static Properties configureGenericProperties() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "localhost:9092,localhost:9192,localhost:9292");
        props.put(ProducerConfig.ACKS_CONFIG, "1");
        return props;
    }
    public static Properties configureAccountProperties() {
        Properties props = configureGenericProperties();
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                org.apache.kafka.common.serialization.StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                com.redhat.training.kafka.serde.BankAccountSerializer.class.getName());
        return props;
    }
    public static Properties configureRiskProperties() {
        Properties props = configureGenericProperties();
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                org.apache.kafka.common.serialization.StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                com.redhat.training.kafka.serde.RiskAssessmentSerializer.class.getName());
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

    public static void main(String[] args) throws Exception {
        Random random = new Random();

        if (System.getProperty("initialize.accounts") != null &&
                System.getProperty("initialize.accounts").equals("true")) {
            LOG.info("Initializing account data...");
            Producer<String,BankAccount> adp = new KafkaProducer<>(configureAccountProperties());
            ProducerRecord<String,BankAccount> ad =
                new ProducerRecord<String,BankAccount>("account-data",
                    "4f1fabc1-2dfc-475d-ad59-dbe9be76f381",
                    new BankAccount("4f1fabc1-2dfc-475d-ad59-dbe9be76f381",
                                    "jdoe",
                                    "John Doe",
                                    0));
            adp.send(ad).get();

            ad = new ProducerRecord<String,BankAccount>("account-data",
                    "c2119898-eae8-45a8-b24a-83e964c3440f",
                    new BankAccount("c2119898-eae8-45a8-b24a-83e964c3440f",
                                    "janed",
                                    "Jane Doe",
                                    0));
            adp.send(ad).get();

            ad = new ProducerRecord<String,BankAccount>("account-data",
                    "a29112f1-ffc8-486d-b8aa-07f14daa4ea1",
                    new BankAccount("a29112f1-ffc8-486d-b8aa-07f14daa4ea1",
                                    "tjones",
                                    "Tom Jones",
                                    0));
            adp.send(ad).get();

            ad = new ProducerRecord<String,BankAccount>("account-data",
                    "961eb104-ef35-46f6-9fa5-9493513157ca",
                    new BankAccount("961eb104-ef35-46f6-9fa5-9493513157ca",
                                    "ljohnson",
                                    "Linda Johnson",
                                    0));
            adp.send(ad).get();

            ad = new ProducerRecord<String,BankAccount>("account-data",
                    "70998997-6acf-43f5-98c7-41315975c5cc",
                    new BankAccount("70998997-6acf-43f5-98c7-41315975c5cc",
                                    "mikep",
                                    "Mike Pearson",
                                    0));
            adp.send(ad).get();

            ad = new ProducerRecord<String,BankAccount>("account-data",
                    "96686115-6ca7-4739-9198-5dd52084f563",
                    new BankAccount("96686115-6ca7-4739-9198-5dd52084f563",
                                    "catbat",
                                    "Cathy Bates",
                                    0));
            adp.send(ad).get();

            ad = new ProducerRecord<String,BankAccount>("account-data",
                    "cc151e37-694c-46ef-a5e5-3ece1939485c",
                    new BankAccount("cc151e37-694c-46ef-a5e5-3ece1939485c",
                                    "qmd",
                                    "Quasi Modo",
                                    0));
            adp.send(ad).get();

            ad = new ProducerRecord<String,BankAccount>("account-data",
                    "8ae565a0-0d76-464b-8f32-be4a116c0d4c",
                    new BankAccount("8ae565a0-0d76-464b-8f32-be4a116c0d4c",
                                    "py",
                                    "老百姓",
                                    0));
            adp.send(ad).get();

            ad = new ProducerRecord<String,BankAccount>("account-data",
                    "ea4e728a-a33c-4fcc-a43b-aba37b58f598",
                    new BankAccount("ea4e728a-a33c-4fcc-a43b-aba37b58f598",
                                    "aletter",
                                    "Anita Letterback",
                                    0));
            adp.send(ad).get();

            ad = new ProducerRecord<String,BankAccount>("account-data",
                    "8e81d57f-eb56-4a39-80c3-89b0019ea316",
                    new BankAccount("8e81d57f-eb56-4a39-80c3-89b0019ea316",
                                    "abug",
                                    "Aida Bugg",
                                    0));
            adp.send(ad).get();
            adp.close();
        }

        Producer<String,Integer> pp = new KafkaProducer<>(configurePaymentProperties());
        Producer<String,RiskAssessment> rap = new KafkaProducer<>(configureRiskProperties());
        while (true) {
            // send a random payment event to a random account
            String k = accounts[random.nextInt(accounts.length)];
            Integer v = random.nextInt(100000);
            ProducerRecord<String,Integer> pr = new ProducerRecord<String,Integer>("payments", k, v);
            pp.send(pr).get();
            LOG.info("Sent record " + k + " -> " + v);

            // sometimes, also send a customer risk assessment update
            if (random.nextBoolean()) {
                RiskAssessment ra = new RiskAssessment();
                ra.setCustomerId(users[random.nextInt(users.length)]);
                ra.setAssessmentScore(random.nextInt(10) + 1);
                LOG.info("Updating customer risk status for " + ra.getCustomerId() + " to " + ra.getAssessmentScore());
                ProducerRecord<String,RiskAssessment> rapr =
                    new ProducerRecord<String,RiskAssessment>("customer-risk-status", ra.getCustomerId(), ra);
                rap.send(rapr);
            }

            Thread.sleep(1000);
        }
    }
}
