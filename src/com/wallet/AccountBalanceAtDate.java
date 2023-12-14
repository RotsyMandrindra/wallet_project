package com.wallet;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class AccountBalanceAtDate {
    public static void main(String[] args) {
        try {
            Properties properties = loadProperties();

            String jdbcUrl = properties.getProperty("jdbcUrl");
            String dbUser = properties.getProperty("dbUser");
            String dbPassword = properties.getProperty("dbPassword");

            try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword)) {
                int accountId = 1;
                Timestamp targetDate = Timestamp.valueOf("2023-12-06 15:45:00");

                double balance = getAccountBalanceAtDate(connection, accountId, targetDate);
                System.out.println("Solde du compte à la date spécifiée : " + balance);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private static Properties loadProperties() throws IOException {
        Properties properties = new Properties();
        try (InputStream input = AccountBalanceAtDate.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return properties;
            }
            properties.load(input);
        }
        return properties;
    }

    private static double getAccountBalanceAtDate(Connection connection, int accountId, Timestamp targetDate) throws SQLException {
        String sql = "SELECT COALESCE(SUM(CASE WHEN transaction_type = 'credit' THEN amount ELSE -amount END), 0) AS balance " +
                "FROM transaction " +
                "WHERE account_id = ? AND transaction_date <= ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, accountId);
            statement.setTimestamp(2, targetDate);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble("balance");
                } else {
                    throw new SQLException("Aucun résultat obtenu.");
                }
            }
        }
    }
}