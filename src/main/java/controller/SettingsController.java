package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import util.CustomLogger;

public class SettingsController implements Controller {

    private static final ObservableList<String> listItems = FXCollections.
            observableArrayList();
    @FXML public ListView<String> listView;
    @FXML public CheckBox consoleOutput;

    @FXML
    private void changeConsoleOutput() {
        CustomLogger.changeConsoleOutput();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        registerController();
        listItems.addAll("Logging");
        listView.setItems(listItems);
        listView.getSelectionModel().select(0);
        if (CustomLogger.isConsoleOn()) {
            consoleOutput.setSelected(true);
        }
    }

    @FXML
    @Override
    public void close() {
        listItems.clear();
        unregisterController();
    }
}
