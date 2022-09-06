package controller;

import algorithm.SceneBuilder;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import shapes.TrackingSphere;

import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

public class VisualController implements Controller {

    @FXML
    Group meshGroup;
    @FXML
    Button loadStlFile;
    @FXML
    Button start;
    @FXML
    CheckBox cullBack;
    @FXML
    CheckBox wireframe;
    @FXML
    VBox posBox;
    @FXML
    VBox rotBox;
    @FXML
    ColorPicker colorPicker;
    @FXML
    Slider trackerSlider;
    @FXML
    ScrollPane scrollPane;

    TrackingDataController trackingDataController;
    SceneBuilder sceneBuilder;

    HashMap<String, Label> position;
    HashMap<String, Label> rotation;

    private Label statusLabel;
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private final BooleanProperty sourceConnected = new SimpleBooleanProperty(false);
//    private final BooleanProperty meshViewEmpty = new SimpleBooleanProperty(true);


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        registerController();
//        start.disableProperty().bind(meshViewEmpty);
        position = new HashMap<>();
        rotation = new HashMap<>();
    }

    public void injectStatusLabel(Label statusLabel) {
        this.statusLabel = statusLabel;
        this.statusLabel.setText("");
    }

    public void injectTrackingDataController(TrackingDataController trackingDataController) {
        this.trackingDataController = trackingDataController;
    }

    public void injectSceneBuilder(SceneBuilder sceneBuilder) {
        this.sceneBuilder = sceneBuilder;
    }

    @FXML
    private void loadSTLFile() {
        sceneBuilder.showFigure(meshGroup, scrollPane);
    }

    @FXML
    private void startTracking() {
        trackingDataController.visualizeTracking();
    }

    /**
     * Set a color for the tracker in the visualisation view
     */
    @FXML
    public void setTrackerColor() {
        TrackingSphere[] trackingSpheres = sceneBuilder.getTrackingSpheres();
        for (TrackingSphere sphere : trackingSpheres) {
            sphere.setMaterial(new PhongMaterial(colorPicker.getValue()));
            sphere.setDrawMode(DrawMode.LINE);
        }
    }

    /**
     * Set a size for the tracker in the visualisation view
     */
    @FXML
    public void setTrackerSize() {
        TrackingSphere[] trackingSpheres = sceneBuilder.getTrackingSpheres();
        for (TrackingSphere sphere : trackingSpheres
        )
            sphere.setRadius(trackerSlider.getValue());
    }

    @FXML
    private void pauseVisualization() {
        trackingDataController.freezeVisualization();
    }

    /**
     * Set DrawMode to Line or CullFace to none for every MeshView in the scene
     */
    @FXML
    private void changeView(Event e) {
        MeshView[] meshView = sceneBuilder.getMeshView();
        if (e.getSource().equals(cullBack)) {
            for (MeshView mesh : meshView) {
                mesh.cullFaceProperty().bind(
                        Bindings.when(
                                        cullBack.selectedProperty())
                                .then(CullFace.NONE)
                                .otherwise(CullFace.BACK)
                );
            }
        } else if (e.getSource().equals(wireframe)) {
            for (MeshView mesh : meshView) {
                mesh.drawModeProperty().bind(
                        Bindings.when(
                                        wireframe.selectedProperty())
                                .then(DrawMode.LINE)
                                .otherwise(DrawMode.FILL)
                );
            }
        }
    }

    @Override
    public void close() {
        statusLabel.setText("");
        unregisterController();
    }
}
