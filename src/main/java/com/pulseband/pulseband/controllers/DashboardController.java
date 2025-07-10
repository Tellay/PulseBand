package com.pulseband.pulseband.controllers;

import com.pulseband.pulseband.dtos.*;
import com.pulseband.pulseband.mqtt.MqttClientManager;
import com.pulseband.pulseband.mqtt.MqttConfig;
import com.pulseband.pulseband.mqtt.MqttMessageHandler;
import com.pulseband.pulseband.mqtt.MqttStatusListener;
import com.pulseband.pulseband.services.DriverService;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DashboardController implements MqttStatusListener, MqttMessageHandler {
    @FXML private Label welcomeLabel;
    @FXML private Label mqttConnectionStatusLabel;
    @FXML private TextArea mqttMessagesReceivedTextArea;
    @FXML private TableView<DriverDTO> driversTable;
    @FXML private TableColumn<DriverDTO, Integer> colId;
    @FXML private TableColumn<DriverDTO, String> colName;
    @FXML private TableColumn<DriverDTO, String> colEmail;
    @FXML private TableColumn<DriverDTO, String> colPhone;
    @FXML public TableColumn<DriverDTO, String> colBPMs;
    @FXML public TableColumn<DriverDTO, String> colAlerts;
    @FXML private TableColumn<DriverDTO, String> colEmergencyContact;

    private UserDTO user;
    private final DriverService driverService = new DriverService();
    private static final Map<Integer, String> SEVERITY_MAP = Map.of(
            1, "Low",
            2, "Medium",
            3, "High"
    );
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
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));

        colEmergencyContact.setCellValueFactory(cell -> {
            EmergencyContactDTO contact = cell.getValue().getEmergencyContact();
            String info = (contact != null)
                    ? String.format("%s | %s | %s", contact.getFullName(), contact.getPhone(), contact.getEmail())
                    : "No contact.";
            return new ReadOnlyStringWrapper(info);
        });

        colBPMs.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getFormattedBpms()));
        colAlerts.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getFormattedAlerts()));
    }


    private void loadDrivers() {
        try {
            List<DriverDTO> drivers = driverService.getAllDriversWithDetails();

            for (DriverDTO driver : drivers) {
                driver.setFormattedBpms(formatBpms(driver.getVitals()));
                driver.setFormattedAlerts(formatAlerts(driver.getAlerts()));
            }

            driversTable.setItems(FXCollections.observableArrayList(drivers));
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Erro ao carregar motoristas: " + e.getMessage());
        }
    }

    private String formatBpms(List<VitalDTO> vitals) {
        if (vitals == null || vitals.isEmpty()) return "No data.";
        return vitals.stream()
                .map(v -> String.valueOf(v.getBpm()))
                .collect(Collectors.joining(" | "));
    }

    private String formatAlerts(List<AlertDTO> alerts) {
        if (alerts == null || alerts.isEmpty()) return "No alerts.";
        return alerts.stream()
                .map(alert -> String.format("%s (%s)", alert.getMessage(), getSeverityText(alert.getSeverityId())))
                .collect(Collectors.joining(" | "));
    }

    private String getSeverityText(int severityId) {
        return SEVERITY_MAP.getOrDefault(severityId, "Unknown");
    }

    @Override
    public void onConnectionStatusChanged(String status) {
        Platform.runLater(() -> mqttConnectionStatusLabel.setText(status));
    }

    @Override
    public void onMessageReceived(String topic, String message) {
        String formattedMessage = String.format("[%s] %s", topic, message);
        Platform.runLater(() -> mqttMessagesReceivedTextArea.appendText(formattedMessage + "\n"));

        try {
            int bpm = Integer.parseInt(message.trim());

            int driverId = 2;
            driverService.addBpmDriverId(driverId, bpm);
            loadDrivers();

            String decryptedTopic = new MqttConfig().getMqttAppDecypheredBpmTopic();
            mqttClient.publish(decryptedTopic, String.valueOf(bpm));

        } catch (NumberFormatException e) {
            System.out.println("Mensagem ignorada (não numérica): " + message);
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Erro ao salvar BPM no banco: " + e.getMessage());
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
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