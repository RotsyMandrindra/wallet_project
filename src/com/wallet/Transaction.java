package com.wallet;

import java.sql.Timestamp;

public class Transaction {
    private int transactionId;
    private int accountId;
    private double amount;
    private Timestamp transaction_date;
    private String description;
    private String transaction_type;
    private String label;

    public Transaction(int transactionId, int accountId, double amount, Timestamp transaction_date, String description, String transaction_type, String label) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.amount = amount;
        this.transaction_date = transaction_date;
        this.description = description;
        this.transaction_type = transaction_type;
        this.label = label;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Timestamp getTransaction_date() {
        return transaction_date;
    }

    public void setTransaction_date(Timestamp transaction_date) {
        this.transaction_date = transaction_date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTransaction_type() {
        return transaction_type;
    }

    public void setTransaction_type(String transaction_type) {
        this.transaction_type = transaction_type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}

