package controller;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import algorithm.VisualizationManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class MainController implements Controller {

    @FXML
    TabPane tabPane;
    @FXML
    Tab trackingDataTab;
    @FXML
    Tab visualizationTab;
    @FXML
    Tab guidanceTab;
    @FXML
    TrackingController trackingController;
    @FXML
    VideoController videoController;
    @FXML
    VisualizationController visualizationController;
    @FXML
    GuidancePlanningController guidancePlanningController;

    @FXML
    Label status;
    private FXMLLoader loader;
	private ExampleController exampleController;
    private SettingsController settingsController;
    private final VisualizationManager visualizationManager = new VisualizationManager();
    private final GuidanceHandler guidanceHandler = new GuidanceHandler();
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private int selectedSource;

    @FXML
    private Label igtLinkState;

    @FXML
    private Circle igtLinkCircle;

    @FXML
    private Label currentState;

    @FXML
    private Circle videoStatusCircle;


    private int statusIndex = 0; // Tracks the current state



    @FXML
    public void handleChangeStatus(int sourceOrdinal, int statusIndex) {
        // Determine the source type based on the sourceOrdinal
        if (sourceOrdinal == 0) { // VideoController source
            switch (statusIndex) {
                case 0: // Not connected
                    currentState.setText("Video: Not Connected");
                    updateCircleColors(videoStatusCircle, Color.rgb(255, 0, 0, 1.0)); // Red bright
                    break;
                case 1: // Connected but not yet tracking
                    currentState.setText("Video: Connected");
                    updateCircleColors(videoStatusCircle, Color.rgb(255, 173, 51, 1.0)); // Yellow bright
                    break;
                case 2: // Connected and tracking
                    currentState.setText("Video: Running");
                    updateCircleColors(videoStatusCircle, Color.rgb(0, 255, 0, 1.0)); // Green bright
                    break;
                default:
                    throw new IllegalArgumentException("Invalid status index for VideoController");
            }
        } else if (sourceOrdinal == 1) { // OpenIGTLink source
            switch (statusIndex) {
                case 0: // Not connected
                    igtLinkState.setText("Tracking: Not Connected");
                    updateCircleColors(igtLinkCircle, Color.rgb(255, 0, 0, 1.0)); // Red bright
                    break;
                case 1: // Connected but not yet tracking
                    igtLinkState.setText("Tracking: Connected");
                    updateCircleColors(igtLinkCircle, Color.rgb(255, 173, 51, 1.0)); // Yellow bright
                    break;
                case 2: // Connected and tracking
                    igtLinkState.setText("Tracking: Running");
                    updateCircleColors(igtLinkCircle, Color.rgb(0, 255, 0, 1.0)); // Green bright
                    break;
                default:
                    throw new IllegalArgumentException("Invalid status index for TrackingContoller");
            }
        } else {
            throw new IllegalArgumentException("Invalid source ordinal");
        }
    }

    // Helper method to update circle colors
    private void updateCircleColors(Circle circle, Color color) {
        if (circle != null) {
            circle.setFill(color);
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        registerController();
        trackingController.injectStatusLabel(status);
        trackingController.injectVisualizationManager(visualizationManager);
        trackingController.injectVisualizationController(visualizationController);
        trackingController.injectGuidanceHandler(guidanceHandler);
        videoController.injectStatusLabel(status);
        visualizationController.injectStatusLabel(status);
        visualizationController.injectTrackingDataController(trackingController);
        visualizationController.injectVisualizationManager(visualizationManager);
        visualizationManager.injectStatusLabel(status);

        videoController.setMainController(this);
        trackingController.setMainController(this);

        initializeGuidance();
    }
	
	@FXML
    private void openExampleView(){
        if (this.exampleController != null) return;

        try {
            setupFXMLLoader("ExampleView");
            Tab t = new Tab("Example", this.loader.load());

            this.exampleController = this.loader.getController();

            this.tabPane.getTabs().add(t);
            this.tabPane.getSelectionModel().select(t);
            t.setOnCloseRequest(e -> {
                this.exampleController.close();
                this.exampleController = null;
            });
        } catch(IOException e) {
            logger.log(Level.SEVERE, "Error loading Example View", e);
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
            visualizationManager.setPane(trackingController.scrollPane);
            visualizationManager.setMeshGroup(trackingController.meshGroup);
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
        try {
            setupFXMLLoader("InfoView");
            Stage newWindow = new Stage();
            newWindow.setTitle("Info");
            newWindow.setScene(new Scene(this.loader.load()));
            // set main window as parent of new window
            newWindow.initModality(Modality.WINDOW_MODAL);
            newWindow.initOwner(tabPane.getScene().getWindow());
            newWindow.show();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error loading Info View", e);
        }
//        Alert a = new Alert(Alert.AlertType.INFORMATION);
//        a.setTitle("About");
//        a.setHeaderText("IGT Prototyping Tool");
//        a.setContentText("This application was and currently is developed by students of THU.\nIt is actively supervised by Prof. Dr. Alfred Franz.\nThe source code can be found at https://github.com/NAMI-THU/IGTPrototypingTool");
//        a.showAndWait();
    }



    /*
    This function has the implementation of dark and light mode for the whole application
     */
    @FXML
    private void handleToggleTheme(ActionEvent event) {
        try {
            // Accessing the Scene from the MenuItem indirectly
            MenuItem menuItem = (MenuItem) event.getSource();
            Scene scene = menuItem.getParentPopup().getOwnerWindow().getScene();

            String lightModeUrl = Objects.requireNonNull(getClass().getResource("/css/customstyle.css")).toExternalForm();
            String darkModeUrl = Objects.requireNonNull(getClass().getResource("/css/dark-mode.css")).toExternalForm();


            if (lightModeUrl == null || darkModeUrl == null) {
                throw new Exception("Theme CSS file(s) not found.");
            }

            if (scene.getStylesheets().contains(darkModeUrl)) {
                scene.getStylesheets().remove(darkModeUrl);
                scene.getStylesheets().add(lightModeUrl);
            } else {
                scene.getStylesheets().remove(lightModeUrl);
                scene.getStylesheets().add(darkModeUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to toggle theme: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }



    /**
     * This method gives needed references to classes. It is used to maintain the guidance architecture.
     * */
    private void initializeGuidance() {
        guidanceHandler.setMainController(this);
        guidancePlanningController.setGuidanceHandler(guidanceHandler);
    }

    /**
     * This method makes sure, whenever a guidance controller is being called
     * to set references of the {@link GuidanceHandler}.
     * This is especially needed, because the loader returns a new instance of the controller.
     * */
    private void updateGuidanceControllers() {
        if (loader.getController() instanceof GuidanceController) {
            GuidanceController guidanceController = loader.getController();
            guidanceController.setGuidanceHandler(guidanceHandler);
        }
    }

    /**
     * This method is used to switch out the content of the tab.
     * @param fileName The file name of the .fxml file.
     * */
    public void switchContentOfTab(String fileName) {
        setupFXMLLoader(fileName);

        try {
            Node content = loader.load();

            Tab currentTab = tabPane.getSelectionModel().getSelectedItem();
            currentTab.setContent(content);

            guidanceHandler.resetControllers();

            guidanceHandler.updateKeyHandler(currentTab);
            guidanceHandler.registerKeyHandler(content.getScene());

            updateGuidanceControllers();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
