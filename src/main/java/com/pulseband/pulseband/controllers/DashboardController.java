package com.pulseband.pulseband.controllers;

import com.pulseband.pulseband.daos.DriverDAO;
import com.pulseband.pulseband.daos.UserDAO;
import com.pulseband.pulseband.dtos.*;
import com.pulseband.pulseband.mqtt.MqttClientManager;
import com.pulseband.pulseband.mqtt.MqttConfig;
import com.pulseband.pulseband.mqtt.MqttMessageHandler;
import com.pulseband.pulseband.mqtt.MqttStatusListener;
import com.pulseband.pulseband.services.DriverService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.sql.SQLException;
import java.util.List;

public class DashboardController implements MqttStatusListener, MqttMessageHandler {
    @FXML private Label welcomeLabel;
    @FXML private Label mqttConnectionStatusLabel;
    @FXML private TextArea mqttMessagesReceivedTextArea;

    private UserDTO user;
    private MqttClientManager mqttClient;

    @FXML public void initialize() {
        configureMqttClient();

        try {
            loadDrivers();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadDrivers() throws SQLException {
        DriverService driverService = new DriverService();
        List<DriverDTO> drivers = driverService.getAllDrivers();

        for(DriverDTO driver : drivers) {
            System.out.println(driver.getLastBpm());
        }
    }

    private void configureMqttClient() {
        try {
            MqttConfig config = new MqttConfig();
            mqttClient = new MqttClientManager(
                    config.getMqttBrokerUrl(),
                    config.getMqttClientId(),
                    config.getMqttTopic(),
                    config.getMqttAppDecypheredBpmTopic(),
                    this
            );
            mqttClient.setMessageHandler(this);
            mqttClient.connect();
        } catch (MqttException e) {
            onConnectionStatusChanged("Error connecting to MQTT: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionStatusChanged(String status) {
        Platform.runLater(() -> mqttConnectionStatusLabel.setText(status));
    }

    @Override
    public void onMessageReceived(String topic, String message) {
        String formattedMessage = String.format("[%s] %s", topic, message);
        Platform.runLater(() -> mqttMessagesReceivedTextArea.appendText(formattedMessage + "\n"));
    }

    public void setUser(UserDTO user) {
        this.user = user;
        welcomeLabel.setText("Welcome " + user.getFullName());
    }

    private void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}