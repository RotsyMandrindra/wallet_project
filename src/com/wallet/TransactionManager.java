package com.wallet;

import javax.print.DocFlavor;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.*;
import java.util.Properties;


public class TransactionManager {
    public static void main(String[] args) {
        try {
            Properties properties = loadProperties();

            String jdbcUrl = properties.getProperty("jdbcUrl");
            String dbUser = properties.getProperty("dbUser");
            String dbPassword = properties.getProperty("dbPassword");

            try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword)) {

            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private static Properties loadProperties() throws IOException {
        Properties properties = new Properties();
        try (InputStream input = TransactionManager.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return properties;
            }
            properties.load(input);
        }
        return properties;
    }
    public static void performTransaction(int accountId, double amount, String transactionType, String description, String label) throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String accountType = getAccountType(connection, accountId);

            if (("Banque".equals(accountType) || "Mobile Money".equals(accountType)) || "credit".equals(transactionType)) {
                updateAccountBalance(connection, accountId, amount);
            } else if ("Espèce".equals(accountType) && "debit".equals(transactionType)) {
                if (isBalanceSufficient(connection, accountId, amount)) {
                    updateAccountBalance(connection, accountId, -amount);
                } else {
                    throw new SQLException("Solde insuffisant pour effectuer la transaction.");
                }
            }
            insertTransaction(connection, accountId, amount, transactionType, description, label);

            displayAccountDetails(connection, accountId);
        }
    }

    private static String getAccountType(Connection connection, int accountId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT account_type FROM account WHERE account_id = ?")) {
            statement.setInt(1, accountId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("account_type");
                } else {
                    throw new SQLException("Account ID" + accountId + " doesn't find");
                }
            }
        }
    }

    private static void updateAccountBalance(Connection connection, int accountId, double amount) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE account SET balance = balance + ? WHERE account_id = ?")) {
            statement.setDouble(1, amount);
            statement.setInt(2, accountId);
            statement.executeUpdate();
        }
    }

    private static boolean isBalanceSufficient(Connection connection, int accountId, double amount) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT balance FROM account WHERE account_id = ?")) {
            statement.setInt(1, accountId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    double balance = resultSet.getDouble("balance");
                    return balance >= amount;
                } else {
                    throw new SQLException("Account ID " + accountId + " does not find.");
                }
            }
        }
    }

    private static void insertTransaction(Connection connection, int accountId, double amount, String transactionType, String description, String label) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO \"transaction\" (account_id, amount, transaction_date, description, transaction_type, label) " +
                        "VALUES (?, ?, CURRENT_TIMESTAMP, ?, ?, ?)")) {
            statement.setInt(1, accountId);
            statement.setDouble(2, amount);
            statement.setString(3, description);
            statement.setString(4, transactionType);
            statement.setString(5, label);
            statement.executeUpdate();
        }
    }

    private static void displayAccountDetails(Connection connection, int accountId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT account_id, account_name, balance, currency.currency_name " +
                        "FROM account INNER JOIN currency ON account.currency_id = currency.currency_id " +
                        "WHERE account_id = ?")) {
            statement.setInt(1, accountId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt("account_id");
                    String name = resultSet.getString("account_name");
                    double balance = resultSet.getDouble("balance");
                    String currencyName = resultSet.getString("currency_name");

                    System.out.println("Account ID: " + id);
                    System.out.println("Account Name: " + name);
                    System.out.println("Balance: " + balance);
                    System.out.println("Currency: " + currencyName);
                } else {
                    throw new SQLException("Account ID " + accountId + " does not find.");
                }
            }
        }
    }
}
