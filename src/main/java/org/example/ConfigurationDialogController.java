package org.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class ConfigurationDialogController {
    @FXML
    private Button applyButton;

    @FXML
    private Button cancelButton;

    @FXML
    private TextField chatGPTAPIKeyInput;

    private static Stage currentStage;

    @FXML
    private void initialize() {
        chatGPTAPIKeyInput.setText(
                App.getConfigurationManager().getChatGPTAPIKey()
        );
    }

    @FXML
    private void onApplyButtonClicked() {
        App.getConfigurationManager().setChatGPTAPIKey(
                chatGPTAPIKeyInput.getText()
        );
        Utils.sendCloseWindowEvent(currentStage);
    }

    @FXML
    private void onCancelButtonClicked() {
        Utils.sendCloseWindowEvent(currentStage);
    }

    public static void showDialog() {
        currentStage = Utils.createDialogStage("/views/configuration-dialog.fxml", "Configuration Manager");
        currentStage.showAndWait();
    }
}
