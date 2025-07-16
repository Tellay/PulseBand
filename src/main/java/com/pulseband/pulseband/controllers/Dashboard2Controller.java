package com.pulseband.pulseband.controllers;

import com.pulseband.pulseband.dtos.DriverDTO;
import com.pulseband.pulseband.mqtt.MqttClientManager;
import com.pulseband.pulseband.mqtt.MqttConfig;
import com.pulseband.pulseband.mqtt.MqttMessageHandler;
import com.pulseband.pulseband.mqtt.MqttStatusListener;
import com.pulseband.pulseband.services.DriverService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static com.pulseband.pulseband.utils.TextUtils.normalize;

public class Dashboard2Controller {
    private static final int SECONDS_TO_REFRESH = 20;
    private static final int GRID_COLUMNS = 3;

    @FXML
    private HBox mqttStatusPill;
    @FXML
    private Circle mqttStatusCircle;
    @FXML
    private Label mqttStatusLabel;
    @FXML
    private Label totalDriversLabel;
    @FXML
    private Label activeDriversLabel;
    @FXML
    private Label averageBpmLabel;
    @FXML
    private TextField searchDriversInput;
    @FXML
    private GridPane driverGridPane;

    private final DriverService driverService = new DriverService();
    private List<DriverDTO> allDrivers;
    private MqttClientManager mqttClientManager;

    @FXML
    public void initialize() {
        setupMqttClient();
        loadAllDrivers();
        loadStatistics();

        if (allDrivers != null && !allDrivers.isEmpty()) {
            showDrivers(allDrivers);
        }

        setupSearchFilter();
        setupAutoRefresh();
    }

    public void setupMqttClient() {
        try {
            MqttConfig mqttConfig = new MqttConfig();
            mqttClientManager = new MqttClientManager(
                    mqttConfig.getMqttBrokerUrl(),
                    mqttConfig.getMqttClientId(),
                    mqttConfig.getMqttTopic(),
                    mqttConfig.getMqttAppDecypheredBpmTopic(),
                    getMqttStatusListener()
            );

            mqttClientManager.setMessageHandler(getMessageHandler());
            mqttClientManager.connectAsync();

        } catch (MqttException e) {
            handleMqttSetupError(e);
        }
    }

    private MqttStatusListener getMqttStatusListener() {
        return (connected, message) -> Platform.runLater(() -> updateMqttStatus(connected, message));
    }

    private MqttMessageHandler getMessageHandler() {
        return (topic, message) -> {
            System.out.println("MQTT message: " + message);
        };
    }

    private void updateMqttStatus(boolean connected, String message) {
        mqttStatusLabel.setText(message);
        mqttStatusPill.getStyleClass().removeAll("success", "error");
        mqttStatusPill.getStyleClass().add(connected ? "success" : "error");
    }

    private void handleMqttSetupError(MqttException e) {
        updateMqttStatus(false, e.getMessage());
        e.printStackTrace();
    }

    private void loadAllDrivers() {
        try {
            allDrivers = driverService.getAllDrivers();
        } catch (SQLException e) {
            System.err.println("Error loading drivers: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadStatistics() {
        loadTotalDrivers();
        loadActiveDrivers();
        loadAverageBpm();
    }

    private void loadTotalDrivers() {
        try {
            int totalDrivers = driverService.getTotalDrivers();
            totalDriversLabel.setText(String.valueOf(totalDrivers));
        } catch (SQLException e) {
            totalDriversLabel.setText("Error");
            System.err.println("Error loading total drivers: " + e.getMessage());
        }
    }

    private void loadActiveDrivers() {
        try {
            int activeDrivers = driverService.getActiveDrivers();
            activeDriversLabel.setText(String.valueOf(activeDrivers));
        } catch (SQLException e) {
            activeDriversLabel.setText("Error");
            System.err.println("Error loading active drivers: " + e.getMessage());
        }
    }

    private void loadAverageBpm() {
        try {
            int averageBpm = driverService.getAverageBpm();
            averageBpmLabel.setText(String.valueOf(averageBpm));
        } catch (SQLException e) {
            averageBpmLabel.setText("Error");
            System.err.println("Error loading average BPM: " + e.getMessage());
        }
    }

    private void showDrivers(List<DriverDTO> drivers) {
        driverGridPane.getChildren().clear();

        int column = 0;
        int row = 0;

        for (DriverDTO driver : drivers) {
            try {
                FXMLLoader driverCardLoader = new FXMLLoader(getClass().getResource("/views/DriverCard.fxml"));
                Node driverCard = driverCardLoader.load();

                DriverCardController driverCardController = driverCardLoader.getController();
                driverCardController.setDriverData(driver);

                driverGridPane.add(driverCard, column, row);

                column++;
                if (column == GRID_COLUMNS) {
                    column = 0;
                    row++;
                }
            } catch (IOException e) {
                System.err.println("Error creating driver card: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void setupSearchFilter() {
        searchDriversInput.textProperty().addListener((observable, oldValue, newValue) -> {
            filterDrivers(newValue);
        });
    }

    private void filterDrivers(String filterText) {
        if (allDrivers == null) {
            return;
        }

        if (filterText == null || filterText.trim().isEmpty()) {
            showDrivers(allDrivers);
            return;
        }

        String normalizedFilter = normalize(filterText);
        List<DriverDTO> filteredDrivers = allDrivers.stream()
                .filter(driver -> matchesFilter(driver, normalizedFilter))
                .collect(Collectors.toList());

        showDrivers(filteredDrivers);
    }

    private boolean matchesFilter(DriverDTO driver, String normalizedFilter) {
        if (containsIgnoreCase(driver.getFullName(), normalizedFilter) ||
                containsIgnoreCase(driver.getPhone(), normalizedFilter) ||
                containsIgnoreCase(driver.getEmail(), normalizedFilter)) {
            return true;
        }

        var emergencyContact = driver.getEmergencyContactDTO();
        if (emergencyContact != null) {
            return containsIgnoreCase(emergencyContact.getFullName(), normalizedFilter) ||
                    containsIgnoreCase(emergencyContact.getPhone(), normalizedFilter) ||
                    containsIgnoreCase(emergencyContact.getEmail(), normalizedFilter);
        }

        return false;
    }

    private boolean containsIgnoreCase(String source, String filter) {
        return source != null && normalize(source).contains(filter);
    }

    private void setupAutoRefresh() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(SECONDS_TO_REFRESH), event -> {
            refreshDashboard();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        System.out.println("Auto-refresh configured for " + SECONDS_TO_REFRESH + " seconds");
    }

    private void refreshDashboard() {
        System.out.println("Refreshing dashboard...");

        loadAllDrivers();
        loadStatistics();

        String currentFilter = searchDriversInput.getText();
        if (currentFilter != null && !currentFilter.isBlank()) {
            filterDrivers(currentFilter);
        } else if (allDrivers != null) {
            showDrivers(allDrivers);
        }

        System.out.println("Dashboard refreshed!");
    }
}