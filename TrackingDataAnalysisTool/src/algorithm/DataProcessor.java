package algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.geometry.Point3D;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;

import com.jme3.math.Quaternion;

/** This class provides the methods for processing the measurements. */
public class DataProcessor {

	/**
	 * This method computes the distance of two average points. Then the expected
	 * distance is subtracted from the distance of the points.
	 * 
	 * @param expectedDistance - value of type double
	 * @param firstAverangeMeasurement - value of type AverageMeasurement
	 * @param secondAverangeMeasurement - value of type AverageMeasurement
	 * @return accurate - the distance 
	 */
	public double getAccuracy(double expectedDistance, AverageMeasurement firstAverangeMeasurement,
			AverageMeasurement secondAverangeMeasurement) {
		/* calculates the distance between the point of firstAverangeMeasurement and the point of secondAverangeMeasurement*/
		double accurate = getDistance(firstAverangeMeasurement.getPoint(), secondAverangeMeasurement.getPoint())
				- expectedDistance;
		return accurate;
	}

	/**
	 * This method gets a quaternion (four double values) and two measurements. With the method getRotation the
	 * quaternion of firstMeasurement is fetched. From this value, the second quaternion is subtracted from the 
	 * second measurement. Then the expected quaternion is subtracted. 
	 * 
	 * @param expectedRotation - of type quaternion
	 * @param firstMeasurement - of type Measurement
	 * @param secondMeasurement - of type Measurement
	 * @return result - of type quaternion

	public Quaternion getAccuracyRotation(Quaternion expectedRotation, Measurement firstMeasurement, Measurement secondMeasurement) {
		Quaternion result = firstMeasurement.getRotation().subtract(secondMeasurement.getRotation()).subtract(expectedRotation);
		return result;
		
		 
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
	public BoxPlot getBoxPlot(List<Double> values) {

		BoxPlot boxPlot = new BoxPlot();

		/* The values are sorted by using the method sort of the class Collections. */
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
	double[] toDoubleArray(List<Double> list) {
		double[] ret = new double[list.size()];
		int i = 0;
		for (Double e : list)
			ret[i++] = e.intValue();
		return ret;
	}

	/**
	 * 
	 * 
	 * This method computes the mean of the passed values. getAverageMeasurement gets a list of measurements.
	 * Size of the list is determined. A null point is created. A loop goes over the number of measurements.
	 * In place i the point will be fetched and added to addPoint. 
	 * For x, y and z an average point is calculated. Though all points from addPoint are pitched by the size of the list.
	 * An average measurement and an average point is created. 
	 * 
	 * @param measurements - list with measurements
	 * @return averageMeasurement - a average measurement
	 * 
	 * 
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

	/** 
	 * This method computes the average rotation. The first quaternion of the list of measurement 
	 * on point 0  and the last quaternion is taken.
	 * The time and the size of the measurements are divided. At this value, the movement is exactly the average. 
	 * On the first quaterion method slerp is called by class quaternion. The first and the last quaternion plus positionAtTime
	 * is returned as average rotation.
	 * 
	 * @param measurements - list of measurements
	 * @return firstRotation.slerp(firstRotation, lastRotation, positionAtTime) -  a quaternion
	 * 
	 * 
	 */
	public Quaternion getAverageRotation(List<Measurement> measurements) {

		Quaternion firstRotation = measurements.get(0).getRotation();
		Quaternion lastRotation = measurements.get(measurements.size() - 1).getRotation();

		float positionAtTime = 1 / measurements.size();

		return firstRotation.slerp(firstRotation, lastRotation, positionAtTime);
	}

	/**
	 * This method calculates errors and saves them in a list. With a loop the distance between each point and the average point
	 * is computed. Every distance is added to the list of errors.
	 * 
	 * @param measurements - list of measurements
	 * @param avgPoint - average point of type Point3D
	 * @return errors - of type list
	 */
	public List<Double> getErrors(List<Measurement> measurements, Point3D avgPoint) {
		List<Double> errors = new ArrayList<>();

		for (int i = 0; i < measurements.size(); i++) {
			Point3D point = measurements.get(i).getPoint();
			double distance = getDistance(point, avgPoint);
			errors.add(distance);
		}
		return errors;
	}

	/**
	 * The method getJitter computes the root mean square error. She receives a list of errors and called method
	 * getRMSE, where the RMSE is calculated.
	 * 
	 * @param errors - a list with errors of type double
	 * @return getRMSE - the root mean square error
	 */
	public double getJitter(List<Double> errors) {
		return getRMSE(errors);
	}

	/**
	 * This method computes the Jitter of a Rotation. A list of measurements and an
	 * average rotation is passed. Four lists of errors, for every value of the quaternion, where created.  
	 * In a loop the method gets quaternions on point i. If i > 0, from rotationMovement the quaternion on point i is subtracted. 
	 * Error variables are created. From each quaternion the list from above is added. 
	 * Every list is added in rotationError. From rotation error the root mean square error is calculated. 
	 * 
	 * @param measurements -  list of type measurements
	 * @param avgRotation - average rotation of type Quaternion
	 * @return rotationError - of type quaternion 
	 */

	public Quaternion getRotationJitter(List<Measurement> measurements, Quaternion avgRotation) {

		
		
		List<Double> rotationErrorX = new ArrayList<>();
		List<Double> rotationErrorY = new ArrayList<>();
		List<Double> rotationErrorZ = new ArrayList<>();
		List<Double> rotationErrorW = new ArrayList<>();

		for (int i = 0; i < measurements.size(); i++) {

			Quaternion rotationMovement = measurements.get(i).getRotation();
			
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
		/* Calculation of the jitter. */

		Quaternion rotationError = new Quaternion((float) getRMSE(rotationErrorX), (float) getRMSE(rotationErrorY),
				(float) getRMSE(rotationErrorZ), (float) getRMSE(rotationErrorW));

		return rotationError;
	}

	/**
	 * This method computes the root mean square error. She receives a list of errors. 
	 * In a loop the errors are added and taken in square. 
	 * The RMSE is calculated with the square of additionalPowError divided by the number of errors.  
	 * 
	 * @param errors - list of errors of type double
	 * @return rmse - the Root Mean Square Error, which is just the square root of the mean square error
	 */
	private double getRMSE(List<Double> errors) {
		double additionalPowError = 0;

		for (double error : errors) {
			additionalPowError += Math.pow(error, 2);
		}
		double rmse = Math.sqrt(additionalPowError / errors.size());
		return rmse;
	}

	/**
	 * This method calculates the distance of two points and uses the method distance from the class Point3D
	 * 
	 * @param firstPoint - of type Point3D
	 * @param secondPoint - of type Point3D
	 * @return distance - of type double
	 */
	
	
	private double getDistance(Point3D firstPoint, Point3D secondPoint) {
		double distance = firstPoint.distance(secondPoint);
		return distance;
	}
}