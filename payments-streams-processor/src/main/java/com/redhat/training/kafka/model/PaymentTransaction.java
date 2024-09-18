package com.redhat.training.kafka.model;

public class PaymentTransaction {
    String account;
    int amount;
    public int getAmount() {
        return amount;
    }
    public void setAmount(int amount) {
        this.amount = amount;
    }
    public String getAccount() {
        return account;
    }
    public void setAccount(String account) {
        this.account = account;
    }
}
