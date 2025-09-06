package _365.telegram.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/Telegram365_DB";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "Soheil005";

    private static Connection connection;
    public static void connect() {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Connected to db");
        } catch (SQLException e) {
            System.out.println("could not connect to db");
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
