package com.wallet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TransactionCrudOperations implements CrudOperations<Transaction> {
    @Override
    public List<Transaction> findAll() {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT * FROM \"transaction\"";

        try (Connection connection = DataConfig.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                Transaction transaction = new Transaction();
                transaction.setTransactionId(UUID.fromString(resultSet.getString("transaction_id")));
                transaction.setAccountId(UUID.fromString(resultSet.getString("account_id")));
                transaction.setAmount(resultSet.getDouble("amount"));
                transaction.setTransactionDate(resultSet.getTimestamp("transaction_date"));
                transaction.setDescription(resultSet.getString("description"));
                transaction.setTransactionType(resultSet.getString("transaction_type"));
                transaction.setLabel(resultSet.getString("label"));
                transactions.add(transaction);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactions;
    }

    @Override
    public List<Transaction> saveAll(List<Transaction> toSave) {
        String query = "INSERT INTO \"transaction\" (transaction_id, account_id, amount, transaction_date, description, transaction_type, label) VALUES (?, ?, ?, ?, ?, ?, ?)"
                + " ON DUPLICATE KEY UPDATE account_id=VALUES(account_id), amount=VALUES(amount), transaction_date=VALUES(transaction_date),"
                + " description=VALUES(description), transaction_type=VALUES(transaction_type), label=VALUES(label)";

        try (Connection connection = DataConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            for (Transaction transaction : toSave) {
                preparedStatement.setObject(1, transaction.getTransactionId());
                preparedStatement.setObject(2, transaction.getAccountId());
                preparedStatement.setDouble(3, transaction.getAmount());
                preparedStatement.setTimestamp(4, transaction.getTransactionDate());
                preparedStatement.setString(5, transaction.getDescription());
                preparedStatement.setString(6, transaction.getTransactionType());
                preparedStatement.setString(7, transaction.getLabel());
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return toSave;
    }

    @Override
    public Transaction save(Transaction toSave) {
        String query = "INSERT INTO \"transaction\" (transaction_id, account_id, amount, transaction_date, description, transaction_type, label) VALUES (?, ?, ?, ?, ?, ?, ?)"
                + " ON DUPLICATE KEY UPDATE account_id=VALUES(account_id), amount=VALUES(amount), transaction_date=VALUES(transaction_date),"
                + " description=VALUES(description), transaction_type=VALUES(transaction_type), label=VALUES(label)";

        try (Connection connection = DataConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query,
                     PreparedStatement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setObject(1, toSave.getTransactionId());
            preparedStatement.setObject(2, toSave.getAccountId());
            preparedStatement.setDouble(3, toSave.getAmount());
            preparedStatement.setTimestamp(4, toSave.getTransactionDate());
            preparedStatement.setString(5, toSave.getDescription());
            preparedStatement.setString(6, toSave.getTransactionType());
            preparedStatement.setString(7, toSave.getLabel());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating transaction failed, no rows affected.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return toSave;
    }

    @Override
    public Transaction delete(Transaction toDelete) {
        String query = "DELETE FROM \"transaction\" WHERE transaction_id = ?";

        try (Connection connection = DataConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setObject(1, toDelete.getTransactionId());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return toDelete;
    }
}
