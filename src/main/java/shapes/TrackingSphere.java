package shapes;

import javafx.scene.Group;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;


public class TrackingSphere extends Group {
    /*
    Field variables
     */
    public Rotate rx = new Rotate(0, Rotate.X_AXIS);
    public Rotate ry = new Rotate(0, Rotate.Y_AXIS);
    public Rotate rz = new Rotate(0, Rotate.Z_AXIS);


    /*
    Constructors
     */
    public TrackingSphere(double radius, int divisions, Color color) {
        PhongMaterial material = new PhongMaterial(color);

        Sphere sphere = new Sphere(radius, divisions);
        sphere.setMaterial(material);

        Box x = new Box(100, 2, 2);
        x.setMaterial(new PhongMaterial(Color.BLUE));
        Box y = new Box(2, 100, 2);
        y.setMaterial(new PhongMaterial(Color.BLUEVIOLET));
        Box z = new Box(2, 2, 100);
        z.setMaterial(new PhongMaterial(Color.BURLYWOOD));

        getChildren().addAll(sphere, x, y, z);

        // only activate for orientation
        boolean showAxis = false;
        if (showAxis) {
            CategoryAxis lineXAxis = new CategoryAxis();
            CategoryAxis lineYAxis = new CategoryAxis();
            CategoryAxis lineZAxis = new CategoryAxis();
            lineYAxis.getTransforms().addAll(new Rotate(90, Rotate.Z_AXIS));
            lineZAxis.getTransforms().addAll(new Rotate(90, Rotate.Y_AXIS));

            lineXAxis.setLabel("X");
            lineYAxis.setLabel("Y");
            lineZAxis.setLabel("Z");
            getChildren().addAll(lineXAxis, lineYAxis, lineZAxis);
        }

        getTransforms().addAll(rx, ry, rz);
    }
}