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

    public String getEmailSmtpAuth() {
        return properties.getProperty("mail.smtp.auth");
    }

    public String getEmailSmtpStartTlsEnable() {
        return properties.getProperty("mail.starttls.enable");
    }

    public String getEmailSmtpHost() { return properties.getProperty("mail.smtp.host"); }

    public String getEmailSmtpPort() {
        return properties.getProperty("mail.smtp.port");
    }

    public String getEmailUser() { return properties.getProperty("mail.username"); }

    public String getEmailPassword() { return properties.getProperty("mail.password"); }
}
