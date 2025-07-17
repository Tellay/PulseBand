package com.pulseband.pulseband.controllers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pulseband.pulseband.dtos.DriverDTO;
import com.pulseband.pulseband.dtos.EmergencyContactDTO;
import com.pulseband.pulseband.dtos.UserDTO;
import com.pulseband.pulseband.mqtt.MqttClientManager;
import com.pulseband.pulseband.mqtt.MqttConfig;
import com.pulseband.pulseband.mqtt.MqttMessageHandler;
import com.pulseband.pulseband.mqtt.MqttStatusListener;
import com.pulseband.pulseband.services.DriverService;
import com.pulseband.pulseband.services.EmailService;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.pulseband.pulseband.utils.TextUtils.normalize;

public class Dashboard2Controller {
    private static final int GRID_COLUMNS = 3;

    @FXML
    private HBox mqttStatusPill;
    @FXML
    private Label mqttStatusLabel;
    @FXML
    private Button logOutBtn;
    @FXML
    private Label totalDriversLabel;
    @FXML
    private Label activeDriversLabel;
    @FXML
    private Label averageBpmLabel;
    @FXML
    private Label remainingTimeLabel;
    @FXML
    private TextField searchDriversInput;
    @FXML
    private GridPane driverGridPane;

    private final DriverService driverService = new DriverService();
    private List<DriverDTO> allDrivers;

    MqttConfig mqttConfig = new MqttConfig();
    private MqttClientManager mqttClientManager;

    private final DriverDTO driver = new DriverDTO();
    private final EmergencyContactDTO emergencyContact = new EmergencyContactDTO();

    @FXML
    public void initialize() {
        setupMqttClient();
        loadAllDrivers();
        loadStatistics();
        renderDriverCards();
        setupSearchFilter();
        startAlertBorderRefresh();
    }

    private void setupMqttClient() {
        try {
            mqttClientManager = new MqttClientManager(
                    mqttConfig.getMqttBrokerUrl(),
                    mqttConfig.getMqttClientId(),
                    mqttConfig.getMqttBpmTopic(),
                    mqttConfig.getMqttAlertTopic(),
                    mqttConfig.getMqttDecryptedBpmTopic(),
                    mqttConfig.getMqttDecryptedAlertTopic(),
                    getMqttStatusListener()
            );
            mqttClientManager.setMessageHandler(getMessageHandler());
            mqttClientManager.connectAsync();
        } catch (MqttException e) {
            updateMqttStatus(false, e.getMessage());
            e.printStackTrace();
        }
    }

    private MqttStatusListener getMqttStatusListener() {
        return (connected, message) -> Platform.runLater(() -> updateMqttStatus(connected, message));
    }

    private MqttMessageHandler getMessageHandler() {
        return (topic, message) -> {
            System.out.println("MQTT message: " + message);
            handleIncomingMessage(message);
        };
    }

    private void handleIncomingMessage(String message) {
        try {
            EmailService emailService = new EmailService();

            JsonObject json = JsonParser.parseString(message).getAsJsonObject();
            if (!json.has("id")) return;

            int id = json.get("id").getAsInt();
            Optional<DriverDTO> optDriver = allDrivers.stream()
                    .filter(driver -> driver.getId() == id)
                    .findFirst();

            optDriver.ifPresent(driver -> {
                driver.setLastBpmTimestamp(LocalDateTime.now());

                if (json.has("status")) {
                    String msg = json.get("status").getAsString();
                    driver.setTemporaryStatus(msg);
                    driver.setLastBpm(null);
                    System.out.println("⚠ Temporary state: " + driver.getFullName() + " | " + msg);
                    try {
                        mqttClientManager.publish(mqttConfig.getMqttDecryptedBpmTopic(), msg);
                    } catch (MqttException e) {
                        throw new RuntimeException(e);
                    }
                }

                if (json.has("bpm")) {
                    int bpm = json.get("bpm").getAsInt();
                    driver.setLastBpmTimestamp(LocalDateTime.now());
                    try {
                        driverService.insertDriverBpm(id, bpm);
                        Integer lastBpm = driverService.getLastBpm(id);
                        driver.setLastBpm(lastBpm);

                        JsonObject outgoingJson = new JsonObject();
                        outgoingJson.addProperty("id", id);
                        outgoingJson.addProperty("bpm", lastBpm);

                        mqttClientManager.publish(mqttConfig.getMqttDecryptedBpmTopic(), String.valueOf(bpm));

                    } catch (SQLException | MqttException e) {
                        e.printStackTrace();
                    }

                    driver.setTemporaryStatus(null);
                }

                if (json.has("alert")) {
                    String alert = json.get("alert").getAsString();
                    driver.setLastAlertTimestamp(LocalDateTime.now());
                    try {
                        emailService.sendEmail(driver.getEmergencyContactDTO().getEmail(), "\uD83D\uDEA8 Alert!", "The driver " + driver.getFullName() + " is suffering from " + alert.toLowerCase());
                        driverService.insertDriverAlert(id, alert);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    System.out.println("⚠ ALERT for " + driver.getFullName() + ": " + alert);

                    Platform.runLater(() -> {
                        for (Node card : driverGridPane.getChildren()) {
                            if (card.getUserData() instanceof DriverCardController controller
                                    && controller.driver.getId() == driver.getId()) {
                                controller.updateAlertBorder();
                            }
                        }
                    });

                    try {
                        mqttClientManager.publish(mqttConfig.getMqttDecryptedAlertTopic(), alert);
                    } catch (MqttException e) {
                        throw new RuntimeException(e);
                    }
                }

            });

            Platform.runLater(() -> {
                String currentFilter = searchDriversInput.getText();
                if (currentFilter != null && !currentFilter.isBlank()) {
                    filterDrivers(currentFilter);
                } else if (allDrivers != null) {
                    showDrivers(allDrivers);
                }
            });

        } catch (Exception e) {
            System.err.println("Error processing JSON from MQTT: " + e.getMessage());
        }
    }

    private void updateMqttStatus(boolean connected, String message) {
        mqttStatusLabel.setText(message);
        mqttStatusPill.getStyleClass().removeAll("success", "error");
        mqttStatusPill.getStyleClass().add(connected ? "success" : "error");
    }

    private void loadAllDrivers() {
        try {
            allDrivers = driverService.getAllDrivers();
        } catch (SQLException e) {
            System.err.println("Error laoding drivers: " + e.getMessage());
        }
    }

    private void renderDriverCards() {
        if (allDrivers != null && !allDrivers.isEmpty()) {
            showDrivers(allDrivers);
        }
    }

    private void loadStatistics() {
        try {
            totalDriversLabel.setText(String.valueOf(driverService.getTotalDrivers()));
            activeDriversLabel.setText(String.valueOf(driverService.getActiveDrivers()));
            averageBpmLabel.setText(String.valueOf(driverService.getAverageBpm()));
        } catch (SQLException e) {
            totalDriversLabel.setText("Error");
            activeDriversLabel.setText("Error");
            averageBpmLabel.setText("Error");
        }
    }

    private void showDrivers(List<DriverDTO> drivers) {
        driverGridPane.getChildren().clear();
        int column = 0;
        int row = 0;

        for (DriverDTO driver : drivers) {
            try {
                FXMLLoader driverCardLoader = new FXMLLoader(getClass().getResource("/views/DriverCardView.fxml"));
                Node card = driverCardLoader.load();
                DriverCardController cardController = driverCardLoader.getController();
                card.setUserData(cardController);

                cardController.setDashboardController(this);
                cardController.setDriverData(driver);

                driverGridPane.add(card, column, row);

                if (++column == GRID_COLUMNS) {
                    column = 0;
                    row++;
                }
            } catch (IOException e) {
                System.err.println("error trying to create driver card: " + e.getMessage());
            }
        }
    }

    private void setupSearchFilter() {
        searchDriversInput.textProperty().addListener((obs, oldVal, newVal) -> filterDrivers(newVal));
    }

    private void filterDrivers(String filterText) {
        if (allDrivers == null) return;

        if (filterText == null || filterText.isBlank()) {
            showDrivers(allDrivers);
            return;
        }

        String normalized = normalize(filterText);
        List<DriverDTO> filtered = allDrivers.stream()
                .filter(driver -> matchesFilter(driver, normalized))
                .collect(Collectors.toList());
        showDrivers(filtered);
    }

    private boolean matchesFilter(DriverDTO driver, String normalized) {
        return containsIgnoreCase(driver.getFullName(), normalized) ||
                containsIgnoreCase(driver.getPhone(), normalized) ||
                containsIgnoreCase(driver.getEmail(), normalized) ||
                (driver.getEmergencyContactDTO() != null && (
                        containsIgnoreCase(driver.getEmergencyContactDTO().getFullName(), normalized) ||
                                containsIgnoreCase(driver.getEmergencyContactDTO().getPhone(), normalized) ||
                                containsIgnoreCase(driver.getEmergencyContactDTO().getEmail(), normalized)));
    }

    private boolean containsIgnoreCase(String source, String filter) {
        return source != null && normalize(source).contains(filter);
    }

    private void refreshDashboard() {
        System.out.println("Updating dashboard...");
        loadAllDrivers();
        loadStatistics();
        String currentFilter = searchDriversInput.getText();
        if (currentFilter != null && !currentFilter.isBlank()) {
            filterDrivers(currentFilter);
        } else if (allDrivers != null) {
            showDrivers(allDrivers);
        }
        System.out.println("Dashboard updated!");
    }



    public void handleAddDriverAction(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/DriverFormView.fxml"));
            Parent root = loader.load();

            DriverFormController controller = loader.getController();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add Driver");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            if (controller.isConfirmed()) {
                driverService.addDriver(controller.getDriver(), controller.getContact());
                refreshDashboard();
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleEditDriverAction(DriverDTO driver) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/DriverFormView.fxml"));
            Parent root = loader.load();

            DriverFormController controller = loader.getController();
            controller.setEditMode(driver, driver.getEmergencyContactDTO());

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Edit Driver");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            if (controller.isConfirmed()) {
                driverService.editDriver(controller.getDriver(), controller.getContact());
                refreshDashboard();
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleDeleteDriverAction(DriverDTO driver) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Driver");
        alert.setHeaderText("Are you sure you want to delete this driver?");
        alert.setContentText(driver.getFullName());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                driverService.deleteDriver(driver.getId());
                refreshDashboard();
            } catch (Exception e) {
                e.printStackTrace();
                showError("Error", "Could not delete driver.");
            }
        }
    }

    @FXML
    public void handleLogOutAction(ActionEvent event) {
        openLogin();
    }

    private void openLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LoginView.fxml"));
            Parent root = loader.load();

            Scene scene = logOutBtn.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startAlertBorderRefresh() {
        Timeline alertRefreshTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    Platform.runLater(() -> {
                        for (Node card : driverGridPane.getChildren()) {
                            if (card.getUserData() instanceof DriverCardController controller) {
                                controller.updateAlertBorder();
                            }
                        }
                    });
                })
        );
        alertRefreshTimeline.setCycleCount(Animation.INDEFINITE);
        alertRefreshTimeline.play();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}