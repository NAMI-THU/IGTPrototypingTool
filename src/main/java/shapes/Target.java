package shapes;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import util.Vector3D;

/**
 * This class represents the target. It contains the position and a sphere as the visual representation
 */
public class Target extends Group {
    private Vector3D pos;
    private Sphere sphere;
    public Target(double x, double y, double z) {
        sphere = new Sphere(5);
        sphere.setMaterial(new PhongMaterial(Color.RED));
        getChildren().add(sphere);
        setPos(x,y,z);
    }

    public void setPos(double x, double y, double z) {
        this.pos = new Vector3D(x,y,z);
        update();
    }

    public void update() {
        setTranslateX(pos.getX());
        setTranslateY(pos.getY());
        setTranslateZ(pos.getZ());
    }

    public void setSphereColor(Color col) {
        sphere.setMaterial(new PhongMaterial(col));
    }

    public Sphere getSphere() {
        return sphere;
    }
}
