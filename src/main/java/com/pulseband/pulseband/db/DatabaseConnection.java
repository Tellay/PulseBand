package com.pulseband.pulseband.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private final Connection connection;

    public DatabaseConnection() {
        try {
            DatabaseConfig databaseConfig = new DatabaseConfig();
            this.connection = DriverManager.getConnection(
                    databaseConfig.getDbUrl(),
                    databaseConfig.getDbUser(),
                    databaseConfig.getDbPassword()
            );
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to the database: ", e);
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
