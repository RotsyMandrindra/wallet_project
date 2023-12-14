package com.wallet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.wallet.DataConfig.getConnection;

public class CurrencyValueCrudOperationsClass implements CurrencyValueCrudOperationsInterface {
    private final Connection connection;

    public CurrencyValueCrudOperationsClass(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<CurrencyValue> findAll() {
        String query = "SELECT * FROM currencyValue";
        return executeQueryAndReturnList(query);
    }

    @Override
    public List<CurrencyValue> saveAll(List<CurrencyValue> toSave) {
        String query = "INSERT INTO currencyValue (id_currency_value, id_currency, id_currency_destination, amount, date_effect) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING *";
        return executeBatchInsertAndReturnList(query, toSave);
    }

    @Override
    public CurrencyValue save(CurrencyValue toSave) {
        String query = "INSERT INTO currencyValue (id_currency_value, id_currency, id_currency_destination, amount, date_effect) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING *";
        return executeInsertAndReturnEntity(query, toSave);
    }

    @Override
    public CurrencyValue delete(CurrencyValue toDelete) {
        String query = "DELETE FROM currencyValue WHERE id_currency_value = ? RETURNING *";
        return executeDeleteAndReturnEntity(query, toDelete);
    }

    @Override
    public List<CurrencyValue> findByDateEffect(Timestamp dateEffect) {
        String query = "SELECT * FROM currencyValue WHERE date_effect = ?";
        return executeQueryWithParameterAndReturnList(query, dateEffect);
    }

    private List<CurrencyValue> executeQueryAndReturnList(String query) {
        List<CurrencyValue> currencyValues = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                CurrencyValue currencyValue = new CurrencyValue(
                        UUID.fromString(resultSet.getString("id_currency_value")),
                        UUID.fromString(resultSet.getString("id_currency")),
                        UUID.fromString(resultSet.getString("id_currency_destination")),
                        resultSet.getDouble("amount"),
                        resultSet.getTimestamp("date_effect")
                );

                currencyValues.add(currencyValue);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return currencyValues;
    }


    private List<CurrencyValue> executeQueryWithParameterAndReturnList(String query, Timestamp parameter) {
        List<CurrencyValue> currencyValues = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setTimestamp(1, parameter);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    CurrencyValue currencyValue = new CurrencyValue(
                            UUID.fromString(resultSet.getString("id_currency_value")),
                            UUID.fromString(resultSet.getString("id_currency")),
                            UUID.fromString(resultSet.getString("id_currency_destination")),
                            resultSet.getDouble("amount"),
                            resultSet.getTimestamp("date_effect")
                    );

                    currencyValues.add(currencyValue);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return currencyValues;
    }


    private List<CurrencyValue> executeBatchInsertAndReturnList(String query, List<CurrencyValue> toSave) {
        List<CurrencyValue> savedCurrencyValues = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            for (CurrencyValue currencyValue : toSave) {
                statement.setString(1, currencyValue.getIdCurrency().toString());
                statement.setString(2, currencyValue.getIdCurrencyDestination().toString());
                statement.setDouble(3, currencyValue.getAmount());
                statement.setTimestamp(4, currencyValue.getDateEffect());

                statement.addBatch();
            }

            int[] result = statement.executeBatch();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                for (int i = 0; i < result.length; i++) {
                    if (result[i] == Statement.SUCCESS_NO_INFO) {
                    } else if (result[i] == Statement.EXECUTE_FAILED) {
                    } else {
                        if (generatedKeys.next()) {
                            UUID idCurrencyValue = UUID.fromString(generatedKeys.getString(1));
                            CurrencyValue savedCurrencyValue = new CurrencyValue(
                                    idCurrencyValue,
                                    toSave.get(i).getIdCurrency(),
                                    toSave.get(i).getIdCurrencyDestination(),
                                    toSave.get(i).getAmount(),
                                    toSave.get(i).getDateEffect()
                            );
                            savedCurrencyValues.add(savedCurrencyValue);
                        }
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return savedCurrencyValues;
    }


    private CurrencyValue executeInsertAndReturnEntity(String query, CurrencyValue toSave) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, toSave.getIdCurrency().toString());
            statement.setString(2, toSave.getIdCurrencyDestination().toString());
            statement.setDouble(3, toSave.getAmount());
            statement.setTimestamp(4, toSave.getDateEffect());

            int result = statement.executeUpdate();

            if (result == Statement.SUCCESS_NO_INFO) {
                return null;
            } else if (result == Statement.EXECUTE_FAILED) {
                return null;
            } else {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        UUID idCurrencyValue = UUID.fromString(generatedKeys.getString(1));
                        return new CurrencyValue(
                                idCurrencyValue,
                                toSave.getIdCurrency(),
                                toSave.getIdCurrencyDestination(),
                                toSave.getAmount(),
                                toSave.getDateEffect()
                        );
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    private CurrencyValue executeDeleteAndReturnEntity(String query, CurrencyValue toDelete) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, toDelete.getIdCurrencyValue().toString());

            int result = statement.executeUpdate();

            if (result == Statement.SUCCESS_NO_INFO) {
                return null;
            } else if (result == Statement.EXECUTE_FAILED) {
                return null;
            } else {
                return toDelete;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

}
