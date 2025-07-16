package com.pulseband.pulseband.controllers;

import com.pulseband.pulseband.dtos.DriverDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javafx.event.ActionEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.time.Duration;
import java.time.LocalDateTime;

public class DriverCardController {

    @FXML private Label nameLabel;
    @FXML private Label phoneLabel;
    @FXML private Label emailLabel;
    @FXML private Label lastDriverBpm;
    @FXML private Label emergencyContactNameLabel;
    @FXML private Label emergencyContactPhoneLabel;
    @FXML private Label emergencyContactEmailLabel;
    @FXML private Circle statusCircle;
    @FXML private VBox rootCard;

    private DriverDTO driver;

    public void setDriverData(DriverDTO driver) {
        this.driver = driver;

        nameLabel.setText(driver.getFullName());
        phoneLabel.setText(driver.getPhone());
        emailLabel.setText(driver.getEmail());
        emergencyContactNameLabel.setText(driver.getEmergencyContactDTO().getFullName());
        emergencyContactPhoneLabel.setText(driver.getEmergencyContactDTO().getPhone());
        emergencyContactEmailLabel.setText(driver.getEmergencyContactDTO().getEmail());

        String displayText;
        if (driver.getTemporaryStatus() != null) {
            displayText = driver.getTemporaryStatus();
        } else if (driver.getLastBpm() != null) {
            displayText = driver.getLastBpm() + " BPM";
        } else {
            displayText = "---";
        }
        lastDriverBpm.setText(displayText);

        updateStatusCircle();

        if (driver.getTemporaryStatus() != null && driver.getTemporaryStatus().toLowerCase().contains("alert")) {
            if (!rootCard.getStyleClass().contains("alert")) {
                rootCard.getStyleClass().add("alert");
            }
        } else {
            rootCard.getStyleClass().remove("alert");
        }
    }

    private Dashboard2Controller dashboard2Controller;

    private void updateStatusCircle() {
        statusCircle.getStyleClass().removeAll("online", "offline");

        boolean isOnline = false;

        if (driver.getLastBpmTimestamp() != null) {
            Duration duration = Duration.between(driver.getLastBpmTimestamp(), LocalDateTime.now());
            isOnline = duration.getSeconds() <= 10;
        }

        if (isOnline)
            statusCircle.getStyleClass().add("online");
        else
            statusCircle.getStyleClass().add("offline");
    }

    public void setDashboardController(Dashboard2Controller controller) {
        this.dashboard2Controller = controller;
    }

    public void handleEditDriverAction() {
        if (dashboard2Controller != null && driver != null) {
            dashboard2Controller.handleEditDriverAction(driver);
        }
    }

    public void handleDeleteDriverAction() {
        if (dashboard2Controller != null && driver != null) {
            dashboard2Controller.handleDeleteDriverAction(driver);
        }
    }
}