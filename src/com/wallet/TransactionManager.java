package com.wallet;

import java.io.IOException;
import java.io.InputStream;
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
                performTransaction(connection, 1, 100.0, "credit", "Shoes", "Espece");
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private static Properties loadProperties() throws IOException {
        Properties properties = new Properties();
        try (InputStream input = TransactionManager.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, I can't find config.properties");
                return properties;
            }
            properties.load(input);
        }
        return properties;
    }

    public static void performTransaction(Connection connection, int accountId, double amount, String transactionType, String description, String label) throws SQLException {
        String accountType = getAccountType(connection, accountId);

        updateAccountBalance(connection, accountId, amount, accountType, transactionType);

        insertTransaction(connection, accountId, amount, transactionType, description, label);

        displayAccountDetails(connection, accountId);
    }

    private static String getAccountType(Connection connection, int accountId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT account_type FROM account WHERE account_id = ?")) {
            statement.setInt(1, accountId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("account_type");
                } else {
                    throw new SQLException("ID Account " + accountId + " doesn't found");
                }
            }
        }
    }

    private static void updateAccountBalance(Connection connection, int accountId, double amount, String accountType, String transactionType) throws SQLException {
        String updateSql;
        if (("Bank".equals(accountType) || "Mobile Money".equals(accountType)) || "credit".equals(transactionType)) {
            updateSql = "UPDATE account SET balance = balance + ? WHERE account_id = ?";
        } else if ("Espece".equals(accountType) && "debit".equals(transactionType)) {
            updateSql = "UPDATE account SET balance = balance - ? WHERE account_id = ?";
        } else {
            throw new SQLException("Type of account or transaction not supported.");
        }

        try (PreparedStatement statement = connection.prepareStatement(updateSql)) {
            statement.setDouble(1, amount);
            statement.setInt(2, accountId);
            statement.executeUpdate();
        }
    }

    private static void insertTransaction(Connection connection, int accountId, double amount, String transactionType, String description, String label) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO transaction (account_id, amount, transaction_date, description, transaction_type, label) " +
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
                    System.out.println("Account name: " + name);
                    System.out.println("Balance: " + balance);
                    System.out.println("Currency: " + currencyName);
                } else {
                    throw new SQLException("Account ID " + accountId + " does not found.");
                }
            }
        }
    }
}
