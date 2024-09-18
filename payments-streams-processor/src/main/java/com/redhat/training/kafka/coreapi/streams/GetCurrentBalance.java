package com.redhat.training.kafka.coreapi.streams;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import com.redhat.training.kafka.model.BankAccount;

import io.quarkus.kafka.client.serialization.ObjectMapperSerde;

// Just dumps the latest contents of the account-data KTable.
public class GetCurrentBalance {
    public static Properties configureProperties() {
        Config cf = ConfigProvider.getConfig();

        Properties props = new Properties();

        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "currentBalanceDump");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, cf.getValue("kafka.server", String.class));
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, cf.getOptionalValue("kafka.protocol", String.class).orElse("PLAINTEXT"));
        if (props.get(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG).equals("SSL")) {
            props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, cf.getValue("ssl.truststore", String.class));
            props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, cf.getValue("ssl.password", String.class));
        }

        return props;
    }

    public static void main(String[] args) {
        Properties cfg = configureProperties();
        String srcTopic = ConfigProvider.getConfig().getOptionalValue("kafka.topic.accounts", String.class).orElse("account-data");

        Serde<String> ks = Serdes.String();
        Serde<BankAccount> vs = new ObjectMapperSerde<>(BankAccount.class);

        StreamsBuilder b = new StreamsBuilder();

        b.table(srcTopic, Consumed.with(ks, vs)).toStream().foreach((acctId, acct) -> {
            System.out.println(acct.getAccountNumber() + ": " + acct.getCustomerName() + "(" + acct.getCustomerId() + ")\t-> " + acct.getBalance());
        });
        Topology t = b.build();
        KafkaStreams str = new KafkaStreams(t, cfg);

        final CountDownLatch cd = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown") {
            @Override
            public void run() {
                str.close();
                cd.countDown();
            }
        });

        // start the application
        try {
            System.out.println("Dumping current account balances...");
            str.start();
            cd.await();
        } catch (InterruptedException ie) {
            System.out.println("Interrupted during await()...");
        }
    }
}
