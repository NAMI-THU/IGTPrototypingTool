package tracking;

import com.sun.jdi.DoubleValue;
import util.Quaternion;
import util.Vector3D;

import java.util.ArrayList;
import java.util.List;

public class MeasurementStatistics {
    private final List<Measurement> measurements = new ArrayList<Measurement>();

    public MeasurementStatistics(List<Measurement> measurements) {
        // Make a copy of that list
        this.measurements.addAll(measurements);
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
    private static Quaternion getMeanRotationBetweenFirstAndLast(List<Measurement> measurements) {
        Quaternion firstRotation = measurements.getFirst().getOrientation();
        Quaternion lastRotation = measurements.getLast().getOrientation();

        float positionAtTime = 1f / measurements.size();

        return firstRotation.slerp(firstRotation, lastRotation, positionAtTime);
    }

    /**
     * TODO: PROBABLY FAULTY CALCULATION!
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
        Quaternion avgRotation = this.getMeanRotationBetweenFirstAndLast(measurements);

        /** Create four array lists */

        List<Double> rotationErrorX = new ArrayList<>();
        List<Double> rotationErrorY = new ArrayList<>();
        List<Double> rotationErrorZ = new ArrayList<>();
        List<Double> rotationErrorW = new ArrayList<>();

        for (int i = 0; i < measurements.size(); i++) {
            Quaternion rotationMovement = measurements.get(i).getOrientation();

            if (i > 0) {
                rotationMovement = rotationMovement.subtract(measurements.get(i-1).getOrientation());
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

        var rotationError = new Quaternion((float) getRMSE(rotationErrorX),
                                          (float) getRMSE(rotationErrorY),
                                          (float) getRMSE(rotationErrorZ),
                                          (float) getRMSE(rotationErrorW));
//        Quaternion rotationError = new Quaternion(0, 0, 0, 1);
        return rotationError;
    }

    /*
    Probably not correct for the rotation jitter
     */
    private double getRMSE(List<Double> values) {
        var sum = values.stream().reduce(0d, Double::sum);
        var average = sum / values.size();
        var squaredDifferences = new ArrayList<Double>();
        for(var value : values) {
            var difference = value - average;
            var squaredDifference = Math.pow(difference, 2);
            squaredDifferences.add(squaredDifference);
        }
        var squaredDifferencesSum = squaredDifferences.stream().reduce(0d, Double::sum);
        var squaredDifferencesAverage = squaredDifferencesSum / squaredDifferences.size();
        return Math.sqrt(squaredDifferencesAverage);
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
            Vector3D point = measurement.getPosition();
            double distance = point.distTo(avgPoint);
            errors.add(distance);
        }
        return errors;
    }


    /**
     * The method getJitter computes the root mean square error. She receives a
     * list of errors and called method getRMSE, where the RMSE is calculated.
     *
     * @return getRMSE - the root mean square error
     */
    public double getJitter() {
        Vector3D averagePoint = getAveragePoint(measurements);
        List<Double> errors = new ArrayList<>();

        for (Measurement measurement : measurements) {
            Vector3D point = measurement.getPosition();
            double distance = point.distTo(averagePoint);
            errors.add(distance);
        }

        double additionalPowError = 0;

        for (double error : errors) {
            additionalPowError += Math.pow(error, 2);
        }
        return Math.sqrt(additionalPowError / errors.size());
    }

    private static Vector3D getAveragePoint(List<Measurement> measurements) {
        int measureSize = measurements.size();
        Vector3D addPoint = new Vector3D();

        for (int i = 0; i < measureSize; i++) {
            Measurement measurement = measurements.get(i);
            Vector3D point = measurement.getPosition();
            addPoint.addLocal(point);
        }

        double averageX = addPoint.getX() / measureSize;
        double averageY = addPoint.getY() / measureSize;
        double averageZ = addPoint.getZ() / measureSize;

        return new Vector3D(averageX, averageY, averageZ);
    }

    /**
     * This method computes the distance of two average points. Then the
     * expected distance is subtracted from the distance of the points.
     *
     * @param expectedDistance          - value of type double
     * @param tool1Points  - value of type List<Measurement>
     * @param tool2Points - value of type List<Measurement>
     * @return accurate - the distance
     */
    public static double getAccuracy(double expectedDistance,
                                     List<Measurement> tool1Points,
                                     List<Measurement> tool2Points) {
        var pos1 = getAveragePoint(tool1Points);
        var pos2 = getAveragePoint(tool2Points);
        return pos1.distTo(pos2) - expectedDistance;
    }

    /**
     * This method gets a quaternion (four double values) and two
     * measurements. With the method getRotation the quaternion of
     * firstMeasurement is fetched. From this value, the second
     * quaternion is subtracted from the second measurement.
     * Then the expected quaternion is subtracted.
     *
     * @param expectedRotation  - of type quaternion
     * @param tool1Points  - of type List<Measurement>
     * @param tool2Points - of type List<Measurement>
     * @return result - of type quaternion
     */

    public static Quaternion getAccuracyRotation(Quaternion expectedRotation,
                                                 List<Measurement> tool1Points,
                                                 List<Measurement> tool2Points) {
        var middleRotation1 = getMeanRotationBetweenFirstAndLast(tool1Points);
        var middleRotation2 = getMeanRotationBetweenFirstAndLast(tool2Points);
        return middleRotation1.subtract(middleRotation2).subtract(expectedRotation);
    }
}
