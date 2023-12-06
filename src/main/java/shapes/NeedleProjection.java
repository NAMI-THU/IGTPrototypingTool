package shapes;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import util.Matrix3D;


public class NeedleProjection extends Group {
    /*
    Field variables
     */
    private final int LEN = 75;

    /*
    Constructors
     */
    public NeedleProjection() {
        int thickness = 1;
        Box line = new Box(LEN,thickness, thickness);
        line.setMaterial(new PhongMaterial(Color.RED));
        line.setTranslateX(-LEN / 2);
        //line.setTranslateY(LEN / 2);
        //line.setTranslateZ(-LEN / 2);
        // Only show z-axis

        getChildren().addAll(line);

        //getTransforms().addAll(rx, ry, rz);
        getTransforms().add(new Rotate(0,0,0,0));
    }

    public void rotate(Point3D p, double d) {
        getTransforms().setAll(new Rotate(Math.toDegrees(d), 0, 0, 0, p));
    }

    public void rotateMatrix(Matrix3D rotMat) {
        getTransforms().clear();
        getTransforms().add(new Affine(1.0,0.0,0.0,0.0,
                0.0,1.0,0.0,0.0,
                0.0,0.0,-1.0,0.0));
        getTransforms().add(new Affine(rotMat.get(0,0),rotMat.get(0,1),rotMat.get(0,2),0.0,
                rotMat.get(1,0),rotMat.get(1,1),rotMat.get(1,2),0.0,
                rotMat.get(2,0),rotMat.get(2,1),rotMat.get(2,2),0.0));
    }
}
