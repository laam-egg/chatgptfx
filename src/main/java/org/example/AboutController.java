package org.example;

import javafx.fxml.FXML;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.net.URL;

public class AboutController {
    @FXML
    private WebView informationView;

    private static Stage currentStage;

    @FXML
    private void initialize() {
        try {
            URL aboutCSSLocation = getClass().getResource("/css/about.css");
            if (aboutCSSLocation == null) {
                throw new Exception("Could not retrieve resource path for /css/about.css");
            }
            informationView.getEngine().setUserStyleSheetLocation(aboutCSSLocation.toString());
            informationView.getEngine().loadContent(
                    Utils.loadResource("/html/about.html")
            );
        } catch (Exception e) {
            App.exitWithFatalError(e);
        }
    }

    @FXML
    private void viewSourceCodeOnGitHub() {
        App.getInstance().getHostServices().showDocument("https://github.com/laam-egg/chatgptfx");
    }

    public static void showDialog() {
        currentStage = Utils.createDialogStage("/views/about.fxml", "About this application");
        currentStage.showAndWait();
    }
}
