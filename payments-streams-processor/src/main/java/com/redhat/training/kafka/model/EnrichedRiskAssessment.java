package com.redhat.training.kafka.model;

public class EnrichedRiskAssessment {
    String accountId;
    String customerId;
    int transactionAmount;
    int riskScore;
    public String getAccountId() {
        return accountId;
    }
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
    public String getCustomerId() {
        return customerId;
    }
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    public int getTransactionAmount() {
        return transactionAmount;
    }
    public void setTransactionAmount(int transactionAmount) {
        this.transactionAmount = transactionAmount;
    }
    public int getRiskScore() {
        return riskScore;
    }
    public void setRiskScore(int riskScore) {
        this.riskScore = riskScore;
    }
    public String toString() {
        return String.format("{\"accountId\": \"%s\", \"customerId\": \"%s\", \"amount\": %d, \"riskScore\": %d}",
                this.accountId, this.customerId, this.transactionAmount, this.riskScore);
    }
}
