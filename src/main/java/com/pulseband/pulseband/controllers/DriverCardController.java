package com.pulseband.pulseband.controllers;

import com.pulseband.pulseband.dtos.DriverDTO;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import javafx.util.Duration;
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
    @FXML private String displayText;

    public DriverDTO driver;
    private Timeline statusChecker;

    public void setDriverData(DriverDTO driver) {
        this.driver = driver;

        nameLabel.setText(driver.getFullName());
        phoneLabel.setText(driver.getPhone());
        emailLabel.setText(driver.getEmail());
        emergencyContactNameLabel.setText(driver.getEmergencyContactDTO().getFullName());
        emergencyContactPhoneLabel.setText(driver.getEmergencyContactDTO().getPhone());
        emergencyContactEmailLabel.setText(driver.getEmergencyContactDTO().getEmail());

        if (driver.getTemporaryStatus() != null) {
            displayText = driver.getTemporaryStatus();
        } else if (driver.getLastBpm() != null) {
            displayText = driver.getLastBpm() + " BPM";
        } else {
            displayText = "---";
        }
        lastDriverBpm.setText(displayText);

        updateStatusCircle();
        updateAlertBorder();
        startStatusMonitor();

        if (driver.getTemporaryStatus() != null && driver.getTemporaryStatus().toLowerCase().contains("alert")) {
            if (!rootCard.getStyleClass().contains("alert")) {
                rootCard.getStyleClass().add("alert");
            }
        } else {
            rootCard.getStyleClass().remove("alert");
        }
    }

    private DashboardController dashboard2Controller;

    private void updateStatusCircle() {
        statusCircle.getStyleClass().removeAll("online", "offline");

        boolean isOnline = false;

        if (driver.getLastBpmTimestamp() != null) {
            java.time.Duration duration = java.time.Duration.between(driver.getLastBpmTimestamp(), LocalDateTime.now());
            isOnline = duration.getSeconds() <= 10;
        }

        System.out.println("Atualizar statusCircle para " + (isOnline ? "online" : "offline"));

        if (isOnline) {
            statusCircle.getStyleClass().add("online");

            if (driver.getTemporaryStatus() != null) {
                lastDriverBpm.setText(driver.getTemporaryStatus());
            } else if (driver.getLastBpm() != null) {
                lastDriverBpm.setText(driver.getLastBpm() + " BPM");
            } else {
                lastDriverBpm.setText("---");
            }

        } else {
            statusCircle.getStyleClass().add("offline");
            lastDriverBpm.setText("---");
        }
    }

    private void startStatusMonitor() {
        System.out.println("⚠ A aplicar estilo de alerta temporário");

        rootCard.getStyleClass().remove("alert");
        rootCard.getStyleClass().add("alert");

        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(event -> {
            System.out.println("⏳ A remover estilo de alerta");
            rootCard.getStyleClass().remove("alert");
        });
        pause.playFromStart();
    }

    public void updateAlertBorder() {
        boolean alert = false;

        if (driver.getLastAlertTimestamp() != null) {
            java.time.Duration duration = java.time.Duration.between(driver.getLastAlertTimestamp(), LocalDateTime.now());
            alert = duration.getSeconds() <= 10;
        }

        boolean currentlyAlerted = rootCard.getStyleClass().contains("alert");

        if (alert && !currentlyAlerted) {
            rootCard.getStyleClass().add("alert");
        } else if (!alert && currentlyAlerted) {
            rootCard.getStyleClass().remove("alert");
        }
    }

    public void setDashboardController(DashboardController controller) {
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