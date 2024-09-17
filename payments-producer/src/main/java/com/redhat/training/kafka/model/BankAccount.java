package com.redhat.training.kafka.model;

public class BankAccount {
    String accountNumber;
    String customerId;
    String customerName;
    long balance;
    public BankAccount(String acct, String cid, String cname, long balance) {
        this.setAccountNumber(acct);
        this.setCustomerId(cid);
        this.setCustomerName(cname);
        this.setBalance(balance);
    }
    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public String getAccountNumber() {
        return accountNumber;
    }
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    public long getBalance() {
        return balance;
    }
    public void setBalance(long balance) {
        this.balance = balance;
    }
    public String getCustomerId() {
        return customerId;
    }
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    
}
