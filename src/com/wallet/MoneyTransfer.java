package com.wallet;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;
import java.util.UUID;

public class MoneyTransfer {
    public static void main(String[] args) {
        try {
            Properties properties = loadProperties();

            String jdbcUrl = properties.getProperty("jdbcUrl");
            String dbUser = properties.getProperty("dbUser");
            String dbPassword = properties.getProperty("dbPassword");

            try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword)) {
                UUID debitAccountId = UUID.fromString("2e243ca9-a49d-4ca4-b699-3f4a6d64364d");
                UUID creditAccountId = UUID.fromString("1a2d4281-1204-42f1-b9ca-adcc85fbadf9");
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

    private static void transferMoney(Connection connection, UUID debitAccountId, UUID creditAccountId, double amount) throws SQLException {
        if (debitAccountId == creditAccountId) {
            throw new IllegalArgumentException("Le compte débiteur ne peut pas être le même que le compte créditeur.");
        }

        connection.setAutoCommit(false);

        try {
            UUID debitTransactionId = performTransaction(connection, debitAccountId, -amount, "Débit pour transfert", "Transfert");

            UUID creditTransactionId = performTransaction(connection, creditAccountId, amount, "Crédit pour transfert", "Transfert");

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

    private static UUID performTransaction(Connection connection, UUID accountId, double amount, String description, String label) throws SQLException {
        String sql = "INSERT INTO transaction (account_id, amount, transaction_date, description, transaction_type, label) " +
                "VALUES (?, ?, CURRENT_TIMESTAMP, ?, ?, ?) RETURNING transaction_id";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, accountId);
            statement.setDouble(2, amount);
            statement.setString(3, description);
            statement.setString(4, amount < 0 ? "debit" : "credit");
            statement.setString(5, label);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return (UUID) resultSet.getObject("transaction_id", UUID.class);
            } else {
                throw new SQLException("Échec de l'insertion de la transaction.");
            }
        }
    }


    private static void addTransferHistoryEntry(Connection connection, UUID debitTransactionId, UUID creditTransactionId) throws SQLException {
        String sql = "INSERT INTO TransferHistory (debit_transaction_id, credit_transaction_id, transfer_date) " +
                "VALUES (?, ?, CURRENT_TIMESTAMP)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, debitTransactionId);
            statement.setObject(2, creditTransactionId);

            statement.executeUpdate();
        }
    }

    private static double getAriaryBalanceAtDate(Connection connection, int accountId, Timestamp targetDate) throws SQLException {
        String sql = "SELECT COALESCE(SUM(CASE WHEN transaction_type = 'credit' THEN amount * cv.Montant ELSE -amount * cv.Montant END), 0) AS balance " +
                "FROM \"transaction\" t " +
                "JOIN CurrencyValue cv ON t.transaction_date >= cv.Date_effet " +
                "WHERE t.account_id = ? AND t.transaction_date <= ? " +
                "AND cv.ID_Devise_source = (SELECT currency_id FROM currency WHERE currency_name = 'EURO') " +
                "AND cv.ID_Devise_destination = (SELECT currency_id FROM currency WHERE currency_name = 'ARIARY')";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, accountId);
            statement.setTimestamp(2, targetDate);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble("balance");
                } else {
                    throw new SQLException("No results obtained.");
                }
            }
        }
    }
}
