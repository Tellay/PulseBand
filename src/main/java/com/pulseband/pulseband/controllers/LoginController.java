package com.pulseband.pulseband.controllers;

import com.pulseband.pulseband.auth.AuthService;
import com.pulseband.pulseband.auth.exceptions.InvalidCredentialsException;
import com.pulseband.pulseband.auth.exceptions.UnauthorizedUserTypeException;
import com.pulseband.pulseband.dtos.UserDTO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.io.IOException;

public class LoginController {
    @FXML private TextField emailInput;
    @FXML private PasswordField passwordInput;
    @FXML private Button loginBtn;
    @FXML private Label feedbackLabel;

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        setupValidation();
    }

    private void setupValidation() {
        ValidationSupport validationSupport = new ValidationSupport();

        validationSupport.registerValidator(emailInput,
                Validator.createEmptyValidator("Email is required."));
        validationSupport.registerValidator(emailInput,
                Validator.createPredicateValidator(
                        email -> email != null && email.toString().matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+"),
                        "Email is invalid."));

        validationSupport.registerValidator(passwordInput,
                Validator.createEmptyValidator("Password is required."));

        validationSupport.validationResultProperty().addListener((obs, oldResult, newResult) -> {
            loginBtn.setDisable(!newResult.getErrors().isEmpty());
        });

        loginBtn.setDisable(true);
    }

    @FXML
    public void handleLoginAction() {
        String email = emailInput.getText();
        String password = passwordInput.getText();

        try {
            UserDTO authenticatedUser = authService.login(email, password);

            feedbackLabel.setText("Login successful. Welcome, " + authenticatedUser.getFullName() + "!");
            openDashboard(authenticatedUser);

        } catch (InvalidCredentialsException | UnauthorizedUserTypeException authEx) {
            feedbackLabel.setText(authEx.getMessage());

        } catch (Exception ex) {
            feedbackLabel.setText("Unexpected error.");
            ex.printStackTrace();
        }
    }

    private void openDashboard(UserDTO user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/DashboardView.fxml"));
            Parent root = loader.load();

            DashboardController dashboardController = loader.getController();
            dashboardController.setUser(user);

            Scene scene = loginBtn.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            feedbackLabel.setText("Error opening dashboard.");
            e.printStackTrace();
        }
    }
}
