package com.pulseband.pulseband.controllers;

import com.pulseband.pulseband.dtos.DriverDTO;
import com.pulseband.pulseband.services.DriverService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static com.pulseband.pulseband.utils.TextUtils.normalize;

public class Dashboard2Controller {
    private static final int SECONDS_TO_REFRESH = 20;
    private static final int GRID_COLUMNS = 3;

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

    @FXML
    public void initialize() {
        // Carrega os dados iniciais diretamente, sem chamadas recursivas
        loadAllDrivers();
        loadStatistics();

        // Só exibe se a lista foi carregada com sucesso
        if (allDrivers != null && !allDrivers.isEmpty()) {
            showDrivers(allDrivers);
        }

        setupSearchFilter();
        setupAutoRefresh();
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
        // Verifica campos do driver principal
        if (containsIgnoreCase(driver.getFullName(), normalizedFilter) ||
                containsIgnoreCase(driver.getPhone(), normalizedFilter) ||
                containsIgnoreCase(driver.getEmail(), normalizedFilter)) {
            return true;
        }

        // Verifica campos do contacto de emergência
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
            // Recarrega apenas os dados necessários, sem recriar toda a estrutura
            refreshDashboard();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        System.out.println("Auto-refresh configured for " + SECONDS_TO_REFRESH + " seconds");
    }

    private void refreshDashboard() {
        System.out.println("Refreshing dashboard...");

        // Recarrega os dados
        loadAllDrivers();
        loadStatistics();

        // Mantém o filtro atual se existir
        String currentFilter = searchDriversInput.getText();
        if (currentFilter != null && !currentFilter.isBlank()) {
            filterDrivers(currentFilter);
        } else if (allDrivers != null) {
            showDrivers(allDrivers);
        }

        System.out.println("Dashboard refreshed!");
    }
}