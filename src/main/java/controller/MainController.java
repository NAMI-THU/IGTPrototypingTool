package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class MainController implements Controller {

    @FXML TabPane tabPane;
    @FXML TrackingDataController trackingDataController;
    @FXML Label status;
    private FXMLLoader loader;
    private MeasurementController measurementController;
    private ThrombectomyController thrombectomyController;

    private final static Logger LOGGER = Logger.getLogger(MainController.class.getName());

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        trackingDataController.setStatusLabel(status);
    }

    @FXML
    private void openMeasurementView() {
        if (measurementController != null) return;

        loadView("MeasurementView");
        measurementController = loader.getController();
        measurementController.setTrackingDataController(
                trackingDataController);
        measurementController.setStatusLabel(status);
    }

    @FXML
    private void openThrombectomyView() {
        if (thrombectomyController != null) return;

        loadView("ThrombectomyView");
        thrombectomyController = loader.getController();
        thrombectomyController.setTrackingDataController(
                trackingDataController);
        thrombectomyController.setStatusLabel(status);
    };

    /**
     * Opens a view defined in a fxml file.
     * @param name of the view that is loaded
     * @throws IOException
     */
    private void loadView(String name) {
        loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/" + name
            + ".fxml"));
        try {
            Tab t = new Tab(name, loader.load());
            tabPane.getTabs().add(t);
            tabPane.getSelectionModel().select(t);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, name + " could not be opened.", e);
            status.setText(name + "could not be opened");
        }
    }

    public void close() {
    }
}
