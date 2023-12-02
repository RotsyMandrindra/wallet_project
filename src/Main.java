import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        String jdbcUrl = "jdbc:postgresql://localhost:5432/wallet_management";
        String user = "mandrindra";
        String password = "Mandrindra$";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, user, password)) {
            System.out.println("successful connection !");
        } catch (SQLException e) {
            System.err.println("Connection error : " + e.getMessage());
        }
    }
}