package controller;

import algorithm.DataService;
import algorithm.TrackingService;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import util.PointSet;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the guidance planning view.
 * <p>
 * Handles selection of the tracking plane and the selected puncture path.
 * Also triggers the start of the guidance procedure.
 * </p>
 * */
public class GuidancePlanningController implements GuidanceController {

    @FXML
    private ToggleButton xyPlane;
    @FXML
    private ToggleButton zxPlane;
    @FXML
    private ToggleButton yzPlane;
    @FXML
    private ComboBox<PointSet> pathComboBox;
    @FXML
    public Spinner<Double> slerpSpinner;
    @FXML
    public CheckBox slerpCheckBox;

    private GuidanceHandler guidanceHandler;

    @Override
    public void registerController() {
        GuidanceController.super.registerController();
    }

    @Override
    public void unregisterController() {
        GuidanceController.super.unregisterController();
    }

    @Override
    public void injectStatusLabel(Label statusLabel) {
        GuidanceController.super.injectStatusLabel(statusLabel);
    }

    @Override
    public void close() {
        unregisterController();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        registerController();

        slerpSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.1, 1.0, 0.6, 0.05));

        // Updates the slerp factor when the spinner value changes
        slerpSpinner.valueProperty().addListener((obs, oldVal, newVal)
                -> guidanceHandler.setSlerpFactor(newVal.floatValue()));

        pathComboBox.setItems(DataService.getInstance().getPointSet());

        // Displayed text in the combo box is the .MPS file name
        pathComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(PointSet pointSet) {
                return (pointSet != null) ? pointSet.getName() : "";
            }

            @Override
            public PointSet fromString(String s) {
                return null;
            }
        });

        // Reselectes the stored point when the controller is reloaded
        pathComboBox.setOnAction(event -> {
            PointSet pointSet = pathComboBox.getValue();

            if (pointSet != null) {
                guidanceHandler.updatePlannedPoints(pointSet.getV1(), pointSet.getV2());
                guidanceHandler.setActivePointSet(pointSet);
            }
        });

        // If a new point set is being added, always select the first one in the combo box
        DataService.getInstance().getPointSet().addListener((ListChangeListener<PointSet>) change -> {
            if (!DataService.getInstance().getPointSet().isEmpty()) {
                pathComboBox.setValue(DataService.getInstance().getPointSet().get(0));
            }
        });

        ToggleGroup planeGroup = new ToggleGroup();

        xyPlane.setToggleGroup(planeGroup);
        zxPlane.setToggleGroup(planeGroup);
        yzPlane.setToggleGroup(planeGroup);
    }

    @Override
    public void setGuidanceHandler(GuidanceHandler guidanceHandler) {
        this.guidanceHandler = guidanceHandler;
        guidanceHandler.addGuidanceController(this);

        restoreSettingsIfAvailable();
    }

    /**
     * Reselects the previously selected point set in the combo box, after controller reload.
     *
     * @param pointSet The restored point set.
     * */
    private void setActivePointSet(PointSet pointSet) {
        if (pathComboBox.getValue() == null && pointSet != null) {
            pathComboBox.setValue(pointSet);
        }
    }

    /**
     * Reselects the previously selected plane, after controller reload.
     *
     * @param plane The restored plane.
     * */
    private void selectPlane(GuidanceHandler.Plane plane) {
        switch (plane) {
            case XY -> xyPlane.setSelected(true);
            case ZX -> zxPlane.setSelected(true);
            case YZ -> yzPlane.setSelected(true);
        }
    }

    /**
     * Called when the start button is clicked by the view. It delegates to the
     * {@link GuidanceHandler} to prepare for the guidance procedure.
     * */
    public void onStartVisualizationClicked() {
        if (requirementsFailed()) {
            return;
        }

        guidanceHandler.switchContentOfTab("GuidanceAlignmentView");
        guidanceHandler.prepareScene();
        guidanceHandler.disableDepthViewCrosshair();
        guidanceHandler.startGuidance();
    }

    /**
     * Checks if all requirements to start the guidance are met.
     *
     * @return {@code true}, if a requirement is not met. {@code false}, if all requirements are met.
     * */
    private boolean requirementsFailed() {
        if (TrackingService.getInstance().getDataService() == null) {
            showAlert("Please select a tracking source!");
            return true;
        }
        if (DataService.getInstance().getPointSet().isEmpty()) {
            showAlert("Please load a puncture path!");
            return true;
        }

        if (!xyPlane.isSelected() && !zxPlane.isSelected() && !yzPlane.isSelected()) {
            guidanceHandler.setPlaneSelected(null);
        }

        if (guidanceHandler.getPlaneSelected() == null) {
            showAlert("Please select a plane!");
            return true;
        }

        return false;
    }

    /**
     * This method restores the selected settings after visualization has been stopped.
     * */
    private void restoreSettingsIfAvailable() {
        // Restore selected plane
        if (guidanceHandler.getPlaneSelected() != null) {
            selectPlane(guidanceHandler.getPlaneSelected());
        }

        // Restore active point set
        if (guidanceHandler.getActivePointSet() != null) {
            setActivePointSet(guidanceHandler.getActivePointSet());
        }

        // Restore slerp settings
        slerpCheckBox.setSelected(guidanceHandler.isSlerpEnabled());
        slerpSpinner.setDisable(!guidanceHandler.isSlerpEnabled());
        slerpSpinner.getValueFactory().setValue((double) guidanceHandler.getSlerpFactor());
    }

    /**
     * Shows a warning message to the screen.
     *
     * @param message The message to show.
     * */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Called when the smoothing checkbox is toggled by the view.
     * */
    public void onSlerpToggled() {
        boolean enabled = slerpCheckBox.isSelected();
        guidanceHandler.setSlerpEnabled(enabled);
        slerpSpinner.setDisable(!enabled);
    }

    /**
     * Called when XY-plane selected, dependent on the lab setup.
     * */
    public void onXYClicked() {
        guidanceHandler.setPlaneSelected(GuidanceHandler.Plane.XY);
    }

    /**
     * Called when ZX-plane selected, dependent on the lab setup.
     * */
    public void onZXClicked() {
        guidanceHandler.setPlaneSelected(GuidanceHandler.Plane.ZX);
    }

    /**
     * Called when YZ-plane selected, dependent on the lab setup.
     * */
    public void onYZClicked() {
        guidanceHandler.setPlaneSelected(GuidanceHandler.Plane.YZ);
    }

}
