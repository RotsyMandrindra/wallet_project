package com.wallet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionCrudOperations implements CrudOperations<Transaction>{
    @Override
    public List<Transaction> findAll() {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT * FROM \"transaction\"";

        try (Connection connection = DataConfig.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                Transaction transaction = new Transaction();
                transaction.setTransactionId(resultSet.getInt("transaction_id"));
                transaction.setAccountId(resultSet.getInt("account_id"));
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
        String query = "INSERT INTO \"transaction\" (transaction_id, account_id, amount, transaction_date, description, transaction_type, label) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DataConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            for (Transaction transaction : toSave) {
                preparedStatement.setInt(1, transaction.getAccountId());
                preparedStatement.setInt(2, transaction.getAccountId());
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
        return null;
    }

    @Override
    public Transaction delete(Transaction toDelete) {
        return null;
    }
}
