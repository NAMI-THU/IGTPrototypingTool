package algorithm;

import com.jme3.math.Quaternion;
import javafx.geometry.Point3D;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;

import java.util.Collections;
import java.util.List;

/**
 * This class provides the methods for processing the measurements. (
 */
public class TrackingDataProcessor {

    /**
     * This method computes the distance of two average points. Then the
     * expected distance is subtracted from the distance of the points.
     *
     * @param expectedDistance          - value of type double
     * @param firstAverageMeasurement  - value of type AverageMeasurement
     * @param secondAverageMeasurement - value of type AverageMeasurement
     * @return accurate - the distance
     */
    public static double getAccuracy(double expectedDistance,
            AverageMeasurement firstAverageMeasurement,
            AverageMeasurement secondAverageMeasurement) {

        /*
         * calculates the distance between the points of
         * firstAverageMeasurement and secondAverageMeasurement
         */
        return getDistance(firstAverageMeasurement.getPoint(),
                secondAverageMeasurement.getPoint()) - expectedDistance;
    }

    /**
     * This method gets a quaternion (four double values) and two
     * measurements. With the method getRotation the quaternion of
     * firstMeasurement is fetched. From this value, the second
     * quaternion is subtracted from the second measurement.
     * Then the expected quaternion is subtracted.
     *
     * @param expectedRotation  - of type quaternion
     * @param firstMeasurement  - of type Measurement
     * @param secondMeasurement - of type Measurement
     * @return result - of type quaternion
     */

    public static Quaternion getAccuracyRotation(Quaternion expectedRotation,
                                          Measurement firstMeasurement,
                                          Measurement secondMeasurement) {
        return secondMeasurement.getRotation()
                .subtract(firstMeasurement.getRotation())
                .subtract(expectedRotation);
    }

    /**
     * This method calculates the values for a boxplot. The values are sorted by
     * using the sort method of the Collections class. After that, an array is
     * created from the ArrayList using the method toDoubleArray, because the
     * Percentile class can only count on arrays. The values are calculated using
     * the available methods in these two classes.
     *
     * @param values - a list with values
     * @return boxPlot - of type boxPlot with results
     */
    public static BoxPlot getBoxPlot(List<Double> values) {

        BoxPlot boxPlot = new BoxPlot();

        /*
         * The values are sorted by using the method sort of the class Collections.
         */
        Collections.sort(values);

        /* Create an array. */
        Percentile percentile = new Percentile();
        percentile.setData(toDoubleArray(values));

        /* Calculation of the values. */
        boxPlot.setMax(Collections.max(values));
        boxPlot.setMin(Collections.min(values));
        boxPlot.setQ1(percentile.evaluate(25));
        boxPlot.setMedian(percentile.evaluate(50));
        boxPlot.setQ3(percentile.evaluate(75));

        return boxPlot;
    }

    /* toDoubleArray converts a list into an array */
    private static double[] toDoubleArray(List<Double> list) {
        double[] ret = new double[list.size()];
        int i = 0;
        for (Double e : list) {
            ret[i++] = e.intValue();
        }
        return ret;
    }

    /**
     * This method calculates the distance of two points and uses the method
     * distance from the class Point3D
     *
     * @param firstPoint  - of type Point3D
     * @param secondPoint - of type Point3D
     * @return distance - of type double
     */

    private static double getDistance(Point3D firstPoint, Point3D secondPoint) {
        return firstPoint.distance(secondPoint);
    }
}
