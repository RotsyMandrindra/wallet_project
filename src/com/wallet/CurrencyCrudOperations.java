package com.wallet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CurrencyCrudOperations implements CrudOperations<Currency> {
    @Override
    public List<Currency> findAll() {
        List<Currency> currencies = new ArrayList<>();
        String query = "SELECT * FROM currency";

        try (Connection connection = DataConfig.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                Currency currency = new Currency();
                currency.setCurrency_id(UUID.fromString(resultSet.getString("currency_id")));
                currency.setCurrency_name(resultSet.getString("currency_name"));
                currency.setCurrency_code(resultSet.getString("currency_code"));
                currencies.add(currency);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return currencies;
    }

    @Override
    public List<Currency> saveAll(List<Currency> toSave) {
        String query = "INSERT INTO currency (currency_id, currency_name, currency_code) VALUES (?, ?, ?)"
                + " ON DUPLICATE KEY UPDATE currency_name=VALUES(currency_name), currency_code=VALUES(currency_code)";

        try (Connection connection = DataConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            for (Currency currency : toSave) {
                preparedStatement.setObject(1, currency.getCurrency_id());
                preparedStatement.setString(2, currency.getCurrency_name());
                preparedStatement.setString(3, currency.getCurrency_code());
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return toSave;
    }

    @Override
    public Currency save(Currency toSave) {
        String query = "INSERT INTO currency (currency_id, currency_name, currency_code) VALUES (?, ?, ?)"
                + " ON DUPLICATE KEY UPDATE currency_name=VALUES(currency_name), currency_code=VALUES(currency_code)";

        try (Connection connection = DataConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query,
                     PreparedStatement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setObject(1, toSave.getCurrency_id());
            preparedStatement.setString(2, toSave.getCurrency_name());
            preparedStatement.setString(3, toSave.getCurrency_code());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating currency failed, no rows affected.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return toSave;
    }

    @Override
    public Currency delete(Currency toDelete) {
        String query = "DELETE FROM currency WHERE currency_id = ?";

        try (Connection connection = DataConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setObject(1, toDelete.getCurrency_id());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return toDelete;
    }
}
