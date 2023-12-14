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

                performTransfer(connection, accountId, UUID.randomUUID(), 1.0, "2023-12-06 12:00:00");
                performTransfer(connection, accountId, UUID.randomUUID(), 1.0, "2023-12-06 18:00:00");

                performExpense(connection, accountId, UUID.randomUUID(), "New shoe at this time", "2023-12-06 16:00:00");

                updateExchangeRate(connection, "2023-12-06 12:00:00", 4500);
                updateExchangeRate(connection, "2023-12-06 06:00:00", 4600);
                updateExchangeRate(connection, "2023-12-06 12:00:00", 4550);
                updateExchangeRate(connection, "2023-12-06 16:00:00", 4650);
                updateExchangeRate(connection, "2023-12-06 18:00:00", 4690);

                double currentBalance = getAccountBalance(connection, accountId, "2023-12-06 14:00:00");
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

    private static void performTransfer(Connection connection, UUID accountId, UUID transactionId, double euroAmount, String transactionDate) throws SQLException {
        double exchangeRate = getWeightedAverageExchangeRate(connection, transactionDate);
        double ariaryAmount = euroAmount * exchangeRate;

        String insertTransactionSql = "INSERT INTO transaction (transaction_id, account_id, amount, transaction_date, transaction_type) VALUES (?, ?, ?, ?, 'credit')";
        try (PreparedStatement statement = connection.prepareStatement(insertTransactionSql)) {
            statement.setObject(1, transactionId, Types.OTHER);
            statement.setObject(2, accountId, Types.OTHER);
            statement.setDouble(3, ariaryAmount);
            statement.setTimestamp(4, Timestamp.valueOf(transactionDate));
            statement.executeUpdate();
        }
    }

    private static void performExpense(Connection connection, UUID accountId, UUID transactionId, String description, String transactionDate) throws SQLException {
        String checkTransactionSql = "SELECT COUNT(*) FROM transaction WHERE account_id = ? AND transaction_date = ?::timestamp AND description = ?";
        try (PreparedStatement checkStatement = connection.prepareStatement(checkTransactionSql)) {
            checkStatement.setObject(1, accountId, Types.OTHER);
            checkStatement.setTimestamp(2, Timestamp.valueOf(transactionDate));
            checkStatement.setString(3, description);

            try (ResultSet resultSet = checkStatement.executeQuery()) {
                resultSet.next();
                int count = resultSet.getInt(1);

                if (count == 0) {
                    String insertTransactionSql = "INSERT INTO transaction (transaction_id, account_id, amount, transaction_date, transaction_type, description) VALUES (?, ?, ?, ?, 'debit', ?)";
                    try (PreparedStatement statement = connection.prepareStatement(insertTransactionSql)) {
                        statement.setObject(1, transactionId, Types.OTHER);
                        statement.setObject(2, accountId, Types.OTHER);
                        statement.setDouble(3, 30000);
                        statement.setTimestamp(4, Timestamp.valueOf(transactionDate));
                        statement.setString(5, description);
                        statement.executeUpdate();
                    }
                } else {
                    System.out.println("The transaction already exists.");
                }
            }
        }
    }

    private static void updateExchangeRate(Connection connection, String date, double newRate) throws SQLException {
        String updateRateSql = "UPDATE exchange_rate SET rate = ? WHERE date = ?";
        try (PreparedStatement statement = connection.prepareStatement(updateRateSql)) {
            statement.setDouble(1, newRate);
            statement.setTimestamp(2, Timestamp.valueOf(date));
            statement.executeUpdate();
        }
    }

    private static double getWeightedAverageExchangeRate(Connection connection, String date) throws SQLException {
        String selectWeightedAverageSql = "SELECT AVG(rate) FROM exchange_rate WHERE date::date = ?::date";
        try (PreparedStatement statement = connection.prepareStatement(selectWeightedAverageSql)) {
            statement.setString(1, date);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble(1);
                } else {
                    throw new SQLException("Weighted average exchange rate not found for the specified date.");
                }
            }
        }
    }

    private static double getAccountBalance(Connection connection, UUID accountId, String date) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT COALESCE(SUM(CASE WHEN transaction_type = 'credit' THEN amount ELSE -amount END), 0) AS balance FROM transaction WHERE account_id = ? AND transaction_date <= ?::timestamp")) {
            statement.setObject(1, accountId, Types.OTHER);
            statement.setTimestamp(2, Timestamp.valueOf(date));
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble("balance");
                } else {
                    throw new SQLException("Account balance not found.");
                }
            }
        }
    }
    private static double getExchangeRate(Connection connection, String date, AggregationType aggregationType) throws SQLException {
        String aggregationFunction;
        switch (aggregationType) {
            case WEIGHTED_AVERAGE:
                aggregationFunction = "AVG";
                break;
            case MINIMUM:
                aggregationFunction = "MIN";
                break;
            case MAXIMUM:
                aggregationFunction = "MAX";
                break;
            case MEDIAN:
                throw new UnsupportedOperationException("Median aggregation not supported in PostgreSQL");
            default:
                throw new IllegalArgumentException("Invalid aggregation type");
        }

        String selectExchangeRateSql = String.format("SELECT %s(rate) FROM exchange_rate WHERE date::date = ?::date", aggregationFunction);
        try (PreparedStatement statement = connection.prepareStatement(selectExchangeRateSql)) {
            statement.setString(1, date);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble(1);
                } else {
                    throw new SQLException("Exchange rate not found for the specified date.");
                }
            }
        }
    }

    private enum AggregationType {
        WEIGHTED_AVERAGE,
        MINIMUM,
        MAXIMUM,
        MEDIAN
    }

}
