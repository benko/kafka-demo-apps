package com.redhat.training.kafka.quarkus.streams;

import java.time.Duration;
import java.util.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.Suppressed;
import org.apache.kafka.streams.kstream.TimeWindows;

import com.redhat.training.kafka.model.AggregatePaymentData;
import com.redhat.training.kafka.model.BankAccount;
import com.redhat.training.kafka.model.EnrichedRiskAssessment;
import com.redhat.training.kafka.model.PaymentTransaction;
import com.redhat.training.kafka.model.RiskAssessment;

import io.quarkus.kafka.client.serialization.ObjectMapperSerde;

import static org.apache.kafka.streams.kstream.Suppressed.BufferConfig.unbounded;

@ApplicationScoped
public class PaymentsStream {
    private final Logger LOG = Logger.getLogger(PaymentsStream.class.getName());

    // Deserializer for message keys.
    private final Serde<String> stringSerde = Serdes.String();

    // Serializers for message values
    private final Serde<Integer> integerSerde = Serdes.Integer();
    private final Serde<Long> longSerde = Serdes.Long();
    private final ObjectMapperSerde<PaymentTransaction> ptSerde = new ObjectMapperSerde<>(PaymentTransaction.class);
    private final ObjectMapperSerde<BankAccount> baSerde = new ObjectMapperSerde<>(BankAccount.class);
    private final ObjectMapperSerde<RiskAssessment> raSerde = new ObjectMapperSerde<>(RiskAssessment.class);
    private final ObjectMapperSerde<EnrichedRiskAssessment> eraSerde = new ObjectMapperSerde<>(EnrichedRiskAssessment.class);
    private final ObjectMapperSerde<AggregatePaymentData> apdSerde = new ObjectMapperSerde<>(AggregatePaymentData.class);

    @Produces
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        // Input data: payments topic (use producer's default profile exec:java),
        //              bank account data (use producer -Pbank-account-data exec:java to initialize), and
        //              risk status updates (use producer -Prisk-assessment-updates exec:java to start updates)
        KStream<String,Integer> payments = builder.stream("transactions", Consumed.with(stringSerde, integerSerde));
        KTable<String,BankAccount> acctTable = builder.table("account-data",
                                                            Consumed.with(stringSerde, baSerde));
        GlobalKTable<String,RiskAssessment> riskStatusTable = builder.globalTable("customer-risk-status",
                                                             Consumed.with(stringSerde,raSerde));

        // Basically logging: emit the input of String,Integer in the form of String,PaymentTransaction on transaction-data topic.
        payments
            .map(new KeyValueMapper<String, Integer, KeyValue<String, PaymentTransaction>>() {
                public KeyValue<String, PaymentTransaction> apply(String key, Integer value) {
                    PaymentTransaction pt = new PaymentTransaction();
                    pt.setAccount(key);
                    pt.setAmount(value);
                    return new KeyValue<>(key, pt);
            }},
            Named.as("transform-int-to-payment-transaction"))
            .to("transaction-data", Produced.with(stringSerde, ptSerde));

        // The "main" part:
        //  - filter for large payments only,
        //  - create data container object;
        //  - join on account table and enrich data;
        //  - join on risk status table, enrich again;
        // Finally, apply risk assessment logic in a filter and send to "large-payments" if needed.
        //
        // TODO: There is potential for improvement here:
        //       - branch instead of filtering
        //       - mark low transactions as auto-approved and send them to transaction-data (branch 1)
        //       - mark high transactions with acceptable risk as auto-approved + risky, same destination (branch 2-1)
        //       - send high transactions with high risk to large-payments topic (branch 2-2)
        //       - receive transactions from approved-payments, mark them as manually approved and send them to transaction-data
        payments
            .filter((acctId, amt) -> amt > 10000)
            .mapValues((amt) -> {
                EnrichedRiskAssessment era = new EnrichedRiskAssessment();
                era.setTransactionAmount(amt.intValue());
                return era;
            })
            .join(acctTable,
                        (era, acct) -> {
                            era.setAccountId(acct.getAccountNumber());
                            era.setCustomerId(acct.getCustomerId());
                            return era;
                        })
            .join(riskStatusTable,
                (acctId, era) -> era.getCustomerId(),
                (era, risk) -> {
                    era.setRiskScore(risk.getAssessmentScore());
                    return era;
                }
            )
            .peek((k, v) -> LOG.info("Considering for further evaluation: " + v))
            .filter((acctId, era) -> {
                // for payments above 75k we need a low risk score
                if (era.getTransactionAmount() > 75000 && era.getRiskScore() > 2) {
                    return true;
                // above 50k we need medium-low score
                } else if (era.getTransactionAmount() > 50000 && era.getRiskScore() > 4) {
                    return true;
                // above 25k we allow up to medium-high score
                } else if (era.getTransactionAmount() > 25000 && era.getRiskScore() > 6) {
                    return true;
                // anything else allows a risk score up to 8
                } else if (era.getRiskScore() > 8) {
                    return true;
                }
                // any other payments are accepted
                return false;
            })
            .peek((k, v) -> LOG.info("Sending record " + v + " to large-payments topic."))
            .to("large-payments", Produced.with(stringSerde, eraSerde));

        // Produce aggregated payment data (how many aggregate transactions in the last 30 seconds of time window).
        // TODO: 
        payments
            .groupByKey()
            .windowedBy(TimeWindows.ofSizeAndGrace(Duration.ofSeconds(30), Duration.ofSeconds(5)))
            .aggregate(() -> 0L,
                         (acctId, amt, agg) -> agg + amt,
                         Materialized.with(stringSerde, longSerde))
            .suppress(Suppressed.untilWindowCloses(unbounded()))
            .toStream()
            .peek((acctId, seenAmt) -> LOG.info("Account " + acctId + " aggregated payments: " + seenAmt))
            .map((acctId, newAggregate) -> {
                AggregatePaymentData apd = new AggregatePaymentData();
                apd.setAccountId(acctId.key());
                apd.setAggregateSum(newAggregate);
                return new KeyValue<String, AggregatePaymentData>(acctId.key(), apd);
            })
            .to("aggregate-data",
                Produced.with(stringSerde, apdSerde));
        // TODO: There is potential for continuation of the story here:
        //       implement fraud detection logic for accounts that have seen above x amt transactions in the last window

        // Update each account with the latest balance available.
        payments.join(acctTable,
                        (amt, acct) -> {
                            acct.setBalance(acct.getBalance() + amt.longValue());
                            return acct;
                        })
            .peek((acctId, acct) -> LOG.info("Account " + acctId + " updated with new balance: " + acct.getBalance()))
            .to("account-data",
                        Produced.with(stringSerde, baSerde));

        Topology t = builder.build();

        LOG.info("Topology: " + t.describe().toString());

        return t;
    }
}
