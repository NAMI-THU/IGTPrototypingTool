package shapes;

import javafx.scene.Group;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;


public class NeedleProjection extends Group {
    /*
    Field variables
     */
    public Rotate rx = new Rotate(-90, Rotate.X_AXIS);
    public Rotate ry = new Rotate(0, Rotate.Y_AXIS);
    public Rotate rz = new Rotate(0, Rotate.Z_AXIS);

    /*
    Constructors
     */
    public NeedleProjection() {
        int len = 75;
        int thickness = 1;
        Box z = new Box(thickness, thickness, len);
        z.setMaterial(new PhongMaterial(Color.RED));
        z.setTranslateZ(-len / 2);
        // Only show z-axis
        getChildren().addAll(z);

        getTransforms().addAll(rx, ry, rz);
    }
}
