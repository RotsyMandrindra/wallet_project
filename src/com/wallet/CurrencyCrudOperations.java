package com.wallet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurrencyCrudOperations implements CrudOperations<Currency>{
    @Override
    public List<Currency> findAll() {
        List<Currency> currencies = new ArrayList<>();
        String query = "SELECT * FROM currency";

        try (Connection connection = DataConfig.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                Currency currency = new Currency();
                currency.setCurrency_id(resultSet.getInt("currency_id"));
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
        String query = "INSERT INTO currency (currency_name, currency_code) VALUES (?, ?)";

        try (Connection connection = DataConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            for (Currency currency : toSave) {
                preparedStatement.setString(1, currency.getCurrency_name());
                preparedStatement.setString(2, currency.getCurrency_code());
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
        String query = "INSERT INTO currency (currency_name, currency_code) VALUES (?, ?)";

        try (Connection connection = DataConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query,
                     PreparedStatement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, toSave.getCurrency_name());
            preparedStatement.setString(2, toSave.getCurrency_code());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        toSave.setCurrency_id(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Creating currency failed, no ID obtained.");
                    }
                }
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

            preparedStatement.setInt(1, toDelete.getCurrency_id());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return toDelete;
    }
}
