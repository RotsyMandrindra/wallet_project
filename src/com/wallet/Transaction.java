package com.wallet;

import java.time.LocalDateTime;

public class Transaction {
    private int transaction_id;
    private int account_id;
    private double amount;
    private LocalDateTime transaction_date;
    private String description;

    public Transaction(int transaction_id, int account_id, double amount, LocalDateTime transaction_date, String description) {
        this.transaction_id = transaction_id;
        this.account_id = account_id;
        this.amount = amount;
        this.transaction_date = transaction_date;
        this.description = description;
    }

    public int getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(int transaction_id) {
        this.transaction_id = transaction_id;
    }

    public int getAccount_id() {
        return account_id;
    }

    public void setAccount_id(int account_id) {
        this.account_id = account_id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getTransaction_date() {
        return transaction_date;
    }

    public void setTransaction_date(LocalDateTime transaction_date) {
        this.transaction_date = transaction_date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
