package algorithm;

import com.google.gson.annotations.Expose;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import shapes.NeedleProjection;
import shapes.STLModel;
import shapes.Target;
import shapes.TrackingCone;
import util.Matrix3D;
import util.Quaternion;
import util.Vector3D;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
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
    private List<Target> targets;

    public Tool(String name) {
        this.name = name;
        init();
    }

    public Tool() {
        init();
    }

    /**
     * Method to initialize the attributes of the tool
     */
    public void init() {
        this.measurements = new ArrayList<>();
        this.cone = new TrackingCone(36, 4, 10);
        this.projection = new NeedleProjection();
        this.projection.setVisible(true);
        this.setConeColor(new PhongMaterial(Color.GRAY));

        List<Double> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/matrixoutputInverse.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] valuesStr = line.split(";");
                for (int i = 0; i < 4; i++) {
                    records.add(Double.parseDouble(valuesStr[i]));
                }
            }

            double[] matrixArr = new double[9];
            double[] vectorArr = new double[3];
            byte count = 0;

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 4; j++) {
                    if (j == 3) {
                        vectorArr[i] = records.get(4*i + j);
                    } else {
                        matrixArr[count] = records.get(4*i + j);
                        count++;
                    }
                }
            }
            transformMatrix = new Matrix3D(matrixArr);
            offsetVec = new Vector3D(vectorArr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method to update the current position of the tool and perform all necessary transformations on it
     */
    public void show() {
        Quaternion rotationMovement = measurements.get(measurements.size() - 1).getRotation();
        Matrix3D rotMat = rotationMovement.toRotationMatrix();

        double x = measurements.get(measurements.size() - 1).getPos().getX();
        double y = measurements.get(measurements.size() - 1).getPos().getY();
        double z = measurements.get(measurements.size() - 1).getPos().getZ();

        setPos(new Vector3D(x,y,z));
        rotate(rotMat);
        translate(transformMatrix);
        movePos(offsetVec);
        checkTargets();
    }

    /**
     * Apply a translation matrix to the current position
     * @param mat
     */
    private void translate(Matrix3D mat) {
        Vector3D newPos = mat.mult(pos);
        setPos(newPos);
    }

    /**
     * Set the current position to a new one
     */
    private void setPos(Vector3D newPos) {
        pos = newPos;
        updatePos();
    }

    /**
     * Apply the current position of the tool to the cone and projection visualisation
     */
    private void updatePos() {
        cone.setTranslateX(pos.getX());
        cone.setTranslateY(pos.getY());
        cone.setTranslateZ(pos.getZ());

        projection.setTranslateX(pos.getX());
        projection.setTranslateY(pos.getY());
        projection.setTranslateZ(pos.getZ());
    }

    /**
     * Add a vector to the current position, effectivly moving it by this vector
     * @param vec the vector to be added to the current position
     */
    private void movePos(Vector3D vec) {
        pos.addLocal(vec);
        updatePos();
    }

    /**
     * Rotate the cone and projection visualisation by a rotation matrix
     * @param rotMat the rotation matrix
     */
    private void rotate(Matrix3D rotMat) {
        cone.rotateMatrix(rotMat);
        projection.rotateMatrix(rotMat);
    }

    /**
     * Helper method for adding the projection and the cone to the visualisation group
     * @param root
     */
    public void addVisualizationToRoot(Group root) {
        root.getChildren().add(projection);
        root.getChildren().addAll(cone);
    }

    /**
     * Set the targets of the tool
     * @param targets the new targets
     */
    public void setTargets(LinkedList<Target> targets) {
        this.targets = targets;
    }

    /**
     * Method to check if the projection intersects a target and if so, change the color of the target
     * Color red implies an intersection between the projection and the target
     * Color green implies no intersection was detected
     */
    public void checkTargets() {
        if (this.targets != null) {
            for (Target t: targets) {
                if (projection.isVisible() && projection.intersectsTarget(t, cone.getPos())) {
                    t.setSphereColor(Color.GREEN);
                } else {
                    t.setSphereColor(Color.RED);
                }
            }
        }
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

    /**
     * Method to set the color of the cone
     * @param color the new cone color
     */
    public void setConeColor(PhongMaterial color) {
        this.color = color;
        cone.setMaterial(color);
    }

    /**
     * Method to set the size of the cone
     * @param size the new cone size
     */
    public void setConeSize(double size) {
        cone.setHeight(size);
        cone.setRadius(size * 0.4);
    }

    /**
     * Method to set the visibility of the cone
     * @param visibility boolean, true if visible
     */
    public void setConeVisibility(boolean visibility) {
        cone.setVisible(visibility);
    }

    /**
     * Return if cone is visible
     * @return true if cone is visible
     */
    public boolean coneIsVisible() {
        return cone.isVisible();
    }

    /**
     * Method to set the visibility of the projection
     * @param visibility boolean, true if visible
     */
    public void setProjectionVisibility(boolean visibility) {
        projection.setVisible(visibility);
    }

    /**
     * Return if projection is visible
     * @return true if projection is visible
     */
    public boolean projectionIsVisible() {
        return projection.isVisible();
    }

    /**
     * Returns the name of the tool
     * @return name of the tool
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the tool
     * @param name new name of the tool
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns a list of measurements
     * @return List containing all the measurements of the tools
     */
    public List<Measurement> getMeasurement() {
        return measurements;
    }

    /**
     * Adds a measurement to the list
     * @param measurement measurement to be added
     */
    public void addMeasurement(Measurement measurement) {
        measurements.add(measurement);
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
            addPoint.addLocal(point);
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

            double errorX = errorRotationOfIterate.getX();
            rotationErrorX.add(errorX);

            double errorY = errorRotationOfIterate.getY();
            rotationErrorY.add(errorY);

            double errorZ = errorRotationOfIterate.getZ();
            rotationErrorZ.add(errorZ);

            double errorW = errorRotationOfIterate.getW();
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

    /**
     * Helper method to print the current position
     */
    public void printPos() {
        System.out.println("X: " + pos.getX() + " Y: " + pos.getY() + " Z: " + pos.getZ());
    }
}
