package shapes;

import javafx.scene.PerspectiveCamera;
import util.Vector3D;

public class CameraContainer {
    private PerspectiveCamera perspectiveCamera;
    private Vector3D pos;

    public CameraContainer(boolean fixedAtZero) {
        perspectiveCamera = new PerspectiveCamera(fixedAtZero);
        perspectiveCamera.setNearClip(0.1);
        perspectiveCamera.setFarClip(2000.0);

        pos = new Vector3D();
    }

    public void setPos(Vector3D newPos) {
        pos = newPos;
        update();
    }

    public void move(Vector3D moveVec) {
        pos.addLocal(moveVec);
        update();
    }

    public void update() {
        perspectiveCamera.setTranslateX(pos.getX());
        perspectiveCamera.setTranslateY(pos.getY());
        perspectiveCamera.setTranslateZ(pos.getZ());
    }

    public PerspectiveCamera getPerspectiveCamera() {
        return perspectiveCamera;
    }
}
