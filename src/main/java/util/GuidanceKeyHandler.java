package util;

import controller.GuidanceHandler;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;

/**
 * This class handles the pressed keys and updates the corresponding phases.
 * */
public class GuidanceKeyHandler {

    /** Reference to the guidance tab. */
    private Tab guidanceTab;

    private final GuidanceHandler guidanceHandler;

    public GuidanceKeyHandler(GuidanceHandler guidanceHandler) {
        this.guidanceHandler = guidanceHandler;
    }

    /**
     * This method receives keys pressed on the scene.
     * <p>
     * It receives the keys pressed, only if the guidance tab is currently selected.
     * It covers the switches to the next phase with key "Q" and to the previous phase with key "E".
     * </p>
     * */
    public void handleKeyPressed(Scene scene) {
        if (guidanceTab != null) {
            scene.setOnKeyPressed(keyEvent -> {
                if (guidanceTab.isSelected()) {
                    if (guidanceHandler.isGuidanceRunning()) {
                        // Switch to next phase
                        if (keyEvent.getCode() == KeyCode.Q) {
                            if (guidanceHandler.getCurrentPhase() == GuidanceHandler.Phase.ALIGNMENT) {
                                guidanceHandler.updateCurrentPhase(GuidanceHandler.Phase.ANGLE);
                            } else if (guidanceHandler.getCurrentPhase() == GuidanceHandler.Phase.ANGLE) {
                                guidanceHandler.updateCurrentPhase(GuidanceHandler.Phase.DEPTH);
                            }
                        }
                        // Switch to last phase
                        if (keyEvent.getCode() == KeyCode.E) {
                            if (guidanceHandler.getCurrentPhase() == GuidanceHandler.Phase.ANGLE) {
                                guidanceHandler.updateCurrentPhase(GuidanceHandler.Phase.ALIGNMENT);
                            } else if (guidanceHandler.getCurrentPhase() == GuidanceHandler.Phase.DEPTH) {
                                guidanceHandler.updateCurrentPhase(GuidanceHandler.Phase.ANGLE);
                            }
                        }
                    }
                }
            });
        }
    }

    public void setContentNode(Tab guidanceTab) {
        this.guidanceTab = guidanceTab;
    }

}
