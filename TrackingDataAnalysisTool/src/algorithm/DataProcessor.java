package algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javafx.geometry.Point3D;

import org.apache.commons.math3.complex.Quaternion;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;

public class DataProcessor {

	public double getAccuracy(double expectedDistance, AverageMeasurement firstAverangeMeasurement,
			AverageMeasurement secondAverangeMeasurement) {
		return getDistance(firstAverangeMeasurement.getPoint(), secondAverangeMeasurement.getPoint())
				- expectedDistance;
	}

	public double getAccuracyRotation(double expectedAngle, Measurement firstMeasurement,
			Measurement secondMeasurement) {
		return (firstMeasurement.getRotation().getAngle() - secondMeasurement.getRotation().getAngle()) - expectedAngle;
	}

	public BoxPlot getBoxPlot(List<Double> values) {

		BoxPlot boxPlot = new BoxPlot();
		Collections.sort(values);
		Percentile percentile = new Percentile();
		percentile.setData(toDoubleArray(values));

		boxPlot.setMax(Collections.max(values));
		boxPlot.setMin(Collections.min(values));
		boxPlot.setQ1(percentile.evaluate(25));
		boxPlot.setMedian(percentile.evaluate(50));
		boxPlot.setQ3(percentile.evaluate(75));

		return boxPlot;
	}

	double[] toDoubleArray(List<Double> list) {
		double[] ret = new double[list.size()];
		int i = 0;
		for (Double e : list)
			ret[i++] = e.intValue();
		return ret;
	}

	// public Measurement getCalibration(Point referencePoint, Measurement
	// mainToolMeasurement) {
	//
	// for (int f = 0; f < mainToolMeasurement.getPoints().size(); f++) {
	// Point mainPoint = mainToolMeasurement.getPoints().get(f);
	// double distance = getDistance(referencePoint, mainPoint);
	// mainPoint.setDistanceToPike(distance);
	// }
	// return mainToolMeasurement;
	// }

	// Methode zum Berechnen des Mittelwerts;
	public AverageMeasurement getAverageMeasurement(List<Measurement> measurements) {

		int measureSize = measurements.size();
		Point3D addPoint = new Point3D(0, 0, 0);
		Point3D addRotationPoint = new Point3D(0, 0, 0);
		double scalar = 0;

		for (int i = 0; i < measureSize; i++) {
			Measurement measurement = measurements.get(i);
			Point3D point = measurement.getPoint();
			addPoint = addPoint.add(point);
			Rotation rotation = measurement.getRotation();
			Point3D rotationPoint = new Point3D(rotation.getQ1(), rotation.getQ2(), rotation.getQ3());
			addRotationPoint = addRotationPoint.add(rotationPoint);
			scalar += rotation.getQ0();
		}

		double averageX = addPoint.getX() / measureSize;
		double averageY = addPoint.getY() / measureSize;
		double averageZ = addPoint.getZ() / measureSize;

		double avgRotationS = scalar / measureSize;
		double avgRotationX = addRotationPoint.getX() / measureSize;
		double avgRotationY = addRotationPoint.getY() / measureSize;
		double avgRotationZ = addRotationPoint.getZ() / measureSize;

		AverageMeasurement averageMeasurement = new AverageMeasurement();
		Point3D averagePoint = new Point3D(averageX, averageY, averageZ);
		Rotation averageRotation = new Rotation(avgRotationS, avgRotationX, avgRotationY, avgRotationZ, true);

		averageMeasurement.setPoint(averagePoint);
		averageMeasurement.setRotation(averageRotation);
		return averageMeasurement;
	}

	public List<Double> getErrors(List<Measurement> measurements, Point3D avgPoint) {
		List<Double> errors = new ArrayList<>();

		for (int i = 0; i < measurements.size(); i++) {
			Point3D point = measurements.get(i).getPoint();
			double distance = getDistance(point, avgPoint);
			errors.add(distance);
		}
		return errors;
	}

	public double getJitter(List<Double> errors) {
		return getRMSE(errors);
	}

	public RotationError getRotationJitter(List<Measurement> measurements, Rotation avgRotation) {
		List<Double> rotationPositionErrors = new ArrayList<>();
		List<Double> rotationAngleErrors = new ArrayList<>();

		RotationError rotationError = new RotationError();

		for (int i = 0; i < measurements.size(); i++) {
			Rotation rotationPosition = measurements.get(i).getRotation();
			double angle = measurements.get(i).getRotation().getAngle();

			double angleRotation = angle - avgRotation.getAngle();
			double distanceRotation = Rotation.distance(rotationPosition, avgRotation);
			rotationPositionErrors.add(distanceRotation);
			rotationAngleErrors.add(angleRotation);
		}

		rotationError.setRotationPositionError(getRMSE(rotationPositionErrors));
		rotationError.setRotationAngleError(getRMSE(rotationAngleErrors));

		return rotationError;
	}

	// root mean square error
	private double getRMSE(List<Double> errors) {
		double additionalPowError = 0;

		for (double error : errors) {
			additionalPowError += Math.pow(error, 2);
		}
		return Math.sqrt(additionalPowError / errors.size());
	}

	// calculate distance of two points
	private double getDistance(Point3D firstPoint, Point3D secondPoint) {
		return firstPoint.distance(secondPoint);
	}
}