package com.wallet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AccountCrudOperations implements CrudOperations<Account> {
    @Override
    public List<Account> findAll() {
        List<Account> accounts = new ArrayList<>();
        String query = "SELECT * FROM account";

        try (Connection connection = DataConfig.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                Account account = new Account();
                account.setAccountId(UUID.fromString(resultSet.getString("account_id")));
                account.setAccountName(resultSet.getString("account_name"));
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
        String query = "INSERT INTO account (account_id, account_name, balance, password, currency_id, account_type) VALUES (?, ?, ?, ?, ?, ?)"
                + " ON DUPLICATE KEY UPDATE account_name=VALUES(account_name), balance=VALUES(balance), last_update_date=VALUES(last_update_date),"
                + " password=VALUES(password), currency_id=VALUES(currency_id), account_type=VALUES(account_type)";

        try (Connection connection = DataConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            for (Account account : toSave) {
                preparedStatement.setObject(1, account.getAccountId());
                preparedStatement.setString(2, account.getAccountName());
                preparedStatement.setDouble(3, account.getBalance());
                preparedStatement.setTimestamp(4, account.getLastUpdateDate());
                preparedStatement.setString(5, account.getPassword());
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
        String query = "INSERT INTO account (account_id, account_name, balance, password, currency_id, account_type) VALUES (?, ?, ?, ?, ?, ?)"
                + " ON DUPLICATE KEY UPDATE account_name=VALUES(account_name), balance=VALUES(balance), last_update_date=VALUES(last_update_date),"
                + " password=VALUES(password), currency_id=VALUES(currency_id), account_type=VALUES(account_type)";

        try (Connection connection = DataConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query,
                     PreparedStatement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setObject(1, toSave.getAccountId());
            preparedStatement.setString(2, toSave.getAccountName());
            preparedStatement.setDouble(3, toSave.getBalance());
            preparedStatement.setTimestamp(4, toSave.getLastUpdateDate());
            preparedStatement.setString(5, toSave.getPassword());
            preparedStatement.setInt(6, toSave.getCurrencyId());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating account failed, no rows affected.");
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

            preparedStatement.setObject(1, toDelete.getAccountId());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return toDelete;
    }
}
