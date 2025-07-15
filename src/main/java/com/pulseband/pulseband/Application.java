package com.pulseband.pulseband;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/views/LoginView.fxml"));
        Parent loginRoot = loginLoader.load();
        Scene loginScene = new Scene(loginRoot);

        FXMLLoader Dashboard2Loader = new FXMLLoader(getClass().getResource("/views/Dashboard2View.fxml"));
        Parent dashboard2Root = Dashboard2Loader.load();
        Scene dashbord2 = new Scene(dashboard2Root);

        stage.setTitle("PulseBand");
        stage.setScene(dashbord2);
        stage.setMaximized(true);
        stage.show();

        stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch();
    }
}