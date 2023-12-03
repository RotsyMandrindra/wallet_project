package com.wallet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountCrudOperations implements CrudOperations<Account>{
    @Override
    public List<Account> findAll() {
        List<Account> accounts = new ArrayList<>();
        String query = "SELECT * FROM account";

        try (Connection connection = DataConfig.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                Account account = new Account();
                account.setAccount_id(resultSet.getInt("account_id"));
                account.setCustomer_name(resultSet.getString("customer_name"));
                account.setBalance(resultSet.getDouble("balance"));
                account.setPassword(resultSet.getString("password"));
                account.setCurrency_id(resultSet.getInt("currency_id"));
                accounts.add(account);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return accounts;
    }

    @Override
    public List<Account> saveAll(List<Account> toSave) {
        String query = "INSERT INTO account (customer_name, balance, password, currency_id) VALUES (?, ?, ?, ?)";

        try (Connection connection = DataConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            for (Account account : toSave) {
                preparedStatement.setString(1, account.getCustomer_name());
                preparedStatement.setDouble(2, account.getBalance());
                preparedStatement.setString(3, account.getPassword());
                preparedStatement.setInt(4, account.getCurrency_id());
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return toSave;
    }

    @Override
    public Account save(Account toSave) {
        String query = "INSERT INTO account (customer_name, balance, password, currency_id) VALUES (?, ?, ?, ?)";

        try (Connection connection = DataConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query,
                     PreparedStatement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, toSave.getCustomer_name());
            preparedStatement.setDouble(2, toSave.getBalance());
            preparedStatement.setString(3, toSave.getPassword());
            preparedStatement.setInt(4, toSave.getCurrency_id());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        toSave.setAccount_id(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Creating account failed, no ID obtained.");
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return toSave;
    }

    @Override
    public Account delete(Account toDelete) {
        String query = "DELETE FROM account WHERE account_id = ?";

        try (Connection connection = DataConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, toDelete.getAccount_id());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return toDelete;
    }
}
