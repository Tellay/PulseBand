package com.pulseband.pulseband.mqtt;

import com.pulseband.pulseband.db.DatabaseConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MqttConfig {
    public static final Properties properties = new Properties();

    public MqttConfig() {
        try (InputStream input = DatabaseConfig.class.getResourceAsStream("/mqtt.properties")) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Unable to find mqtt.properties: ", e);
        }
    }

    public boolean shouldDecrypt(String topic) {
        return topic.equals(getMqttBpmTopic()) || topic.equals(getMqttAlertTopic());
    }

    public String getMqttBrokerUrl() {
        return properties.getProperty("mqtt.brokerUrl");
    }

    public String getMqttClientId() {
        return properties.getProperty("mqtt.clientId");
    }

    public String getMqttBpmTopic() {
        return properties.getProperty("mqtt.bpmTopic");
    }

    public String getMqttAlertTopic() {
        return properties.getProperty("mqtt.alertTopic");
    }

    public String getMqttDecryptedTopic() {
        return properties.getProperty("mqtt.decryptedTopic");
    }
}