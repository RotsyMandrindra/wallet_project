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
                account.setAccountId(resultSet.getInt("account_id"));
                account.setAccountName(resultSet.getString(" account_name"));
                account.setBalance(resultSet.getDouble("balance"));
                account.setLastUpdateDate(resultSet.getTimestamp("last_update_date"));
                account.setPassword(resultSet.getString("password"));
                account.setCurrencyId(resultSet.getInt("currency_id"));
                account.setAccountType(resultSet.getString("account_type"));
                accounts.add(account);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return accounts;
    }

    @Override
    public List<Account> saveAll(List<Account> toSave) {
        String query = "INSERT INTO account (account_id, account_name, balance, password, currency_id, account_type) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DataConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            for (Account account : toSave) {
                preparedStatement.setString(1, account.getAccountName());
                preparedStatement.setDouble(2, account.getBalance());
                preparedStatement.setTimestamp(3, account.getLastUpdateDate());
                preparedStatement.setString(4, account.getPassword());
                preparedStatement.setString(5, account.getAccountType());
                preparedStatement.setInt(6, account.getCurrencyId());
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
        String query = "INSERT INTO account (account_id, account_name, balance, password, currency_id, account_type) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DataConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query,
                     PreparedStatement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, toSave.getAccountName());
            preparedStatement.setDouble(2, toSave.getBalance());
            preparedStatement.setTimestamp(3, toSave.getLastUpdateDate());
            preparedStatement.setString(4, toSave.getPassword());
            preparedStatement.setString(5, toSave.getAccountType());
            preparedStatement.setInt(6, toSave.getCurrencyId());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        toSave.setAccountId(generatedKeys.getInt(1));
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

            preparedStatement.setInt(1, toDelete.getAccountId());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return toDelete;
    }
}
