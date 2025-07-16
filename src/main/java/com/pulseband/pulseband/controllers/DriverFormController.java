package com.pulseband.pulseband.controllers;

import com.pulseband.pulseband.dtos.DriverDTO;
import com.pulseband.pulseband.dtos.EmergencyContactDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;
import java.time.Period;

public class DriverFormController {
    @FXML private Button confirmButton;
    @FXML private Label formTitleLabel;

    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private PasswordField passwordField;
    @FXML private DatePicker birthDatePicker;
    @FXML private DatePicker admissionDatePicker;

    @FXML private TextField emergencyNameField;
    @FXML private TextField emergencyPhoneField;
    @FXML private TextField emergencyEmailField;

    private boolean confirmed = false;
    private DriverDTO driver;
    private EmergencyContactDTO contact;

    private boolean isEditMode = false;
    private DriverDTO existingDriver;
    private EmergencyContactDTO existingContact;

    private final ValidationSupport validationSupport = new ValidationSupport();

    @FXML
    public void initialize() {
        validationSupport.registerValidator(fullNameField, Validator.createEmptyValidator("Nome é obrigatório"));
        validationSupport.registerValidator(emailField, Validator.createRegexValidator("Email inválido", "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", Severity.ERROR));
        validationSupport.registerValidator(phoneField, Validator.createRegexValidator("Telefone inválido", "^\\+?\\d{9,15}$", Severity.ERROR));
        validationSupport.registerValidator(birthDatePicker, Validator.createEmptyValidator("Data de nascimento obrigatória"));
        validationSupport.registerValidator(admissionDatePicker, Validator.createEmptyValidator("Data de admissão obrigatória"));
        validationSupport.registerValidator(emergencyNameField, Validator.createEmptyValidator("Nome do contacto de emergência é obrigatório"));
        validationSupport.registerValidator(emergencyPhoneField, Validator.createRegexValidator("Telefone do contacto inválido", "^\\+?\\d{9,15}$", Severity.ERROR));
        validationSupport.registerValidator(emergencyEmailField, Validator.createRegexValidator("Email do contacto inválido", "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", Severity.ERROR));

        validationSupport.registerValidator(passwordField,
                Validator.createPredicateValidator(
                        value -> {
                            if (isEditMode) {
                                return true;
                            }
                            return value != null && !value.toString().isEmpty();
                        },
                        "Password é obrigatória"
                )
        );
    }

    public void setEditMode(DriverDTO driver, EmergencyContactDTO contact) {
        this.isEditMode = true;
        this.existingDriver = driver;
        this.existingContact = contact;

        confirmButton.setText("Edit");
        formTitleLabel.setText("Edit Driver");


        fullNameField.setText(driver.getFullName());
        emailField.setText(driver.getEmail());
        phoneField.setText(driver.getPhone());
        birthDatePicker.setValue(driver.getBirthDate().toLocalDate());
        admissionDatePicker.setValue(driver.getAdmissionDate().toLocalDate());

        if (contact != null) {
            emergencyNameField.setText(contact.getFullName());
            emergencyPhoneField.setText(contact.getPhone());
            emergencyEmailField.setText(contact.getEmail());
        }

        passwordField.setPromptText("(Leave in blank to keep)");
    }

    @FXML
    public void onAddDriver(ActionEvent event) {
        if (validationSupport.isInvalid()) {
            showAlert("Error", "Fill in all spaces to continue.");
            return;
        }

        LocalDateTime birthDate = birthDatePicker.getValue().atStartOfDay();
        int age = Period.between(birthDate.toLocalDate(), LocalDateTime.now().toLocalDate()).getYears();
        if (age < 21 || age > 67) {
            showAlert("Invalid age", "Driver must be between 21 and 67 years old.");
            return;
        }

        if (isEditMode) {
            existingDriver.setFullName(fullNameField.getText().trim());
            existingDriver.setEmail(emailField.getText().trim());
            existingDriver.setPhone(phoneField.getText().trim());
            existingDriver.setBirthDate(birthDate);
            existingDriver.setAdmissionDate(admissionDatePicker.getValue().atStartOfDay());

            if (!passwordField.getText().isBlank()) {
                String hashedPassword = BCrypt.hashpw(passwordField.getText(), BCrypt.gensalt());
                existingDriver.setPasswordHash(hashedPassword);
            }

            existingContact.setFullName(emergencyNameField.getText().trim());
            existingContact.setPhone(emergencyPhoneField.getText().trim());
            existingContact.setEmail(emergencyEmailField.getText().trim());

            driver = existingDriver;
            contact = existingContact;
        } else {
            String hashedPassword = BCrypt.hashpw(passwordField.getText(), BCrypt.gensalt());

            driver = new DriverDTO();
            driver.setFullName(fullNameField.getText().trim());
            driver.setEmail(emailField.getText().trim());
            driver.setPhone(phoneField.getText().trim());
            driver.setPasswordHash(hashedPassword);
            driver.setBirthDate(birthDate);
            driver.setAdmissionDate(admissionDatePicker.getValue().atStartOfDay());

            contact = new EmergencyContactDTO();
            contact.setFullName(emergencyNameField.getText().trim());
            contact.setPhone(emergencyPhoneField.getText().trim());
            contact.setEmail(emergencyEmailField.getText().trim());
        }

        confirmed = true;
        Stage stage = (Stage) fullNameField.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void onCancel(ActionEvent event) {
        confirmed = false;
        Stage stage = (Stage) fullNameField.getScene().getWindow();
        stage.close();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public DriverDTO getDriver() {
        return driver;
    }

    public EmergencyContactDTO getContact() {
        return contact;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}