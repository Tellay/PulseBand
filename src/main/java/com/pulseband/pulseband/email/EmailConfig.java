package com.pulseband.pulseband.email;

import com.pulseband.pulseband.db.DatabaseConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class EmailConfig {
    public static final Properties properties = new Properties();

    public EmailConfig() {
        try (InputStream input = DatabaseConfig.class.getResourceAsStream("/email.properties")) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Unable to find email.properties: ", e);
        }
    }

    public String getEmailFrom() {
        return properties.getProperty("email.from");
    }

    public String getEmailHost() {
        return properties.getProperty("email.host");
    }

    public String getEmailUsername() {
        return properties.getProperty("email.username");
    }

    public String getEmailPassword() {
        return properties.getProperty("email.password");
    }
}
