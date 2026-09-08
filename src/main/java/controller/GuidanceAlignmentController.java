package controller;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the guidance alignment view.
 * <p>
 * This view covers the whole guidance procedure. Its nodes are manipulated by the
 * {@link GuidanceHandler} in runtime, depending on the current phase.
 * </p>
 * */
public class GuidanceAlignmentController implements GuidanceController {

    @FXML
    private Circle tLight1;
    @FXML
    private Circle tLight2;
    @FXML
    private Circle tLight3;
    @FXML
    private Label depth;
    @FXML
    private Group targetCross;
    @FXML
    private Rectangle fillRect;
    @FXML
    private Circle targetCircle;
    @FXML
    private Text title;
    @FXML
    private StackPane subScenePane;
    @FXML
    private Group guidanceCircle;
    @FXML
    private Label distance;
    @FXML
    private Label hitError;
    @FXML
    private Group depthViewCrosshair;

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
    }

    @Override
    public void setGuidanceHandler(GuidanceHandler guidanceHandler) {
        this.guidanceHandler = guidanceHandler;
        guidanceHandler.addGuidanceController(this);
    }

    /**
     * Called when the stop button is clicked by the view. It delegates to the
     * {@link GuidanceHandler} to prepare for a view content switch.
     * */
    public void onStopVisualizationClicked() {
        guidanceHandler.switchContentOfTab("GuidancePlanningView");
        guidanceHandler.stopGuidance();
    }

    public Circle getTLight1() {
        return tLight1;
    }

    public Circle getTLight2() {
        return tLight2;
    }

    public Circle getTLight3() {
        return tLight3;
    }

    public Label getDepthLabel() {
        return depth;
    }

    public Group getTargetCross() {
        return targetCross;
    }

    public Rectangle getDepthRectangle() {
        return fillRect;
    }

    public Circle getTargetCircle() {
        return targetCircle;
    }

    public Text getTitle() {
        return title;
    }

    public StackPane getSubScene() {
        return subScenePane;
    }

    public Group getGuidanceCircle() {
        return guidanceCircle;
    }

    public Label getDistanceLabel() {
        return distance;
    }

    public Label getHitErrorLabel() {
        return hitError;
    }

    public Group getDepthViewCrosshair() {
        return depthViewCrosshair;
    }

}
