package com.wallet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TransactionCrudOperationsClass implements TransactionCrudOperationsInterface {
    private final Connection connection;

    public TransactionCrudOperationsClass(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Transaction> findAll() {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT * FROM transaction";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                transactions.add(mapResultSetToTransaction(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    @Override
    public List<Transaction> saveAll(List<Transaction> toSave) {
        String query = "INSERT INTO transaction (transaction_id, account_id, amount, transaction_date, description, transaction_type, label) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING *";

        List<Transaction> savedTransactions = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            for (Transaction transaction : toSave) {
                statement.setObject(1, transaction.getTransactionId());
                statement.setObject(2, transaction.getAccountId());
                statement.setDouble(3, transaction.getAmount());
                statement.setTimestamp(4, transaction.getTransactionDate());
                statement.setString(5, transaction.getDescription());
                statement.setString(6, transaction.getTransactionType());
                statement.setString(7, transaction.getLabel());

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        savedTransactions.add(mapResultSetToTransaction(resultSet));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return savedTransactions;
    }


    @Override
    public Transaction save(Transaction toSave) {
        String query = "INSERT INTO transaction (account_id, amount, transaction_date, description, transaction_type, label) VALUES (?, ?, ?, ?, ?, ?) RETURNING *";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, toSave.getAccountId());
            statement.setDouble(2, toSave.getAmount());
            statement.setTimestamp(3, toSave.getTransactionDate());
            statement.setString(4, toSave.getDescription());
            statement.setString(5, toSave.getTransactionType());
            statement.setString(6, toSave.getLabel());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToTransaction(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Transaction delete(Transaction toDelete) {
        String query = "DELETE FROM transaction WHERE transaction_id = ? RETURNING *";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, toDelete.getTransactionId());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToTransaction(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public List<Transaction> findByAccountId(UUID accountId) {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT * FROM transaction WHERE account_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, accountId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    transactions.add(mapResultSetToTransaction(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    @Override
    public List<Transaction> findByDateRange(UUID accountId, Timestamp startDate, Timestamp endDate) {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT * FROM transaction WHERE account_id = ? AND transaction_date BETWEEN ? AND ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, accountId);
            statement.setTimestamp(2, startDate);
            statement.setTimestamp(3, endDate);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    transactions.add(mapResultSetToTransaction(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return connection;
    }

    private Transaction mapResultSetToTransaction(ResultSet resultSet) throws SQLException {
        UUID transactionId = (UUID) resultSet.getObject("transaction_id");
        UUID accountId = (UUID) resultSet.getObject("account_id");
        double amount = resultSet.getDouble("amount");
        Timestamp transactionDate = resultSet.getTimestamp("transaction_date");
        String description = resultSet.getString("description");
        String transactionType = resultSet.getString("transaction_type");
        String label = resultSet.getString("label");
        return new Transaction(transactionId, accountId, amount, transactionDate, description, transactionType, label);
    }
}
