package com.wallet;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class AccountBalanceHistory {
    public static void main(String[] args) {
        try {
            Properties properties = loadProperties();

            String jdbcUrl = properties.getProperty("jdbcUrl");
            String dbUser = properties.getProperty("dbUser");
            String dbPassword = properties.getProperty("dbPassword");

            try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword)) {
                int accountId = 1;
                Timestamp startDate = Timestamp.valueOf("2023-12-01 00:00:00");
                Timestamp endDate = Timestamp.valueOf("2023-12-06 16:00:00");

                List<BalanceHistoryEntry> balanceHistory = getAccountBalanceHistory(connection, accountId, startDate, endDate);
                for (BalanceHistoryEntry entry : balanceHistory) {
                    System.out.println("Date: " + entry.getDate() + ", Solde: " + entry.getBalance());
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private static Properties loadProperties() throws IOException {
        Properties properties = new Properties();
        try (InputStream input = AccountBalanceHistory.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return properties;
            }
            properties.load(input);
        }
        return properties;
    }

    private static List<BalanceHistoryEntry> getAccountBalanceHistory(Connection connection, int accountId, Timestamp startDate, Timestamp endDate) throws SQLException {
        String sql = "SELECT transaction_date, COALESCE(SUM(CASE WHEN transaction_type = 'credit' THEN amount ELSE -amount END), 0) AS balance " +
                "FROM transaction " +
                "WHERE account_id = ? AND transaction_date BETWEEN ? AND ? " +
                "GROUP BY transaction_date " +
                "ORDER BY transaction_date";

        List<BalanceHistoryEntry> balanceHistory = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, accountId);
            statement.setTimestamp(2, startDate);
            statement.setTimestamp(3, endDate);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Timestamp date = resultSet.getTimestamp("transaction_date");
                    double balance = resultSet.getDouble("balance");

                    BalanceHistoryEntry entry = new BalanceHistoryEntry(date, balance);
                    balanceHistory.add(entry);
                }
            }
        }

        return balanceHistory;
    }

    private static class BalanceHistoryEntry {
        private Timestamp date;
        private double balance;

        public BalanceHistoryEntry(Timestamp date, double balance) {
            this.date = date;
            this.balance = balance;
        }

        public Timestamp getDate() {
            return date;
        }

        public double getBalance() {
            return balance;
        }
    }
}
