package controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import util.CustomLogger;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class InfoController implements Controller {

    @FXML public TextArea licensesArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        registerController();
        // Load COPYING text
        try {
            var licenseTexts = Files.readAllLines(Path.of("COPYING"));
            licensesArea.setText(String.join("\n", licenseTexts));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    @Override
    public void close() {
        unregisterController();
    }
}
