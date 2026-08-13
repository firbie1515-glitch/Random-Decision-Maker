package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Provides database connections without storing credentials in source code.
 *
 * Configure the connection with environment variables:
 * DB_URL, DB_USER, DB_PASSWORD
 */
public class DBConnector {
    private static final String URL = getEnvOrDefault(
            "DB_URL", "jdbc:mysql://localhost:3306/dbproject");
    private static final String USER = getEnvOrDefault("DB_USER", "root");
    private static final String PASS = System.getenv("DB_PASSWORD");

    private static String getEnvOrDefault(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? defaultValue : value;
    }

    public static Connection getConnection() throws SQLException {
        if (PASS == null || PASS.isBlank()) {
            throw new SQLException(
                    "Database password is not configured. Set the DB_PASSWORD environment variable before starting the application.");
        }
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
