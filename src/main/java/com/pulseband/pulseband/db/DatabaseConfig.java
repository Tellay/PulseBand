package com.pulseband.pulseband.db;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfig {
    public static final Properties properties = new Properties();

    public DatabaseConfig() {
        try (InputStream input = DatabaseConfig.class.getResourceAsStream("/db.properties")) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Unable to find db.properties: ", e);
        }
    }

    public String getDbUrl() {
        return properties.getProperty("db.url");
    }

    public String getDbUser() {
        return properties.getProperty("db.user");
    }

    public String getDbPassword() {
        return properties.getProperty("db.password");
    }
}
