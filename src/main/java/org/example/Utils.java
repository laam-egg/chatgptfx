package org.example;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class Utils {
    public static void setDefaultButton(Alert alert, ButtonType defaultButtonType) {
        // https://stackoverflow.com/a/29789698/13680015
        DialogPane pane = alert.getDialogPane();
        for (ButtonType t : alert.getButtonTypes()) {
            ((Button) pane.lookupButton(t)).setDefaultButton(t == defaultButtonType);
        }
    }

    public static void sendCloseWindowEvent(Stage stage) {
        // https://stackoverflow.com/a/52234104/13680015
        Window window = stage.getScene().getWindow();
        window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    public static Stage createDialogStage(String resourcePath, String title) {
        Stage stage = new Stage();
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource(resourcePath));

            VBox root = loader.load();

            Scene scene = new Scene(root);

            stage.setScene(scene);

            stage.setResizable(false);
            stage.setTitle(title);
        } catch (Exception e) {
            App.exitWithFatalError(e);
        }

        return stage;
    }

    public static String loadResource(final String resourcePath) {
        try {
            URL fileURL = App.class.getResource(resourcePath);
            if (fileURL == null) {
                throw new Exception("Could not load resource: " + resourcePath);
            }
            return FileUtils.readFileToString(
                    new File(URLDecoder.decode(fileURL.getPath(), StandardCharsets.UTF_8)),
                    StandardCharsets.UTF_8
            );
        } catch (Exception e) {
            App.exitWithFatalError(e);
            return "";
        }
    }
}
