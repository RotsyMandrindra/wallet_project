package com.wallet;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class MoneyTransfer {
    public static void main(String[] args) {
        try {
            Properties properties = loadProperties();

            String jdbcUrl = properties.getProperty("jdbcUrl");
            String dbUser = properties.getProperty("dbUser");
            String dbPassword = properties.getProperty("dbPassword");

            try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword)) {
                int debitAccountId = 1;
                int creditAccountId = 2;
                double amount = 100.0;

                transferMoney(connection, debitAccountId, creditAccountId, amount);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private static Properties loadProperties() throws IOException {
        Properties properties = new Properties();
        try (InputStream input = MoneyTransfer.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return properties;
            }
            properties.load(input);
        }
        return properties;
    }

    private static void transferMoney(Connection connection, int debitAccountId, int creditAccountId, double amount) throws SQLException {
        if (debitAccountId == creditAccountId) {
            throw new IllegalArgumentException("Le compte débiteur ne peut pas être le même que le compte créditeur.");
        }

        connection.setAutoCommit(false);

        try {
            int debitTransactionId = performTransaction(connection, debitAccountId, -amount, "Débit pour transfert", "Transfert");

            int creditTransactionId = performTransaction(connection, creditAccountId, amount, "Crédit pour transfert", "Transfert");

            addTransferHistoryEntry(connection, debitTransactionId, creditTransactionId);

            connection.commit();

            System.out.println("Transfert d'argent effectué avec succès.");
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private static int performTransaction(Connection connection, int accountId, double amount, String description, String label) throws SQLException {
        String sql = "INSERT INTO transaction (account_id, amount, transaction_date, description, transaction_type, label) " +
                "VALUES (?, ?, CURRENT_TIMESTAMP, ?, ?, ?) RETURNING transaction_id";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, accountId);
            statement.setDouble(2, amount);
            statement.setString(3, description);
            statement.setString(4, amount < 0 ? "debit" : "credit");
            statement.setString(5, label);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("transaction_id");
            } else {
                throw new SQLException("Échec de l'insertion de la transaction.");
            }
        }
    }

    private static void addTransferHistoryEntry(Connection connection, int debitTransactionId, int creditTransactionId) throws SQLException {
        String sql = "INSERT INTO TransferHistory (debit_transaction_id, credit_transaction_id, transfer_date) " +
                "VALUES (?, ?, CURRENT_TIMESTAMP)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, debitTransactionId);
            statement.setInt(2, creditTransactionId);

            statement.executeUpdate();
        }
    }
}
