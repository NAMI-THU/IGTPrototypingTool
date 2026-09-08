package controller;

import algorithm.DataService;
import algorithm.GuidanceSceneCoordinator;
import algorithm.GuidanceManager;
import algorithm.TrackingTool;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import shapes.CameraContainer;
import util.GuidanceKeyHandler;
import util.PointSet;
import util.Vector3D;

import java.util.ArrayList;
import java.util.List;

/**
 * This class acts as a facade over the guidance subsystem.
 * <p>
 * It coordinates phase transitions (Alignment, Angle, Depth) and delegates to the
 * underlying subsystems, without holding significant logic of its own.
 * </p>
 * */
public class GuidanceHandler {

    /** The 3 main phases after guidance started. */
    public enum Phase { ALIGNMENT, ANGLE, DEPTH }

    /** The standard planes depending on the position of the EM-Tracker */
    public enum Plane { XY, ZX, YZ }

    /** The phase the user is currently in. */
    private Phase currentPhase = Phase.ALIGNMENT;

    /** Holds state if the guidance running. */
    private boolean guidanceRunning = false;

    /** The current selected plane from the planing section. */
    private Plane planeSelected;

    /** Whether camera rotation smoothing is active. */
    private boolean slerpEnabled = false;

    /** The factor for camera rotation smoothing. 1.0 = no smoothing, lower = smoother but more latency. */
    private float slerpFactor = 0.6f;

    /** The boolean if a phase switch occured. */
    private boolean phaseSwitched = true;

    /** Holds the selected value of the path combo box */
    private PointSet activePointSet;

    /** List, which contains the current active controller */
    private final List<GuidanceController> guidanceControllers = new ArrayList<>();

    private MainController mainController;

    private final GuidanceManager guidanceManager = new GuidanceManager(this);

    private final GuidanceKeyHandler guidanceKeyHandler = new GuidanceKeyHandler(this);

    private final GuidanceSceneCoordinator guidanceSceneCoordinator = new GuidanceSceneCoordinator(this);

    /**
     * This method updates the selected target points, loads and transforms the meshes selected
     * from the visualization section.
     */
    public void prepareScene() {
        guidanceSceneCoordinator.initialize();
        guidanceSceneCoordinator.addActiveModelsToRoot();

        if (!DataService.getInstance().meshNameContainsInList("torso")) {
            guidanceSceneCoordinator.setTorsoNull();
        }
    }

    /**
     * Updates the selected points to the guidance manager.
     *
     * @param entry The entry point.
     * @param target The target point.
     * */
    public void updatePlannedPoints(Vector3D entry, Vector3D target) {
        guidanceManager.updatePlannedPoints(entry, target);
    }

    /**
     * Starts the guidance.
     * */
    public void startGuidance() {
        guidanceRunning = true;
    }

    /**
     * Stops the guidance.
     * */
    public void stopGuidance() {
        guidanceRunning = false;
        // Reset phase after visualization stopped.
        currentPhase = Phase.ALIGNMENT;
    }

    /**
     * Delegates the tab switch from the guidance controllers to the main controller.
     * */
    public void switchContentOfTab(String fileName) {
        mainController.switchContentOfTab(fileName);
    }

    /**
     * This method is called by switching tabs. It closes and removes the last active controller.
     * */
    public void resetControllers() {
        for (GuidanceController controller : guidanceControllers) {
            controller.close();
        }
        guidanceControllers.clear();
    }

    /**
     * Adds the current active controller to the list.
     * */
    public void addGuidanceController(GuidanceController controller) {
        this.guidanceControllers.add(controller);
    }

    /**
     * This method is being called by the {@link GuidanceKeyHandler},
     * if a key has been pressed.
     *
     * @param currentPhase The phase the user is currently in.
     * */
    public void updateCurrentPhase(Phase currentPhase) {
        phaseSwitched = true;
        this.currentPhase = currentPhase;
        viewAdjustments();
    }

    /**
     * Calls related methods based of the current phase.
     * */
    private void viewAdjustments() {
        if (currentPhase.equals(Phase.ALIGNMENT)) {
            tipAlignmentViewAdjustments();
        } else if (currentPhase.equals(Phase.ANGLE)) {
            angulationViewAdjustments();
        } else if (currentPhase.equals(Phase.DEPTH)) {
            sceneDepthViewAdjustments();
        }
    }

    /**
     * Performs view changes by switching from angulation to tip alignment phase.
     * */
    private void tipAlignmentViewAdjustments() {
        GuidanceAlignmentController controller = getGuidanceAlignmentControllerIfActive();

        controller.getTitle().setText("Phase 2: Tip Alignment");

        controller.getTargetCross().setVisible(true);
        controller.getTargetCircle().setVisible(false);

        controller.getDepthViewCrosshair().setVisible(false);

        controller.getTLight1().setId("glowTrafficLight1");
        controller.getTLight2().setId("trafficLight2");

        guidanceSceneCoordinator.torsoVisible(true);
    }

    /**
     * Performs view changes by switching to angulation phase.
     * */
    private void angulationViewAdjustments() {
        GuidanceAlignmentController controller = getGuidanceAlignmentControllerIfActive();

        controller.getTitle().setText("Phase 3: Angulation");

        controller.getTargetCross().setVisible(false);
        controller.getTargetCircle().setVisible(true);

        controller.getDepthViewCrosshair().setVisible(false);

        controller.getTLight1().setId("trafficLight1");
        controller.getTLight2().setId("glowTrafficLight2");
        controller.getTLight3().setId("trafficLight3");

        controller.getGuidanceCircle().setVisible(true);

        controller.getHitErrorLabel().setText("? mm");

        guidanceSceneCoordinator.torsoVisible(false);
    }

    /**
     * Performs view changes by switching from angulation to scene depth phase.
     * */
    private void sceneDepthViewAdjustments() {
        GuidanceAlignmentController controller = getGuidanceAlignmentControllerIfActive();

        controller.getTitle().setText("Phase 4: Depth");

        controller.getTargetCross().setVisible(false);
        controller.getTargetCircle().setVisible(false);

        controller.getDepthViewCrosshair().setVisible(true);

        controller.getTLight2().setId("trafficLight2");
        controller.getTLight3().setId("glowTrafficLight3");

        controller.getGuidanceCircle().setVisible(false);
    }

    /**
     * Gives access to the {@link GuidanceAlignmentController}.
     * <p>
     * This method will throw an {@link IllegalStateException} if the controller is not active.
     * It should only be called from certain places where the guidance view is active.
     * </p>
     * @return The {@link GuidanceAlignmentController}.
     * */
    private GuidanceAlignmentController getGuidanceAlignmentControllerIfActive() {
        if (guidanceControllers.get(0) instanceof GuidanceAlignmentController) {
            return ((GuidanceAlignmentController) guidanceControllers.get(0));
        }
        throw new IllegalStateException("GuidanceAlignmentController is not active");
    }

    /**
     * Gives access to the {@link GuidancePlanningController}.
     * <p>
     * This method will throw an {@link IllegalStateException} if the controller is not active.
     * It should only be called from certain places where the planning view is active.
     * </p>
     * @return The {@link GuidancePlanningController}.
     * */
    private GuidancePlanningController getGuidancePlanningControllerIfActive() {
        if (guidanceControllers.get(0) instanceof GuidancePlanningController) {
            return ((GuidancePlanningController) guidanceControllers.get(0));
        }
        throw new IllegalStateException("GuidancePlanningController is not active");
    }

    /**
     * Provides access to the current guidance tab and passes it to the {@link GuidanceKeyHandler}.
     *
     * @param guidanceTab The current guidance tab.
     * */
    public void updateKeyHandler(Tab guidanceTab) {
        guidanceKeyHandler.setContentNode(guidanceTab);
    }

    /**
     * Provides access to the scene and passes it to the {@link GuidanceKeyHandler}
     *
     * @param scene The scene which will be used to gain access of the keys pressed.
     * */
    public void registerKeyHandler(Scene scene) {
        guidanceKeyHandler.handleKeyPressed(scene);
    }

    /**
     * Used after first start of visualization.
     * */
    public void disableDepthViewCrosshair() {
        getGuidanceAlignmentControllerIfActive().getDepthViewCrosshair().setVisible(false);
    }

    /*----------------------------------------GETTER/SETTER----------------------------------------*/

    public Plane getPlaneSelected() {
        return planeSelected;
    }

    public Phase getCurrentPhase() {
        return currentPhase;
    }

    public PointSet getActivePointSet() {
        return activePointSet;
    }

    public boolean isSlerpEnabled() {
        return slerpEnabled;
    }

    public float getSlerpFactor() {
        return slerpFactor;
    }

    public boolean isPhaseSwitched() {
        return phaseSwitched;
    }

    public boolean isGuidanceRunning() {
        return guidanceRunning;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setPlaneSelected(Plane planeSelected) {
        this.planeSelected = planeSelected;
    }

    public void setActivePointSet(PointSet activePointSet) {
        this.activePointSet = activePointSet;
    }

    public void setPhaseSwitched(boolean phaseSwitched) {
        this.phaseSwitched = phaseSwitched;
    }

    public void setSlerpEnabled(boolean slerpEnabled) {
        this.slerpEnabled = slerpEnabled;
    }

    public void setSlerpFactor(float slerpFactor) {
        this.slerpFactor = slerpFactor;
    }

    /*----------------------------------------DELEGATES----------------------------------------*/

    public void guidance(List<TrackingTool> trackingTools) {
        guidanceManager.alignment(trackingTools);
    }

    public Group getWorld() {
        return guidanceSceneCoordinator.getWorld();
    }

    public CameraContainer getCamera() {
        return guidanceSceneCoordinator.getCamera();
    }

    public Group getTargetCross() {
        return getGuidanceAlignmentControllerIfActive().getTargetCross();
    }

    public Circle getTargetCircle() {
        return getGuidanceAlignmentControllerIfActive().getTargetCircle();
    }

    public Rectangle getDepthRectangle() {
        return getGuidanceAlignmentControllerIfActive().getDepthRectangle();
    }

    public Label getDepthLabel() {
        return getGuidanceAlignmentControllerIfActive().getDepthLabel();
    }

    public Label getDistanceLabel() {
        return getGuidanceAlignmentControllerIfActive().getDistanceLabel();
    }

    public Label getHitErrorLabel() {
        return getGuidanceAlignmentControllerIfActive().getHitErrorLabel();
    }

    public StackPane getSubScene() {
        return getGuidanceAlignmentControllerIfActive().getSubScene();
    }

}
