package algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.geometry.Point3D;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;

import com.jme3.math.Quaternion;

/** This class provides the methods for processing the measurements. */
public class DataProcessor {

	/** This method computes the distance of two average points. */
	public double getAccuracy(double expectedDistance, AverageMeasurement firstAverangeMeasurement,
			AverageMeasurement secondAverangeMeasurement)

	{
		/** Method getDistance from class Point 3D. */
		return getDistance(firstAverangeMeasurement.getPoint(), secondAverangeMeasurement.getPoint())
				- expectedDistance;
	}

	/** */
	public Quaternion getAccuracyRotation(Quaternion expectedRotation, Measurement firstMeasurement,
			Measurement secondMeasurement) {
		Quaternion result = firstMeasurement.getRotation().subtract(secondMeasurement.getRotation())
				.subtract(expectedRotation);
		return result;
	}

	/** This method calculates the values for a boxplot. */
	public BoxPlot getBoxPlot(List<Double> values) {

		BoxPlot boxPlot = new BoxPlot();

		/** The values are sorted by using the method sort of the class Collections. */
		Collections.sort(values);

		/** Create an array. */
		Percentile percentile = new Percentile();
		percentile.setData(toDoubleArray(values));

		/** Calculation oft the values. */
		boxPlot.setMax(Collections.max(values));
		boxPlot.setMin(Collections.min(values));
		boxPlot.setQ1(percentile.evaluate(25));
		boxPlot.setMedian(percentile.evaluate(50));
		boxPlot.setQ3(percentile.evaluate(75));

		return boxPlot;
	}

	/** toDoubleArray converts a list into an array */
	double[] toDoubleArray(List<Double> list) {
		double[] ret = new double[list.size()];
		int i = 0;
		for (Double e : list)
			ret[i++] = e.intValue();
		return ret;
	}

	/**
	 * This method computes the mean of the passed values.
	 */
	public AverageMeasurement getAverageMeasurement(List<Measurement> measurements) {

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
		averageMeasurement.setRotation(getAverageRotation(measurements));
		return averageMeasurement;
	}

	public Quaternion getAverageRotation(List<Measurement> measurements) {

		Quaternion firstRotation = measurements.get(0).getRotation();
		Quaternion lastRotation = measurements.get(measurements.size() - 1).getRotation();

		// Zeit durch Anzahl teilen
		// (bei diesem Wert ist die Bewegung genau die des durchschnitts)
		// (insofern das Tool auf dem küzesten weg nach lastRotation bewegt wurde = kein
		// richtungswechel in der Bewegung)
		float positionAtTime = 1 / measurements.size();

		return firstRotation.slerp(firstRotation, lastRotation, positionAtTime);
	}

	/** This method calculates errors and saves them in a list */
	public List<Double> getErrors(List<Measurement> measurements, Point3D avgPoint) {
		List<Double> errors = new ArrayList<>();

		for (int i = 0; i < measurements.size(); i++) {
			Point3D point = measurements.get(i).getPoint();
			double distance = getDistance(point, avgPoint);
			errors.add(distance);
		}
		return errors;
	}

	/** getJitter computes from errors RMSE */
	public double getJitter(List<Double> errors) {
		return getRMSE(errors);
	}

	/** berechnet Jitter von Rotation */
	public Quaternion getRotationJitter(List<Measurement> measurements, Quaternion avgRotation) {

		/** Create two array lists */
		List<Double> rotationPositionErrors = new ArrayList<>();
		List<Double> rotationErrorX = new ArrayList<>();
		List<Double> rotationErrorY = new ArrayList<>();
		List<Double> rotationErrorZ = new ArrayList<>();
		List<Double> rotationErrorW = new ArrayList<>();

		for (int i = 0; i < measurements.size(); i++) {

			Quaternion rotationMovement = measurements.get(i).getRotation();
			// vorraussetzung: liste muss nach zeitstempel sortiert sein
			if (i > 0) {
				rotationMovement = rotationMovement.subtract(measurements.get(i - 1).getRotation());
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
		/** Calculation of the jitter. */

		Quaternion rotationError = new Quaternion((float) getRMSE(rotationErrorX), (float) getRMSE(rotationErrorY),
				(float) getRMSE(rotationErrorZ), (float) getRMSE(rotationErrorW));

		return rotationError;
	}

	// geht nicht da Quaternion an position 0 ist w = 1
	private double getRotationIndize(Quaternion rotation) {
		return rotation.getX() + rotation.getY() + rotation.getZ() + rotation.getW();
	}

	/** This method computes the root mean square error */
	private double getRMSE(List<Double> errors) {
		double additionalPowError = 0;

		for (double error : errors) {
			additionalPowError += Math.pow(error, 2);
		}
		return Math.sqrt(additionalPowError / errors.size());
	}

	/** This method calculates the distance of two points. */
	private double getDistance(Point3D firstPoint, Point3D secondPoint) {
		return firstPoint.distance(secondPoint);
	}
}