package algorithm;

import com.google.gson.annotations.Expose;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import shapes.STLModel;
import shapes.TrackingCone;
import shapes.NeedleProjection;
import util.Matrix3D;
import util.Quaternion;
import util.Vector3D;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The class Tool represents the name of a tool,
 * its respective measurements and the visualisation.
 */
public class Tool {
    @Expose
    private String name;
    @Expose
    private List<Measurement> measurements;
    private NeedleProjection projection;
    private TrackingCone cone;
    private Vector3D pos;
    private Matrix3D transformMatrix;
    private Vector3D offsetVec;
    private PhongMaterial color;

    public Tool(String name) {
        this.name = name;
        init();
    }

    public Tool() {
        init();
    }

    public void init() {
        this.measurements = new ArrayList<>();
        this.cone = new TrackingCone(36, 4, 10);
        this.projection = new NeedleProjection();
        this.projection.setVisible(false);
        this.setConeColor(new PhongMaterial(Color.GRAY));
        JSONParser jsonParser = new JSONParser();
        try {
            JSONObject jsonTransformationMatrix = (JSONObject) jsonParser.parse(new FileReader("src/main/resources/json/transformationMatrix.json"));
            double[] arr = new double[9];
            JSONArray transformationArray = (JSONArray) jsonTransformationMatrix.get("transformTracker");
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    JSONArray row = (JSONArray) transformationArray.get(i);
                    arr[j + i * 3] = (double) row.get(j);
                }
            }
            transformMatrix = new Matrix3D(arr);
            JSONArray offset = (JSONArray) jsonTransformationMatrix.get("trackerOffset");
            offsetVec = new Vector3D((double) offset.get(0), (double) offset.get(1), (double) offset.get(2));
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public void show() {
        Quaternion rotationMovement = measurements.get(measurements.size() - 1).getRotation();
        Matrix3D rotMat = rotationMovement.toRotationMatrix();

        double x = measurements.get(measurements.size() - 1).getPos().getX();
        double y = measurements.get(measurements.size() - 1).getPos().getY();
        double z = measurements.get(measurements.size() - 1).getPos().getZ();

        setPos(new Vector3D(x,y,z));
        //movePos(new Vector3D(-18.175,15.0,10.501));
        //translate(transformMatrix);
        //movePos(offsetVec);
        Matrix3D testMat = new Matrix3D(new double[]{-0.959308, -0.281717, -0.0190508, 0.282028, 0.959268, -0.0162585, -0.0136945, -0.0209698, -0.999686});
        Vector3D testVec = new Vector3D(-2.47192, -247.737,-1423.7);
        translate(testMat);
        movePos(testVec);
        rotate(rotMat);
        printPos();
    }

    private void translate(Matrix3D mat) {
        Vector3D newPos = mat.mult(pos);
        setPos(newPos);
    }

    private void setPos(Vector3D newPos) {
        pos = newPos;
        updatePos();
    }

    private void updatePos() {
        cone.setTranslateX(pos.getX());
        cone.setTranslateY(pos.getY());
        cone.setTranslateZ(pos.getZ());

        projection.setTranslateX(pos.getX());
        projection.setTranslateY(pos.getY());
        projection.setTranslateZ(pos.getZ());
    }

    private void movePos(Vector3D vec) {
        pos.add(vec);
        updatePos();
    }

    private void rotate(Matrix3D rotMat) {
        double d = Math.acos((rotMat.getTrace() - 1d) / 2d);
        if (d != 0d) {
            double den = 2d * Math.sin(d);
            Point3D p = new Point3D((rotMat.get(2,1) - rotMat.get(1,2)) / den, (rotMat.get(0,2) - rotMat.get(2,0)) / den, (rotMat.get(1,0) - rotMat.get(0,1)) / den);
            cone.setRotationAxis(p);
            cone.setRotate(Math.toDegrees(d));
            projection.setRotationAxis(p);
            projection.setRotate(Math.toDegrees(d));
        }
    }

    public void addVisualizationToRoot(Group root) {
        root.getChildren().add(projection);
        root.getChildren().addAll(cone);
    }

    /**
     * Checks for collision and changes tracker color accordingly
     * Color red implies a collision with the model was detected
     * Color green implies no collision was detected
     */

    public void checkBounds(ArrayList<STLModel> stlModels) {
        if (stlModels != null) {
            for (STLModel stlModel : stlModels) {
                if (cone.getBoundsInParent().intersects(stlModel.getMeshView().getBoundsInParent())) {
                    cone.setMaterial(new PhongMaterial(Color.RED));
                } else {
                    cone.setMaterial(this.color);
                }
            }
        }
    }

    public void setConeColor(PhongMaterial color) {
        this.color = color;
        cone.setMaterial(color);
    }

    public void setConeSize(double size) {
        cone.setHeight(size);
        cone.setRadius(size * 0.4);
    }

    public void setConeVisibility(boolean visibility) {
        cone.setVisible(visibility);
    }

    public boolean coneIsVisible() {
        return cone.isVisible();
    }

    public void setProjectionVisibility(boolean visibility) {
        projection.setVisible(visibility);
    }

    public boolean projectionIsVisible() {
        return projection.isVisible();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Measurement> getMeasurement() {
        return measurements;
    }

    public void addMeasurement(Measurement measurement) {
        measurements.add(measurement);
    }

    public Vector3D getOffsetVec() {
        return offsetVec;
    }

    /**
     * This method calculates errors and saves them in a list. With a loop the
     * distance between each point and the average point is computed. Every
     * distance is added to the list of errors.
     *
     * @param avgPoint - average point of type Point3D
     * @return errors - of type list
     */
    public List<Double> getErrors(Vector3D avgPoint) {
        List<Double> errors = new ArrayList<>();

        for (Measurement measurement : measurements) {
            Vector3D point = measurement.getPos();
            double distance = point.distTo(avgPoint);
            errors.add(distance);
        }
        return errors;
    }

    /**
     * This method computes the mean of the passed values.
     * getAverageMeasurement gets a list of measurements. Size of the list is
     * determined. A null point is created. A loop goes over the number of
     * measurements. In place i the point will be fetched and added to
     * addPoint. For x, y and z an average point is calculated. Though all
     * points from addPoint are pitched by the size of the list. An average
     * measurement and an average point is created.
     *
     * @return averageMeasurement - a average measurement
     */
    public AverageMeasurement getAverageMeasurement() {

        int measureSize = measurements.size();
        Vector3D addPoint = new Vector3D();

        for (int i = 0; i < measureSize; i++) {
            Measurement measurement = measurements.get(i);
            Vector3D point = measurement.getPos();
            addPoint.add(point);
        }

        double averageX = addPoint.getX() / measureSize;
        double averageY = addPoint.getY() / measureSize;
        double averageZ = addPoint.getZ() / measureSize;

        AverageMeasurement averageMeasurement = new AverageMeasurement();
        Vector3D averagePoint = new Vector3D(averageX, averageY, averageZ);

        averageMeasurement.setPos(averagePoint);
        averageMeasurement.setRotation(getAverageRotation());
        averageMeasurement.setRotationJitter(this.getRotationJitter());
        averageMeasurement.setErrors(this.getErrors(averagePoint));
        return averageMeasurement;
    }

    /**
     * This method computes the average rotation. The first quaternion of the
     * list of measurement on point 0 and the last quaternion is taken. The
     * time and the size of the measurements are divided. At this value, the
     * movement is exactly the average. On the first quaterion method slerp is
     * called by class quaternion. The first and the last quaternion plus
     * positionAtTime is returned as average rotation.
     *
     * @return firstRotation.slerp(firstRotation, lastRotation, positionAtTime)
     * - a quaternion
     */
    public Quaternion getAverageRotation() {

        Quaternion firstRotation = measurements.get(0).getRotation();
        Quaternion lastRotation = measurements.get(measurements.size() - 1)
                .getRotation();

        float positionAtTime = 1f / measurements.size();

        return firstRotation.slerp(firstRotation, lastRotation, positionAtTime);
    }

    /**
     * This method computes the Jitter of a Rotation. A list of measurements
     * and an average rotation is passed. Four lists of errors, for every value
     * of the quaternion, where created. In a loop the method gets quaternions
     * on point i.  If i > 0, from rotationMovement the quaternion on point i
     * is subtracted.  Error variables are created. From each quaternion the
     * list from above is added. Every list is added in rotationError. From
     * rotation error the root mean square error is calculated.
     *
     * @return rotationError - of type quaternion
     */

    public Quaternion getRotationJitter() {

        Quaternion avgRotation = this.getAverageRotation();

        /** Create four array lists */

        List<Double> rotationErrorX = new ArrayList<>();
        List<Double> rotationErrorY = new ArrayList<>();
        List<Double> rotationErrorZ = new ArrayList<>();
        List<Double> rotationErrorW = new ArrayList<>();

        for (int i = 0; i < measurements.size(); i++) {

            Quaternion rotationMovement = measurements.get(i).getRotation();

            if (i > 0) {
                rotationMovement = rotationMovement.subtract(measurements.get(i - 1)
                        .getRotation());
            }

            Quaternion errorRotationOfIterate = rotationMovement.subtract(avgRotation);

            double errorX = errorRotationOfIterate.getX1();
            rotationErrorX.add(errorX);

            double errorY = errorRotationOfIterate.getX2();
            rotationErrorY.add(errorY);

            double errorZ = errorRotationOfIterate.getX3();
            rotationErrorZ.add(errorZ);

            double errorW = errorRotationOfIterate.getX0();
            rotationErrorW.add(errorW);

        }
        /* Calculation of the jitter. */
        /*
        Quaternion rotationError = new Quaternion((float) getRMSE(rotationErrorX),
                                                  (float) getRMSE(rotationErrorY),
                                                  (float) getRMSE(rotationErrorZ),
                                                  (float) getRMSE(rotationErrorW));*/
        Quaternion rotationError = new Quaternion(0, 0, 0, 1);

        return rotationError;
    }

    public void printPos() {
        System.out.println("X: " + pos.getX() + " Y: " + pos.getY() + " Z: " + pos.getZ());
    }
}
