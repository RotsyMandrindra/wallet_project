import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        try {
            Properties properties = loadProperties();

            String jdbcUrl = properties.getProperty("jdbcUrl");
            String dbUser = properties.getProperty("dbUser");
            String dbPassword = properties.getProperty("dbPassword");

            try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword)) {

                displayAccounts(connection);

                displayTransactions(connection);

                displayCurrencies(connection);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private static Properties loadProperties() throws IOException {
        Properties properties = new Properties();
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return properties;
            }
            properties.load(input);
        }
        return properties;
    }

    private static void displayTransactions(Connection connection) throws SQLException {
        System.out.println("Transactions:");
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM \"transaction\"");
            while (resultSet.next()) {
                int transactionId = resultSet.getInt("transaction_id");
                int accountId = resultSet.getInt("account_id");
                Double amount = resultSet.getDouble("amount");
                Timestamp transactionDate = resultSet.getTimestamp("transaction_date");
                String description = resultSet.getString("description");
                String transactionType = resultSet.getString("transaction_type");
                String label = resultSet.getString("label");

                System.out.println("Transaction ID: " + transactionId +
                        ", Account ID: " + accountId +
                        ", Amount: " + amount +
                        ", Transaction Date: " + transactionDate +
                        ", Description: " + description +
                        ", Transaction type: " + transactionType +
                        ", Label: " + label);
            }
        }
    }

    private static void displayAccounts(Connection connection) throws SQLException {
        System.out.println("Accounts:");
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM account");
            while (resultSet.next()) {
                int accountId = resultSet.getInt("account_id");
                String accountName = resultSet.getString("account_name");
                Double balance = resultSet.getDouble("balance");
                Timestamp lastUpdateDate = resultSet.getTimestamp("last_update_date");
                String password = resultSet.getString("password");
                String accountType = resultSet.getString("account_type");
                int currencyId = resultSet.getInt("currency_id");

                System.out.println("Account ID: " + accountId +
                        ", Account Name: " + accountName +
                        ", Balance: " + balance +
                        ", Last Update Date: " + lastUpdateDate +
                        ", Password: " + password +
                        ", Account Type: " + accountType +
                        ", Currency ID: " + currencyId);
            }
        }
    }
    private static void displayCurrencies(Connection connection) throws SQLException {
        System.out.println("Currencies:");
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM currency");
            while (resultSet.next()) {
                int currencyId = resultSet.getInt("currency_id");
                String currencyName = resultSet.getString("currency_name");
                String currencyCode = resultSet.getString("currency_code");

                System.out.println("Currency ID: " + currencyId +
                        ", Currency Name: " + currencyName +
                        ", Currency Code: " + currencyCode);
            }
        }
    }
}