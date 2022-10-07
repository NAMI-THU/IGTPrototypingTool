package controller;

import algorithm.TrackingService;
import algorithm.VisualizationManager;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import shapes.TrackingCone;

import java.net.URL;
import java.util.*;

public class VisualizationController implements Controller {

    @FXML
    Group meshGroup;
    @FXML
    Button loadStlFile;
    @FXML
    Button start;
    @FXML
    ToggleButton pause;
    @FXML
    ToggleButton cullBack;
    @FXML
    ToggleButton wireframe;
    @FXML
    ColorPicker colorPicker;
    @FXML
    Slider trackerSlider;
    @FXML
    ScrollPane scrollPane;

    TrackingDataController trackingDataController;
    VisualizationManager visualizationManager;

    TrackingService trackingService = TrackingService.getInstance();

    private Label statusLabel;
    private final BooleanProperty visualizationRunning = new SimpleBooleanProperty(false);
    private final BooleanProperty sourceConnected = new SimpleBooleanProperty(false);


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        registerController();
        start.disableProperty().bind(visualizationRunning.or(sourceConnected.not()));
    }

    public void injectStatusLabel(Label statusLabel) {
        this.statusLabel = statusLabel;
        this.statusLabel.setText("");
    }

    public void injectTrackingDataController(TrackingDataController trackingDataController) {
        this.trackingDataController = trackingDataController;
    }

    public void injectVisualizationManager(VisualizationManager visualizationManager) {
        this.visualizationManager = visualizationManager;
    }

    public void setVisualizationRunning(Boolean value) {
        this.visualizationRunning.set(value);
    }

    public void setSourceConnected(Boolean value) {
        this.sourceConnected.set(value);
    }

    @FXML
    private void loadSTLFile() {
        if (trackingService.getTrackingDataSource() == null) {
            statusLabel.setText("Select Tracking Data Source first");
            return;
        }
        visualizationManager.setPane(this.scrollPane);
        visualizationManager.setMeshGroup(this.meshGroup);
        visualizationManager.loadStlModel();
        visualizationManager.showFigure();
    }

    @FXML
    private void startTracking() {
        trackingDataController.visualizeTracking();
    }

    /**
     * Set a color for the tracker in the visualisation view
     */
    @FXML
    private void setTrackerColor() {
        if (visualizationManager.visualizeCone().get()) {
            if (visualizationManager.getTrackingCones() == null) {
                statusLabel.setText("No Tracking Data Source");
                return;
            }
            statusLabel.setText("");

            TrackingCone[] trackingCones = visualizationManager.getTrackingCones();
            for (TrackingCone trackingCone : trackingCones) {
                trackingCone.setMaterial(new PhongMaterial(colorPicker.getValue()));
            }
        }
    }

    /**
     * Set a size for the tracker in the visualisation view
     */
    @FXML
    private void setTrackerSize() {
        if (visualizationManager.visualizeCone().get()) {
            if (visualizationManager.getTrackingCones() == null) {
                statusLabel.setText("No Tracking Data Source");
                return;
            }
            statusLabel.setText("");

            TrackingCone[] trackingCones = visualizationManager.getTrackingCones();
            for (TrackingCone trackingCone : trackingCones) {
                trackingCone.setHeight(trackerSlider.getValue());
                trackingCone.setRadius(trackerSlider.getValue() * 0.4);
            }
        }
    }

    /**
     * Pauses the visualization
     */
    @FXML
    private void pauseVisualization() {
        trackingDataController.freezeVisualization();
    }

    /**
     * Set CullFace to none for every MeshView in the scene
     */
    @FXML
    private void toggleCullFace() {
        if (visualizationManager.getMeshView() == null) {
            statusLabel.setText("No 3D-Model selected");
            return;
        }
        statusLabel.setText("");

        MeshView[] meshView = visualizationManager.getMeshView();

        if (cullBack.isSelected()) {
            for (MeshView mesh : meshView) {
                mesh.setCullFace(CullFace.NONE);
            }
        } else {
            for (MeshView mesh : meshView) {
                mesh.setCullFace(CullFace.BACK);
            }
        }
    }

    /**
     * Set DrawMode to Line for every MeshView in the scene
     */
    @FXML
    private void toggleWireframe() {
        if (visualizationManager.getMeshView() == null) {
            statusLabel.setText("No 3D-Model selected");
            return;
        }
        statusLabel.setText("");

        MeshView[] meshView = visualizationManager.getMeshView();

        if (wireframe.isSelected()) {
            for (MeshView mesh : meshView) {
                mesh.setDrawMode(DrawMode.LINE);
            }
        } else {
            for (MeshView mesh : meshView) {
                mesh.setDrawMode(DrawMode.FILL);
            }
        }
    }

    @Override
    public void close() {
        statusLabel.setText("");
        unregisterController();
    }
}
