package com.redhat.training.kafka.model;

public class AggregatePaymentData {
    String accountId;
    long aggregateSum;
    public String getAccountId() {
        return accountId;
    }
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
    public long getAggregateSum() {
        return aggregateSum;
    }
    public void setAggregateSum(long aggregateSum) {
        this.aggregateSum = aggregateSum;
    }
    public String toString() {
        return String.format("{\"accountId\": \"%s\", \"aggregateSum\": %d}",
                this.accountId, this.aggregateSum);
    }
}
