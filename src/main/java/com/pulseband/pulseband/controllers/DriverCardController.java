package com.pulseband.pulseband.controllers;

import com.pulseband.pulseband.dtos.DriverDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DriverCardController {
    @FXML
    private Label nameLabel;
    @FXML
    private Label phoneLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label lastDriverBpm;
    @FXML
    private Label emergencyContactNameLabel;
    @FXML
    private Label emergencyContactPhoneLabel;
    @FXML
    private Label emergencyContactEmailLabel;

    public void setDriverData(DriverDTO driver) {
        Integer lastBpm = driver.getLastBpm();

        nameLabel.setText(driver.getFullName());
        phoneLabel.setText(driver.getPhone());
        emailLabel.setText(driver.getEmail());
        lastDriverBpm.setText(lastBpm == null ? "No BPM" : lastBpm + " BPM");
        emergencyContactNameLabel.setText(driver.getEmergencyContactDTO().getFullName());
        emergencyContactPhoneLabel.setText(driver.getEmergencyContactDTO().getPhone());
        emergencyContactEmailLabel.setText(driver.getEmergencyContactDTO().getEmail());
    }
}
