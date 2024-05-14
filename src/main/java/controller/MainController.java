package controller;

import algorithm.VisualizationManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainController implements Controller {

    @FXML
    TabPane tabPane;
    @FXML
    Tab trackingDataTab;
    @FXML
    Tab visualizationTab;
    // These three controller will be automatically injected since we annotated the "trackingData", "video" and "visual" element in the fxml
    @FXML
    TrackingDataController trackingDataController;
    @FXML
    VideoController videoController;
    @FXML
    VisualizationController visualizationController;
    @FXML
    Label status;
    private FXMLLoader loader;
    private MeasurementController measurementController;
    private ThrombectomyController thrombectomyController;
    private AutoTrackController autoTrackController;

    private AiController AiController;
    private SettingsController settingsController;
    private final VisualizationManager visualizationManager = new VisualizationManager();
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        registerController();
        trackingDataController.injectStatusLabel(status);
        trackingDataController.injectVisualizationManager(visualizationManager);
        trackingDataController.injectVisualizationController(visualizationController);
        videoController.injectStatusLabel(status);
        visualizationController.injectStatusLabel(status);
        visualizationController.injectTrackingDataController(trackingDataController);
        visualizationController.injectVisualizationManager(visualizationManager);
        visualizationManager.injectStatusLabel(status);
    }

    @FXML
    private void openMeasurementView() {
        try {
            setupFXMLLoader("MeasurementView");
            Tab t = new Tab("Measurement View", this.loader.load());
            // set up connections between measurement and other parts of application
            this.measurementController = this.loader.getController();
            this.measurementController.injectStatusLabel(this.status);

            this.tabPane.getTabs().add(t);
            this.tabPane.getSelectionModel().select(t);
            t.setOnCloseRequest(e -> this.measurementController.close());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error loading Measurement View", e);
        }
    }

    @FXML
    private void openThrombectomyView() {
        if (this.thrombectomyController != null) return;

        try {
            setupFXMLLoader("ThrombectomyView");
            Tab t = new Tab("Thrombectomy View", this.loader.load());

            this.thrombectomyController = this.loader.getController();
            this.thrombectomyController.injectStatusLabel(this.status);

            this.tabPane.getTabs().add(t);
            this.tabPane.getSelectionModel().select(t);
            t.setOnCloseRequest(e -> this.thrombectomyController.close());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error loading Thrombectomy View", e);
        }
    }

    @FXML
    private void openAutoTrackView(){
        if (this.autoTrackController != null) return;

        try {
            setupFXMLLoader("AutoTrackView");
            Tab t = new Tab("Autotrack", this.loader.load());

            this.autoTrackController = this.loader.getController();
            this.autoTrackController.setStatusLabel(this.status);

            this.tabPane.getTabs().add(t);
            this.tabPane.getSelectionModel().select(t);
            t.setOnCloseRequest(e -> {
                this.autoTrackController.close();
                this.autoTrackController = null;
            });
        } catch(IOException e) {
            logger.log(Level.SEVERE, "Error loading AutoTrack View", e);
        }
    }

    @FXML
    private void openAIView(){
        if (this.AiController != null) return;

        try {
            setupFXMLLoader("AiView");
            Tab t = new Tab("AiView", this.loader.load());

            this.AiController = this.loader.getController();
            this.AiController.setStatusLabel(this.status);

            this.tabPane.getTabs().add(t);
            this.tabPane.getSelectionModel().select(t);
            t.setOnCloseRequest(e -> {
                this.AiController.close();
                this.AiController = null;
            });
        } catch(IOException e) {
            logger.log(Level.SEVERE, "Error loading AutoTrack View", e);
        }
    }

    @FXML
    private void openSettings() {
        try {
            setupFXMLLoader("SettingsView");
            Stage newWindow = new Stage();
            newWindow.setTitle("Settings");
            newWindow.setScene(new Scene(this.loader.load()));
            // set main window as parent of new window
            newWindow.initModality(Modality.WINDOW_MODAL);
            newWindow.initOwner(tabPane.getScene().getWindow());
            newWindow.show();

            this.settingsController = this.loader.getController();
            newWindow.setOnCloseRequest(e -> this.settingsController.close());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error loading Settings View", e);
        }
    }

    /**
     * Changes the scrollPane and meshGroup of the visualizationManager to the one of the selected Tab
     */
    @FXML
    private void onChangeView() {
        if (trackingDataTab.isSelected()) {
            visualizationManager.setPane(trackingDataController.scrollPane);
            visualizationManager.setMeshGroup(trackingDataController.meshGroup);
//            visualizationManager.setViewportSize(800);
            visualizationManager.showFigure();
        }
        else if (visualizationTab.isSelected()) {
            visualizationManager.setPane(visualizationController.scrollPane);
            visualizationManager.setMeshGroup(visualizationController.meshGroup);
//            visualizationManager.setViewportSize(350);
            visualizationManager.showFigure();
        }
    }

    private void setupFXMLLoader(String fileName) {
        this.loader = new FXMLLoader();
        this.loader.setLocation(getClass().getResource("/view/" + fileName + ".fxml"));
    }

    /**
     * Close application
     */
    @FXML
    @Override
    public void close() {
        Platform.exit();
    }

    /**
     * Creates a dialogue to display some information about the application
     */
    @FXML
    public void openAboutView() {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("About");
        a.setHeaderText("IGT Prototyping Tool");
        a.setContentText("This application was and currently is developed by students of THU.\nIt is actively supervised by Prof. Dr. Alfred Franz.\nThe source code can be found at https://github.com/Alfred-Franz/IGTPrototypingTool");
        a.showAndWait();
    }
}
