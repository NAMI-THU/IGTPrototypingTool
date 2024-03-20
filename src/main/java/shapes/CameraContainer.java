package shapes;

import javafx.scene.PerspectiveCamera;
import util.Vector3D;

/**
 * A class to handle the camera movement more easily.
 * It contains a PerspectiveCamera and a 3D Vector for its position
 */
public class CameraContainer {
    private PerspectiveCamera perspectiveCamera;
    private Vector3D pos;

    public CameraContainer(boolean fixedAtZero) {
        perspectiveCamera = new PerspectiveCamera(fixedAtZero);
        perspectiveCamera.setNearClip(0.1);
        perspectiveCamera.setFarClip(2000.0);

        pos = new Vector3D();
    }

    /**
     * Sets the new position of the vector and updates the position of the camera
     * @param newPos the new position of the vector
     */
    public void setPos(Vector3D newPos) {
        pos = newPos;
        update();
    }

    /**
     * Moves the position vector by adding a new one to it and updates the position of the camera
     * @param moveVec the vector to be added to the current position vector
     */
    public void move(Vector3D moveVec) {
        pos.addLocal(moveVec);
        update();
    }

    /**
     * Updates the position of the camera to the position of the vector
     */
    public void update() {
        perspectiveCamera.setTranslateX(pos.getX());
        perspectiveCamera.setTranslateY(pos.getY());
        perspectiveCamera.setTranslateZ(pos.getZ());
    }

    public PerspectiveCamera getPerspectiveCamera() {
        return perspectiveCamera;
    }
}
