package com.pulseband.pulseband.controllers;

import com.pulseband.pulseband.dtos.DriverDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javafx.event.ActionEvent;

public class DriverCardController {

    @FXML private Label nameLabel;
    @FXML private Label phoneLabel;
    @FXML private Label emailLabel;
    @FXML private Label lastDriverBpm;
    @FXML private Label emergencyContactNameLabel;
    @FXML private Label emergencyContactPhoneLabel;
    @FXML private Label emergencyContactEmailLabel;
    @FXML private Button editDriverBtn;

    private DriverDTO driver;

    public void setDriverData(DriverDTO driver) {
        this.driver = driver;

        String displayText;
        if (driver.getTemporaryStatus() != null) {
            displayText = driver.getTemporaryStatus();
        } else if (driver.getLastBpm() != null) {
            displayText = driver.getLastBpm() + " BPM";
        } else {
            displayText = "No BPM";
        }

        nameLabel.setText(driver.getFullName());
        phoneLabel.setText(driver.getPhone());
        emailLabel.setText(driver.getEmail());
        lastDriverBpm.setText(displayText);
        emergencyContactNameLabel.setText(driver.getEmergencyContactDTO().getFullName());
        emergencyContactPhoneLabel.setText(driver.getEmergencyContactDTO().getPhone());
        emergencyContactEmailLabel.setText(driver.getEmergencyContactDTO().getEmail());
    }

    private Dashboard2Controller dashboard2Controller;

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
