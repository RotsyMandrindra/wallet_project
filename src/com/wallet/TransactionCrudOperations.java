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
                transaction.setTransaction_id(resultSet.getInt("transaction_id"));
                transaction.setAccount_id(resultSet.getInt("account_id"));
                transaction.setAmount(resultSet.getDouble("amount"));
                transaction.setTransaction_date(resultSet.getTimestamp("transaction_date"));
                transaction.setDescription(resultSet.getString("description"));
                transactions.add(transaction);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactions;
    }

    @Override
    public List<Transaction> saveAll(List<Transaction> toSave) {
        String query = "INSERT INTO \"transaction\" (account_id, amount, transaction_date, description) VALUES (?, ?, ?, ?)";

        try (Connection connection = DataConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            for (Transaction transaction : toSave) {
                preparedStatement.setInt(1, transaction.getAccount_id());
                preparedStatement.setDouble(2, transaction.getAmount());
                preparedStatement.setTimestamp(3, transaction.getTransaction_date());
                preparedStatement.setString(4, transaction.getDescription());
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
