package shapes;

import javafx.scene.PerspectiveCamera;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import util.Quaternion;
import util.Vector3D;

public class CameraContainer {
    private PerspectiveCamera perspectiveCamera;
    private Vector3D pos;

    public CameraContainer(boolean fixedAtZero, Rotate rotateX, Rotate rotateY) {
        perspectiveCamera = new PerspectiveCamera(fixedAtZero);
        perspectiveCamera.setNearClip(0.1);
        perspectiveCamera.setFarClip(2000.0);
        perspectiveCamera.getTransforms().addAll(rotateX, rotateY, new Translate(0, 0, 0));

        pos = new Vector3D();
    }

    public void setPos(Vector3D newPos) {
        pos = newPos;
        update();
    }

    public void move(Vector3D moveVec) {
        pos.add(moveVec);
        update();
    }

    public void update() {
        perspectiveCamera.setTranslateX(pos.get(0));
        perspectiveCamera.setTranslateY(pos.get(1));
        perspectiveCamera.setTranslateZ(pos.get(2));
    }

    public PerspectiveCamera getPerspectiveCamera() {
        return perspectiveCamera;
    }
}
