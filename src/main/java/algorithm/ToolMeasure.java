package algorithm;

import com.jme3.math.Quaternion;
import javafx.geometry.Point3D;

import java.util.ArrayList;
import java.util.List;

/**
 * The class ToolMeasure represents the name of a tool and
 * its respective measurements.
 */
public class ToolMeasure {

    private String name;
    private List<Measurement> measurements;

    public ToolMeasure(String name) {
        this.name = name;
        this.measurements = new ArrayList<>();
    }

    public ToolMeasure() {
        this.measurements = new ArrayList<>();
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

    /**
     * This method calculates errors and saves them in a list. With a loop the
     * distance between each point and the average point is computed. Every
     * distance is added to the list of errors.
     *
     * @param avgPoint - average point of type Point3D
     * @return errors - of type list
     */
    public List<Double> getErrors(Point3D avgPoint) {
        List<Double> errors = new ArrayList<>();

        for (int i = 0; i < measurements.size(); i++) {
            Point3D point = measurements.get(i).getPoint();
            double distance = getDistance(point, avgPoint);
            errors.add(distance);
        }
        return errors;
    }

    /**
     * This method calculates the distance of two points and uses the method
     * distance from the class Point3D
     *
     * @param firstPoint  - of type Point3D
     * @param secondPoint - of type Point3D
     * @return distance - of type double
     */

    private double getDistance(Point3D firstPoint, Point3D secondPoint) {
        double distance = firstPoint.distance(secondPoint);
        return distance;
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
     * @param measurements - list with measurements
     * @return averageMeasurement - a average measurement
     */
    public AverageMeasurement getAverageMeasurement() {

        int measureSize = measurements.size();
        Point3D addPoint = new Point3D(0, 0, 0);

        for (int i = 0; i < measureSize; i++) {
            Measurement measurement = measurements.get(i);
            Point3D point = measurement.getPoint();
            addPoint = addPoint.add(point);
        }

        double averageX = addPoint.getX() / measureSize;
        double averageY = addPoint.getY() / measureSize;
        double averageZ = addPoint.getZ() / measureSize;

        AverageMeasurement averageMeasurement = new AverageMeasurement();
        Point3D averagePoint = new Point3D(averageX, averageY, averageZ);

        averageMeasurement.setPoint(averagePoint);
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
     * @param measurements - list of measurements
     * @return firstRotation.slerp(firstRotation, lastRotation, positionAtTime)
     * - a quaternion
     */
    public Quaternion getAverageRotation() {

        Quaternion firstRotation = measurements.get(0).getRotation();
        Quaternion lastRotation = measurements.get(measurements.size() - 1)
        		.getRotation();

        float positionAtTime = 1 / measurements.size();

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
     * @param avgRotation - average rotation of type Quaternion
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
}
