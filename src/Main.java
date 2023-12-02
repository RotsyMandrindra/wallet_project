import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        String jdbcUrl = "jdbc:jdbc://localhost:5432/wallet_management";
        String user = "rotsy";
        String password = "rotsy123";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, user, password)) {
            System.out.println("successful connection !");
        } catch (SQLException e) {
            System.err.println("Connection error : " + e.getMessage());
        }
    }
}