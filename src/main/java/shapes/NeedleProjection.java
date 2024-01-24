package shapes;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Affine;
import util.Matrix3D;
import util.Vector3D;


public class NeedleProjection extends Group {
    /*
    Field variables
     */
    private final int LEN = 75;
    private Box line;
    Vector3D direction;

    /*
    Constructors
     */
    public NeedleProjection() {
        int thickness = 1;
        line = new Box(LEN,thickness, thickness);
        line.setMaterial(new PhongMaterial(Color.RED));
        line.setTranslateX((double) -LEN / 2);
        // Only show z-axis
        getChildren().add(line);
    }
    public void rotateMatrix(Matrix3D rotMat) {
        getTransforms().clear();
        getTransforms().add(new Affine(1.0,0.0,0.0,0.0,
                0.0,1.0,0.0,0.0,
                0.0,0.0,-1.0,0.0));
        getTransforms().add(new Affine(rotMat.get(0,0),rotMat.get(0,1),rotMat.get(0,2),0.0,
                rotMat.get(1,0),rotMat.get(1,1),rotMat.get(1,2),0.0,
                rotMat.get(2,0),rotMat.get(2,1),rotMat.get(2,2),0.0));
        direction = rotMat.mult(new Vector3D(-1,0,0));
        Matrix3D temp = new Matrix3D(new double[] {1.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,-1.0});
        direction = temp.mult(direction);
        direction.setMag(1);
    }

    public boolean intersectsTarget(Target target, Vector3D startPos) {
        Vector3D targetCenter = new Vector3D(target.getTranslateX(), target.getTranslateY(), target.getTranslateZ());

        double r = target.getSphere().getRadius();
        /*
         https://en.wikipedia.org/wiki/Line%E2%80%93sphere_intersection
         u = direction;
         o = startPos;
         c = targetCenter;
         r = r
         Formula:
         (u * (o - c))^2 - (o - c) * (o - c) - r^2
         */
        Vector3D o_c = startPos.sub(targetCenter);
        double determinant = Math.pow(direction.dot(o_c), 2) - o_c.dot(o_c) + r*r;
        return determinant >= 0 && targetCenter.distTo(startPos) <= r + LEN;
    }
}
