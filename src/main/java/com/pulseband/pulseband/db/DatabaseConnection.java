package com.pulseband.pulseband.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public static Connection getConnection() {
        try {
            DatabaseConfig databaseConfig = new DatabaseConfig();
            return DriverManager.getConnection(
                    databaseConfig.getDbUrl(),
                    databaseConfig.getDbUser(),
                    databaseConfig.getDbPassword()
            );
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to the database: ", e);
        }
    }
}
