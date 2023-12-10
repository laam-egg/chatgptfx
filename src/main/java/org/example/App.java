package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.FileInputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class App extends Application {
    private static App INSTANCE;

    private static Stage primaryStage;

    private static ConfigurationManager configurationManager;

    public static App getInstance() {
        return INSTANCE;
    }

    @Override
    public void start(Stage stage) {
        INSTANCE = this;

        try {
            configurationManager = new ConfigurationManager();

            stage.setResizable(false);
            stage.setTitle("ChatGPTFX");

            FXMLLoader loader = new FXMLLoader();

            loader.setLocation(getClass().getResource("/views/chat.fxml"));

            VBox root = loader.load();

            Scene scene = new Scene(root);

            // Add the scene to the stage
            stage.setScene(scene);

            // Exit confirmation
            // https://stackoverflow.com/a/52234104/13680015
            scene.getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::handleWindowCloseEvent);

            // Show stage
            stage.show();

            primaryStage = stage;
        } catch (Exception e) {
            App.exitWithFatalError(e);
        }
    }

    private void handleWindowCloseEvent(WindowEvent event) {
        Alert exitConfirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        exitConfirmationAlert.setTitle("Confirmation");
        exitConfirmationAlert.setHeaderText("Exit");
        exitConfirmationAlert.setContentText("Are you sure you want to quit ?");

        Utils.setDefaultButton(exitConfirmationAlert, ButtonType.CANCEL);

        exitConfirmationAlert.showAndWait();
        if (ButtonType.OK != exitConfirmationAlert.getResult()) {
            event.consume();
        }
    }

    public static void exit() {
        Platform.exit();
    }

    public static void exitWithFatalError(Exception e) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Fatal Error");
        errorAlert.setHeaderText("Fatal Error");
        errorAlert.setContentText(e.getMessage());
        errorAlert.showAndWait();

        exit();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static ConfigurationManager getConfigurationManager() {
        return configurationManager;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
