import java.io.InputStream;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        Properties prop = new Properties();

        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }
            prop.load(input);

            String jdbcUrl = prop.getProperty("jdbcUrl");
            String user = prop.getProperty("dbUser");
            String password = prop.getProperty("dbPassword");
            System.out.println("Connection successfully");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}