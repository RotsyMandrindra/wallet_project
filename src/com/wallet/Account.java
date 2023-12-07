package com.wallet;

import java.sql.Timestamp;

public class Account {
     private int accountId;
    private String accountName;
    private double balance;
    private Timestamp lastUpdateDate;
    private String password;
    private String accountType;
    private int currencyId;

    public Account(int accountId, String accountName, double balance, Timestamp lastUpdateDate, String password, String accountType, int currencyId) {
        this.accountId = accountId;
        this.accountName = accountName;
        this.balance = balance;
        this.lastUpdateDate = lastUpdateDate;
        this.password = password;
        this.accountType = accountType;
        this.currencyId = currencyId;
    }

    public Account() {

    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Timestamp getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Timestamp lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public int getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(int currencyId) {
        this.currencyId = currencyId;
    }
}
