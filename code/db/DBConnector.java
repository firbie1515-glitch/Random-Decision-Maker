package db;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// 連結到資料庫
public class DBConnector {
    private static final String URL = "jdbc:mysql://localhost:3306/dbproject";
    private static final String USER = "root";
    private static final String PASS = "firbie15";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}



