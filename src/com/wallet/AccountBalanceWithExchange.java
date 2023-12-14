package com.wallet;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;
import java.util.UUID;

public class AccountBalanceWithExchange {
    public static void main(String[] args) {
        try {
            Properties properties = loadProperties();

            String jdbcUrl = properties.getProperty("jdbcUrl");
            String dbUser = properties.getProperty("dbUser");
            String dbPassword = properties.getProperty("dbPassword");

            try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword)) {
                UUID accountId = UUID.fromString("efcf34e5-c7a2-427d-a402-f466b36453d1");

                performTransfer(connection, accountId, 1, 1.0, "2023-12-05 12:00:00");
                performTransfer(connection, accountId, 2, 1.0, "2023-12-06 12:00:00");

                performExpense(connection, accountId, UUID.randomUUID(), "New shoe at this time", "2023-12-06 16:00:00");

                updateExchangeRate(connection, "2023-12-06", 10000);

                double currentBalance = getAccountBalance(connection, accountId);
                System.out.println("Current balance : " + currentBalance + " Ariary");
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private static Properties loadProperties() throws IOException {
        Properties properties = new Properties();
        try (InputStream input = AccountBalanceWithExchange.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return properties;
            }
            properties.load(input);
        }
        return properties;
    }

    private static void performTransfer(Connection connection, UUID accountId, int transactionId, double euroAmount, String transactionDate) throws SQLException {
        double exchangeRate = getExchangeRate(connection, transactionDate);
        double ariaryAmount = euroAmount * exchangeRate;

        String insertTransactionSql = "INSERT INTO transaction (transaction_id, account_id, amount, transaction_date, transaction_type) VALUES (?, ?, ?, ?, 'credit')";
        try (PreparedStatement statement = connection.prepareStatement(insertTransactionSql)) {
            statement.setObject(1, UUID.randomUUID());
            statement.setObject(2, accountId, Types.OTHER);
            statement.setDouble(3, ariaryAmount);
            statement.setTimestamp(4, Timestamp.valueOf(transactionDate));

            statement.executeUpdate();
        }
    }


    private static void performExpense(Connection connection, UUID accountId, UUID transactionId, String description, String transactionDate) throws SQLException {
        String selectTransactionSql = "SELECT COUNT(*) FROM transaction WHERE account_id = ? AND transaction_date = ? AND description = ?";

        try (PreparedStatement selectStatement = connection.prepareStatement(selectTransactionSql)) {
            selectStatement.setObject(1, accountId, Types.OTHER);
            selectStatement.setTimestamp(2, Timestamp.valueOf(transactionDate));
            selectStatement.setString(3, description);

            try (ResultSet resultSet = selectStatement.executeQuery()) {
                resultSet.next();
                int count = resultSet.getInt(1);

                if (count == 0) {
                    String insertTransactionSql = "INSERT INTO transaction (transaction_id, account_id, amount, transaction_date, transaction_type, description) VALUES (?, ?, ?, ?, 'debit', ?)";
                    try (PreparedStatement insertStatement = connection.prepareStatement(insertTransactionSql)) {
                        insertStatement.setObject(1, transactionId);
                        insertStatement.setObject(2, accountId, Types.OTHER);
                        insertStatement.setDouble(3, 30000);
                        insertStatement.setTimestamp(4, Timestamp.valueOf(transactionDate));
                        insertStatement.setString(5, description);
                        insertStatement.executeUpdate();
                    }
                } else {
                    System.out.println("Transaction already exists, no insertion required.");
                }
            }
        }
    }


    private static void updateExchangeRate(Connection connection, String date, double newRate) throws SQLException {
        String formattedDate = date + " 00:00:00";
        String updateRateSql = "UPDATE exchange_rate SET rate = ? WHERE date = ?";
        try (PreparedStatement statement = connection.prepareStatement(updateRateSql)) {
            statement.setDouble(1, newRate);
            statement.setTimestamp(2, Timestamp.valueOf(formattedDate));
            statement.executeUpdate();
        }
    }


    private static double getExchangeRate(Connection connection, String date) throws SQLException {
        String selectRateSql = "SELECT rate FROM exchange_rate WHERE date <= ?::timestamp ORDER BY date DESC LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(selectRateSql)) {
            statement.setString(1, date);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble("rate");
                } else {
                    throw new SQLException("Exchange rate not found for the specified date.");
                }
            }
        }
    }



    private static double getAccountBalance(Connection connection, UUID accountId) throws SQLException {
        String selectBalanceSql = "SELECT COALESCE(SUM(CASE WHEN transaction_type = 'credit' THEN amount ELSE -amount END), 0) AS balance FROM transaction WHERE account_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(selectBalanceSql)) {
            statement.setObject(1, accountId, Types.OTHER);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble("balance");
                } else {
                    throw new SQLException("Account balance not found.");
                }
            }
        }
    }
}
