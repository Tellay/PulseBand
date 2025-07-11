package com.pulseband.pulseband.controllers;

import com.pulseband.pulseband.dtos.*;
import com.pulseband.pulseband.mqtt.MqttClientManager;
import com.pulseband.pulseband.mqtt.MqttConfig;
import com.pulseband.pulseband.mqtt.MqttMessageHandler;
import com.pulseband.pulseband.mqtt.MqttStatusListener;
import com.pulseband.pulseband.services.DriverService;
import com.pulseband.pulseband.services.VitalService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.postgresql.Driver;

import java.sql.SQLException;
import java.util.List;

public class DashboardController implements MqttStatusListener, MqttMessageHandler {
    @FXML private Label welcomeLabel;
    @FXML private Label mqttConnectionStatusLabel;
    @FXML private TextArea mqttMessagesReceivedTextArea;
    @FXML private TableView<DriverDTO> driversTable;
    @FXML private TableColumn<DriverDTO, String> colName;
    @FXML private TableColumn<DriverDTO, String> colPhone;
    @FXML private TableColumn<DriverDTO, String> colEmail;
    @FXML private TableColumn<DriverDTO, String> colEmergencyContactName;
    @FXML private TableColumn<DriverDTO, String> colEmergencyContactPhone;

    private UserDTO user;
    private final DriverService driverService = new DriverService();
    private final VitalService vitalService = new VitalService();
    private MqttClientManager mqttClient;

    public void initialize() {
        configureMqttClient();
        configureTableColumns();
        loadDrivers();
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

    private void configureTableColumns() {
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEmergencyContactName.setCellValueFactory(cellData -> {
            EmergencyContactDTO ec = cellData.getValue().getEmergencyContact();
            String value = (ec != null) ? ec.getFullName() : "";
            return new javafx.beans.property.SimpleStringProperty(value);
        });
        colEmergencyContactPhone.setCellValueFactory(cellData -> {
            EmergencyContactDTO ec = cellData.getValue().getEmergencyContact();
            String value = (ec != null) ? ec.getPhone() : "";
            return new javafx.beans.property.SimpleStringProperty(value);
        });

        loadDrivers();
    }

    private void loadDrivers() {
        try {
            List<DriverDTO> drivers = driverService.getAllDrivers();
            ObservableList<DriverDTO> obsList = FXCollections.observableArrayList(drivers);
            driversTable.setItems(obsList);
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error loading drivers: " + e.getMessage());
        }
    }

    @Override
    public void onMessageReceived(String topic, String message) {
        String formattedMessage = String.format("[%s] %s", topic, message);
        Platform.runLater(() -> mqttMessagesReceivedTextArea.appendText(formattedMessage + "\n"));

        try {
            int bpm = Integer.parseInt(message.trim());

            int driverId = 2;
            vitalService.addVitalToDriver(driverId, bpm);
        } catch (NumberFormatException e) {
            System.out.println("Mensagem ignorada (não numérica): " + message);
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Erro ao processar mensagem: " + e.getMessage());
        }
    }

    @Override
    public void onConnectionStatusChanged(String status) {
        Platform.runLater(() -> mqttConnectionStatusLabel.setText(status));
    }

//    @Override
//    public void onMessageReceived(String topic, String message) {
//        String formattedMessage = String.format("[%s] %s", topic, message);
//        Platform.runLater(() -> mqttMessagesReceivedTextArea.appendText(formattedMessage + "\n"));
//    }

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