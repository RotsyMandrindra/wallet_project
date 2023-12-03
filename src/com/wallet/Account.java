package com.wallet;

public class Account {
    private int account_id;
    private String customer_name;
    private double balance;
    private String password;
    private int currency_id;

    public Account(int account_id, String customer_name, double balance, String password, int currency_id) {
        this.account_id = account_id;
        this.customer_name = customer_name;
        this.balance = balance;
        this.password = password;
        this.currency_id = currency_id;
    }

    public int getAccount_id() {
        return account_id;
    }

    public void setAccount_id(int account_id) {
        this.account_id = account_id;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getCurrency_id() {
        return currency_id;
    }

    public void setCurrency_id(int currency_id) {
        this.currency_id = currency_id;
    }
}
