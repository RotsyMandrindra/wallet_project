package com.wallet;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;
import java.util.UUID;

public class DataMapping {
    public static void main(String[] args) {
        Properties properties = loadProperties();
        UUID accountId = UUID.fromString("efcf34e5-c7a2-427d-a402-f466b36453d1");
        Timestamp startDate = Timestamp.valueOf("2023-12-08 17:33:57.331554");
        Timestamp endDate = Timestamp.valueOf("2023-12-08 17:33:57.331554");

        calculateBalance(accountId, startDate, endDate, properties);

        calculateCategoryBalance(accountId, startDate, endDate, properties);
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = DataMapping.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return properties;
            }
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    private static void calculateBalance(UUID accountId, Timestamp startDate, Timestamp endDate, Properties properties) {
        String jdbcUrl = properties.getProperty("jdbcUrl");
        String dbUser = properties.getProperty("dbUser");
        String dbPassword = properties.getProperty("dbPassword");

        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword)) {
            String query = "SELECT * FROM calculate_balance(?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setObject(1, accountId);
                statement.setTimestamp(2, startDate);
                statement.setTimestamp(3, endDate);

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        Double totalCredit = resultSet.getDouble("total_credit");
                        Double totalDebit = resultSet.getDouble("total_debit");
                        System.out.println("Total Credit : " + totalCredit + ", Total Debit : " + totalDebit);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void calculateCategoryBalance(UUID accountId, Timestamp startDate, Timestamp endDate, Properties properties) {
        String jdbcUrl = properties.getProperty("jdbcUrl");
        String dbUser = properties.getProperty("dbUser");
        String dbPassword = properties.getProperty("dbPassword");

        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword)) {
            String query = "SELECT * FROM calculate_category_balance(?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setObject(1, accountId);
                statement.setTimestamp(2, startDate);
                statement.setTimestamp(3, endDate);

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        Double phoneMultimedia = resultSet.getDouble("phone_multimedia");
                        Double restaurant = resultSet.getDouble("restaurant");
                        Double salary = resultSet.getDouble("salary");
                        Double loan = resultSet.getDouble("loan");

                        System.out.println("Restaurant: " + restaurant + ", Phone and Multimedia: " + phoneMultimedia +
                                ", Salary: " + salary + ", Loan: " + loan);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
